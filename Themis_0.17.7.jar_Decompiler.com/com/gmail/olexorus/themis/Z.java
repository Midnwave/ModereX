package com.gmail.olexorus.themis;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

final class z implements Iterator<X> {
   private X Z;
   private final i6 g;
   private final Set<gG> f;
   private final Deque<X> R;

   z(X var1, i6 var2, Set<gG> var3) {
      this.Z = var1;
      this.g = var2;
      this.f = var3;
      this.R = new ArrayDeque();
   }

   public boolean hasNext() {
      return this.Z != null || !this.R.isEmpty();
   }

   public X N() {
      if (this.Z != null) {
         X var1 = this.Z;
         this.Z = null;
         this.g.O(var1, this.R, this.f);
         return var1;
      } else if (this.R.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         this.Z = (X)this.R.poll();
         return this.N();
      }
   }
}
