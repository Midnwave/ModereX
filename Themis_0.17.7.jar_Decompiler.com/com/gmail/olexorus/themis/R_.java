package com.gmail.olexorus.themis;

import java.util.BitSet;

public class r_ implements Cloneable {
   private boolean z;
   private BitSet S;
   private BitSet Q;
   private BitSet k;
   private BitSet M;
   private int i;
   private int J;
   private byte[][] B;
   private byte[][] N;

   public r_ a() {
      try {
         r_ var1 = (r_)super.clone();
         var1.S = (BitSet)this.S.clone();
         var1.Q = (BitSet)this.Q.clone();
         var1.k = (BitSet)this.k.clone();
         var1.M = (BitSet)this.M.clone();
         var1.B = (byte[][])this.B.clone();
         var1.N = (byte[][])this.N.clone();
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }

   public static r_ R(lm<?> var0) {
      r_ var1 = new r_();
      zZ var2 = var0.R();
      if (var2.R(zZ.V_1_19_4)) {
         var1.z = var0.P();
      }

      var1.Q = JJ.p(var0);
      var1.S = JJ.p(var0);
      var1.M = JJ.p(var0);
      var1.k = JJ.p(var0);
      boolean var3 = var2.i(zZ.V_1_17);
      var1.i = var3 ? var0.Q() : 18;
      var1.B = new byte[var1.i][];

      int var4;
      for(var4 = 0; var4 < var1.i; ++var4) {
         if (var3 || var1.Q.get(var4)) {
            var1.B[var4] = var0.w();
         }
      }

      var1.J = var3 ? var0.Q() : 18;
      var1.N = new byte[var1.J][];

      for(var4 = 0; var4 < var1.J; ++var4) {
         if (var3 || var1.S.get(var4)) {
            var1.N[var4] = var0.w();
         }
      }

      return var1;
   }

   public static void Z(lm<?> var0, r_ var1) {
      zZ var2 = var0.R();
      if (var2.R(zZ.V_1_19_4)) {
         var0.I(var1.z);
      }

      JJ.O(var0, var1.Q);
      JJ.O(var0, var1.S);
      JJ.O(var0, var1.M);
      JJ.O(var0, var1.k);
      boolean var3 = var2.i(zZ.V_1_17);
      if (var3) {
         var0.E(var1.i);
      }

      int var4;
      for(var4 = 0; var4 < var1.i; ++var4) {
         if (var3 || var1.Q.get(var4)) {
            var0.N(var1.B[var4]);
         }
      }

      if (var3) {
         var0.E(var1.J);
      }

      for(var4 = 0; var4 < var1.J; ++var4) {
         if (var3 || var1.S.get(var4)) {
            var0.N(var1.N[var4]);
         }
      }

   }
}
