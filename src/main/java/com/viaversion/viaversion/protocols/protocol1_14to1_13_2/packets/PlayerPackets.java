package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;

public class PlayerPackets {
   public static void register(final Protocol protocol) {
      protocol.registerClientbound(ClientboundPackets1_13.OPEN_SIGN_EDITOR, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.POSITION, Type.POSITION1_14);
         }
      });
      protocol.registerServerbound(ServerboundPackets1_14.QUERY_BLOCK_NBT, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.map(Type.POSITION1_14, Type.POSITION);
         }
      });
      protocol.registerServerbound(ServerboundPackets1_14.EDIT_BOOK, new PacketRemapper() {
         public void registerMap() {
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  Item item = (Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                  protocol.getItemRewriter().handleItemToServer(item);
                  if (Via.getConfig().isTruncate1_14Books()) {
                     if (item == null) {
                        return;
                     }

                     CompoundTag tag = item.tag();
                     if (tag == null) {
                        return;
                     }

                     Tag pages = tag.get("pages");
                     if (!(pages instanceof ListTag)) {
                        return;
                     }

                     ListTag listTag = (ListTag)pages;
                     if (listTag.size() <= 50) {
                        return;
                     }

                     listTag.setValue(listTag.getValue().subList(0, 50));
                  }

               }
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_14.PLAYER_DIGGING, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.map(Type.POSITION1_14, Type.POSITION);
            this.map(Type.BYTE);
         }
      });
      protocol.registerServerbound(ServerboundPackets1_14.RECIPE_BOOK_DATA, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  int type = (Integer)wrapper.get(Type.VAR_INT, 0);
                  if (type == 0) {
                     wrapper.passthrough(Type.STRING);
                  } else if (type == 1) {
                     wrapper.passthrough(Type.BOOLEAN);
                     wrapper.passthrough(Type.BOOLEAN);
                     wrapper.passthrough(Type.BOOLEAN);
                     wrapper.passthrough(Type.BOOLEAN);
                     wrapper.read(Type.BOOLEAN);
                     wrapper.read(Type.BOOLEAN);
                     wrapper.read(Type.BOOLEAN);
                     wrapper.read(Type.BOOLEAN);
                  }

               }
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_14.UPDATE_COMMAND_BLOCK, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.POSITION1_14, Type.POSITION);
         }
      });
      protocol.registerServerbound(ServerboundPackets1_14.UPDATE_STRUCTURE_BLOCK, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.POSITION1_14, Type.POSITION);
         }
      });
      protocol.registerServerbound(ServerboundPackets1_14.UPDATE_SIGN, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.POSITION1_14, Type.POSITION);
         }
      });
      protocol.registerServerbound(ServerboundPackets1_14.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
         public void registerMap() {
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  int hand = (Integer)wrapper.read(Type.VAR_INT);
                  Position position = (Position)wrapper.read(Type.POSITION1_14);
                  int face = (Integer)wrapper.read(Type.VAR_INT);
                  float x = (Float)wrapper.read(Type.FLOAT);
                  float y = (Float)wrapper.read(Type.FLOAT);
                  float z = (Float)wrapper.read(Type.FLOAT);
                  wrapper.read(Type.BOOLEAN);
                  wrapper.write(Type.POSITION, position);
                  wrapper.write(Type.VAR_INT, face);
                  wrapper.write(Type.VAR_INT, hand);
                  wrapper.write(Type.FLOAT, x);
                  wrapper.write(Type.FLOAT, y);
                  wrapper.write(Type.FLOAT, z);
               }
            });
         }
      });
   }
}
