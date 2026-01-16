package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class cp extends id implements md {
   private final String N;
   private final w3 m;
   private final float R;
   private final GP H;
   private final bv p;
   private static final long b = kt.a(-6707412518802932118L, -5560199873672484960L, MethodHandles.lookup().lookupClass()).a(234209205134539L);

   public cp(z2 var1, String var2, w3 var3, float var4, GP var5, bv var6) {
      super(var1);
      this.N = var2;
      this.m = var3;
      this.R = var4;
      this.H = var5;
      this.p = var6;
   }

   public md p(z2 var1) {
      return new cp(var1, this.N, this.m, this.R, this.H, this.p);
   }

   public String a() {
      return this.N;
   }

   public w3 C() {
      return this.m;
   }

   public float a() {
      return this.R;
   }

   public GP U() {
      return this.H;
   }

   public bv s() {
      return this.p;
   }

   public boolean I(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof cp)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         cp var2 = (cp)var1;
         if (Float.compare(var2.R, this.R) != 0) {
            return false;
         } else if (!this.N.equals(var2.N)) {
            return false;
         } else if (this.m != var2.m) {
            return false;
         } else if (this.H != var2.H) {
            return false;
         } else {
            return this.p == var2.p;
         }
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.N, this.m, this.R, this.H, this.p});
   }

   public String toString() {
      long var1 = b ^ 75687305470890L;
      return "StaticDamageType{messageId='" + this.N + '\'' + ", scaling=" + this.m + ", exhaustion=" + this.R + ", effects=" + this.H + ", deathMessageType=" + this.p + '}';
   }
}
