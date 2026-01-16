package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class lE {
   private al i;
   private UUID q;
   private double K;
   private ON U;
   private static final long a = kt.a(4727419288945700454L, 6691475067069586410L, MethodHandles.lookup().lookupClass()).a(251134754758175L);

   public lE(al var1, UUID var2, double var3, ON var5) {
      this.i = var1;
      this.q = var2;
      this.K = var3;
      this.U = var5;
   }

   public static UUID v(al var0) {
      long var1 = a ^ 134449782365405L;
      String var3 = "packetevents_" + var0.toString();
      return UUID.nameUUIDFromBytes(var3.getBytes(StandardCharsets.UTF_8));
   }

   public al W() {
      return this.i;
   }

   public UUID A() {
      return this.q;
   }

   static al B(lE var0) {
      return var0.i;
   }

   static UUID G(lE var0) {
      return var0.q;
   }

   static double P(lE var0) {
      return var0.K;
   }

   static ON p(lE var0) {
      return var0.U;
   }
}
