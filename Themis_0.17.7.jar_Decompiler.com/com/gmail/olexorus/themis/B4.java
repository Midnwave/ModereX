package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum b4 {
   NONE,
   LEFT_RIGHT,
   FRONT_BACK;

   // $FF: synthetic method
   private static b4[] e() {
      return new b4[]{NONE, LEFT_RIGHT, FRONT_BACK};
   }

   static {
      long var0 = kt.a(5424917819988989068L, -8584391182099731518L, MethodHandles.lookup().lookupClass()).a(267198172595916L) ^ 101295303474750L;
      NONE = new b4("NONE", 0);
      LEFT_RIGHT = new b4("LEFT_RIGHT", 1);
      FRONT_BACK = new b4("FRONT_BACK", 2);
   }
}
