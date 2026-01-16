package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class oR implements Iterator<GK>, lK {
   private int j;
   private int Q;
   private int U;
   private GK l;
   private int w;
   final zP t;
   private static final long a = kt.a(2335670592170820075L, -4476033599681927766L, MethodHandles.lookup().lookupClass()).a(263339942178196L);

   oR(zP var1) {
      this.t = var1;
      this.j = -1;
      this.Q = W3.S(zP.D(var1), 0, zP.Y(var1).length());
      this.U = this.Q;
   }

   private final void h() {
      if (this.U < 0) {
         this.j = 0;
         this.l = null;
      } else {
         label27: {
            label26: {
               if (zP.Z(this.t) > 0) {
                  ++this.w;
                  if (this.w >= zP.Z(this.t)) {
                     break label26;
                  }
               }

               if (this.U <= zP.Y(this.t).length()) {
                  z0 var1 = (z0)zP.p(this.t).d(zP.Y(this.t), this.U);
                  if (var1 == null) {
                     this.l = new GK(this.Q, OR.g(zP.Y(this.t)));
                     this.U = -1;
                  } else {
                     int var2 = ((Number)var1.A()).intValue();
                     int var3 = ((Number)var1.k()).intValue();
                     this.l = W3.d(this.Q, var2);
                     this.Q = var2 + var3;
                     this.U = this.Q + (var3 == 0 ? 1 : 0);
                  }
                  break label27;
               }
            }

            this.l = new GK(this.Q, OR.g(zP.Y(this.t)));
            this.U = -1;
         }

         this.j = 1;
      }

   }

   public GK M() {
      if (this.j == -1) {
         this.h();
      }

      if (this.j == 0) {
         throw new NoSuchElementException();
      } else {
         GK var1 = this.l;
         this.l = null;
         this.j = -1;
         return var1;
      }
   }

   public boolean hasNext() {
      if (this.j == -1) {
         this.h();
      }

      return this.j == 1;
   }

   public void remove() {
      long var1 = a ^ 120213413320537L;
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
