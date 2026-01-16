package com.gmail.olexorus.themis;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TX {
   private final int X;
   private final int u;
   private final boolean d;
   private final g3[] o;
   private final GU[] B;
   private final boolean V;
   private RT C;
   private Map<TJ, long[]> v;
   private final boolean W;
   private int[] q;
   private byte[] m;

   public TX(int var1, int var2, boolean var3, g3[] var4, GU[] var5, int[] var6) {
      this.X = var1;
      this.u = var2;
      this.d = var3;
      this.o = (g3[])Arrays.copyOf(var4, var4.length);
      this.B = var5 != null ? var5 : new GU[0];
      this.V = false;
      this.C = new RT();
      this.W = true;
      this.q = var6 != null ? Arrays.copyOf(var6, var6.length) : null;
   }

   public TX(int var1, int var2, boolean var3, g3[] var4, GU[] var5) {
      this.X = var1;
      this.u = var2;
      this.d = var3;
      this.o = (g3[])Arrays.copyOf(var4, var4.length);
      this.B = var5 != null ? var5 : new GU[0];
      this.V = false;
      this.C = new RT();
      this.W = false;
      this.q = new int[1024];
   }

   public TX(int var1, int var2, boolean var3, g3[] var4, GU[] var5, RT var6) {
      this.X = var1;
      this.u = var2;
      this.d = var3;
      this.o = (g3[])Arrays.copyOf(var4, var4.length);
      this.B = var5 != null ? var5 : new GU[0];
      this.V = true;
      this.C = var6;
      this.W = false;
      this.q = new int[1024];
   }

   public TX(int var1, int var2, boolean var3, g3[] var4, GU[] var5, Map<TJ, long[]> var6) {
      this.X = var1;
      this.u = var2;
      this.d = var3;
      this.o = (g3[])Arrays.copyOf(var4, var4.length);
      this.B = var5 != null ? var5 : new GU[0];
      this.V = true;
      this.C = null;
      this.v = var6;
      this.W = false;
      this.q = new int[1024];
   }

   public TX(int var1, int var2, boolean var3, g3[] var4, GU[] var5, RT var6, int[] var7) {
      this.X = var1;
      this.u = var2;
      this.d = var3;
      this.o = (g3[])Arrays.copyOf(var4, var4.length);
      this.B = var5 != null ? var5 : new GU[0];
      this.V = true;
      this.C = var6;
      this.W = true;
      this.q = var7 != null ? Arrays.copyOf(var7, var7.length) : null;
   }

   public TX(int var1, int var2, boolean var3, g3[] var4, GU[] var5, RT var6, byte[] var7) {
      this.X = var1;
      this.u = var2;
      this.d = var3;
      this.o = (g3[])Arrays.copyOf(var4, var4.length);
      this.B = var5 != null ? var5 : new GU[0];
      this.V = true;
      this.C = var6;
      this.W = true;
      this.m = var7 != null ? Arrays.copyOf(var7, var7.length) : null;
   }

   public TX(int var1, int var2, boolean var3, g3[] var4, GU[] var5, byte[] var6) {
      this.X = var1;
      this.u = var2;
      this.d = var3;
      this.o = (g3[])Arrays.copyOf(var4, var4.length);
      this.B = var5 != null ? var5 : new GU[0];
      this.V = false;
      this.C = new RT();
      this.W = true;
      this.m = var6 != null ? Arrays.copyOf(var6, var6.length) : null;
   }

   public int d() {
      return this.X;
   }

   public int Y() {
      return this.u;
   }

   public boolean k() {
      return this.d;
   }

   public g3[] b() {
      return this.o;
   }

   public GU[] W() {
      return this.B;
   }

   public RT N() {
      if (this.C == null) {
         this.C = new RT();
         Iterator var1 = this.y().entrySet().iterator();

         while(var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            this.C.j(((TJ)var2.getKey()).n(), new mt((long[])var2.getValue()));
         }
      }

      return this.C;
   }

   public Map<TJ, long[]> y() {
      if (this.v == null) {
         if (this.V && !this.C.R()) {
            this.v = new EnumMap(TJ.class);
            Iterator var1 = this.C.q().entrySet().iterator();

            while(var1.hasNext()) {
               Entry var2 = (Entry)var1.next();
               TJ var3 = TJ.q((String)var2.getKey());
               if (var3 != null && var2.getValue() instanceof mt) {
                  long[] var4 = ((mt)var2.getValue()).i();
                  this.v.put(var3, var4);
               }
            }
         } else {
            this.v = Collections.emptyMap();
         }
      }

      return this.v;
   }

   public boolean R() {
      return this.W;
   }

   public int[] F() {
      return this.q;
   }

   public byte[] B() {
      return this.m;
   }
}
