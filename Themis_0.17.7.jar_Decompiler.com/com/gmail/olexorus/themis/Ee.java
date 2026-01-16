package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum EE {
   SEQUENCE,
   AUTO,
   REDSTONE;

   private static final EE[] Q;
   private static final EE[] N;

   public static EE j(int var0) {
      return Q[var0];
   }

   private static EE[] G() {
      return new EE[]{SEQUENCE, AUTO, REDSTONE};
   }

   static {
      long var0 = kt.a(-836088175256522396L, -3710502308722790654L, MethodHandles.lookup().lookupClass()).a(19300721046569L) ^ 107704739168471L;
      SEQUENCE = new EE("SEQUENCE", 0);
      AUTO = new EE("AUTO", 1);
      REDSTONE = new EE("REDSTONE", 2);
      N = G();
      Q = values();
   }
}
