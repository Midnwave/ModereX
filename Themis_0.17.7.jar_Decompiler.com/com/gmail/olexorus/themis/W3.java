package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum w3 {
   NEVER,
   WHEN_CAUSED_BY_LIVING_NON_PLAYER,
   ALWAYS;

   public static final l3<String, w3> ID_INDEX;
   private final String V;
   private static final w3[] J;

   private w3(String var3) {
      this.V = var3;
   }

   public String E() {
      return this.V;
   }

   private static w3[] T() {
      return new w3[]{NEVER, WHEN_CAUSED_BY_LIVING_NON_PLAYER, ALWAYS};
   }

   static {
      long var0 = kt.a(1344781540272455758L, 8421799686751048898L, MethodHandles.lookup().lookupClass()).a(125989203327122L) ^ 20948981623382L;
      NEVER = new w3("NEVER", 0, "never");
      WHEN_CAUSED_BY_LIVING_NON_PLAYER = new w3("WHEN_CAUSED_BY_LIVING_NON_PLAYER", 1, "when_caused_by_living_non_player");
      ALWAYS = new w3("ALWAYS", 2, "always");
      J = T();
      ID_INDEX = l3.Q(w3.class, w3::E);
   }
}
