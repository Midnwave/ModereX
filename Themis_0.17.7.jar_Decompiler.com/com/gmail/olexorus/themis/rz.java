package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class RZ {
   public static final RZ C = new RZ((vL)null);
   private static final vL[] I = vL.values();
   private final vL f;
   private final vL L;
   private static final long a = kt.a(5872685854875087427L, 1003133582631704292L, MethodHandles.lookup().lookupClass()).a(34850909633542L);

   public RZ(vL var1) {
      this(var1, var1);
   }

   public RZ(vL var1, vL var2) {
      long var3 = a ^ 4987443312713L;
      super();
      this.f = var1 != null ? var1 : vL.S();
      this.L = var2 != null ? var2 : vL.n();
      if (this.f.compareTo(this.L) > 0) {
         throw new IllegalArgumentException("Minimum version is newer than maximum version: " + this.f + " > " + this.L);
      }
   }

   public boolean g(vL var1) {
      return var1.compareTo(this.f) >= 0 && var1.compareTo(this.L) <= 0;
   }

   public String toString() {
      long var1 = a ^ 40628123749670L;
      return "(" + this.f + " to " + this.L + ")";
   }
}
