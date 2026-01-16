package com.gmail.olexorus.themis;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterators;

final class uF extends AbstractCollection<NW> {
   final c8 F;

   uF(c8 var1) {
      this.F = var1;
   }

   public Iterator<NW> iterator() {
      return Spliterators.iterator(Arrays.spliterator((NW[])this.toArray(c8.U())));
   }

   public boolean isEmpty() {
      return false;
   }

   public Object[] toArray() {
      Object[] var1 = new Object[c8.z()];

      for(int var2 = 0; var2 < c8.z(); ++var2) {
         var1[var2] = this.F.Y(c8.H[var2]);
      }

      return var1;
   }

   public <T> T[] toArray(T[] var1) {
      if (var1.length < c8.z()) {
         return Arrays.copyOf(this.toArray(), c8.z(), var1.getClass());
      } else {
         System.arraycopy(this.toArray(), 0, var1, 0, c8.z());
         if (var1.length > c8.z()) {
            var1[c8.z()] = null;
         }

         return var1;
      }
   }

   public boolean contains(Object var1) {
      return var1 instanceof NW && super.contains(var1);
   }

   public int size() {
      return c8.z();
   }
}
