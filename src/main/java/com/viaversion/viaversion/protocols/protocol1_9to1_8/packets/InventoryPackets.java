package com.viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.InventoryTracker;

public class InventoryPackets {
   public static void register(Protocol protocol) {
      protocol.registerClientbound(ClientboundPackets1_8.WINDOW_PROPERTY, (PacketRemapper)(new PacketRemapper() {
         public void registerMap() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.SHORT);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  final short windowId = (Short)wrapper.get(Type.UNSIGNED_BYTE, 0);
                  final short property = (Short)wrapper.get(Type.SHORT, 0);
                  short value = (Short)wrapper.get(Type.SHORT, 1);
                  InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                  if (inventoryTracker.getInventory() != null && inventoryTracker.getInventory().equalsIgnoreCase("minecraft:enchanting_table") && property > 3 && property < 7) {
                     short level = (short)(value >> 8);
                     final short enchantID = (short)(value & 255);
                     wrapper.create(wrapper.getId(), new PacketHandler() {
                        public void handle(PacketWrapper wrapper) throws Exception {
                           wrapper.write(Type.UNSIGNED_BYTE, windowId);
                           wrapper.write(Type.SHORT, property);
                           wrapper.write(Type.SHORT, enchantID);
                        }
                     }).scheduleSend(Protocol1_9To1_8.class);
                     wrapper.set(Type.SHORT, 0, (short)(property + 3));
                     wrapper.set(Type.SHORT, 1, level);
                  }

               }
            });
         }
      }));
      protocol.registerClientbound(ClientboundPackets1_8.OPEN_WINDOW, (PacketRemapper)(new PacketRemapper() {
         public void registerMap() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.STRING);
            this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            this.map(Type.UNSIGNED_BYTE);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  String inventory = (String)wrapper.get(Type.STRING, 0);
                  InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                  inventoryTracker.setInventory(inventory);
               }
            });
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  String inventory = (String)wrapper.get(Type.STRING, 0);
                  if (inventory.equals("minecraft:brewing_stand")) {
                     wrapper.set(Type.UNSIGNED_BYTE, 1, (short)((Short)wrapper.get(Type.UNSIGNED_BYTE, 1) + 1));
                  }

               }
            });
         }
      }));
      protocol.registerClientbound(ClientboundPackets1_8.SET_SLOT, (PacketRemapper)(new PacketRemapper() {
         public void registerMap() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.ITEM);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  Item stack = (Item)wrapper.get(Type.ITEM, 0);
                  boolean showShieldWhenSwordInHand = Via.getConfig().isShowShieldWhenSwordInHand() && Via.getConfig().isShieldBlocking();
                  if (showShieldWhenSwordInHand) {
                     InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                     EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                     short slotID = (Short)wrapper.get(Type.SHORT, 0);
                     byte windowId = ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).byteValue();
                     inventoryTracker.setItemId((short)windowId, slotID, stack == null ? 0 : stack.identifier());
                     entityTracker.syncShieldWithSword();
                  }

                  ItemRewriter.toClient(stack);
               }
            });
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                  short slotID = (Short)wrapper.get(Type.SHORT, 0);
                  if (inventoryTracker.getInventory() != null && inventoryTracker.getInventory().equals("minecraft:brewing_stand") && slotID >= 4) {
                     wrapper.set(Type.SHORT, 0, (short)(slotID + 1));
                  }

               }
            });
         }
      }));
      protocol.registerClientbound(ClientboundPackets1_8.WINDOW_ITEMS, (PacketRemapper)(new PacketRemapper() {
         public void registerMap() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.ITEM_ARRAY);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  Item[] stacks = (Item[])wrapper.get(Type.ITEM_ARRAY, 0);
                  Short windowId = (Short)wrapper.get(Type.UNSIGNED_BYTE, 0);
                  InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                  EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                  boolean showShieldWhenSwordInHand = Via.getConfig().isShowShieldWhenSwordInHand() && Via.getConfig().isShieldBlocking();

                  for(short i = 0; i < stacks.length; ++i) {
                     Item stack = stacks[i];
                     if (showShieldWhenSwordInHand) {
                        inventoryTracker.setItemId(windowId, i, stack == null ? 0 : stack.identifier());
                     }

                     ItemRewriter.toClient(stack);
                  }

                  if (showShieldWhenSwordInHand) {
                     entityTracker.syncShieldWithSword();
                  }

               }
            });
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                  if (inventoryTracker.getInventory() != null && inventoryTracker.getInventory().equals("minecraft:brewing_stand")) {
                     Item[] oldStack = (Item[])wrapper.get(Type.ITEM_ARRAY, 0);
                     Item[] newStack = new Item[oldStack.length + 1];

                     for(int i = 0; i < newStack.length; ++i) {
                        if (i > 4) {
                           newStack[i] = oldStack[i - 1];
                        } else if (i != 4) {
                           newStack[i] = oldStack[i];
                        }
                     }

                     wrapper.set(Type.ITEM_ARRAY, 0, newStack);
                  }

               }
            });
         }
      }));
      protocol.registerClientbound(ClientboundPackets1_8.CLOSE_WINDOW, (PacketRemapper)(new PacketRemapper() {
         public void registerMap() {
            this.map(Type.UNSIGNED_BYTE);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                  inventoryTracker.setInventory((String)null);
                  inventoryTracker.resetInventory((Short)wrapper.get(Type.UNSIGNED_BYTE, 0));
               }
            });
         }
      }));
      protocol.registerClientbound(ClientboundPackets1_8.MAP_DATA, (PacketRemapper)(new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) {
                  wrapper.write(Type.BOOLEAN, true);
               }
            });
         }
      }));
      protocol.registerServerbound(ServerboundPackets1_9.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.SHORT);
            this.map(Type.ITEM);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  Item stack = (Item)wrapper.get(Type.ITEM, 0);
                  boolean showShieldWhenSwordInHand = Via.getConfig().isShowShieldWhenSwordInHand() && Via.getConfig().isShieldBlocking();
                  if (showShieldWhenSwordInHand) {
                     InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                     EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                     short slotID = (Short)wrapper.get(Type.SHORT, 0);
                     inventoryTracker.setItemId((short)0, slotID, stack == null ? 0 : stack.identifier());
                     entityTracker.syncShieldWithSword();
                  }

                  ItemRewriter.toServer(stack);
               }
            });
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  final short slot = (Short)wrapper.get(Type.SHORT, 0);
                  boolean throwItem = slot == 45;
                  if (throwItem) {
                     wrapper.create(ClientboundPackets1_9.SET_SLOT, (PacketHandler)(new PacketHandler() {
                        public void handle(PacketWrapper wrapper) throws Exception {
                           wrapper.write(Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                           wrapper.write(Type.SHORT, slot);
                           wrapper.write(Type.ITEM, (Object)null);
                        }
                     })).send(Protocol1_9To1_8.class);
                     wrapper.set(Type.SHORT, 0, -999);
                  }

               }
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_9.CLICK_WINDOW, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.BYTE);
            this.map(Type.SHORT);
            this.map(Type.VAR_INT, Type.BYTE);
            this.map(Type.ITEM);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  Item stack = (Item)wrapper.get(Type.ITEM, 0);
                  if (Via.getConfig().isShowShieldWhenSwordInHand()) {
                     Short windowId = (Short)wrapper.get(Type.UNSIGNED_BYTE, 0);
                     byte mode = (Byte)wrapper.get(Type.BYTE, 1);
                     short hoverSlot = (Short)wrapper.get(Type.SHORT, 0);
                     byte button = (Byte)wrapper.get(Type.BYTE, 0);
                     InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                     inventoryTracker.handleWindowClick(wrapper.user(), windowId, mode, hoverSlot, button);
                  }

                  ItemRewriter.toServer(stack);
               }
            });
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  final short windowID = (Short)wrapper.get(Type.UNSIGNED_BYTE, 0);
                  final short slot = (Short)wrapper.get(Type.SHORT, 0);
                  boolean throwItem = slot == 45 && windowID == 0;
                  InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                  if (inventoryTracker.getInventory() != null && inventoryTracker.getInventory().equals("minecraft:brewing_stand")) {
                     if (slot == 4) {
                        throwItem = true;
                     }

                     if (slot > 4) {
                        wrapper.set(Type.SHORT, 0, (short)(slot - 1));
                     }
                  }

                  if (throwItem) {
                     wrapper.create(ClientboundPackets1_9.SET_SLOT, (PacketHandler)(new PacketHandler() {
                        public void handle(PacketWrapper wrapper) throws Exception {
                           wrapper.write(Type.UNSIGNED_BYTE, windowID);
                           wrapper.write(Type.SHORT, slot);
                           wrapper.write(Type.ITEM, (Object)null);
                        }
                     })).scheduleSend(Protocol1_9To1_8.class);
                     wrapper.set(Type.BYTE, 0, (byte)0);
                     wrapper.set(Type.BYTE, 1, (byte)0);
                     wrapper.set(Type.SHORT, 0, -999);
                  }

               }
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_9.CLOSE_WINDOW, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.UNSIGNED_BYTE);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                  inventoryTracker.setInventory((String)null);
                  inventoryTracker.resetInventory((Short)wrapper.get(Type.UNSIGNED_BYTE, 0));
               }
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_9.HELD_ITEM_CHANGE, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.SHORT);
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  boolean showShieldWhenSwordInHand = Via.getConfig().isShowShieldWhenSwordInHand() && Via.getConfig().isShieldBlocking();
                  EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                  if (entityTracker.isBlocking()) {
                     entityTracker.setBlocking(false);
                     if (!showShieldWhenSwordInHand) {
                        entityTracker.setSecondHand((Item)null);
                     }
                  }

                  if (showShieldWhenSwordInHand) {
                     entityTracker.setHeldItemSlot((Short)wrapper.get(Type.SHORT, 0));
                     entityTracker.syncShieldWithSword();
                  }

               }
            });
         }
      });
   }
}
