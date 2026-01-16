package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum oF {
   ADDITION,
   MULTIPLY_BASE,
   MULTIPLY_TOTAL;

   // $FF: synthetic method
   private static oF[] a() {
      return new oF[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
   }

   static {
      long var0 = kt.a(1764884442830977320L, 8216264328803691653L, MethodHandles.lookup().lookupClass()).a(191882996785736L) ^ 128283155664975L;
      ADDITION = new oF("ADDITION", 0);
      MULTIPLY_BASE = new oF("MULTIPLY_BASE", 1);
      MULTIPLY_TOTAL = new oF("MULTIPLY_TOTAL", 2);
   }
}
