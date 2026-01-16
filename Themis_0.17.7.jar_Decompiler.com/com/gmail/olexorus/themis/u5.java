package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class u5 {
   public static final u5 U = new u5((al)null, (al)null, (al)null, (zz)null);
   private final al R;
   private final al M;
   private final al D;
   private final zz J;
   private static final long a = kt.a(-8799783163212916478L, -7798488978597307582L, MethodHandles.lookup().lookupClass()).a(201462675892721L);

   public u5(al var1, al var2, al var3, zz var4) {
      this.R = var1;
      this.M = var2;
      this.D = var3;
      this.J = var4;
   }

   public static u5 G(lm<?> var0) {
      al var1 = (al)var0.u(al::c);
      al var2 = (al)var0.u(al::c);
      al var3 = (al)var0.u(al::c);
      zz var4 = (zz)var0.u(zz::t);
      return new u5(var1, var2, var3, var4);
   }

   public static void a(lm<?> var0, u5 var1) {
      var0.l(var1.R, al::C);
      var0.l(var1.M, al::C);
      var0.l(var1.D, al::C);
      var0.l(var1.J, zz::f);
   }

   public static u5 W(RT var0, lm<?> var1) {
      long var2 = a ^ 4320081255776L;
      al var4 = (al)var0.M("texture", al.F, var1);
      al var5 = (al)var0.M("cape", al.F, var1);
      al var6 = (al)var0.M("elytra", al.F, var1);
      zz var7 = (zz)var0.M("model", zz.CODEC, var1);
      return new u5(var4, var5, var6, var7);
   }

   public static void D(RT var0, lm<?> var1, u5 var2) {
      long var3 = a ^ 49214264828650L;
      if (var2.R != null) {
         var0.X("texture", var2.R, al.F, var1);
      }

      if (var2.M != null) {
         var0.X("cape", var2.M, al.F, var1);
      }

      if (var2.D != null) {
         var0.X("elytra", var2.D, al.F, var1);
      }

      if (var2.J != null) {
         var0.X("model", var2.J, zz.CODEC, var1);
      }

   }

   public boolean equals(Object var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         u5 var2 = (u5)var1;
         if (!Objects.equals(this.R, var2.R)) {
            return false;
         } else if (!Objects.equals(this.M, var2.M)) {
            return false;
         } else if (!Objects.equals(this.D, var2.D)) {
            return false;
         } else {
            return this.J == var2.J;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.R, this.M, this.D, this.J});
   }
}
