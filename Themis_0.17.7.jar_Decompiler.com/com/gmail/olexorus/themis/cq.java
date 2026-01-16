package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

final class CQ implements cF {
   private final double K;
   private final double W;
   private final double G;
   private static final long b = kt.a(5526023087426528364L, 4105015926104518861L, MethodHandles.lookup().lookupClass()).a(60992017022392L);

   CQ(double var1, double var3, double var5) {
      this.K = var1;
      this.W = var3;
      this.G = var5;
   }

   public double J() {
      return this.K;
   }

   public double m() {
      return this.W;
   }

   public double s() {
      return this.G;
   }

   public Stream<? extends rE> T() {
      long var1 = b ^ 70482363361299L;
      return Stream.of(rE.b("left", this.K), rE.b("up", this.W), rE.b("forwards", this.G));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof cF)) {
         return false;
      } else {
         cF var2 = (cF)var1;
         return OD.Z(var2.J(), this.J()) && OD.Z(var2.m(), this.m()) && OD.Z(var2.s(), this.s());
      }
   }

   public int hashCode() {
      int var1 = Double.hashCode(this.K);
      var1 = 31 * var1 + Double.hashCode(this.W);
      var1 = 31 * var1 + Double.hashCode(this.G);
      return var1;
   }

   public String toString() {
      long var1 = b ^ 59798602230970L;
      return String.format("^%f ^%f ^%f", this.K, this.W, this.G);
   }

   public String G() {
      return uA.y(this.K) + ' ' + uA.y(this.W) + ' ' + uA.y(this.G);
   }
}
