package com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.packets;

import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord1_16_2;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.types.Chunk1_16_2Type;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.types.Chunk1_16Type;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import java.util.ArrayList;
import java.util.List;

public class WorldPackets {
   private static final BlockChangeRecord[] EMPTY_RECORDS = new BlockChangeRecord[0];

   public static void register(final Protocol protocol) {
      BlockRewriter blockRewriter = new BlockRewriter(protocol, Type.POSITION1_14);
      blockRewriter.registerBlockAction(ClientboundPackets1_16.BLOCK_ACTION);
      blockRewriter.registerBlockChange(ClientboundPackets1_16.BLOCK_CHANGE);
      blockRewriter.registerAcknowledgePlayerDigging(ClientboundPackets1_16.ACKNOWLEDGE_PLAYER_DIGGING);
      protocol.registerClientbound(ClientboundPackets1_16.CHUNK_DATA, new PacketRemapper() {
         public void registerMap() {
            this.handler((wrapper) -> {
               Chunk chunk = (Chunk)wrapper.read(new Chunk1_16Type());
               wrapper.write(new Chunk1_16_2Type(), chunk);

               for(int s = 0; s < chunk.getSections().length; ++s) {
                  ChunkSection section = chunk.getSections()[s];
                  if (section != null) {
                     for(int i = 0; i < section.getPaletteSize(); ++i) {
                        int old = section.getPaletteEntry(i);
                        section.setPaletteEntry(i, protocol.getMappingData().getNewBlockStateId(old));
                     }
                  }
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_16.MULTI_BLOCK_CHANGE, new PacketRemapper() {
         public void registerMap() {
            this.handler((wrapper) -> {
               wrapper.cancel();
               int chunkX = (Integer)wrapper.read(Type.INT);
               int chunkZ = (Integer)wrapper.read(Type.INT);
               long chunkPosition = 0L;
               chunkPosition |= ((long)chunkX & 4194303L) << 42;
               chunkPosition |= ((long)chunkZ & 4194303L) << 20;
               List[] sectionRecords = new List[16];
               BlockChangeRecord[] blockChangeRecord = (BlockChangeRecord[])wrapper.read(Type.BLOCK_CHANGE_RECORD_ARRAY);
               BlockChangeRecord[] var8 = blockChangeRecord;
               int var9 = blockChangeRecord.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  BlockChangeRecord record = var8[var10];
                  int chunkY = record.getY() >> 4;
                  List list = sectionRecords[chunkY];
                  if (list == null) {
                     sectionRecords[chunkY] = list = new ArrayList();
                  }

                  int blockId = protocol.getMappingData().getNewBlockStateId(record.getBlockId());
                  list.add(new BlockChangeRecord1_16_2(record.getSectionX(), record.getSectionY(), record.getSectionZ(), blockId));
               }

               for(int chunkYx = 0; chunkYx < sectionRecords.length; ++chunkYx) {
                  List sectionRecord = sectionRecords[chunkYx];
                  if (sectionRecord != null) {
                     PacketWrapper newPacket = wrapper.create(ClientboundPackets1_16_2.MULTI_BLOCK_CHANGE);
                     newPacket.write(Type.LONG, chunkPosition | (long)chunkYx & 1048575L);
                     newPacket.write(Type.BOOLEAN, false);
                     newPacket.write(Type.VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY, sectionRecord.toArray(WorldPackets.EMPTY_RECORDS));
                     newPacket.send(Protocol1_16_2To1_16_1.class);
                  }
               }

            });
         }
      });
      blockRewriter.registerEffect(ClientboundPackets1_16.EFFECT, 1010, 2001);
   }
}
