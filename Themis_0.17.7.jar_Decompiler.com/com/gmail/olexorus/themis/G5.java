package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum g5 {
   SAVE,
   LOAD,
   CORNER,
   DATA;

   private static final g5[] Z;

   private static g5[] R() {
      return new g5[]{SAVE, LOAD, CORNER, DATA};
   }

   static {
      long var0 = kt.a(-5948698709832698897L, -1851614758660256531L, MethodHandles.lookup().lookupClass()).a(230239240813477L) ^ 43595633149463L;
      SAVE = new g5("SAVE", 0);
      LOAD = new g5("LOAD", 1);
      CORNER = new g5("CORNER", 2);
      DATA = new g5("DATA", 3);
      Z = R();
   }
}
