package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class E7 {
   private int t;
   private RT U;
   private static final long a = kt.a(1926712413782632804L, -6925790289780806546L, MethodHandles.lookup().lookupClass()).a(280838077380706L);

   public E7(int var1, RT var2) {
      this.t = var1;
      this.U = var2;
   }

   public static E7 D(AR var0, Gs var1, vL var2) {
      if (var2 == null) {
         var2 = var1 != null && oS.J().L().T() ? var1.E() : oS.J().g().w().u();
      }

      RT var3 = (RT)AR.w(var0, var2);
      return new E7(var0.f(var2), var3);
   }

   public AR c(Gs var1, vL var2) {
      if (var2 == null) {
         var2 = var1 != null && oS.J().L().T() ? var1.E() : oS.J().g().w().u();
      }

      Object var3 = var1 != null ? var1.W(BW.N(), var2) : BW.N();
      String var4 = this.j();
      return !var4.isEmpty() ? (AR)((VD)var3).u(new al(var4)) : (AR)((VD)var3).e(var2, this.t);
   }

   public String j() {
      long var1 = a ^ 72458883072012L;
      return this.G().g("effects", "");
   }

   public RT G() {
      return this.U;
   }
}
