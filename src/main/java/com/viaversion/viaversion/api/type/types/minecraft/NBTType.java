package com.viaversion.viaversion.api.type.types.minecraft;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.DataInput;
import java.io.DataOutput;

public class NBTType extends Type {
   public NBTType() {
      super(CompoundTag.class);
   }

   public CompoundTag read(ByteBuf buffer) throws Exception {
      Preconditions.checkArgument(buffer.readableBytes() <= 2097152, "Cannot read NBT (got %s bytes)", new Object[]{buffer.readableBytes()});
      int readerIndex = buffer.readerIndex();
      byte b = buffer.readByte();
      if (b == 0) {
         return null;
      } else {
         buffer.readerIndex(readerIndex);
         return NBTIO.readTag((DataInput)(new ByteBufInputStream(buffer)));
      }
   }

   public void write(ByteBuf buffer, CompoundTag object) throws Exception {
      if (object == null) {
         buffer.writeByte(0);
      } else {
         ByteBufOutputStream bytebufStream = new ByteBufOutputStream(buffer);
         NBTIO.writeTag((DataOutput)bytebufStream, object);
      }

   }
}
