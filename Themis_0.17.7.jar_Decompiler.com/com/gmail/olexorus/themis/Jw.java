package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum JW {
   FALSE,
   LOW,
   NONE,
   SIDE,
   TALL,
   TRUE,
   UP;

   // $FF: synthetic method
   private static JW[] b() {
      return new JW[]{FALSE, LOW, NONE, SIDE, TALL, TRUE, UP};
   }

   static {
      long var0 = kt.a(-7323436909324617024L, 4884670642519173734L, MethodHandles.lookup().lookupClass()).a(201741417246343L) ^ 45343140923676L;
      FALSE = new JW("FALSE", 0);
      LOW = new JW("LOW", 1);
      NONE = new JW("NONE", 2);
      SIDE = new JW("SIDE", 3);
      TALL = new JW("TALL", 4);
      TRUE = new JW("TRUE", 5);
      UP = new JW("UP", 6);
   }
}
