package com.gmail.olexorus.themis;

import java.util.Objects;

public class ic extends id implements a2 {
   private final CW M;
   private final float g;
   private final float x;
   private final X d;

   public ic(CW var1, int var2, float var3) {
      this(var1, (float)var2 * 20.0F, var3, X.f());
   }

   public ic(CW var1, float var2, float var3, X var4) {
      this((z2)null, var1, var2, var3, var4);
   }

   public ic(z2 var1, CW var2, float var3, float var4, X var5) {
      super(var1);
      this.M = var2;
      this.g = var3;
      this.x = var4;
      this.d = var5;
   }

   public a2 T(z2 var1) {
      return new ic(var1, this.M, this.g, this.x, this.d);
   }

   public CW I() {
      return this.M;
   }

   public float g() {
      return this.g;
   }

   public float m() {
      return this.x;
   }

   public X B() {
      return this.d;
   }

   public boolean I(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ic)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         ic var2 = (ic)var1;
         if (this.g != var2.g) {
            return false;
         } else if (Float.compare(var2.x, this.x) != 0) {
            return false;
         } else {
            return !this.M.equals(var2.M) ? false : this.d.equals(var2.d);
         }
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.M, this.g, this.x, this.d});
   }
}
