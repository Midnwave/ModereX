package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum Bp implements ou, gh {
   DISCONNECT,
   ENCRYPTION_REQUEST,
   LOGIN_SUCCESS,
   SET_COMPRESSION,
   LOGIN_PLUGIN_REQUEST,
   COOKIE_REQUEST;

   private final int j;
   private final Class<? extends lm<?>> k;
   private static final Bp[] U;

   private Bp(int var3, Class<? extends lm<?>> var4) {
      this.j = var3;
      this.k = var4;
   }

   public static wC d(int var0) {
      switch(var0) {
      case 0:
         return DISCONNECT;
      case 1:
         return ENCRYPTION_REQUEST;
      case 2:
         return LOGIN_SUCCESS;
      case 3:
         return SET_COMPRESSION;
      case 4:
         return LOGIN_PLUGIN_REQUEST;
      case 5:
         return COOKIE_REQUEST;
      default:
         return null;
      }
   }

   public int d() {
      return this.j;
   }

   public RW R() {
      return RW.SERVER;
   }

   private static Bp[] J() {
      return new Bp[]{DISCONNECT, ENCRYPTION_REQUEST, LOGIN_SUCCESS, SET_COMPRESSION, LOGIN_PLUGIN_REQUEST, COOKIE_REQUEST};
   }

   static {
      long var0 = kt.a(-170440416448194294L, -7290267724575092631L, MethodHandles.lookup().lookupClass()).a(246263402566303L) ^ 95730321708623L;
      DISCONNECT = new Bp("DISCONNECT", 0, 0, ZV.class);
      ENCRYPTION_REQUEST = new Bp("ENCRYPTION_REQUEST", 1, 1, Zy.class);
      LOGIN_SUCCESS = new Bp("LOGIN_SUCCESS", 2, 2, ZI.class);
      SET_COMPRESSION = new Bp("SET_COMPRESSION", 3, 3, Zr.class);
      LOGIN_PLUGIN_REQUEST = new Bp("LOGIN_PLUGIN_REQUEST", 4, 4, Zi.class);
      COOKIE_REQUEST = new Bp("COOKIE_REQUEST", 5, 5, ZY.class);
      U = J();
   }
}
