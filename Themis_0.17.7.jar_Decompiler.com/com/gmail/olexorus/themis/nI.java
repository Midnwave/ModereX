package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class Ni {
   private static final long a = kt.a(6768638680589112559L, -3967784588140006460L, MethodHandles.lookup().lookupClass()).a(217148460227702L);

   public static PublicKey F(byte[] var0) {
      long var1 = a ^ 4047877332094L;

      try {
         X509EncodedKeySpec var3 = new X509EncodedKeySpec(var0);
         KeyFactory var4 = KeyFactory.getInstance("RSA");
         return var4.generatePublic(var3);
      } catch (Exception var5) {
         var5.printStackTrace();
         return null;
      }
   }
}
