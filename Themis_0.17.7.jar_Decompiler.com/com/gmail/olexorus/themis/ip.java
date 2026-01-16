package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class iP extends id implements OC {
   private final i_ I;
   private final uk q;
   private final uk n;
   private static final long a = kt.a(-4000530581455931029L, 580145295968679012L, MethodHandles.lookup().lookupClass()).a(79154350456449L);

   public iP(i_ var1, uk var2, uk var3) {
      this((z2)null, var1, var2, var3);
   }

   public iP(z2 var1, i_ var2, uk var3, uk var4) {
      super(var1);
      this.I = var2;
      this.q = var3;
      this.n = var4;
   }

   public static iP q(RT var0, lm<?> var1) {
      long var2 = a ^ 128262892052597L;
      i_ var4 = i_.H(var0, var1);
      uk var5 = (uk)var0.g("yes", uk::x, var1);
      uk var6 = (uk)var0.g("no", uk::x, var1);
      return new iP((z2)null, var4, var5, var6);
   }

   public static void G(RT var0, lm<?> var1, iP var2) {
      long var3 = a ^ 101308342381143L;
      i_.A(var0, var1, var2.I);
      var0.X("yes", var2.q, uk::m, var1);
      var0.X("no", var2.n, uk::m, var1);
   }

   public OC h(z2 var1) {
      return new iP(var1, this.I, this.q, this.n);
   }

   public i_ X() {
      return this.I;
   }

   public uk M() {
      return this.q;
   }

   public uk y() {
      return this.n;
   }

   public T<?> M() {
      return ag.d;
   }

   public boolean I(Object var1) {
      if (!(var1 instanceof iP)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         iP var2 = (iP)var1;
         if (!this.I.equals(var2.I)) {
            return false;
         } else {
            return !this.q.equals(var2.q) ? false : this.n.equals(var2.n);
         }
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.I, this.q, this.n});
   }
}
