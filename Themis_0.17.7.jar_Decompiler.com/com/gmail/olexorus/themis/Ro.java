package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class rO {
   public static final tw<rO> e;
   private final al D;
   private static final long a = kt.a(-4732835692109937969L, 2095144063893352641L, MethodHandles.lookup().lookupClass()).a(305526779071L);

   public rO(al var1) {
      this.D = var1;
   }

   public static rO c(String var0) {
      long var1 = a ^ 10197307645511L;
      rO var3 = q(var0);
      if (var3 == null) {
         throw new IllegalArgumentException("Not a tag: " + var0);
      } else {
         return var3;
      }
   }

   public static rO q(String var0) {
      return !var0.isEmpty() && var0.charAt(0) == '#' ? new rO(new al(var0.substring(1))) : null;
   }

   public String toString() {
      return '#' + this.D.toString();
   }

   private static rO lambda$static$0(String var0) {
      long var1 = a ^ 37721254394565L;
      rO var3 = q(var0);
      if (var3 == null) {
         throw new to("Not a tag: " + var0);
      } else {
         return var3;
      }
   }

   static {
      e = g9.Q.K(rO::lambda$static$0, rO::toString);
   }
}
