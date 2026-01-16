package com.gmail.olexorus.themis;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.BitSet;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class s6 extends lm<s6> {
   private int[] p;
   private int[] N;
   private g3[][] E;
   private byte[][] A;
   private static final long a = kt.a(8600317140904749127L, -167285829977092864L, MethodHandles.lookup().lookupClass()).a(90504083970812L);

   public void t() {
      if (this.I.i(zZ.V_1_8)) {
         this.q();
      } else {
         this.R();
      }

   }

   private void q() {
      boolean var1 = this.P();
      int var2 = this.Q();
      this.p = new int[var2];
      this.N = new int[var2];
      this.E = new g3[var2][];
      this.A = new byte[var2][];
      z9[] var3 = new z9[var2];

      int var4;
      for(var4 = 0; var4 < var2; ++var4) {
         this.p[var4] = this.f();
         this.N[var4] = this.f();
         int var5 = this.C();
         int var6 = Integer.bitCount(var5);
         int var7 = var6 * 10240 + (var1 ? var6 * 2048 : 0);
         byte[] var8 = new byte[var7];
         var3[var4] = new z9(var5, true, var1, var8);
      }

      for(var4 = 0; var4 < var2; ++var4) {
         BitSet var9 = BitSet.valueOf(new long[]{(long)var3[var4].i()});
         g3[] var10 = (new uf()).X(this.Z.j(), var9, (BitSet)null, true, var1, false, 16, var3[var4].u().length, this);
         this.E[var4] = var10;
         this.A[var4] = this.E(256);
      }

   }

   private void R() {
      long var1 = a ^ 51386280476099L;
      short var3 = this.x();
      int var4 = this.f();
      boolean var5 = this.P();
      byte[] var6 = this.E(var4);
      byte[] var7 = new byte[196864 * var3];
      Inflater var8 = new Inflater();
      var8.setInput(var6, 0, var4);

      label99: {
         try {
            var8.inflate(var7);
            break label99;
         } catch (DataFormatException var23) {
            (new IOException("Bad compressed data format")).printStackTrace();
         } finally {
            var8.end();
         }

         return;
      }

      Object var9 = this.g;
      Object var10 = nT.l(var7);
      this.p = new int[var3];
      this.N = new int[var3];
      this.E = new g3[var3][];
      this.A = new byte[var3][];

      for(int var11 = 0; var11 < var3; ++var11) {
         int var12 = this.f();
         int var13 = this.f();
         BitSet var14 = BitSet.valueOf(new long[]{(long)this.C()});
         BitSet var15 = BitSet.valueOf(new long[]{(long)this.C()});
         int var16 = 0;
         int var17 = 0;

         int var18;
         for(var18 = 0; var18 < 16; ++var18) {
            var16 += var14.get(var18) ? 1 : 0;
            var17 += var15.get(var18) ? 1 : 0;
         }

         var18 = 8192 * var16 + 256 + 2048 * var17;
         if (var5) {
            var18 += 2048 * var16;
         }

         this.g = var10;
         g3[] var19 = (new tl()).X(this.Z.j(), var14, var15, true, var5, false, 16, var18, this);
         byte[] var20 = this.E(256);
         this.g = var9;
         this.p[var11] = var12;
         this.N[var11] = var13;
         this.E[var11] = var19;
         this.A[var11] = var20;
      }

      NY.o(var10);
   }

   public void d() {
      if (this.I.i(zZ.V_1_8)) {
         this.U();
      } else {
         this.Y();
      }

   }

   public void T(s6 var1) {
      this.p = var1.p;
      this.N = var1.N;
      this.E = var1.E;
      this.A = var1.A;
   }

   private void U() {
      boolean var1 = false;
      z9[] var2 = new z9[this.E.length];

      int var3;
      for(var3 = 0; var3 < this.E.length; ++var3) {
         var2[var3] = uf.x((Vi[])this.E[var3], this.A[var3]);
         if (var2[var3].q()) {
            var1 = true;
         }
      }

      this.I(var1);
      this.E(this.E.length);

      for(var3 = 0; var3 < this.p.length; ++var3) {
         this.L(this.p[var3]);
         this.L(this.N[var3]);
         this.f(var2[var3].i());
      }

      for(var3 = 0; var3 < this.p.length; ++var3) {
         this.d(var2[var3].u());
      }

   }

   private void Y() {
      int[] var1 = new int[this.E.length];
      int[] var2 = new int[this.E.length];
      int var3 = 0;
      byte[] var4 = new byte[0];
      boolean var5 = false;

      for(int var6 = 0; var6 < this.E.length; ++var6) {
         g3[] var7 = this.E[var6];
         z9 var8 = tl.z((A1[])var7, this.A[var6]);
         if (var4.length < var3 + var8.u().length) {
            byte[] var9 = new byte[var3 + var8.u().length];
            System.arraycopy(var4, 0, var9, 0, var4.length);
            var4 = var9;
         }

         if (var8.q()) {
            var5 = true;
         }

         System.arraycopy(var8.u(), 0, var4, var3, var8.u().length);
         var3 += var8.u().length;
         var1[var6] = var8.i();
         var2[var6] = var8.q();
      }

      Deflater var13 = new Deflater(-1);
      byte[] var14 = new byte[var3];

      int var15;
      try {
         var13.setInput(var4, 0, var3);
         var13.finish();
         var15 = var13.deflate(var14);
      } finally {
         var13.end();
      }

      this.f(this.E.length);
      this.L(var15);
      this.I(var5);

      int var16;
      for(var16 = 0; var16 < var15; ++var16) {
         this.u(var14[var16]);
      }

      for(var16 = 0; var16 < this.E.length; ++var16) {
         this.L(this.p[var16]);
         this.L(this.N[var16]);
         this.f((short)(var1[var16] & '\uffff'));
         this.f((short)(var2[var16] & '\uffff'));
      }

   }
}
