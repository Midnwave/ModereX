package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public interface OC extends GL, rp, uW<OC>, u {
   long b = kt.a(9053233102868633516L, 4247072252114816462L, MethodHandles.lookup().lookupClass()).a(48352531587307L);

   static OC p(lm<?> var0) {
      return (OC)var0.i((VD)b3.d(), (MO)(OC::v));
   }

   static void y(lm<?> var0, OC var1) {
      var0.M(var1, OC::X);
   }

   static OC v(lm<?> var0) {
      return g(var0.v(), var0, (z2)null);
   }

   static void X(lm<?> var0, OC var1) {
      var0.b(P(var1, var0));
   }

   static OC p(Rc var0, lm<?> var1) {
      return var0 instanceof mZ ? (OC)var1.w((VD)b3.d()).N(((mZ)var0).b()) : g(var0, var1, (z2)null);
   }

   static Rc l(lm<?> var0, OC var1) {
      return (Rc)(var1.z() ? new mZ(var1.f().toString()) : P(var1, var0));
   }

   static OC g(Rc var0, lm<?> var1, z2 var2) {
      long var3 = b ^ 98403322198130L;
      RT var5 = (RT)var0;
      String var6 = var5.N("type");
      T var7 = (T)ag.M().N(var6);
      return (OC)var7.H(var5, var1).F(var2);
   }

   static Rc P(OC var0, lm<?> var1) {
      long var2 = b ^ 4244088802911L;
      RT var4 = new RT();
      var4.j("type", new mZ(var0.M().f().toString()));
      var0.M().E(var4, var1, var0);
      return var4;
   }

   T<?> M();
}
