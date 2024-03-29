package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;

public abstract class MetaTypeTemplate extends Type {
   protected MetaTypeTemplate() {
      super("Metadata type", Metadata.class);
   }

   public Class getBaseClass() {
      return MetaTypeTemplate.class;
   }
}
