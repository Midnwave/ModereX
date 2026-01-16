package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum u_ {
   INTERACT,
   ATTACK,
   INTERACT_AT;

   public static final u_[] VALUES;
   private static final u_[] Z;

   private static u_[] j() {
      return new u_[]{INTERACT, ATTACK, INTERACT_AT};
   }

   static {
      long var0 = kt.a(9093720321905448964L, -8090684376854808604L, MethodHandles.lookup().lookupClass()).a(15011411365220L) ^ 38546597055445L;
      INTERACT = new u_("INTERACT", 0);
      ATTACK = new u_("ATTACK", 1);
      INTERACT_AT = new u_("INTERACT_AT", 2);
      Z = j();
      VALUES = values();
   }
}
