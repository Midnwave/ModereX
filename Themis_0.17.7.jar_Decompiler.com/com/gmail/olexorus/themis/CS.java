package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum cs {
   DOWN,
   UP,
   NORTH,
   SOUTH,
   WEST,
   EAST,
   OTHER;

   private static final cs[] U;
   private static final cs[] t;
   final short L;
   final int P;
   final int S;
   final int q;

   private cs(short var3, int var4, int var5, int var6) {
      this.L = var3;
      this.P = var4;
      this.S = var5;
      this.q = var6;
   }

   private cs(int var3, int var4, int var5) {
      this.L = (short)this.ordinal();
      this.P = var3;
      this.S = var4;
      this.q = var5;
   }

   public static cs c(int var0) {
      return var0 == 255 ? OTHER : t[var0 % t.length];
   }

   public static cs f(int var0) {
      return t[var0 % t.length];
   }

   public short i() {
      return this.L;
   }

   // $FF: synthetic method
   private static cs[] k() {
      return new cs[]{DOWN, UP, NORTH, SOUTH, WEST, EAST, OTHER};
   }

   static {
      long var0 = kt.a(6354059151564648645L, 789223165877091787L, MethodHandles.lookup().lookupClass()).a(253597609631657L) ^ 69365954100551L;
      DOWN = new cs("DOWN", 0, 0, -1, 0);
      UP = new cs("UP", 1, 0, 1, 0);
      NORTH = new cs("NORTH", 2, 0, 0, -1);
      SOUTH = new cs("SOUTH", 3, 0, 0, 1);
      WEST = new cs("WEST", 4, -1, 0, 0);
      EAST = new cs("EAST", 5, 1, 0, 0);
      OTHER = new cs("OTHER", 6, (short)255, -1, -1, -1);
      U = values();
      t = new cs[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
   }
}
