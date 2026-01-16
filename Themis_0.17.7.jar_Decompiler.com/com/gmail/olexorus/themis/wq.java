package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public interface WQ extends GL, uW<WQ>, rp {
   long a = kt.a(-9041997400554615898L, -2714700709865453792L, MethodHandles.lookup().lookupClass()).a(256814177153210L);

   CW h();

   X D();

   float P();

   int g();

   static WQ k(Rc var0, lm<?> var1, z2 var2) {
      long var3 = a ^ 83251788781199L;
      RT var5 = (RT)var0;
      CW var6 = (CW)var5.g("sound_event", CW.x, var1);
      X var7 = (X)var5.g("description", var1.x(), var1);
      float var8 = var5.N("length_in_seconds").b();
      int var9 = var5.N("comparator_output").P();
      return new iq(var2, var6, var7, var8, var9);
   }

   static WQ T(lm<?> var0) {
      return (WQ)var0.i((VD)Nm.n(), (MO)(WQ::Q));
   }

   static WQ Q(lm<?> var0) {
      CW var1 = CW.B(var0);
      X var2 = var0.a();
      float var3 = var0.L();
      int var4 = var0.Q();
      return new iq((z2)null, var1, var2, var3, var4);
   }

   static void D(lm<?> var0, WQ var1) {
      var0.M(var1, WQ::d);
   }

   static void d(lm<?> var0, WQ var1) {
      CW.O(var0, var1.h());
      var0.G(var1.D());
      var0.S(var1.P());
      var0.E(var1.g());
   }
}
