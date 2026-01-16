package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum tT {
   SMALL,
   LARGE;

   private static final tT[] z;

   private static tT[] a() {
      return new tT[]{SMALL, LARGE};
   }

   static {
      long var0 = kt.a(6410652824327927118L, -6148086111430646811L, MethodHandles.lookup().lookupClass()).a(261828716659990L) ^ 75677074630650L;
      SMALL = new tT("SMALL", 0);
      LARGE = new tT("LARGE", 1);
      z = a();
   }
}
