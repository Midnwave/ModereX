package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum c {
   CREATE,
   REMOVE,
   UPDATE;

   private static final c[] p;
   private static final c[] G;

   public static c B(int var0) {
      return p[var0];
   }

   private static c[] s() {
      return new c[]{CREATE, REMOVE, UPDATE};
   }

   static {
      long var0 = kt.a(-3082569661607963415L, -4898252445180985331L, MethodHandles.lookup().lookupClass()).a(21865672052427L) ^ 78540113399326L;
      CREATE = new c("CREATE", 0);
      REMOVE = new c("REMOVE", 1);
      UPDATE = new c("UPDATE", 2);
      G = s();
      p = values();
   }
}
