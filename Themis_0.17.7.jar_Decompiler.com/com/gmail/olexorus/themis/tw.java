package com.gmail.olexorus.themis;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator.OfLong;

class tW implements OfLong {
   private int u;
   final B8 W;

   tW(B8 var1) {
      this.W = var1;
   }

   public boolean hasNext() {
      return this.u < this.W.B.length - 1;
   }

   public long nextLong() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.W.B[this.u++];
      }
   }
}
