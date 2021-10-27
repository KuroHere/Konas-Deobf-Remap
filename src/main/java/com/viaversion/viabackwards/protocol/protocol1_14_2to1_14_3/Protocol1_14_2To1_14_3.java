package com.viaversion.viabackwards.protocol.protocol1_14_2to1_14_3;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data.RecipeRewriter1_14;
import com.viaversion.viaversion.rewriter.RecipeRewriter;

public class Protocol1_14_2To1_14_3 extends BackwardsProtocol {
   public Protocol1_14_2To1_14_3() {
      super(ClientboundPackets1_14.class, ClientboundPackets1_14.class, ServerboundPackets1_14.class, ServerboundPackets1_14.class);
   }

   protected void registerPackets() {
      this.registerClientbound(ClientboundPackets1_14.TRADE_LIST, new PacketRemapper() {
         public void registerMap() {
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  wrapper.passthrough(Type.VAR_INT);
                  int size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);

                  for(int i = 0; i < size; ++i) {
                     wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                     wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                     if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
                        wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                     }

                     wrapper.passthrough(Type.BOOLEAN);
                     wrapper.passthrough(Type.INT);
                     wrapper.passthrough(Type.INT);
                     wrapper.passthrough(Type.INT);
                     wrapper.passthrough(Type.INT);
                     wrapper.passthrough(Type.FLOAT);
                  }

                  wrapper.passthrough(Type.VAR_INT);
                  wrapper.passthrough(Type.VAR_INT);
                  wrapper.passthrough(Type.BOOLEAN);
                  wrapper.read(Type.BOOLEAN);
               }
            });
         }
      });
      final RecipeRewriter recipeHandler = new RecipeRewriter1_14(this);
      this.registerClientbound(ClientboundPackets1_14.DECLARE_RECIPES, new PacketRemapper() {
         public void registerMap() {
            this.handler((wrapper) -> {
               int size = (Integer)wrapper.passthrough(Type.VAR_INT);
               int deleted = 0;

               for(int i = 0; i < size; ++i) {
                  String fullType = (String)wrapper.read(Type.STRING);
                  String type = fullType.replace("minecraft:", "");
                  String id = (String)wrapper.read(Type.STRING);
                  if (type.equals("crafting_special_repairitem")) {
                     ++deleted;
                  } else {
                     wrapper.write(Type.STRING, fullType);
                     wrapper.write(Type.STRING, id);
                     recipeHandler.handle(wrapper, type);
                  }
               }

               wrapper.set(Type.VAR_INT, 0, size - deleted);
            });
         }
      });
   }
}
