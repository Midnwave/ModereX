package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum nr {
   ALWAYS,
   NEVER,
   PUSH_OTHER_TEAMS,
   PUSH_OWN_TEAM;

   private final String w;
   private static final nr[] d;

   private nr(String var3) {
      this.w = var3;
   }

   public static nr V(String var0) {
      nr[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         nr var4 = var1[var3];
         if (var4.w.equalsIgnoreCase(var0)) {
            return var4;
         }
      }

      return null;
   }

   public String a() {
      return this.w;
   }

   private static nr[] r() {
      return new nr[]{ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM};
   }

   static {
      long var0 = kt.a(-2642929412879130482L, 8405247753661574145L, MethodHandles.lookup().lookupClass()).a(262396902815003L) ^ 87022242825258L;
      ALWAYS = new nr("ALWAYS", 0, "always");
      NEVER = new nr("NEVER", 1, "never");
      PUSH_OTHER_TEAMS = new nr("PUSH_OTHER_TEAMS", 2, "pushOtherTeams");
      PUSH_OWN_TEAM = new nr("PUSH_OWN_TEAM", 3, "pushOwnTeam");
      d = r();
   }
}
