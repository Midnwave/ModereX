package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum o0 {
   START_DIGGING,
   CANCELLED_DIGGING,
   FINISHED_DIGGING,
   DROP_ITEM_STACK,
   DROP_ITEM,
   RELEASE_USE_ITEM,
   SWAP_ITEM_WITH_OFFHAND,
   STAB;

   private static final o0[] G;

   public int O() {
      return this.ordinal();
   }

   public static o0 d(int var0) {
      return G[var0];
   }

   // $FF: synthetic method
   private static o0[] D() {
      return new o0[]{START_DIGGING, CANCELLED_DIGGING, FINISHED_DIGGING, DROP_ITEM_STACK, DROP_ITEM, RELEASE_USE_ITEM, SWAP_ITEM_WITH_OFFHAND, STAB};
   }

   static {
      long var0 = kt.a(3827873271152117188L, -846324830510198874L, MethodHandles.lookup().lookupClass()).a(34112766290771L) ^ 12955183151541L;
      START_DIGGING = new o0("START_DIGGING", 0);
      CANCELLED_DIGGING = new o0("CANCELLED_DIGGING", 1);
      FINISHED_DIGGING = new o0("FINISHED_DIGGING", 2);
      DROP_ITEM_STACK = new o0("DROP_ITEM_STACK", 3);
      DROP_ITEM = new o0("DROP_ITEM", 4);
      RELEASE_USE_ITEM = new o0("RELEASE_USE_ITEM", 5);
      SWAP_ITEM_WITH_OFFHAND = new o0("SWAP_ITEM_WITH_OFFHAND", 6);
      STAB = new o0("STAB", 7);
      G = values();
   }
}
