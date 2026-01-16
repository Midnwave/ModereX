package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum GP {
   HURT,
   THORNS,
   DROWNING,
   BURNING,
   POKING,
   FREEZING;

   public static final l3<String, GP> ID_INDEX;
   private final String H;
   private static final GP[] j;

   private GP(String var3) {
      this.H = var3;
   }

   public String h() {
      return this.H;
   }

   private static GP[] I() {
      return new GP[]{HURT, THORNS, DROWNING, BURNING, POKING, FREEZING};
   }

   static {
      long var0 = kt.a(7528694984021875585L, 1335656701238002637L, MethodHandles.lookup().lookupClass()).a(8421130264574L) ^ 117604756524420L;
      HURT = new GP("HURT", 0, "hurt");
      THORNS = new GP("THORNS", 1, "thorns");
      DROWNING = new GP("DROWNING", 2, "drowning");
      BURNING = new GP("BURNING", 3, "burning");
      POKING = new GP("POKING", 4, "poking");
      FREEZING = new GP("FREEZING", 5, "freezing");
      j = I();
      ID_INDEX = l3.Q(GP.class, GP::h);
   }
}
