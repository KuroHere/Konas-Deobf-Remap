package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends NumberTag {
   // $FF: renamed from: ID int
   public static final int field_48 = 4;
   private long value;

   public LongTag() {
      this(0L);
   }

   public LongTag(long value) {
      this.value = value;
   }

   /** @deprecated */
   @Deprecated
   public Long getValue() {
      return this.value;
   }

   public void setValue(long value) {
      this.value = value;
   }

   public void read(DataInput in) throws IOException {
      this.value = in.readLong();
   }

   public void write(DataOutput out) throws IOException {
      out.writeLong(this.value);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         LongTag longTag = (LongTag)o;
         return this.value == longTag.value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Long.hashCode(this.value);
   }

   public final LongTag clone() {
      return new LongTag(this.value);
   }

   public byte asByte() {
      return (byte)((int)this.value);
   }

   public short asShort() {
      return (short)((int)this.value);
   }

   public int asInt() {
      return (int)this.value;
   }

   public long asLong() {
      return this.value;
   }

   public float asFloat() {
      return (float)this.value;
   }

   public double asDouble() {
      return (double)this.value;
   }

   public int getTagId() {
      return 4;
   }
}
