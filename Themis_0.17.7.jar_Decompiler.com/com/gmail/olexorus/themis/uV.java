package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

final class uv {
   public static final uv w;
   public static final Integer G;

   private uv() {
   }

   static {
      long var0 = kt.a(4476962653872058511L, 3433966088181290181L, MethodHandles.lookup().lookupClass()).a(245329023073522L) ^ 115624260268598L;
      w = new uv();

      Integer var3;
      try {
         Object var7 = Class.forName("android.os.Build$VERSION").getField("SDK_INT").get((Object)null);
         var3 = var7 instanceof Integer ? (Integer)var7 : null;
      } catch (Throwable var6) {
         var3 = null;
      }

      Integer var10000;
      if (var3 != null) {
         int var4 = ((Number)var3).intValue();
         boolean var5 = false;
         var10000 = var4 > 0 ? var3 : null;
      } else {
         var10000 = null;
      }

      G = var10000;
   }
}
