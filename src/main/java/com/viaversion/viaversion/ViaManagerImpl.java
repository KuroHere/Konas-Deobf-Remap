package com.viaversion.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaManager;
import com.viaversion.viaversion.api.connection.ConnectionManager;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.ProtocolManager;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viaversion.connection.ConnectionManagerImpl;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.protocol.ProtocolManagerImpl;
import com.viaversion.viaversion.protocol.ServerProtocolVersionRange;
import com.viaversion.viaversion.protocol.ServerProtocolVersionSingleton;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.TabCompleteThread;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ViaIdleThread;
import com.viaversion.viaversion.update.UpdateUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViaManagerImpl implements ViaManager {
   private final ProtocolManagerImpl protocolManager = new ProtocolManagerImpl();
   private final ConnectionManager connectionManager = new ConnectionManagerImpl();
   private final ViaProviders providers = new ViaProviders();
   private final ViaPlatform platform;
   private final ViaInjector injector;
   private final ViaCommandHandler commandHandler;
   private final ViaPlatformLoader loader;
   private final Set subPlatforms = new HashSet();
   private List enableListeners = new ArrayList();
   private PlatformTask mappingLoadingTask;
   private boolean debug;

   public ViaManagerImpl(ViaPlatform platform, ViaInjector injector, ViaCommandHandler commandHandler, ViaPlatformLoader loader) {
      this.platform = platform;
      this.injector = injector;
      this.commandHandler = commandHandler;
      this.loader = loader;
   }

   public static ViaManagerImpl.ViaManagerBuilder builder() {
      return new ViaManagerImpl.ViaManagerBuilder();
   }

   public void init() {
      if (System.getProperty("ViaVersion") != null) {
         this.platform.onReload();
      }

      if (this.platform.getConf().isCheckForUpdates()) {
         UpdateUtil.sendUpdateMessage();
      }

      if (!this.injector.lateProtocolVersionSetting()) {
         this.loadServerProtocol();
      }

      this.protocolManager.registerProtocols();

      try {
         this.injector.inject();
      } catch (Exception var3) {
         this.platform.getLogger().severe("ViaVersion failed to inject:");
         var3.printStackTrace();
         return;
      }

      System.setProperty("ViaVersion", this.platform.getPluginVersion());
      Iterator var1 = this.enableListeners.iterator();

      while(var1.hasNext()) {
         Runnable listener = (Runnable)var1.next();
         listener.run();
      }

      this.enableListeners = null;
      this.platform.runSync(this::onServerLoaded);
   }

   public void onServerLoaded() {
      if (!this.protocolManager.getServerProtocolVersion().isKnown()) {
         this.loadServerProtocol();
      }

      ServerProtocolVersion protocolVersion = this.protocolManager.getServerProtocolVersion();
      if (protocolVersion.isKnown()) {
         if (this.platform.isProxy()) {
            this.platform.getLogger().info("ViaVersion detected lowest supported version by the proxy: " + ProtocolVersion.getProtocol(protocolVersion.lowestSupportedVersion()));
            this.platform.getLogger().info("Highest supported version by the proxy: " + ProtocolVersion.getProtocol(protocolVersion.highestSupportedVersion()));
            if (this.debug) {
               this.platform.getLogger().info("Supported version range: " + Arrays.toString(protocolVersion.supportedVersions().toArray(new int[0])));
            }
         } else {
            this.platform.getLogger().info("ViaVersion detected server version: " + ProtocolVersion.getProtocol(protocolVersion.highestSupportedVersion()));
         }

         if (!this.protocolManager.isWorkingPipe()) {
            this.platform.getLogger().warning("ViaVersion does not have any compatible versions for this server version!");
            this.platform.getLogger().warning("Please remember that ViaVersion only adds support for versions newer than the server version.");
            this.platform.getLogger().warning("If you need support for older versions you may need to use one or more ViaVersion addons too.");
            this.platform.getLogger().warning("In that case please read the ViaVersion resource page carefully or use https://jo0001.github.io/ViaSetup");
            this.platform.getLogger().warning("and if you're still unsure, feel free to join our Discord-Server for further assistance.");
         } else if (protocolVersion.highestSupportedVersion() <= ProtocolVersion.v1_12_2.getVersion()) {
            this.platform.getLogger().warning("This version of Minecraft is extremely outdated and support for it has reached its end of life. You will still be able to run Via on this Minecraft version, but we are unlikely to provide any further fixes or help with problems specific to legacy Minecraft versions. Please consider updating to give your players a better experience and to avoid issues that have long been fixed.");
         }
      }

      this.checkJavaVersion();
      this.unsupportedSoftwareWarning();
      this.protocolManager.onServerLoaded();
      this.loader.load();
      this.mappingLoadingTask = Via.getPlatform().runRepeatingSync(() -> {
         if (this.protocolManager.checkForMappingCompletion()) {
            this.mappingLoadingTask.cancel();
            this.mappingLoadingTask = null;
         }

      }, 10L);
      int serverProtocolVersion = this.protocolManager.getServerProtocolVersion().lowestSupportedVersion();
      if (serverProtocolVersion < ProtocolVersion.v1_9.getVersion() && Via.getConfig().isSimulatePlayerTick()) {
         Via.getPlatform().runRepeatingSync(new ViaIdleThread(), 1L);
      }

      if (serverProtocolVersion < ProtocolVersion.v1_13.getVersion() && Via.getConfig().get1_13TabCompleteDelay() > 0) {
         Via.getPlatform().runRepeatingSync(new TabCompleteThread(), 1L);
      }

      this.protocolManager.refreshVersions();
   }

   private void loadServerProtocol() {
      try {
         ProtocolVersion serverProtocolVersion = ProtocolVersion.getProtocol(this.injector.getServerProtocolVersion());
         Object versionInfo;
         if (this.platform.isProxy()) {
            IntSortedSet supportedVersions = this.injector.getServerProtocolVersions();
            versionInfo = new ServerProtocolVersionRange(supportedVersions.firstInt(), supportedVersions.lastInt(), supportedVersions);
         } else {
            versionInfo = new ServerProtocolVersionSingleton(serverProtocolVersion.getVersion());
         }

         this.protocolManager.setServerProtocol((ServerProtocolVersion)versionInfo);
      } catch (Exception var4) {
         this.platform.getLogger().severe("ViaVersion failed to get the server protocol!");
         var4.printStackTrace();
      }

   }

   public void destroy() {
      this.platform.getLogger().info("ViaVersion is disabling, if this is a reload and you experience issues consider rebooting.");

      try {
         this.injector.uninject();
      } catch (Exception var2) {
         this.platform.getLogger().severe("ViaVersion failed to uninject:");
         var2.printStackTrace();
      }

      this.loader.unload();
   }

   private final void checkJavaVersion() {
      String javaVersion = System.getProperty("java.version");
      Matcher matcher = Pattern.compile("(?:1\\.)?(\\d+)").matcher(javaVersion);
      if (!matcher.find()) {
         this.platform.getLogger().warning("Failed to determine Java version; could not parse: " + javaVersion);
      } else {
         String versionString = matcher.group(1);

         int version;
         try {
            version = Integer.parseInt(versionString);
         } catch (NumberFormatException var6) {
            this.platform.getLogger().warning("Failed to determine Java version; could not parse: " + versionString);
            var6.printStackTrace();
            return;
         }

         if (version < 16) {
            this.platform.getLogger().warning("You are running an outdated Java version, please consider updating it to at least Java 16 (your version is " + javaVersion + "). At some point in the future, ViaVersion will no longer be compatible with this version of Java.");
         }

      }
   }

   private final void unsupportedSoftwareWarning() {
      boolean found = false;
      Iterator var2 = this.platform.getUnsupportedSoftwareClasses().iterator();

      while(var2.hasNext()) {
         UnsupportedSoftware software = (UnsupportedSoftware)var2.next();
         if (software.findMatch()) {
            if (!found) {
               this.platform.getLogger().severe("************************************************");
               this.platform.getLogger().severe("You are using unsupported software and may encounter unforeseeable issues.");
               this.platform.getLogger().severe("");
               found = true;
            }

            this.platform.getLogger().severe("We strongly advise against using " + software.getName() + ":");
            this.platform.getLogger().severe(software.getReason());
            this.platform.getLogger().severe("");
         }
      }

      if (found) {
         this.platform.getLogger().severe("We will not provide support in case you encounter issues possibly related to this software.");
         this.platform.getLogger().severe("************************************************");
      }

   }

   public ViaPlatform getPlatform() {
      return this.platform;
   }

   public ConnectionManager getConnectionManager() {
      return this.connectionManager;
   }

   public ProtocolManager getProtocolManager() {
      return this.protocolManager;
   }

   public ViaProviders getProviders() {
      return this.providers;
   }

   public boolean isDebug() {
      return this.debug;
   }

   public void setDebug(boolean debug) {
      this.debug = debug;
   }

   public ViaInjector getInjector() {
      return this.injector;
   }

   public ViaCommandHandler getCommandHandler() {
      return this.commandHandler;
   }

   public ViaPlatformLoader getLoader() {
      return this.loader;
   }

   public Set getSubPlatforms() {
      return this.subPlatforms;
   }

   public void addEnableListener(Runnable runnable) {
      this.enableListeners.add(runnable);
   }

   public static final class ViaManagerBuilder {
      private ViaPlatform platform;
      private ViaInjector injector;
      private ViaCommandHandler commandHandler;
      private ViaPlatformLoader loader;

      public ViaManagerImpl.ViaManagerBuilder platform(ViaPlatform platform) {
         this.platform = platform;
         return this;
      }

      public ViaManagerImpl.ViaManagerBuilder injector(ViaInjector injector) {
         this.injector = injector;
         return this;
      }

      public ViaManagerImpl.ViaManagerBuilder loader(ViaPlatformLoader loader) {
         this.loader = loader;
         return this;
      }

      public ViaManagerImpl.ViaManagerBuilder commandHandler(ViaCommandHandler commandHandler) {
         this.commandHandler = commandHandler;
         return this;
      }

      public ViaManagerImpl build() {
         return new ViaManagerImpl(this.platform, this.injector, this.commandHandler, this.loader);
      }
   }
}
