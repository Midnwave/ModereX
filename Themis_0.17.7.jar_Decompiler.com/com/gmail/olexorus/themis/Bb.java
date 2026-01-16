package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public interface BB extends GL, uW<BB>, rp {
   long b = kt.a(1894523816459573965L, 8128671388983086959L, MethodHandles.lookup().lookupClass()).a(209635395005973L);

   al U();

   i M();

   X n();

   boolean T();

   static BB n(lm<?> var0) {
      return (BB)var0.i((VD)vf.z(), (MO)(BB::t));
   }

   static BB t(lm<?> var0) {
      al var1 = var0.R();
      i var2 = var0.R().i(zZ.V_1_21_5) ? null : (i)var0.e(z1::Z);
      X var3 = var0.a();
      boolean var4 = var0.P();
      return new cW(var1, var2, var3, var4);
   }

   static void v(lm<?> var0, BB var1) {
      var0.M(var1, BB::U);
   }

   static void U(lm<?> var0, BB var1) {
      var0.T(var1.U());
      if (var0.R().m(zZ.V_1_21_5)) {
         var0.j((GL)var1.M());
      }

      var0.G(var1.n());
      var0.I(var1.T());
   }

   static BB N(Rc var0, lm<?> var1, z2 var2) {
      long var3 = b ^ 114906887477930L;
      RT var5 = (RT)var0;
      al var6 = new al(var5.N("asset_id"));
      i var7 = var1.R().i(zZ.V_1_21_5) ? null : z1.x(var5.N("template_item"));
      X var8 = (X)var5.g("description", var1.x(), var1);
      boolean var9 = var1.R().i(zZ.V_1_20_2) && var5.w("decal");
      return new cW(var2, var6, var7, var8, var9);
   }
}
