package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.lang.ref.WeakReference;

public final class un<T extends GL> implements c9<T> {
   private final WeakReference<m2> Q;
   private final vL A;
   private final VD<T> u;
   private final al t;
   private volatile T d;
   private static final long b = kt.a(-1476364386988423388L, -3927101123661875510L, MethodHandles.lookup().lookupClass()).a(127514406534982L);

   public un(lm<?> var1, VD<T> var2, al var3) {
      this(var1.T(), var1.R().u(), var2, var3);
   }

   public un(m2 var1, vL var2, VD<T> var3, al var4) {
      this.Q = new WeakReference(var1);
      this.A = var2;
      this.u = var3;
      this.t = var4;
   }

   public T E() {
      long var1 = b ^ 115710579230973L;
      GL var3 = this.d;
      if (var3 == null) {
         synchronized(this) {
            var3 = this.d;
            if (var3 == null) {
               m2 var5 = (m2)this.Q.get();
               if (var5 == null) {
                  throw new IllegalStateException("Registry holder for " + this.u + "/" + this.A + "/" + this.t + "has disappeared");
               }

               VD var6 = var5.W(this.u, this.A);
               var3 = var6.y(this.A, this.t);
               this.d = var3;
            }
         }
      }

      return var3;
   }

   static al z(un var0) {
      return var0.t;
   }
}
