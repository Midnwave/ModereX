package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum uw {
   DISABLED,
   ACTIVE,
   DORMANT,
   UPROOTED,
   AWAKE;

   private static final uw[] G;

   private static uw[] I() {
      return new uw[]{DISABLED, ACTIVE, DORMANT, UPROOTED, AWAKE};
   }

   static {
      long var0 = kt.a(-76610122443501329L, 3993750791639661351L, MethodHandles.lookup().lookupClass()).a(205864786091946L) ^ 129276235717126L;
      DISABLED = new uw("DISABLED", 0);
      ACTIVE = new uw("ACTIVE", 1);
      DORMANT = new uw("DORMANT", 2);
      UPROOTED = new uw("UPROOTED", 3);
      AWAKE = new uw("AWAKE", 4);
      G = I();
   }
}
