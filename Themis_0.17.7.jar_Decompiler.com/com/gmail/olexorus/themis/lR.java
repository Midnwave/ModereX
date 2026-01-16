package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public interface lr extends GL, uW<lr>, rp {
   long a = kt.a(-3164918723185135125L, 1012587628091086498L, MethodHandles.lookup().lookupClass()).a(268001268903351L);

   int o();

   int i();

   al j();

   static lr d(lm<?> var0) {
      return var0.R().i(zZ.V_1_21) ? (lr)var0.i((VD)R_.G(), (MO)(lr::R)) : (lr)var0.y((VD)R_.G());
   }

   static void H(lm<?> var0, lr var1) {
      if (var0.R().i(zZ.V_1_21)) {
         var0.M(var1, lr::p);
      } else {
         var0.j((GL)var1);
      }

   }

   static lr R(lm<?> var0) {
      int var1 = var0.Q();
      int var2 = var0.Q();
      al var3 = var0.R();
      return new c4(var1, var2, var3);
   }

   static void p(lm<?> var0, lr var1) {
      var0.E(var1.o());
      var0.E(var1.i());
      var0.T(var1.j());
   }

   static lr z(Rc var0, vL var1, z2 var2) {
      long var3 = a ^ 135642308641040L;
      RT var5 = (RT)var0;
      int var6 = var5.N("width").P();
      int var7 = var5.N("height").P();
      al var8 = new al(var5.N("asset_id"));
      return new c4(var2, var6, var7, var8);
   }
}
