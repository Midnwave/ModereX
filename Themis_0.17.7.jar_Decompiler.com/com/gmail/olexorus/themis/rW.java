package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum rw {
   BUILDING,
   REDSTONE,
   EQUIPMENT,
   MISC;

   private static final rw[] K;

   private static rw[] i() {
      return new rw[]{BUILDING, REDSTONE, EQUIPMENT, MISC};
   }

   static {
      long var0 = kt.a(-851274546925377819L, 8747006163490534766L, MethodHandles.lookup().lookupClass()).a(159366459347005L) ^ 55669050981696L;
      BUILDING = new rw("BUILDING", 0);
      REDSTONE = new rw("REDSTONE", 1);
      EQUIPMENT = new rw("EQUIPMENT", 2);
      MISC = new rw("MISC", 3);
      K = i();
   }
}
