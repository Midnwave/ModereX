package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum mc {
   CEILING,
   FLOOR,
   WALL;

   // $FF: synthetic method
   private static mc[] Q() {
      return new mc[]{CEILING, FLOOR, WALL};
   }

   static {
      long var0 = kt.a(4572359090391602529L, 1306184093128284802L, MethodHandles.lookup().lookupClass()).a(63462888772594L) ^ 129172935647472L;
      CEILING = new mc("CEILING", 0);
      FLOOR = new mc("FLOOR", 1);
      WALL = new mc("WALL", 2);
   }
}
