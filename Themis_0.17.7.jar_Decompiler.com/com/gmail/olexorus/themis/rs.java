package com.gmail.olexorus.themis;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

class RS<K, V> implements Comparable<RS<K, V>> {
   final AtomicLong U;
   final AtomicLong s;
   final AtomicReference<ml> F;
   final K D;
   volatile Future<?> d;
   V T;
   volatile boolean r;

   RS(K var1, V var2, AtomicReference<ml> var3, AtomicLong var4) {
      this.D = var1;
      this.T = var2;
      this.F = var3;
      this.U = var4;
      this.s = new AtomicLong();
      this.O();
   }

   public int Z(RS<K, V> var1) {
      if (this.D.equals(var1.D)) {
         return 0;
      } else {
         return this.s.get() < var1.s.get() ? -1 : 1;
      }
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.D == null ? 0 : this.D.hashCode());
      var3 = 31 * var3 + (this.T == null ? 0 : this.T.hashCode());
      return var3;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         RS var2 = (RS)var1;
         if (!this.D.equals(var2.D)) {
            return false;
         } else {
            if (this.T == null) {
               if (var2.T != null) {
                  return false;
               }
            } else if (!this.T.equals(var2.T)) {
               return false;
            }

            return true;
         }
      }
   }

   public String toString() {
      return this.T.toString();
   }

   synchronized boolean E() {
      boolean var1 = this.r;
      if (this.d != null) {
         this.d.cancel(false);
      }

      this.d = null;
      this.r = false;
      return var1;
   }

   synchronized V Q() {
      return this.T;
   }

   void O() {
      this.s.set(this.U.get() + System.nanoTime());
   }

   synchronized void q(Future<?> var1) {
      this.d = var1;
      this.r = true;
   }

   synchronized void m(V var1) {
      this.T = var1;
   }
}
