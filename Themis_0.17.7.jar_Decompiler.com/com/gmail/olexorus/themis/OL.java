package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class oL extends oh {
   private v2 R;
   private float O;
   private static final long a = kt.a(-2003574943748180435L, -9135749880187563197L, MethodHandles.lookup().lookupClass()).a(45495732538646L);

   public oL(v2 var1, float var2) {
      this.R = var1;
      this.O = var2;
   }

   public static oL w(lm<?> var0) {
      if (var0.R().i(zZ.V_1_21_9)) {
         v2 var1 = v2.m(var0);
         float var2 = var0.L();
         return new oL(var1, var2);
      } else {
         return new oL(v2.e, 1.0F);
      }
   }

   public static void k(lm<?> var0, oL var1) {
      if (var0.R().i(zZ.V_1_21_9)) {
         v2.B(var0, var1.R);
         var0.S(var1.O);
      }

   }

   public static oL d(RT var0, vL var1) {
      long var2 = a ^ 98562081912786L;
      v2 var4 = (v2)var0.P("color", v2::u, v2.e, (lm)null);
      float var5 = var0.L("power", 1.0F).floatValue();
      return new oL(var4, var5);
   }

   public static void o(oL var0, vL var1, RT var2) {
      long var3 = a ^ 64225760733138L;
      if (var1.K(vL.V_1_21_9)) {
         if (!v2.e.equals(var0.R)) {
            var2.j("color", v2.j(var0.R, var1));
         }

         if (var0.O != 1.0F) {
            var2.j("power", new m6(var0.O));
         }
      }

   }

   public boolean p() {
      return false;
   }
}
