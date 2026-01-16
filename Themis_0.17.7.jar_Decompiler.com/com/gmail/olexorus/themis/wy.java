package com.gmail.olexorus.themis;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterators;

final class wY extends AbstractSet<ux> {
   public boolean contains(Object var1) {
      return var1 instanceof ux;
   }

   public boolean isEmpty() {
      return false;
   }

   public Object[] toArray() {
      return Arrays.copyOf(c8.H, c8.z(), Object[].class);
   }

   public <T> T[] toArray(T[] var1) {
      if (var1.length < c8.z()) {
         return Arrays.copyOf(c8.H, c8.z(), var1.getClass());
      } else {
         System.arraycopy(c8.H, 0, var1, 0, c8.z());
         if (var1.length > c8.z()) {
            var1[c8.z()] = null;
         }

         return var1;
      }
   }

   public Iterator<ux> iterator() {
      return Spliterators.iterator(Arrays.spliterator(c8.H));
   }

   public int size() {
      return c8.z();
   }
}
