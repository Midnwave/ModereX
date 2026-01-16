package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum bK implements wC, CA {
   CLIENT_SETTINGS,
   PLUGIN_MESSAGE,
   CONFIGURATION_END_ACK,
   KEEP_ALIVE,
   PONG,
   RESOURCE_PACK_STATUS,
   COOKIE_RESPONSE,
   SELECT_KNOWN_PACKS,
   CUSTOM_CLICK_ACTION,
   ACCEPT_CODE_OF_CONDUCT;

   private static int B;
   private static final Map<Byte, Map<Integer, wC>> p;
   private final int[] e = new int[O5.v().x().length];
   private final Class<? extends lm<?>> w;
   private static final bK[] K;

   private bK(Class<? extends lm<?>> var3) {
      Arrays.fill(this.e, -1);
      this.w = var3;
   }

   public static void v() {
      B = 0;
      f(R0.values());
      f(VS.values());
      f(lR.values());
      f(MQ.values());
   }

   private static void f(Enum<?>[] var0) {
      int var1 = B;
      Enum[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Enum var5 = var2[var4];
         int var6 = var5.ordinal();
         bK var7 = valueOf(var5.name());
         var7.e[var1] = var6;
         Map var8 = (Map)p.computeIfAbsent((byte)var1, bK::lambda$loadPacketIds$0);
         var8.put(var6, var7);
      }

      ++B;
   }

   public static wC b(vL var0, int var1) {
      if (!O5.q()) {
         O5.u();
      }

      int var2 = O5.v().H(var0);
      Map var3 = (Map)p.get((byte)var2);
      return (wC)var3.get(var1);
   }

   public int d(vL var1) {
      if (!O5.q()) {
         O5.u();
      }

      int var2 = O5.v().H(var1);
      return this.e[var2];
   }

   public RW R() {
      return RW.CLIENT;
   }

   private static Map lambda$loadPacketIds$0(Byte var0) {
      return new HashMap();
   }

   private static bK[] i() {
      return new bK[]{CLIENT_SETTINGS, PLUGIN_MESSAGE, CONFIGURATION_END_ACK, KEEP_ALIVE, PONG, RESOURCE_PACK_STATUS, COOKIE_RESPONSE, SELECT_KNOWN_PACKS, CUSTOM_CLICK_ACTION, ACCEPT_CODE_OF_CONDUCT};
   }

   static {
      long var0 = kt.a(-8865127584766151409L, 7565967283761325773L, MethodHandles.lookup().lookupClass()).a(32051310042860L) ^ 55470302989114L;
      CLIENT_SETTINGS = new bK("CLIENT_SETTINGS", 0, ls.class);
      PLUGIN_MESSAGE = new bK("PLUGIN_MESSAGE", 1, Zp.class);
      CONFIGURATION_END_ACK = new bK("CONFIGURATION_END_ACK", 2, Zu.class);
      KEEP_ALIVE = new bK("KEEP_ALIVE", 3, Ze.class);
      PONG = new bK("PONG", 4, Zx.class);
      RESOURCE_PACK_STATUS = new bK("RESOURCE_PACK_STATUS", 5, Z4.class);
      COOKIE_RESPONSE = new bK("COOKIE_RESPONSE", 6, lq.class);
      SELECT_KNOWN_PACKS = new bK("SELECT_KNOWN_PACKS", 7, ZX.class);
      CUSTOM_CLICK_ACTION = new bK("CUSTOM_CLICK_ACTION", 8, lk.class);
      ACCEPT_CODE_OF_CONDUCT = new bK("ACCEPT_CODE_OF_CONDUCT", 9, ZJ.class);
      K = i();
      B = 0;
      p = new HashMap();
   }
}
