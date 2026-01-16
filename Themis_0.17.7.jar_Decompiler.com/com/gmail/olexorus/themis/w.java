package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum W {
   ADD,
   REMOVE,
   SET;

   private static final W[] D;

   public static W K(int var0) {
      return D[var0];
   }

   // $FF: synthetic method
   private static W[] r() {
      return new W[]{ADD, REMOVE, SET};
   }

   static {
      long var0 = kt.a(5221644260184400450L, 1184118507573077163L, MethodHandles.lookup().lookupClass()).a(226111650301328L) ^ 66105705653428L;
      ADD = new W("ADD", 0);
      REMOVE = new W("REMOVE", 1);
      SET = new W("SET", 2);
      D = values();
   }
}
