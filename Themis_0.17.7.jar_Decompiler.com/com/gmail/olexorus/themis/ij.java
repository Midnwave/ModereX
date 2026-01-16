package com.gmail.olexorus.themis;

public class iJ extends id implements MI {
   private final al A;
   private final double l;
   private final double p;
   private final double F;

   public iJ(z2 var1, String var2, double var3, double var5, double var7) {
      super(var1);
      this.l = var3;
      this.p = var5;
      this.F = var7;
      this.A = var2 != null && var1 != null ? new al(var1.m().y(), var2 + "." + var1.m().R()) : null;
   }

   public al v(vL var1) {
      if (this.f == null) {
         throw new UnsupportedOperationException();
      } else {
         return !var1.K(vL.V_1_21_2) && this.A != null ? this.A : this.f.m();
      }
   }

   public double h() {
      return this.l;
   }

   public double X() {
      return this.p;
   }

   public double f() {
      return this.F;
   }
}
