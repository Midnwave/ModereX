package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

enum lI {
   UNKNOWN,
   DISABLED,
   ENABLED;

   // $FF: synthetic method
   private static lI[] P() {
      return new lI[]{UNKNOWN, DISABLED, ENABLED};
   }

   static {
      long var0 = kt.a(1431901146420789004L, 2129223543281736548L, MethodHandles.lookup().lookupClass()).a(244517175847301L) ^ 37369466354975L;
      UNKNOWN = new lI("UNKNOWN", 0);
      DISABLED = new lI("DISABLED", 1);
      ENABLED = new lI("ENABLED", 2);
   }
}
