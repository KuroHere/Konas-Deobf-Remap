package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class ByteType extends Type implements TypeConverter {
   public ByteType() {
      super(Byte.class);
   }

   public Byte read(ByteBuf buffer) {
      return buffer.readByte();
   }

   public void write(ByteBuf buffer, Byte object) {
      buffer.writeByte(object);
   }

   public Byte from(Object o) {
      if (o instanceof Number) {
         return ((Number)o).byteValue();
      } else {
         return o instanceof Boolean ? Byte.valueOf((byte)((Boolean)o ? 1 : 0)) : (Byte)o;
      }
   }
}
