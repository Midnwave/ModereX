package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum uZ {
   EYES,
   FEET;

   private static final uZ[] t;

   public int t() {
      return this.ordinal();
   }

   public static uZ i(int var0) {
      return values()[var0];
   }

   private static uZ[] f() {
      return new uZ[]{EYES, FEET};
   }

   static {
      long var0 = kt.a(6094323762049955653L, 6922847194242962243L, MethodHandles.lookup().lookupClass()).a(89317512733824L) ^ 30523145039355L;
      EYES = new uZ("EYES", 0);
      FEET = new uZ("FEET", 1);
      t = f();
   }
}
