package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class Jt implements An {
   private final TU s;
   private final mY l;
   private final boolean U;
   private final boolean g;
   private final int o;
   private final int p;
   private static final long b = kt.a(6186757560940411599L, 8643984922318603406L, MethodHandles.lookup().lookupClass()).a(125373932704282L);

   public Jt(TU var1, mY var2, boolean var3, boolean var4, int var5, int var6) {
      this.s = var1;
      this.l = var2;
      this.U = var3;
      this.g = var4;
      this.o = var5;
      this.p = var6;
   }

   public static Jt P(RT var0, lm<?> var1) {
      long var2 = b ^ 42673861431644L;
      TU var4 = (TU)var0.g("item", TU::Z, var1);
      mY var5 = (mY)var0.M("description", mY::V, var1);
      boolean var6 = var0.v("show_decorations", true);
      boolean var7 = var0.v("show_tooltip", true);
      int var8 = var0.L("width", 16).intValue();
      int var9 = var0.L("height", 16).intValue();
      return new Jt(var4, var5, var6, var7, var8, var9);
   }

   public static void S(RT var0, lm<?> var1, Jt var2) {
      long var3 = b ^ 32114035305411L;
      var0.X("item", var2.s, TU::J, var1);
      if (var2.l != null) {
         var0.X("description", var2.l, mY::e, var1);
      }

      if (!var2.U) {
         var0.j("show_decorations", new mA(false));
      }

      if (!var2.g) {
         var0.j("show_tooltip", new mA(false));
      }

      if (var2.o != 16) {
         var0.j("width", new mz(var2.o));
      }

      if (var2.p != 16) {
         var0.j("height", new mz(var2.p));
      }

   }

   public rs<?> X() {
      return CG.a;
   }
}
