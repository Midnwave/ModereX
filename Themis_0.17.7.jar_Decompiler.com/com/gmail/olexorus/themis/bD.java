package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public enum BD {
   KEBAB,
   AZTEC,
   ALBAN,
   AZTEC2,
   BOMB,
   PLANT,
   WASTELAND,
   POOL,
   COURBET,
   SEA,
   SUNSET,
   CREEBET,
   WANDERER,
   GRAHAM,
   MATCH,
   BUST,
   STAGE,
   VOID,
   SKULL_AND_ROSES,
   WITHER,
   FIGHTERS,
   POINTER,
   PIG_SCENE,
   BURNING_SKULL,
   SKELETON,
   DONKEY_KONG,
   EARTH,
   WIND,
   WATER,
   FIRE;

   private final String q;
   private final int J;
   private final int i;
   private final int U;
   private static final Map<String, BD> H;
   private static final BD[] C;
   private static final BD[] E;

   private BD(String var3, int var4, int var5, int var6) {
      this.q = var3;
      this.J = var4;
      this.i = var5;
      this.U = var6;
   }

   public static BD r(int var0) {
      return C[var0];
   }

   public static BD i(String var0) {
      BD var1 = (BD)H.get(var0);
      if (var1 == null) {
         BD[] var2 = C;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            BD var5 = var2[var4];
            if (var5.q.equals(var0)) {
               H.put(var0, var5);
               return var5;
            }
         }
      }

      return var1;
   }

   public String I() {
      return this.q;
   }

   public int J() {
      return this.J;
   }

   private static BD[] G() {
      return new BD[]{KEBAB, AZTEC, ALBAN, AZTEC2, BOMB, PLANT, WASTELAND, POOL, COURBET, SEA, SUNSET, CREEBET, WANDERER, GRAHAM, MATCH, BUST, STAGE, VOID, SKULL_AND_ROSES, WITHER, FIGHTERS, POINTER, PIG_SCENE, BURNING_SKULL, SKELETON, DONKEY_KONG, EARTH, WIND, WATER, FIRE};
   }

   static {
      long var0 = kt.a(1579602839416310504L, 4763801012218640099L, MethodHandles.lookup().lookupClass()).a(54255245962874L) ^ 37263086783670L;
      KEBAB = new BD("KEBAB", 0, "Kebab", 0, 1, 1);
      AZTEC = new BD("AZTEC", 1, "Aztec", 1, 1, 1);
      ALBAN = new BD("ALBAN", 2, "Alban", 2, 1, 1);
      AZTEC2 = new BD("AZTEC2", 3, "Aztec2", 3, 1, 1);
      BOMB = new BD("BOMB", 4, "Bomb", 4, 1, 1);
      PLANT = new BD("PLANT", 5, "Plant", 5, 1, 1);
      WASTELAND = new BD("WASTELAND", 6, "Wasteland", 6, 1, 1);
      POOL = new BD("POOL", 7, "Pool", 7, 2, 1);
      COURBET = new BD("COURBET", 8, "Courbet", 8, 2, 1);
      SEA = new BD("SEA", 9, "Sea", 9, 2, 1);
      SUNSET = new BD("SUNSET", 10, "Sunset", 10, 2, 1);
      CREEBET = new BD("CREEBET", 11, "Creebet", 11, 2, 1);
      WANDERER = new BD("WANDERER", 12, "Wanderer", 12, 1, 2);
      GRAHAM = new BD("GRAHAM", 13, "Graham", 13, 1, 2);
      MATCH = new BD("MATCH", 14, "Match", 14, 2, 2);
      BUST = new BD("BUST", 15, "Bust", 15, 2, 2);
      STAGE = new BD("STAGE", 16, "Stage", 16, 2, 2);
      VOID = new BD("VOID", 17, "Void", 17, 2, 2);
      SKULL_AND_ROSES = new BD("SKULL_AND_ROSES", 18, "SkullAndRoses", 18, 2, 2);
      WITHER = new BD("WITHER", 19, "Wither", 19, 2, 2);
      FIGHTERS = new BD("FIGHTERS", 20, "Fighters", 20, 4, 2);
      POINTER = new BD("POINTER", 21, "Pointer", 21, 4, 4);
      PIG_SCENE = new BD("PIG_SCENE", 22, "Pigscene", 22, 4, 4);
      BURNING_SKULL = new BD("BURNING_SKULL", 23, "BurningSkull", 23, 4, 4);
      SKELETON = new BD("SKELETON", 24, "Skeleton", 24, 4, 3);
      DONKEY_KONG = new BD("DONKEY_KONG", 25, "DonkeyKong", 25, 4, 3);
      EARTH = new BD("EARTH", 26, "Earth", 26, 2, 2);
      WIND = new BD("WIND", 27, "Wind", 27, 2, 2);
      WATER = new BD("WATER", 28, "Water", 28, 2, 2);
      FIRE = new BD("FIRE", 29, "Fire", 29, 2, 2);
      E = G();
      H = new HashMap();
      C = values();
   }
}
