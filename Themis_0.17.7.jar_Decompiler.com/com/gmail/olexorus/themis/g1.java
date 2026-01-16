package com.gmail.olexorus.themis;

import java.util.Objects;

public class G1 {
   private float R;
   private float X;
   private float x;
   private float H;
   private float W;
   private float m;

   public G1(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.R = var1;
      this.X = var2;
      this.x = var3;
      this.H = var4;
      this.W = var5;
      this.m = var6;
   }

   public static G1 s(lm<?> var0) {
      float var1 = var0.L();
      float var2 = var0.L();
      float var3 = var0.L();
      float var4 = var0.L();
      float var5 = var0.L();
      float var6 = var0.L();
      return new G1(var1, var2, var3, var4, var5, var6);
   }

   public static void t(lm<?> var0, G1 var1) {
      var0.S(var1.R);
      var0.S(var1.X);
      var0.S(var1.x);
      var0.S(var1.H);
      var0.S(var1.W);
      var0.S(var1.m);
   }

   public boolean equals(Object var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         G1 var2 = (G1)var1;
         if (Float.compare(var2.R, this.R) != 0) {
            return false;
         } else if (Float.compare(var2.X, this.X) != 0) {
            return false;
         } else if (Float.compare(var2.x, this.x) != 0) {
            return false;
         } else if (Float.compare(var2.H, this.H) != 0) {
            return false;
         } else if (Float.compare(var2.W, this.W) != 0) {
            return false;
         } else {
            return Float.compare(var2.m, this.m) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.R, this.X, this.x, this.H, this.W, this.m});
   }
}
