package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_14;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;

public abstract class EntityRewriter extends EntityRewriterBase {
   protected EntityRewriter(BackwardsProtocol protocol) {
      this(protocol, MetaType1_14.OptChat, MetaType1_14.Boolean);
   }

   protected EntityRewriter(BackwardsProtocol protocol, MetaType displayType, MetaType displayVisibilityType) {
      super(protocol, displayType, 2, displayVisibilityType, 3);
   }

   public void registerTrackerWithData(ClientboundPacketType packetType, final EntityType fallingBlockType) {
      this.protocol.registerClientbound(packetType, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.VAR_INT);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.INT);
            this.handler(EntityRewriter.this.getSpawnTrackerWithDataHandler(fallingBlockType));
         }
      });
   }

   public PacketHandler getSpawnTrackerWithDataHandler(EntityType fallingBlockType) {
      return (wrapper) -> {
         EntityType entityType = this.setOldEntityId(wrapper);
         if (entityType == fallingBlockType) {
            int blockState = (Integer)wrapper.get(Type.INT, 0);
            wrapper.set(Type.INT, 0, ((BackwardsProtocol)this.protocol).getMappingData().getNewBlockStateId(blockState));
         }

      };
   }

   public void registerSpawnTracker(ClientboundPacketType packetType) {
      this.protocol.registerClientbound(packetType, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               EntityRewriter.this.setOldEntityId(wrapper);
            });
         }
      });
   }

   private EntityType setOldEntityId(PacketWrapper wrapper) throws Exception {
      int typeId = (Integer)wrapper.get(Type.VAR_INT, 1);
      EntityType entityType = this.typeFromId(typeId);
      this.tracker(wrapper.user()).addEntity((Integer)wrapper.get(Type.VAR_INT, 0), entityType);
      int mappedTypeId = this.newEntityId(entityType.getId());
      if (typeId != mappedTypeId) {
         wrapper.set(Type.VAR_INT, 1, mappedTypeId);
      }

      return entityType;
   }
}
