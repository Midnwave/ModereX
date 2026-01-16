package com.gmail.olexorus.themis;

import java.util.Objects;

public class i8 extends id implements gb {
   private final CW q;
   private final CW G;
   private final CW Z;
   private final CW X;
   private final CW i;
   private final CW E;

   public i8(CW var1, CW var2, CW var3, CW var4, CW var5, CW var6) {
      this((z2)null, var1, var2, var3, var4, var5, var6);
   }

   public i8(z2 var1, CW var2, CW var3, CW var4, CW var5, CW var6, CW var7) {
      super(var1);
      this.q = var2;
      this.G = var3;
      this.Z = var4;
      this.X = var5;
      this.i = var6;
      this.E = var7;
   }

   public gb Q(z2 var1) {
      return new i8(var1, this.q, this.G, this.Z, this.X, this.i, this.E);
   }

   public CW i() {
      return this.q;
   }

   public CW f() {
      return this.G;
   }

   public CW X() {
      return this.Z;
   }

   public CW v() {
      return this.X;
   }

   public CW Z() {
      return this.i;
   }

   public CW F() {
      return this.E;
   }

   public boolean I(Object var1) {
      if (!(var1 instanceof i8)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         i8 var2 = (i8)var1;
         if (!this.q.equals(var2.q)) {
            return false;
         } else if (!this.G.equals(var2.G)) {
            return false;
         } else if (!this.Z.equals(var2.Z)) {
            return false;
         } else if (!this.X.equals(var2.X)) {
            return false;
         } else {
            return !this.i.equals(var2.i) ? false : this.E.equals(var2.E);
         }
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.q, this.G, this.Z, this.X, this.i, this.E});
   }
}
