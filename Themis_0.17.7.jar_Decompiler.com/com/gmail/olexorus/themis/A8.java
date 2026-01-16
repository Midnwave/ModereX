package com.gmail.olexorus.themis;

public final class a8 {
   public static float p(float var0) {
      return var0 * var0;
   }

   public static float B(float var0) {
      return var0 * var0 * var0;
   }

   public static float K(float var0, float var1, float var2) {
      return var0 * (var2 - var1) + var1;
   }

   public static int s(float var0, int var1, int var2) {
      return a(var0 * (float)(var2 - var1)) + var1;
   }

   public static int c(int var0, int var1, int var2) {
      return var0 < var1 ? var1 : Math.min(var0, var2);
   }

   public static double F(double var0, double var2, double var4) {
      return var0 < var2 ? var2 : Math.min(var0, var4);
   }

   public static float s(float var0, float var1, float var2) {
      return var0 < var1 ? var1 : Math.min(var0, var2);
   }

   public static int J(double var0) {
      int var2 = (int)var0;
      return var0 < (double)var2 ? var2 - 1 : var2;
   }

   public static int a(float var0) {
      int var1 = (int)var0;
      return var0 < (float)var1 ? var1 - 1 : var1;
   }

   public static long F(double var0) {
      long var2 = (long)var0;
      return var0 > (double)var2 ? var2 + 1L : var2;
   }

   public static double e(double var0, double var2) {
      return Math.max(Math.abs(var0), Math.abs(var2));
   }
}
