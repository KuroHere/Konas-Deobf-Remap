package com.viaversion.viabackwards.protocol.protocol1_9_4to1_10.packets;

import com.viaversion.viabackwards.api.entities.storage.EntityData;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_9_4to1_10.Protocol1_9_4To1_10;
import com.viaversion.viabackwards.utils.Block;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_11Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_12Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.ObjectType;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import java.util.List;
import java.util.Optional;

public class EntityPackets1_10 extends LegacyEntityRewriter {
   public EntityPackets1_10(Protocol1_9_4To1_10 protocol) {
      super(protocol);
   }

   protected void registerPackets() {
      this.protocol.registerClientbound(ClientboundPackets1_9_3.SPAWN_ENTITY, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.BYTE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.INT);
            this.handler(EntityPackets1_10.this.getObjectTrackerHandler());
            this.handler(EntityPackets1_10.this.getObjectRewriter((id) -> {
               return Entity1_11Types.ObjectType.findById(id).orElse(null);
            }));
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  Optional type = Entity1_12Types.ObjectType.findById((Byte)wrapper.get(Type.BYTE, 0));
                  if (type.isPresent() && type.get() == Entity1_12Types.ObjectType.FALLING_BLOCK) {
                     int objectData = (Integer)wrapper.get(Type.INT, 0);
                     int objType = objectData & 4095;
                     int data = objectData >> 12 & 15;
                     Block block = ((Protocol1_9_4To1_10)EntityPackets1_10.this.protocol).getItemRewriter().handleBlock(objType, data);
                     if (block == null) {
                        return;
                     }

                     wrapper.set(Type.INT, 0, block.getId() | block.getData() << 12);
                  }

               }
            });
         }
      });
      this.registerTracker(ClientboundPackets1_9_3.SPAWN_EXPERIENCE_ORB, Entity1_10Types.EntityType.EXPERIENCE_ORB);
      this.registerTracker(ClientboundPackets1_9_3.SPAWN_GLOBAL_ENTITY, Entity1_10Types.EntityType.WEATHER);
      this.protocol.registerClientbound(ClientboundPackets1_9_3.SPAWN_MOB, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.SHORT);
            this.map(Type.SHORT);
            this.map(Type.SHORT);
            this.map(Types1_9.METADATA_LIST);
            this.handler(EntityPackets1_10.this.getTrackerHandler(Type.UNSIGNED_BYTE, 0));
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
                  EntityType type = EntityPackets1_10.this.tracker(wrapper.user()).entityType(entityId);
                  List metadata = (List)wrapper.get(Types1_9.METADATA_LIST, 0);
                  EntityPackets1_10.this.handleMetadata((Integer)wrapper.get(Type.VAR_INT, 0), metadata, wrapper.user());
                  EntityData entityData = EntityPackets1_10.this.entityDataForType(type);
                  if (entityData != null) {
                     WrappedMetadata storage = new WrappedMetadata(metadata);
                     wrapper.set(Type.UNSIGNED_BYTE, 0, (short)entityData.replacementId());
                     if (entityData.hasBaseMeta()) {
                        entityData.defaultMeta().createMeta(storage);
                     }
                  }

               }
            });
         }
      });
      this.registerTracker(ClientboundPackets1_9_3.SPAWN_PAINTING, Entity1_10Types.EntityType.PAINTING);
      this.registerJoinGame(ClientboundPackets1_9_3.JOIN_GAME, Entity1_10Types.EntityType.PLAYER);
      this.registerRespawn(ClientboundPackets1_9_3.RESPAWN);
      this.protocol.registerClientbound(ClientboundPackets1_9_3.SPAWN_PLAYER, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Types1_9.METADATA_LIST);
            this.handler(EntityPackets1_10.this.getTrackerAndMetaHandler(Types1_9.METADATA_LIST, Entity1_11Types.EntityType.PLAYER));
         }
      });
      this.registerRemoveEntities(ClientboundPackets1_9_3.DESTROY_ENTITIES);
      this.registerMetadataRewriter(ClientboundPackets1_9_3.ENTITY_METADATA, Types1_9.METADATA_LIST);
   }

   protected void registerRewrites() {
      this.mapEntityTypeWithData(Entity1_10Types.EntityType.POLAR_BEAR, Entity1_10Types.EntityType.SHEEP).mobName("Polar Bear");
      this.filter().type(Entity1_10Types.EntityType.POLAR_BEAR).index(13).handler((event, meta) -> {
         boolean b = (Boolean)meta.getValue();
         meta.setTypeAndValue(MetaType1_9.Byte, Byte.valueOf((byte)(b ? 14 : 0)));
      });
      this.filter().type(Entity1_10Types.EntityType.ZOMBIE).index(13).handler((event, meta) -> {
         if ((Integer)meta.getValue() == 6) {
            meta.setValue(0);
         }

      });
      this.filter().type(Entity1_10Types.EntityType.SKELETON).index(12).handler((event, meta) -> {
         if ((Integer)meta.getValue() == 2) {
            meta.setValue(0);
         }

      });
      this.filter().removeIndex(5);
   }

   public EntityType typeFromId(int typeId) {
      return Entity1_10Types.getTypeFromId(typeId, false);
   }

   protected EntityType getObjectTypeFromId(int typeId) {
      return Entity1_10Types.getTypeFromId(typeId, true);
   }
}
