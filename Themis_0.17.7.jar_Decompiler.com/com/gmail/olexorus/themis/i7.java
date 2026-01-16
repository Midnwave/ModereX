package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class i7 extends id implements OC {
   private final i_ Q;
   private final Ng<OC> Z;
   private final uk l;
   private final int g;
   private final int T;
   private static final long a = kt.a(-9195118915941752786L, -4259366766204147968L, MethodHandles.lookup().lookupClass()).a(231614381159018L);

   public i7(i_ var1, Ng<OC> var2, uk var3, int var4, int var5) {
      this((z2)null, var1, var2, var3, var4, var5);
   }

   public i7(z2 var1, i_ var2, Ng<OC> var3, uk var4, int var5, int var6) {
      super(var1);
      this.Q = var2;
      this.Z = var3;
      this.l = var4;
      this.g = var5;
      this.T = var6;
   }

   public static i7 j(RT var0, lm<?> var1) {
      long var2 = a ^ 5608500874619L;
      i_ var4 = i_.H(var0, var1);
      Ng var5 = (Ng)var0.g("dialogs", gC::P, var1);
      uk var6 = (uk)var0.M("exit_action", uk::x, var1);
      int var7 = var0.L("columns", 2).intValue();
      int var8 = var0.L("button_width", 150).intValue();
      return new i7((z2)null, var4, var5, var6, var7, var8);
   }

   public static void J(RT var0, lm<?> var1, i7 var2) {
      long var3 = a ^ 134209662240236L;
      i_.A(var0, var1, var2.Q);
      var0.X("dialogs", var2.Z, gC::w, var1);
      if (var2.l != null) {
         var0.X("exit_action", var2.l, uk::m, var1);
      }

      if (var2.g != 2) {
         var0.j("columns", new mz(var2.g));
      }

      if (var2.T != 150) {
         var0.j("button_width", new mz(var2.T));
      }

   }

   public OC y(z2 var1) {
      return new i7(var1, this.Q, this.Z, this.l, this.g, this.T);
   }

   public i_ E() {
      return this.Q;
   }

   public Ng<OC> O() {
      return this.Z;
   }

   public uk e() {
      return this.l;
   }

   public int B() {
      return this.g;
   }

   public int v() {
      return this.T;
   }

   public T<?> M() {
      return ag.F;
   }

   public boolean I(Object var1) {
      if (!(var1 instanceof i7)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         i7 var2 = (i7)var1;
         if (this.g != var2.g) {
            return false;
         } else if (this.T != var2.T) {
            return false;
         } else if (!this.Q.equals(var2.Q)) {
            return false;
         } else {
            return !this.Z.equals(var2.Z) ? false : Objects.equals(this.l, var2.l);
         }
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.Q, this.Z, this.l, this.g, this.T});
   }
}
