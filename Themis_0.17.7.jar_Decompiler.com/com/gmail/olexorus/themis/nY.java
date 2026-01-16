package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;

public class NY {
   private static final long a = kt.a(8019086278975446017L, 4692923447301949798L, MethodHandles.lookup().lookupClass()).a(238439037819222L);

   public static int K(Object var0) {
      return oS.J().T().X().M(var0);
   }

   public static Object x(Object var0, int var1) {
      return oS.J().T().X().M(var0, var1);
   }

   public static int p(Object var0) {
      return oS.J().T().X().K(var0);
   }

   public static Object n(Object var0, int var1) {
      return oS.J().T().X().W(var0, var1);
   }

   public static int P(Object var0) {
      return oS.J().T().X().W(var0);
   }

   public static Object E(Object var0, int var1) {
      return oS.J().T().X().U(var0, var1);
   }

   public static int r(Object var0) {
      return oS.J().T().X().z(var0);
   }

   public static Object A(Object var0) {
      return oS.J().T().X().s(var0);
   }

   public static String B(Object var0, int var1, int var2, Charset var3) {
      return oS.J().T().X().L(var0, var1, var2, var3);
   }

   public static byte o(Object var0) {
      return oS.J().T().X().J(var0);
   }

   public static void Z(Object var0, int var1) {
      oS.J().T().X().d(var0, var1);
   }

   public static void p(Object var0, boolean var1) {
      oS.J().T().X().R(var0, var1);
   }

   public static short C(Object var0) {
      return oS.J().T().X().Z(var0);
   }

   public static void U(Object var0, int var1) {
      oS.J().T().X().g(var0, var1);
   }

   public static short O(Object var0) {
      return oS.J().T().X().b(var0);
   }

   public static int Y(Object var0) {
      return oS.J().T().X().o(var0);
   }

   public static void D(Object var0, int var1) {
      oS.J().T().X().Z(var0, var1);
   }

   public static void V(Object var0, int var1) {
      oS.J().T().X().w(var0, var1);
   }

   public static void M(Object var0, int var1) {
      oS.J().T().X().C(var0, var1);
   }

   public static int s(Object var0) {
      return oS.J().T().X().m(var0);
   }

   public static void p(Object var0, int var1) {
      oS.J().T().X().E(var0, var1);
   }

   public static long k(Object var0) {
      return oS.J().T().X().y(var0);
   }

   public static long Q(Object var0) {
      return oS.J().T().X().o(var0);
   }

   public static void U(Object var0, long var1) {
      oS.J().T().X().g(var0, var1);
   }

   public static float W(Object var0) {
      return oS.J().T().X().b(var0);
   }

   public static void Z(Object var0, float var1) {
      oS.J().T().X().q(var0, var1);
   }

   public static double O(Object var0) {
      return oS.J().T().X().A(var0);
   }

   public static void r(Object var0, double var1) {
      oS.J().T().X().T(var0, var1);
   }

   public static short f(Object var0, int var1) {
      return oS.J().T().X().r(var0, var1);
   }

   public static boolean x(Object var0) {
      return oS.J().T().X().t(var0);
   }

   public static Object Q(Object var0) {
      return oS.J().T().X().G(var0);
   }

   public static Object j(Object var0) {
      return oS.J().T().X().B(var0);
   }

   public static Object g(Object var0, int var1) {
      return oS.J().T().X().m(var0, var1);
   }

   public static Object H(Object var0, byte[] var1, int var2, int var3) {
      return oS.J().T().X().y(var0, var1, var2, var3);
   }

   public static Object I(Object var0, Object var1) {
      return oS.J().T().X().j(var0, var1);
   }

   public static void U(Object var0, byte[] var1) {
      oS.J().T().X().e(var0, var1);
   }

   public static void N(Object var0, byte[] var1) {
      oS.J().T().X().H(var0, var1);
   }

   public static void r(Object var0, byte[] var1, int var2, int var3) {
      oS.J().T().X().r(var0, var1, var2, var3);
   }

   public static boolean o(Object var0) {
      return oS.J().T().X().c(var0);
   }

   public static int N(Object var0) {
      return oS.J().T().X().t(var0);
   }

   public static Object c(Object var0, int var1) {
      return oS.J().T().X().i(var0, var1);
   }

   public static Object D(Object var0) {
      return oS.J().T().X().m(var0);
   }

   public static Object N(Object var0) {
      return oS.J().T().X().o(var0);
   }

   public static Object o(Object var0) {
      return oS.J().T().X().l(var0);
   }

   public static int K(int var0) {
      for(int var1 = 1; var1 < 5; ++var1) {
         if ((var0 & -1 << var1 * 7) == 0) {
            return var1;
         }
      }

      return 5;
   }

   public static int a(Object var0) {
      long var1 = a ^ 66200878699508L;
      int var3 = 0;
      int var4 = 0;

      byte var5;
      do {
         var5 = o(var0);
         var3 |= (var5 & 127) << var4 * 7;
         ++var4;
         if (var4 > 5) {
            throw new RuntimeException("VarInt is too large. Must be smaller than 5 bytes.");
         }
      } while((var5 & 128) == 128);

      return var3;
   }
}
