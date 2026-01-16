package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum CN {
   SWING_MAIN_ARM,
   HURT,
   WAKE_UP,
   SWING_OFF_HAND,
   CRITICAL_HIT,
   MAGIC_CRITICAL_HIT;

   private static final CN[] h;
   private static final CN[] T;

   public static CN o(int var0) {
      return h[var0];
   }

   private static CN[] C() {
      return new CN[]{SWING_MAIN_ARM, HURT, WAKE_UP, SWING_OFF_HAND, CRITICAL_HIT, MAGIC_CRITICAL_HIT};
   }

   static {
      long var0 = kt.a(-5442195034497974877L, -1627645883605762258L, MethodHandles.lookup().lookupClass()).a(277175242435349L) ^ 48021377526609L;
      SWING_MAIN_ARM = new CN("SWING_MAIN_ARM", 0);
      HURT = new CN("HURT", 1);
      WAKE_UP = new CN("WAKE_UP", 2);
      SWING_OFF_HAND = new CN("SWING_OFF_HAND", 3);
      CRITICAL_HIT = new CN("CRITICAL_HIT", 4);
      MAGIC_CRITICAL_HIT = new CN("MAGIC_CRITICAL_HIT", 5);
      T = C();
      h = values();
   }
}
