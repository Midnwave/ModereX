package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum um {
   DARKEN_SCREEN,
   PLAY_BOSS_MUSIC,
   CREATE_WORLD_FOG;

   public static final l3<String, um> NAMES;
   private final String f;
   private static final um[] P;

   private um(String var3) {
      this.f = var3;
   }

   private static String lambda$static$0(um var0) {
      return var0.f;
   }

   private static um[] p() {
      return new um[]{DARKEN_SCREEN, PLAY_BOSS_MUSIC, CREATE_WORLD_FOG};
   }

   static {
      long var0 = kt.a(-5496522129308252447L, 532006490094995815L, MethodHandles.lookup().lookupClass()).a(55989400735802L) ^ 16483888331844L;
      DARKEN_SCREEN = new um("DARKEN_SCREEN", 0, "darken_screen");
      PLAY_BOSS_MUSIC = new um("PLAY_BOSS_MUSIC", 1, "play_boss_music");
      CREATE_WORLD_FOG = new um("CREATE_WORLD_FOG", 2, "create_world_fog");
      P = p();
      NAMES = l3.Q(um.class, um::lambda$static$0);
   }
}
