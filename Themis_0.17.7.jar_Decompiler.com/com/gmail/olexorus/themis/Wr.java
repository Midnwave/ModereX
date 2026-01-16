package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.function.Consumer;

public interface WR extends ck<WR, Nr>, nc, Me, Eu<WR> {
   v1 d;

   static WR O() {
      return nn.T;
   }

   static Nr s() {
      return new R();
   }

   static WR h(Consumer<Nr> var0) {
      return (WR)vr.L(s(), var0);
   }

   static WR c(BX var0) {
      return O().b(var0);
   }

   static WR x(BX var0, ux... var1) {
      Nr var2 = s();
      var2.h(var0);
      var2.f(var1);
      return var2.C();
   }

   default WR Q(Consumer<Nr> var1) {
      return this.x(var1, ba.ALWAYS);
   }

   default WR x(Consumer<Nr> var1, ba var2) {
      return h(this::lambda$edit$0);
   }

   v1 U();

   BX z();

   WR b(BX var1);

   NW m(ux var1);

   default WR h(ux var1) {
      return (WR)Eu.super.R(var1);
   }

   default WR F(ux var1, boolean var2) {
      return (WR)Eu.super.N(var1, var2);
   }

   WR T(ux var1, NW var2);

   Eb Q();

   mk<?> U();

   WR I(AT<?> var1);

   String L();

   default WR N(WR var1, ba var2) {
      return this.V(var1, var2, mb.Y());
   }

   WR V(WR var1, ba var2, Set<mb> var3);

   boolean A();

   Nr y();

   private default void lambda$edit$0(ba var1, Consumer var2, Nr var3) {
      if (var1 == ba.ALWAYS) {
         var3.F(this, var1);
      }

      var2.accept(var3);
      if (var1 == ba.IF_ABSENT_ON_TARGET) {
         var3.F(this, var1);
      }

   }

   static {
      long var0 = kt.a(1997137674878256938L, 8023854977890986255L, MethodHandles.lookup().lookupClass()).a(152576612270571L) ^ 136699538865280L;
      d = v1.t("default");
   }
}
