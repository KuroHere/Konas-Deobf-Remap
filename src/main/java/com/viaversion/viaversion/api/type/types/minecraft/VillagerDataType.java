package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.VillagerData;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class VillagerDataType extends Type {
   public VillagerDataType() {
      super(VillagerData.class);
   }

   public VillagerData read(ByteBuf buffer) throws Exception {
      return new VillagerData(Type.VAR_INT.readPrimitive(buffer), Type.VAR_INT.readPrimitive(buffer), Type.VAR_INT.readPrimitive(buffer));
   }

   public void write(ByteBuf buffer, VillagerData object) throws Exception {
      Type.VAR_INT.writePrimitive(buffer, object.getType());
      Type.VAR_INT.writePrimitive(buffer, object.getProfession());
      Type.VAR_INT.writePrimitive(buffer, object.getLevel());
   }
}
