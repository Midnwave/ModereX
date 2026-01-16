package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

public class ig extends id implements OC {
   private final i_ i;
   private final List<uk> j;
   private final uk q;
   private final int x;
   private static final long a = kt.a(-3378505452200050326L, -445242785921526879L, MethodHandles.lookup().lookupClass()).a(26241652336771L);

   public ig(i_ var1, List<uk> var2, uk var3, int var4) {
      this((z2)null, var1, var2, var3, var4);
   }

   public ig(z2 var1, i_ var2, List<uk> var3, uk var4, int var5) {
      super(var1);
      this.i = var2;
      this.j = var3;
      this.q = var4;
      this.x = var5;
   }

   public static ig b(RT var0, lm<?> var1) {
      long var2 = a ^ 37616904445743L;
      i_ var4 = i_.H(var0, var1);
      List var5 = var0.i("actions", uk::x, var1);
      uk var6 = (uk)var0.M("exit_action", uk::x, var1);
      int var7 = var0.L("columns", 2).intValue();
      int var8 = var0.L("button_width", 150).intValue();
      return new ig((z2)null, var4, var5, var6, var7);
   }

   public static void W(RT var0, lm<?> var1, ig var2) {
      long var3 = a ^ 77171795561304L;
      i_.A(var0, var1, var2.i);
      var0.h("actions", var2.j, uk::m, var1);
      if (var2.q != null) {
         var0.X("exit_action", var2.q, uk::m, var1);
      }

      if (var2.x != 2) {
         var0.j("columns", new mz(var2.x));
      }

   }

   public OC W(z2 var1) {
      return new ig(var1, this.i, this.j, this.q, this.x);
   }

   public i_ w() {
      return this.i;
   }

   public List<uk> H() {
      return this.j;
   }

   public uk l() {
      return this.q;
   }

   public int V() {
      return this.x;
   }

   public T<?> M() {
      return ag.U;
   }

   public boolean I(Object var1) {
      if (!(var1 instanceof ig)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         ig var2 = (ig)var1;
         if (this.x != var2.x) {
            return false;
         } else if (!this.i.equals(var2.i)) {
            return false;
         } else {
            return !this.j.equals(var2.j) ? false : Objects.equals(this.q, var2.q);
         }
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.i, this.j, this.q, this.x});
   }
}
