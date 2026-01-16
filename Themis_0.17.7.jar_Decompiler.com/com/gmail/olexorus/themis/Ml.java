package com.gmail.olexorus.themis;

import java.nio.charset.Charset;

public interface mL {
   int M(Object var1);

   Object M(Object var1, int var2);

   int K(Object var1);

   Object W(Object var1, int var2);

   int W(Object var1);

   Object U(Object var1, int var2);

   int z(Object var1);

   Object s(Object var1);

   byte J(Object var1);

   short b(Object var1);

   int m(Object var1);

   long y(Object var1);

   long o(Object var1);

   void d(Object var1, int var2);

   void Z(Object var1, int var2);

   void w(Object var1, int var2);

   void C(Object var1, int var2);

   void E(Object var1, int var2);

   void g(Object var1, long var2);

   short r(Object var1, int var2);

   boolean t(Object var1);

   Object G(Object var1);

   Object B(Object var1);

   Object m(Object var1, int var2);

   Object y(Object var1, byte[] var2, int var3, int var4);

   void e(Object var1, byte[] var2);

   Object j(Object var1, Object var2);

   Object H(Object var1, byte[] var2);

   Object r(Object var1, byte[] var2, int var3, int var4);

   boolean c(Object var1);

   int t(Object var1);

   Object i(Object var1, int var2);

   String L(Object var1, int var2, int var3, Charset var4);

   Object m(Object var1);

   Object o(Object var1);

   Object l(Object var1);

   default float b(Object var1) {
      return Float.intBitsToFloat(this.m(var1));
   }

   default void q(Object var1, float var2) {
      this.E(var1, Float.floatToIntBits(var2));
   }

   default double A(Object var1) {
      return Double.longBitsToDouble(this.o(var1));
   }

   default void T(Object var1, double var2) {
      this.g(var1, Double.doubleToLongBits(var2));
   }

   default void g(Object var1, int var2) {
      this.Z(var1, var2);
   }

   default int o(Object var1) {
      return this.b(var1) & '\uffff';
   }

   default short Z(Object var1) {
      return (short)(this.J(var1) & 255);
   }

   default void R(Object var1, boolean var2) {
      this.d(var1, var2 ? 1 : 0);
   }
}
