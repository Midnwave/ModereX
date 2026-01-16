package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum V8 {
   SET_SIZE,
   LERP_SIZE,
   SET_CENTER,
   INITIALIZE,
   SET_WARNING_TIME,
   SET_WARNING_BLOCKS;

   private static final V8[] I;

   private static V8[] G() {
      return new V8[]{SET_SIZE, LERP_SIZE, SET_CENTER, INITIALIZE, SET_WARNING_TIME, SET_WARNING_BLOCKS};
   }

   static {
      long var0 = kt.a(-4393587607667625497L, -1533112258848857940L, MethodHandles.lookup().lookupClass()).a(235152233506139L) ^ 133672867798124L;
      SET_SIZE = new V8("SET_SIZE", 0);
      LERP_SIZE = new V8("LERP_SIZE", 1);
      SET_CENTER = new V8("SET_CENTER", 2);
      INITIALIZE = new V8("INITIALIZE", 3);
      SET_WARNING_TIME = new V8("SET_WARNING_TIME", 4);
      SET_WARNING_BLOCKS = new V8("SET_WARNING_BLOCKS", 5);
      I = G();
   }
}
