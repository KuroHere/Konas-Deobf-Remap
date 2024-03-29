package com.viaversion.viaversion.protocols.base;

import com.google.common.base.Joiner;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.protocol.ProtocolManagerImpl;
import com.viaversion.viaversion.protocol.ServerProtocolVersionSingleton;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.util.ChatColorUtil;
import com.viaversion.viaversion.util.GsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class BaseProtocol1_7 extends AbstractSimpleProtocol {
   protected void registerPackets() {
      this.registerClientbound(State.STATUS, 0, 0, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.STRING);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  ProtocolInfo info = wrapper.user().getProtocolInfo();
                  String originalStatus = (String)wrapper.get(Type.STRING, 0);

                  try {
                     JsonElement json = (JsonElement)GsonUtil.getGson().fromJson(originalStatus, JsonElement.class);
                     int protocolVersion = 0;
                     JsonObject version;
                     if (json.isJsonObject()) {
                        if (json.getAsJsonObject().has("version")) {
                           version = json.getAsJsonObject().get("version").getAsJsonObject();
                           if (version.has("protocol")) {
                              protocolVersion = Long.valueOf(version.get("protocol").getAsLong()).intValue();
                           }
                        } else {
                           json.getAsJsonObject().add("version", version = new JsonObject());
                        }
                     } else {
                        json = new JsonObject();
                        json.getAsJsonObject().add("version", version = new JsonObject());
                     }

                     if (Via.getConfig().isSendSupportedVersions()) {
                        version.add("supportedVersions", GsonUtil.getGson().toJsonTree(Via.getAPI().getSupportedVersions()));
                     }

                     if (!Via.getAPI().getServerVersion().isKnown()) {
                        ProtocolManagerImpl protocolManager = (ProtocolManagerImpl)Via.getManager().getProtocolManager();
                        protocolManager.setServerProtocol(new ServerProtocolVersionSingleton(ProtocolVersion.getProtocol(protocolVersion).getVersion()));
                     }

                     VersionProvider versionProvider = (VersionProvider)Via.getManager().getProviders().get(VersionProvider.class);
                     if (versionProvider == null) {
                        wrapper.user().setActive(false);
                        return;
                     }

                     int closestServerProtocol = versionProvider.getClosestServerProtocol(wrapper.user());
                     List protocols = null;
                     if (info.getProtocolVersion() >= closestServerProtocol || Via.getPlatform().isOldClientsAllowed()) {
                        protocols = Via.getManager().getProtocolManager().getProtocolPath(info.getProtocolVersion(), closestServerProtocol);
                     }

                     if (protocols != null) {
                        if (protocolVersion == closestServerProtocol || protocolVersion == 0) {
                           ProtocolVersion prot = ProtocolVersion.getProtocol(info.getProtocolVersion());
                           version.addProperty("protocol", prot.getOriginalVersion());
                        }
                     } else {
                        wrapper.user().setActive(false);
                     }

                     if (Via.getConfig().getBlockedProtocols().contains(info.getProtocolVersion())) {
                        version.addProperty("protocol", -1);
                     }

                     wrapper.set(Type.STRING, 0, GsonUtil.getGson().toJson(json));
                  } catch (JsonParseException var11) {
                     var11.printStackTrace();
                  }

               }
            });
         }
      });
      this.registerClientbound(State.STATUS, 1, 1);
      this.registerClientbound(State.LOGIN, 0, 0);
      this.registerClientbound(State.LOGIN, 1, 1);
      this.registerClientbound(State.LOGIN, 2, 2, new PacketRemapper() {
         public void registerMap() {
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  ProtocolInfo info = wrapper.user().getProtocolInfo();
                  info.setState(State.PLAY);
                  UUID uuid = BaseProtocol1_7.this.passthroughLoginUUID(wrapper);
                  info.setUuid(uuid);
                  String username = (String)wrapper.passthrough(Type.STRING);
                  info.setUsername(username);
                  Via.getManager().getConnectionManager().onLoginSuccess(wrapper.user());
                  if (!info.getPipeline().hasNonBaseProtocols()) {
                     wrapper.user().setActive(false);
                  }

                  if (Via.getManager().isDebug()) {
                     Via.getPlatform().getLogger().log(Level.INFO, "{0} logged in with protocol {1}, Route: {2}", new Object[]{username, info.getProtocolVersion(), Joiner.on(", ").join(info.getPipeline().pipes(), ", ", new Object[0])});
                  }

               }
            });
         }
      });
      this.registerClientbound(State.LOGIN, 3, 3);
      this.registerServerbound(State.LOGIN, 4, 4);
      this.registerServerbound(State.STATUS, 0, 0);
      this.registerServerbound(State.STATUS, 1, 1);
      this.registerServerbound(State.LOGIN, 0, 0, new PacketRemapper() {
         public void registerMap() {
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  int protocol = wrapper.user().getProtocolInfo().getProtocolVersion();
                  if (Via.getConfig().getBlockedProtocols().contains(protocol)) {
                     if (!wrapper.user().getChannel().isOpen()) {
                        return;
                     }

                     if (!wrapper.user().shouldApplyBlockProtocol()) {
                        return;
                     }

                     PacketWrapper disconnectPacket = PacketWrapper.create(0, null, wrapper.user());
                     Protocol1_9To1_8.FIX_JSON.write(disconnectPacket, ChatColorUtil.translateAlternateColorCodes(Via.getConfig().getBlockedDisconnectMsg()));
                     wrapper.cancel();
                     ChannelFuture future = disconnectPacket.sendFuture(BaseProtocol.class);
                     future.addListener((f) -> {
                        wrapper.user().getChannel().close();
                     });
                  }

               }
            });
         }
      });
      this.registerServerbound(State.LOGIN, 1, 1);
      this.registerServerbound(State.LOGIN, 2, 2);
   }

   public boolean isBaseProtocol() {
      return true;
   }

   public static String addDashes(String trimmedUUID) {
      StringBuilder idBuff = new StringBuilder(trimmedUUID);
      idBuff.insert(20, '-');
      idBuff.insert(16, '-');
      idBuff.insert(12, '-');
      idBuff.insert(8, '-');
      return idBuff.toString();
   }

   protected UUID passthroughLoginUUID(PacketWrapper wrapper) throws Exception {
      String uuidString = (String)wrapper.passthrough(Type.STRING);
      if (uuidString.length() == 32) {
         uuidString = addDashes(uuidString);
      }

      return UUID.fromString(uuidString);
   }
}
