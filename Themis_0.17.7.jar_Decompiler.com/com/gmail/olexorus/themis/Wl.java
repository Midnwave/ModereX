package com.gmail.olexorus.themis;

public class WL extends WZ<WL> {
   private final CW g;

   public WL(CW var1) {
      super(gi.y);
      this.g = var1;
   }

   public static WL I(lm<?> var0) {
      CW var1 = CW.B(var0);
      return new WL(var1);
   }

   public static void X(lm<?> var0, WL var1) {
      CW.O(var0, var1.g);
   }
}
