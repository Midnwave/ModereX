package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum rx {
   FULL,
   NONE,
   PARTIAL,
   UNSTABLE;

   // $FF: synthetic method
   private static rx[] c() {
      return new rx[]{FULL, NONE, PARTIAL, UNSTABLE};
   }

   static {
      long var0 = kt.a(-266873823509057951L, 4983233342565369132L, MethodHandles.lookup().lookupClass()).a(131075852958432L) ^ 100221752016559L;
      FULL = new rx("FULL", 0);
      NONE = new rx("NONE", 1);
      PARTIAL = new rx("PARTIAL", 2);
      UNSTABLE = new rx("UNSTABLE", 3);
   }
}
