package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum J0 {
   TASK,
   CHALLENGE,
   GOAL;

   // $FF: synthetic method
   private static J0[] Z() {
      return new J0[]{TASK, CHALLENGE, GOAL};
   }

   static {
      long var0 = kt.a(3046480683284728736L, 7968216018982465100L, MethodHandles.lookup().lookupClass()).a(212778381826911L) ^ 86839926412911L;
      TASK = new J0("TASK", 0);
      CHALLENGE = new J0("CHALLENGE", 1);
      GOAL = new J0("GOAL", 2);
   }
}
