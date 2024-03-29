package com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.rewriters.EntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.Protocol1_16_4To1_17;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16_2Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_17Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_16;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.type.types.version.Types1_16;
import com.viaversion.viaversion.api.type.types.version.Types1_17;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import java.util.Iterator;

public final class EntityPackets1_17 extends EntityRewriter {
   public EntityPackets1_17(Protocol1_16_4To1_17 protocol) {
      super(protocol);
   }

   protected void registerPackets() {
      this.registerTrackerWithData(ClientboundPackets1_17.SPAWN_ENTITY, Entity1_17Types.FALLING_BLOCK);
      this.registerSpawnTracker(ClientboundPackets1_17.SPAWN_MOB);
      this.registerTracker(ClientboundPackets1_17.SPAWN_EXPERIENCE_ORB, Entity1_17Types.EXPERIENCE_ORB);
      this.registerTracker(ClientboundPackets1_17.SPAWN_PAINTING, Entity1_17Types.PAINTING);
      this.registerTracker(ClientboundPackets1_17.SPAWN_PLAYER, Entity1_17Types.PLAYER);
      this.registerMetadataRewriter(ClientboundPackets1_17.ENTITY_METADATA, Types1_17.METADATA_LIST, Types1_16.METADATA_LIST);
      this.protocol.registerClientbound(ClientboundPackets1_17.REMOVE_ENTITY, ClientboundPackets1_16_2.DESTROY_ENTITIES, new PacketRemapper() {
         public void registerMap() {
            this.handler((wrapper) -> {
               int entityId = (Integer)wrapper.read(Type.VAR_INT);
               EntityPackets1_17.this.tracker(wrapper.user()).removeEntity(entityId);
               int[] array = new int[]{entityId};
               wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, array);
            });
         }
      });
      this.protocol.registerClientbound(ClientboundPackets1_17.JOIN_GAME, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.BYTE);
            this.map(Type.STRING_ARRAY);
            this.map(Type.NBT);
            this.map(Type.NBT);
            this.handler((wrapper) -> {
               byte previousGamemode = (Byte)wrapper.get(Type.BYTE, 0);
               if (previousGamemode == -1) {
                  wrapper.set(Type.BYTE, 0, (byte)0);
               }

            });
            this.handler(EntityPackets1_17.this.getTrackerHandler(Entity1_17Types.PLAYER, Type.INT));
            this.handler(EntityPackets1_17.this.worldDataTrackerHandler(1));
            this.handler((wrapper) -> {
               CompoundTag registry = (CompoundTag)wrapper.get(Type.NBT, 0);
               CompoundTag biomeRegsitry = (CompoundTag)registry.get("minecraft:worldgen/biome");
               ListTag biomes = (ListTag)biomeRegsitry.get("value");
               Iterator var5 = biomes.iterator();

               while(var5.hasNext()) {
                  Tag biome = (Tag)var5.next();
                  CompoundTag biomeCompound = (CompoundTag)((CompoundTag)biome).get("element");
                  StringTag category = (StringTag)biomeCompound.get("category");
                  if (category.getValue().equalsIgnoreCase("underground")) {
                     category.setValue("none");
                  }
               }

               CompoundTag dimensionRegistry = (CompoundTag)registry.get("minecraft:dimension_type");
               ListTag dimensions = (ListTag)dimensionRegistry.get("value");
               Iterator var12 = dimensions.iterator();

               while(var12.hasNext()) {
                  Tag dimension = (Tag)var12.next();
                  CompoundTag dimensionCompound = (CompoundTag)((CompoundTag)dimension).get("element");
                  EntityPackets1_17.this.reduceExtendedHeight(dimensionCompound, false);
               }

               EntityPackets1_17.this.reduceExtendedHeight((CompoundTag)wrapper.get(Type.NBT, 1), true);
            });
         }
      });
      this.protocol.registerClientbound(ClientboundPackets1_17.RESPAWN, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.NBT);
            this.handler(EntityPackets1_17.this.worldDataTrackerHandler(0));
            this.handler((wrapper) -> {
               EntityPackets1_17.this.reduceExtendedHeight((CompoundTag)wrapper.get(Type.NBT, 0), true);
            });
         }
      });
      this.protocol.registerClientbound(ClientboundPackets1_17.PLAYER_POSITION, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               wrapper.read(Type.BOOLEAN);
            });
         }
      });
      this.protocol.registerClientbound(ClientboundPackets1_17.ENTITY_PROPERTIES, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               wrapper.write(Type.INT, wrapper.read(Type.VAR_INT));
            });
         }
      });
      ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.COMBAT_ENTER, ClientboundPackets1_16_2.COMBAT_EVENT, 0);
      ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.COMBAT_END, ClientboundPackets1_16_2.COMBAT_EVENT, 1);
      ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.COMBAT_KILL, ClientboundPackets1_16_2.COMBAT_EVENT, 2);
   }

   protected void registerRewrites() {
      this.filter().handler((event, meta) -> {
         meta.setMetaType(MetaType1_16.byId(meta.metaType().typeId()));
         MetaType type = meta.metaType();
         if (type == MetaType1_16.PARTICLE) {
            Particle particle = (Particle)meta.getValue();
            if (particle.getId() == 15) {
               particle.getArguments().subList(4, 7).clear();
            } else if (particle.getId() == 36) {
               particle.setId(0);
               particle.getArguments().clear();
               return;
            }

            this.rewriteParticle(particle);
         } else if (type == MetaType1_16.POSE) {
            int pose = (Integer)meta.value();
            if (pose == 6) {
               meta.setValue(1);
            } else if (pose > 6) {
               meta.setValue(pose - 1);
            }
         }

      });
      this.registerMetaTypeHandler(MetaType1_16.ITEM, MetaType1_16.BLOCK_STATE, null, MetaType1_16.OPT_COMPONENT);
      this.mapTypes(Entity1_17Types.values(), Entity1_16_2Types.class);
      this.filter().type(Entity1_17Types.AXOLOTL).cancel(17);
      this.filter().type(Entity1_17Types.AXOLOTL).cancel(18);
      this.filter().type(Entity1_17Types.AXOLOTL).cancel(19);
      this.filter().type(Entity1_17Types.GLOW_SQUID).cancel(16);
      this.filter().type(Entity1_17Types.GOAT).cancel(17);
      this.mapEntityTypeWithData(Entity1_17Types.AXOLOTL, Entity1_17Types.TROPICAL_FISH).jsonName("Axolotl");
      this.mapEntityTypeWithData(Entity1_17Types.GOAT, Entity1_17Types.SHEEP).jsonName("Goat");
      this.mapEntityTypeWithData(Entity1_17Types.GLOW_SQUID, Entity1_17Types.SQUID).jsonName("Glow Squid");
      this.mapEntityTypeWithData(Entity1_17Types.GLOW_ITEM_FRAME, Entity1_17Types.ITEM_FRAME);
      this.filter().type(Entity1_17Types.SHULKER).addIndex(17);
      this.filter().removeIndex(7);
   }

   public EntityType typeFromId(int typeId) {
      return Entity1_17Types.getTypeFromId(typeId);
   }

   private void reduceExtendedHeight(CompoundTag tag, boolean warn) {
      IntTag minY = (IntTag)tag.get("min_y");
      IntTag height = (IntTag)tag.get("height");
      IntTag logicalHeight = (IntTag)tag.get("logical_height");
      if (minY.asInt() != 0 || height.asInt() > 256 || logicalHeight.asInt() > 256) {
         if (warn) {
            ViaBackwards.getPlatform().getLogger().severe("Custom worlds heights are NOT SUPPORTED for 1.16 players and older and may lead to errors!");
            ViaBackwards.getPlatform().getLogger().severe("You have min/max set to " + minY.asInt() + "/" + height.asInt());
         }

         height.setValue(Math.min(256, height.asInt()));
         logicalHeight.setValue(Math.min(256, logicalHeight.asInt()));
      }

   }
}
