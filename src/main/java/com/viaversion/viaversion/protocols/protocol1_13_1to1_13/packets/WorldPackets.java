package com.viaversion.viaversion.protocols.protocol1_13_1to1_13.packets;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.BlockRewriter;

public class WorldPackets {
   public static void register(final Protocol protocol) {
      BlockRewriter blockRewriter = new BlockRewriter(protocol, Type.POSITION);
      protocol.registerClientbound(ClientboundPackets1_13.CHUNK_DATA, new PacketRemapper() {
         public void registerMap() {
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                  Chunk chunk = (Chunk)wrapper.passthrough(new Chunk1_13Type(clientWorld));
                  ChunkSection[] var4 = chunk.getSections();
                  int var5 = var4.length;

                  for(int var6 = 0; var6 < var5; ++var6) {
                     ChunkSection section = var4[var6];
                     if (section != null) {
                        for(int i = 0; i < section.getPaletteSize(); ++i) {
                           section.setPaletteEntry(i, protocol.getMappingData().getNewBlockStateId(section.getPaletteEntry(i)));
                        }
                     }
                  }

               }
            });
         }
      });
      blockRewriter.registerBlockAction(ClientboundPackets1_13.BLOCK_ACTION);
      blockRewriter.registerBlockChange(ClientboundPackets1_13.BLOCK_CHANGE);
      blockRewriter.registerMultiBlockChange(ClientboundPackets1_13.MULTI_BLOCK_CHANGE);
      blockRewriter.registerEffect(ClientboundPackets1_13.EFFECT, 1010, 2001);
      protocol.registerClientbound(ClientboundPackets1_13.JOIN_GAME, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.INT);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.INT);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
                  int dimensionId = (Integer)wrapper.get(Type.INT, 1);
                  clientChunks.setEnvironment(dimensionId);
               }
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_13.RESPAWN, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.INT);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                  int dimensionId = (Integer)wrapper.get(Type.INT, 0);
                  clientWorld.setEnvironment(dimensionId);
               }
            });
         }
      });
   }
}
