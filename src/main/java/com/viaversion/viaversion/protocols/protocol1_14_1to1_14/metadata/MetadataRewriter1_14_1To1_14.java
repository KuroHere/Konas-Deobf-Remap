package com.viaversion.viaversion.protocols.protocol1_14_1to1_14.metadata;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_14Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.protocols.protocol1_14_1to1_14.Protocol1_14_1To1_14;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.List;

public class MetadataRewriter1_14_1To1_14 extends EntityRewriter {
   public MetadataRewriter1_14_1To1_14(Protocol1_14_1To1_14 protocol) {
      super(protocol);
   }

   public void handleMetadata(int entityId, EntityType type, Metadata metadata, List metadatas, UserConnection connection) {
      if (type != null) {
         if ((type == Entity1_14Types.VILLAGER || type == Entity1_14Types.WANDERING_TRADER) && metadata.method_71() >= 15) {
            metadata.setId(metadata.method_71() + 1);
         }

      }
   }

   public EntityType typeFromId(int type) {
      return Entity1_14Types.getTypeFromId(type);
   }
}
