package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class oz extends oh {
   private float B;
   private v2 s;
   private v2 Y;
   private static final long a = kt.a(-850359716241712863L, -451405188174909426L, MethodHandles.lookup().lookupClass()).a(226097762450450L);

   public oz(float var1, v2 var2, v2 var3) {
      this.B = var1;
      this.s = var2;
      this.Y = var3;
   }

   public static oz K(lm<?> var0) {
      v2 var1;
      float var2;
      float var4;
      if (var0.R().i(zZ.V_1_21_2)) {
         var1 = new v2(var0.f());
      } else {
         var2 = var0.L();
         float var3 = var0.L();
         var4 = var0.L();
         var1 = new v2(var2, var3, var4);
      }

      var2 = 0.0F;
      if (var0.R().m(zZ.V_1_20_5)) {
         var2 = var0.L();
      }

      v2 var7;
      if (var0.R().i(zZ.V_1_21_2)) {
         var7 = new v2(var0.f());
      } else {
         var4 = var0.L();
         float var5 = var0.L();
         float var6 = var0.L();
         var7 = new v2(var4, var5, var6);
      }

      if (var0.R().i(zZ.V_1_20_5)) {
         var2 = var0.L();
      }

      return new oz(var2, var1, var7);
   }

   public static void I(lm<?> var0, oz var1) {
      if (var0.R().i(zZ.V_1_21_2)) {
         var0.L(var1.k().V());
      } else {
         var0.S(var1.A());
         var0.S(var1.k());
         var0.S(var1.Z());
      }

      if (var0.R().m(zZ.V_1_20_5)) {
         var0.S(var1.e());
      }

      if (var0.R().i(zZ.V_1_21_2)) {
         var0.L(var1.u().V());
      } else {
         var0.S(var1.T());
         var0.S(var1.s());
         var0.S(var1.K());
      }

      if (var0.R().i(zZ.V_1_20_5)) {
         var0.S(var1.e());
      }

   }

   public static oz M(RT var0, vL var1) {
      long var2 = a ^ 105689580884560L;
      String var4 = "from_color";
      String var5 = "to_color";
      if (var1.X(vL.V_1_20_5)) {
         var4 = "fromColor";
         var5 = "toColor";
      }

      v2 var6 = v2.o(var0.W(var4), var1);
      v2 var7 = v2.o(var0.W(var5), var1);
      float var8 = var0.N("scale").b();
      return new oz(var8, var6, var7);
   }

   public static void G(oz var0, vL var1, RT var2) {
      long var3 = a ^ 2194272990263L;
      String var5 = "from_color";
      String var6 = "to_color";
      if (var1.X(vL.V_1_20_5)) {
         var5 = "fromColor";
         var6 = "toColor";
      }

      var2.j(var5, v2.j(var0.s, var1));
      var2.j(var6, v2.j(var0.Y, var1));
      var2.j("scale", new m6(var0.B));
   }

   public boolean p() {
      return false;
   }

   public float A() {
      return (float)this.s.k() / 255.0F;
   }

   public float k() {
      return (float)this.s.v() / 255.0F;
   }

   public float Z() {
      return (float)this.s.D() / 255.0F;
   }

   public float T() {
      return (float)this.Y.k() / 255.0F;
   }

   public float s() {
      return (float)this.Y.v() / 255.0F;
   }

   public float K() {
      return (float)this.Y.D() / 255.0F;
   }

   public float e() {
      return this.B;
   }

   public v2 k() {
      return this.s;
   }

   public v2 u() {
      return this.Y;
   }
}
