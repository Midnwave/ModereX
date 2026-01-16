package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class i5 extends id implements OC {
   private final i_ K;
   private final uk s;
   private final int G;
   private final int o;
   private static final long a = kt.a(-8092004964388756952L, 6231399021336656552L, MethodHandles.lookup().lookupClass()).a(212139652525175L);

   public i5(i_ var1, uk var2, int var3, int var4) {
      this((z2)null, var1, var2, var3, var4);
   }

   public i5(z2 var1, i_ var2, uk var3, int var4, int var5) {
      super(var1);
      this.K = var2;
      this.s = var3;
      this.G = var4;
      this.o = var5;
   }

   public static i5 B(RT var0, lm<?> var1) {
      long var2 = a ^ 86292088790048L;
      i_ var4 = i_.H(var0, var1);
      uk var5 = (uk)var0.M("exit_action", uk::x, var1);
      int var6 = var0.L("columns", 2).intValue();
      int var7 = var0.L("button_width", 150).intValue();
      return new i5((z2)null, var4, var5, var6, var7);
   }

   public static void p(RT var0, lm<?> var1, i5 var2) {
      long var3 = a ^ 10613860523092L;
      i_.A(var0, var1, var2.K);
      if (var2.s != null) {
         var0.X("exit_action", var2.s, uk::m, var1);
      }

      if (var2.G != 2) {
         var0.j("columns", new mz(var2.G));
      }

      if (var2.o != 150) {
         var0.j("button_width", new mz(var2.o));
      }

   }

   public OC y(z2 var1) {
      return new i5(var1, this.K, this.s, this.G, this.o);
   }

   public i_ L() {
      return this.K;
   }

   public uk A() {
      return this.s;
   }

   public int r() {
      return this.G;
   }

   public int c() {
      return this.o;
   }

   public T<?> M() {
      return ag.g;
   }

   public boolean I(Object var1) {
      if (!(var1 instanceof i5)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         i5 var2 = (i5)var1;
         if (this.G != var2.G) {
            return false;
         } else if (this.o != var2.o) {
            return false;
         } else {
            return !this.K.equals(var2.K) ? false : Objects.equals(this.s, var2.s);
         }
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.K, this.s, this.G, this.o});
   }
}
