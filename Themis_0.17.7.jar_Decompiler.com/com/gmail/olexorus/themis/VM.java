package com.gmail.olexorus.themis;

import java.util.Objects;

public class vM {
   private final cB w;
   private final float D;
   private final float F;
   private final boolean P;
   private final boolean N;
   private final boolean K;
   private final boolean V;
   private final boolean X;
   private final zd L;

   public vM(z2 var1, float var2, float var3, boolean var4, boolean var5, boolean var6, boolean var7, boolean var8, zd var9) {
      this.w = new cB(this, var1);
      this.D = var2;
      this.F = var3;
      this.P = var4;
      this.N = var5;
      this.K = var6;
      this.V = var7;
      this.X = var8;
      this.L = var9;
   }

   public cB s() {
      return this.w;
   }

   public VO d() {
      return VO.C(oS.J().g().w().u(), this);
   }

   public String o() {
      return this.w.f().R();
   }

   public String toString() {
      return this.o();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         vM var2 = (vM)var1;
         return Float.compare(this.D, var2.D) == 0 && Float.compare(this.F, var2.F) == 0 && this.P == var2.P && this.N == var2.N && this.K == var2.K && this.V == var2.V && this.X == var2.X && Objects.equals(this.o(), var2.o()) && this.L == var2.L;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.o(), this.D, this.F, this.P, this.N, this.K, this.V, this.X, this.L});
   }
}
