package com.gmail.olexorus.themis;

import java.time.Instant;
import java.util.UUID;

public class C8 implements RH {
   public wA c(lm<?> var1) {
      int var2 = var1.Q();
      UUID var3 = var1.V();
      int var4 = var1.Q();
      byte[] var5 = (byte[])var1.u(C8::lambda$readChatMessage$0);
      String var6 = var1.m(256);
      Instant var7 = var1.V();
      long var8 = var1.k();
      oX var10 = var1.E();
      X var11 = (X)var1.u(lm::a);
      Tx var12 = var1.q();
      r0 var13 = var1.f();
      return new wZ(var2, var3, var4, var5, var6, var7, var8, var10, var11, var12, var13);
   }

   public void V(lm<?> var1, wA var2) {
      wZ var3 = (wZ)var2;
      var1.E(var3.F());
      var1.y(var3.Q());
      var1.E(var3.X());
      var1.l(var3.k(), lm::d);
      var1.I(var3.H());
      var1.e(var3.X());
      var1.A(var3.j());
      var1.M(var3.s());
      var1.l((X)var3.f().orElse((Object)null), lm::G);
      var1.L(var3.O());
      var1.B(var3.T());
   }

   private static byte[] lambda$readChatMessage$0(lm var0) {
      return var0.E(256);
   }
}
