package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum W9 implements H {
   NONE,
   TAIGA,
   EXTREME_HILLS,
   JUNGLE,
   MESA,
   PLAINS,
   SAVANNA,
   ICY,
   THE_END,
   BEACH,
   FOREST,
   OCEAN,
   DESERT,
   RIVER,
   SWAMP,
   MUSHROOM,
   NETHER,
   UNDERGROUND,
   MOUNTAIN;

   public static final tw<W9> CODEC;
   public static final l3<String, W9> ID_INDEX;
   private final String g;
   private static final W9[] C;

   private W9(String var3) {
      this.g = var3;
   }

   public String n() {
      return this.g;
   }

   public String g() {
      return this.g;
   }

   private static W9[] X() {
      return new W9[]{NONE, TAIGA, EXTREME_HILLS, JUNGLE, MESA, PLAINS, SAVANNA, ICY, THE_END, BEACH, FOREST, OCEAN, DESERT, RIVER, SWAMP, MUSHROOM, NETHER, UNDERGROUND, MOUNTAIN};
   }

   static {
      long var0 = kt.a(-5699504434575919091L, 6248491948546015535L, MethodHandles.lookup().lookupClass()).a(153223987250182L) ^ 103512403471596L;
      NONE = new W9("NONE", 0, "none");
      TAIGA = new W9("TAIGA", 1, "taiga");
      EXTREME_HILLS = new W9("EXTREME_HILLS", 2, "extreme_hills");
      JUNGLE = new W9("JUNGLE", 3, "jungle");
      MESA = new W9("MESA", 4, "mesa");
      PLAINS = new W9("PLAINS", 5, "plains");
      SAVANNA = new W9("SAVANNA", 6, "savanna");
      ICY = new W9("ICY", 7, "icy");
      THE_END = new W9("THE_END", 8, "the_end");
      BEACH = new W9("BEACH", 9, "beach");
      FOREST = new W9("FOREST", 10, "forest");
      OCEAN = new W9("OCEAN", 11, "ocean");
      DESERT = new W9("DESERT", 12, "desert");
      RIVER = new W9("RIVER", 13, "river");
      SWAMP = new W9("SWAMP", 14, "swamp");
      MUSHROOM = new W9("MUSHROOM", 15, "mushroom");
      NETHER = new W9("NETHER", 16, "nether");
      UNDERGROUND = new W9("UNDERGROUND", 17, "underground");
      MOUNTAIN = new W9("MOUNTAIN", 18, "mountain");
      C = X();
      CODEC = g9.c(values());
      ID_INDEX = l3.Q(W9.class, W9::n);
   }
}
