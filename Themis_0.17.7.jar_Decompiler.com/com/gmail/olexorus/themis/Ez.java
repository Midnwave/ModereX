package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum EZ implements r9 {
   WHITE,
   ORANGE,
   MAGENTA,
   LIGHT_BLUE,
   YELLOW,
   LIME,
   PINK,
   GRAY,
   LIGHT_GRAY,
   CYAN,
   PURPLE,
   BLUE,
   BROWN,
   GREEN,
   RED,
   BLACK;

   private static final EZ[] V;
   private final v2 g;
   private final v2 Z;
   private final v2 v;
   private final int c;

   private EZ(v2 var3, v2 var4, v2 var5, int var6) {
      this.g = var3;
      this.Z = var4;
      this.v = var5;
      this.c = var6;
   }

   public static EZ b(lm<?> var0) {
      return (EZ)var0.w((Enum[])V);
   }

   public static void M(lm<?> var0, EZ var1) {
      var0.o((Enum)var1);
   }

   public int k() {
      return this.g.k();
   }

   public int v() {
      return this.g.v();
   }

   public int D() {
      return this.g.D();
   }

   // $FF: synthetic method
   private static EZ[] K() {
      return new EZ[]{WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK};
   }

   static {
      long var0 = kt.a(-4803299563087466588L, 8132737560909113710L, MethodHandles.lookup().lookupClass()).a(8932699796554L) ^ 128035399491420L;
      WHITE = new EZ("WHITE", 0, new v2(16383998), new v2(16777215), new v2(15790320), 8);
      ORANGE = new EZ("ORANGE", 1, new v2(16351261), new v2(16738335), new v2(15435844), 15);
      MAGENTA = new EZ("MAGENTA", 2, new v2(13061821), new v2(16711935), new v2(12801229), 16);
      LIGHT_BLUE = new EZ("LIGHT_BLUE", 3, new v2(3847130), new v2(10141901), new v2(6719955), 17);
      YELLOW = new EZ("YELLOW", 4, new v2(16701501), new v2(16776960), new v2(14602026), 18);
      LIME = new EZ("LIME", 5, new v2(8439583), new v2(12582656), new v2(4312372), 19);
      PINK = new EZ("PINK", 6, new v2(15961002), new v2(16738740), new v2(14188952), 20);
      GRAY = new EZ("GRAY", 7, new v2(4673362), new v2(8421504), new v2(4408131), 21);
      LIGHT_GRAY = new EZ("LIGHT_GRAY", 8, new v2(10329495), new v2(13882323), new v2(11250603), 22);
      CYAN = new EZ("CYAN", 9, new v2(1481884), new v2(65535), new v2(2651799), 23);
      PURPLE = new EZ("PURPLE", 10, new v2(8991416), new v2(10494192), new v2(8073150), 24);
      BLUE = new EZ("BLUE", 11, new v2(3949738), new v2(255), new v2(2437522), 25);
      BROWN = new EZ("BROWN", 12, new v2(8606770), new v2(9127187), new v2(5320730), 26);
      GREEN = new EZ("GREEN", 13, new v2(6192150), new v2(65280), new v2(3887386), 27);
      RED = new EZ("RED", 14, new v2(11546150), new v2(16711680), new v2(11743532), 28);
      BLACK = new EZ("BLACK", 15, new v2(1908001), new v2(0), new v2(1973019), 29);
      V = values();
   }
}
