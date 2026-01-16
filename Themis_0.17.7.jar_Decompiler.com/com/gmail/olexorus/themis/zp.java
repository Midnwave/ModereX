package com.gmail.olexorus.themis;

import java.util.Iterator;

final class zP implements EL<GK> {
   private final CharSequence M;
   private final int U;
   private final int x;
   private final Vt<CharSequence, Integer, z0<Integer, Integer>> s;

   public zP(CharSequence var1, int var2, int var3, Vt<? super CharSequence, ? super Integer, z0<Integer, Integer>> var4) {
      this.M = var1;
      this.U = var2;
      this.x = var3;
      this.s = var4;
   }

   public Iterator<GK> H() {
      return (Iterator)(new oR(this));
   }

   public static final int D(zP var0) {
      return var0.U;
   }

   public static final CharSequence Y(zP var0) {
      return var0.M;
   }

   public static final int Z(zP var0) {
      return var0.x;
   }

   public static final Vt p(zP var0) {
      return var0.s;
   }
}
