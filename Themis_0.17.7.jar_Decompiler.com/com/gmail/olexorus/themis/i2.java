package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class i2 extends id implements mE, Cy {
   private final X t;
   private final Go b;
   private final Ng<mE> a;
   private gC<mE> d;
   private final zQ l;
   private static final long e = kt.a(1914773513713768490L, 2837450034230860426L, MethodHandles.lookup().lookupClass()).a(270222927036030L);

   public i2(X var1, Go var2, gC<mE> var3, zQ var4) {
      this((z2)null, var1, var2, var3, var4);
   }

   public i2(z2 var1, X var2, Go var3, Ng<mE> var4, zQ var5) {
      super(var1);
      this.t = var2;
      this.b = var3;
      this.a = var4;
      this.l = var5;
   }

   public void w(lm<?> var1) {
      this.d = this.a.o(var1, EY.R());
   }

   public mE r(z2 var1) {
      i2 var2 = new i2(var1, this.t, this.b, this.a, this.l);
      var2.d = this.d;
      return var2;
   }

   public X i() {
      return this.t;
   }

   public Go e() {
      return this.b;
   }

   public gC<mE> M() {
      if (this.d == null) {
         this.d = this.a.C(oS.J().g().w().u(), CC.k, EY.R());
      }

      return this.d;
   }

   public Ng<mE> f() {
      return this.a;
   }

   public zQ U() {
      return this.l;
   }

   public boolean I(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof i2)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         i2 var2 = (i2)var1;
         if (!this.t.equals(var2.t)) {
            return false;
         } else if (!this.b.equals(var2.b)) {
            return false;
         } else {
            return !this.a.equals(var2.a) ? false : this.l.equals(var2.l);
         }
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.t, this.b, this.a, this.l});
   }

   public String toString() {
      long var1 = e ^ 126308365594929L;
      return "StaticEnchantmentType{description=" + this.t + ", definition=" + this.b + ", exclusiveSetRef=" + this.a + ", effects=" + this.l + "}";
   }
}
