package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

public final class la {
   private static final long a = kt.a(-6974187420970983069L, -2881919264197702970L, MethodHandles.lookup().lookupClass()).a(14419261143297L);

   public static UUID G(int[] var0) {
      long var1 = a ^ 84916095024912L;
      if (var0.length != 4) {
         throw new IllegalStateException("Invalid encoded uuid length: " + var0.length + " != 4");
      } else {
         return new UUID((long)var0[0] << 32 | (long)var0[1] & 4294967295L, (long)var0[2] << 32 | (long)var0[3] & 4294967295L);
      }
   }

   public static int[] P(UUID var0) {
      return new int[]{(int)(var0.getMostSignificantBits() >> 32), (int)var0.getMostSignificantBits(), (int)(var0.getLeastSignificantBits() >> 32), (int)var0.getLeastSignificantBits()};
   }
}
