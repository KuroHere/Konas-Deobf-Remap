package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntTag extends NumberTag {
   // $FF: renamed from: ID int
   public static final int field_51 = 3;
   private int value;

   public IntTag() {
      this(0);
   }

   public IntTag(int value) {
      this.value = value;
   }

   /** @deprecated */
   @Deprecated
   public Integer getValue() {
      return this.value;
   }

   public void setValue(int value) {
      this.value = value;
   }

   public void read(DataInput in) throws IOException {
      this.value = in.readInt();
   }

   public void write(DataOutput out) throws IOException {
      out.writeInt(this.value);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         IntTag intTag = (IntTag)o;
         return this.value == intTag.value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public final IntTag clone() {
      return new IntTag(this.value);
   }

   public byte asByte() {
      return (byte)this.value;
   }

   public short asShort() {
      return (short)this.value;
   }

   public int asInt() {
      return this.value;
   }

   public long asLong() {
      return this.value;
   }

   public float asFloat() {
      return (float)this.value;
   }

   public double asDouble() {
      return this.value;
   }

   public int getTagId() {
      return 3;
   }
}
