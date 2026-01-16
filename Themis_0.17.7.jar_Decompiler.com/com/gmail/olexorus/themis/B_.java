package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum b_ {
   INACTIVE,
   ACTIVE,
   UNLOCKING,
   EJECTING;

   // $FF: synthetic method
   private static b_[] x() {
      return new b_[]{INACTIVE, ACTIVE, UNLOCKING, EJECTING};
   }

   static {
      long var0 = kt.a(-5273151971915745464L, -7416799492848830046L, MethodHandles.lookup().lookupClass()).a(118535147300176L) ^ 117313389822292L;
      INACTIVE = new b_("INACTIVE", 0);
      ACTIVE = new b_("ACTIVE", 1);
      UNLOCKING = new b_("UNLOCKING", 2);
      EJECTING = new b_("EJECTING", 3);
   }
}
