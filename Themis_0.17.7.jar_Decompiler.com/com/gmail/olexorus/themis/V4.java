package com.gmail.olexorus.themis;

import java.util.List;

public final class v4 {
   private final String a;
   private final double g;
   private final double Z;
   private final double M;
   private final List<String> O;
   private static vh[] R;

   public v4(String var1, double var2, double var4, double var6, List<String> var8) {
      this.a = var1;
      this.g = var2;
      this.Z = var4;
      this.M = var6;
      this.O = var8;
   }

   public final String s(Object[] var1) {
      return this.a;
   }

   public final double d(Object[] var1) {
      return this.g;
   }

   public final double M(Object[] var1) {
      return this.Z;
   }

   public final double S(Object[] var1) {
      return this.M;
   }

   public final List Z(Object[] var1) {
      return this.O;
   }

   public static void o(vh[] var0) {
      R = var0;
   }

   public static vh[] b() {
      return R;
   }

   static {
      if (b() != null) {
         o(new vh[5]);
      }

   }
}
