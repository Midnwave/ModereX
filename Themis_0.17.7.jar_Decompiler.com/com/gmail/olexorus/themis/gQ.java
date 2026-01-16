package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public interface Gq {
   int z;
   int y;

   static Gq d() {
      return new TZ();
   }

   static Gq S(Object var0) {
      return g(var0, z);
   }

   static Gq g(Object var0, int var1) {
      return S(var0, var1, y);
   }

   static Gq S(Object var0, int var1, int var2) {
      return new th(var1, var0, var2);
   }

   void e(int var1);

   void r(int var1);

   void s();

   void d();

   static {
      long var0 = kt.a(-3772447208597171571L, 6455412544947246244L, MethodHandles.lookup().lookupClass()).a(244374187006762L) ^ 129234843224989L;
      z = Integer.getInteger("packetevents.nbt.default-max-size", 2097152);
      y = Integer.getInteger("packetevents.nbt.default-max-depth", 512);
   }
}
