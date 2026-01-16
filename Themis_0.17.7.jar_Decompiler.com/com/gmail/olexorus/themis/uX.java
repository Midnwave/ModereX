package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum ux implements rA, B5 {
   OBFUSCATED,
   BOLD,
   STRIKETHROUGH,
   UNDERLINED,
   ITALIC;

   public static final l3<String, ux> NAMES;
   private final String Q;
   private static final ux[] W;

   private ux(String var3) {
      this.Q = var3;
   }

   public String toString() {
      return this.Q;
   }

   private static String lambda$static$0(ux var0) {
      return var0.Q;
   }

   private static ux[] t() {
      return new ux[]{OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINED, ITALIC};
   }

   static {
      long var0 = kt.a(3315478017956716399L, -2857941981000015130L, MethodHandles.lookup().lookupClass()).a(185059007449349L) ^ 79784696693059L;
      OBFUSCATED = new ux("OBFUSCATED", 0, "obfuscated");
      BOLD = new ux("BOLD", 1, "bold");
      STRIKETHROUGH = new ux("STRIKETHROUGH", 2, "strikethrough");
      UNDERLINED = new ux("UNDERLINED", 3, "underlined");
      ITALIC = new ux("ITALIC", 4, "italic");
      W = t();
      NAMES = l3.Q(ux.class, ux::lambda$static$0);
   }
}
