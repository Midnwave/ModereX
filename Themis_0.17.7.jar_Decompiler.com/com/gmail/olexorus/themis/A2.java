package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public interface a2 extends GL, uW<a2>, rp {
   long a = kt.a(4041769742217806789L, -6234784869438779860L, MethodHandles.lookup().lookupClass()).a(128957214039485L);

   CW I();

   float g();

   default int m() {
      return a8.a(this.g() * 20.0F);
   }

   float m();

   X B();

   static a2 R(lm<?> var0) {
      return (a2)var0.i((VD)VX.W(), (MO)(a2::I));
   }

   static a2 I(lm<?> var0) {
      CW var1 = CW.B(var0);
      float var2 = var0.R().i(zZ.V_1_21_2) ? var0.L() : (float)var0.Q() * 20.0F;
      float var3 = var0.L();
      Object var4 = var0.R().i(zZ.V_1_21_2) ? var0.a() : X.f();
      return new ic(var1, var2, var3, (X)var4);
   }

   static void Y(lm<?> var0, a2 var1) {
      var0.M(var1, a2::u);
   }

   static void u(lm<?> var0, a2 var1) {
      CW.O(var0, var1.I());
      if (var0.R().i(zZ.V_1_21_2)) {
         var0.S(var1.g());
      } else {
         var0.E(var1.m());
      }

      var0.S(var1.m());
      if (var0.R().i(zZ.V_1_21_2)) {
         var0.G(var1.B());
      }

   }

   static a2 h(Rc var0, lm<?> var1, z2 var2) {
      long var3 = a ^ 77390228087338L;
      RT var5 = (RT)var0;
      CW var6 = (CW)var5.g("sound_event", CW.x, var1);
      float var7 = var5.N("use_duration").b();
      float var8 = var5.N("range").b();
      X var9 = (X)var5.g("description", var1.x(), var1);
      return new ic(var2, var6, var7, var8, var9);
   }
}
