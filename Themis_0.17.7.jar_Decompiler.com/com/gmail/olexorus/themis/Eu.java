package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

final class EU extends Eq<s, vt> implements s {
   private final String l;
   private static final long c = kt.a(6763375599059068840L, -1160837185149262104L, MethodHandles.lookup().lookupClass()).a(211554283735687L);

   static s k(List<? extends lv> var0, WR var1, String var2, boolean var3, lv var4, String var5) {
      long var6 = c ^ 31195813348129L;
      return new EU(lv.Z(var0, p), (WR)Objects.requireNonNull(var1, "style"), (String)Objects.requireNonNull(var2, "nbtPath"), var3, lv.z(var4), (String)Objects.requireNonNull(var5, "selector"));
   }

   EU(List<X> var1, WR var2, String var3, boolean var4, X var5, String var6) {
      super(var1, var2, var3, var4, var5);
      this.l = var6;
   }

   public X V() {
      return this.i;
   }

   public String w() {
      return this.l;
   }

   public s O(List<? extends lv> var1) {
      return k(var1, this.F, this.v, this.X, this.i, this.l);
   }

   public s r(WR var1) {
      return k(this.E, var1, this.v, this.X, this.i, this.l);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof s)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         EU var2 = (EU)var1;
         return Objects.equals(this.l, var2.w());
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + this.l.hashCode();
      return var1;
   }

   public String toString() {
      return cH.M(this);
   }

   public vt o() {
      return new AK(this);
   }
}
