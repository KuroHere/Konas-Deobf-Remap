package com.viaversion.viaversion.api;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.legacy.LegacyViaAPI;
import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import io.netty.buffer.ByteBuf;
import java.util.SortedSet;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ViaAPI {
   default int apiVersion() {
      return 3;
   }

   ServerProtocolVersion getServerVersion();

   int getPlayerVersion(Object var1);

   int getPlayerVersion(UUID var1);

   boolean isInjected(UUID var1);

   @Nullable
   UserConnection getConnection(UUID var1);

   String getVersion();

   void sendRawPacket(Object var1, ByteBuf var2);

   void sendRawPacket(UUID var1, ByteBuf var2);

   SortedSet getSupportedVersions();

   SortedSet getFullSupportedVersions();

   LegacyViaAPI legacyAPI();
}
