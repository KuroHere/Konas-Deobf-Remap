package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.Set;

public abstract class AbstractIntSet extends AbstractIntCollection implements Cloneable, IntSet {
   protected AbstractIntSet() {
   }

   public abstract IntIterator iterator();

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Set)) {
         return false;
      } else {
         Set s = (Set)o;
         return s.size() == this.size() && this.containsAll(s);
      }
   }

   public int hashCode() {
      int h = 0;
      int n = this.size();

      int k;
      for(IntIterator i = this.iterator(); n-- != 0; h += k) {
         k = i.nextInt();
      }

      return h;
   }

   public boolean remove(int k) {
      return super.rem(k);
   }

   /** @deprecated */
   @Deprecated
   public boolean rem(int k) {
      return this.remove(k);
   }
}
