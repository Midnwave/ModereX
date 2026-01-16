package com.gmail.olexorus.themis;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class L {
   public static final long q(InputStream var0, OutputStream var1, int var2) {
      long var3 = 0L;
      byte[] var5 = new byte[var2];

      for(int var6 = var0.read(var5); var6 >= 0; var6 = var0.read(var5)) {
         var1.write(var5, 0, var6);
         var3 += (long)var6;
      }

      return var3;
   }

   // $FF: synthetic method
   public static long a(InputStream var0, OutputStream var1, int var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = 8192;
      }

      return q(var0, var1, var2);
   }

   public static final byte[] o(InputStream var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(Math.max(8192, var0.available()));
      a(var0, (OutputStream)var1, 0, 2, (Object)null);
      return var1.toByteArray();
   }
}
