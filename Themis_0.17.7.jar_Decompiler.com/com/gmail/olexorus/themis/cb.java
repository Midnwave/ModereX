package com.gmail.olexorus.themis;

import java.util.concurrent.ConcurrentHashMap.KeySetView;

public final class Cb {
   private static int v;

   private Cb() {
   }

   public final KeySetView g(Object[] var1) {
      return JH.L(new Object[0]);
   }

   public Cb(MH var1) {
      this();
   }

   public static void k(int var0) {
      v = var0;
   }

   public static int Q() {
      return v;
   }

   public static int P() {
      int var0 = Q();

      try {
         return var0 == 0 ? 64 : 0;
      } catch (RuntimeException var1) {
         throw a(var1);
      }
   }

   private static RuntimeException a(RuntimeException var0) {
      return var0;
   }

   static {
      if (P() != 0) {
         k(93);
      }

   }
}
