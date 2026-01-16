package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum bC implements H {
   OVERRIDE,
   ALPHA_BLEND,
   ADD,
   SUBTRACT,
   MULTIPLY,
   BLEND_TO_GRAY,
   MINIMUM,
   MAXIMUM,
   AND,
   NAND,
   OR,
   NOR,
   XOR,
   XNOR;

   public static final tw<bC> CODEC;
   private final String j;
   private static final bC[] o;

   private bC(String var3) {
      this.j = var3;
   }

   public String g() {
      return this.j;
   }

   private static bC[] l() {
      return new bC[]{OVERRIDE, ALPHA_BLEND, ADD, SUBTRACT, MULTIPLY, BLEND_TO_GRAY, MINIMUM, MAXIMUM, AND, NAND, OR, NOR, XOR, XNOR};
   }

   static {
      long var0 = kt.a(-8007381598716787670L, 2363104801122621576L, MethodHandles.lookup().lookupClass()).a(260012682595184L) ^ 68176590272503L;
      OVERRIDE = new bC("OVERRIDE", 0, "override");
      ALPHA_BLEND = new bC("ALPHA_BLEND", 1, "alpha_blend");
      ADD = new bC("ADD", 2, "add");
      SUBTRACT = new bC("SUBTRACT", 3, "subtract");
      MULTIPLY = new bC("MULTIPLY", 4, "multiply");
      BLEND_TO_GRAY = new bC("BLEND_TO_GRAY", 5, "blend_to_gray");
      MINIMUM = new bC("MINIMUM", 6, "minimum");
      MAXIMUM = new bC("MAXIMUM", 7, "maximum");
      AND = new bC("AND", 8, "and");
      NAND = new bC("NAND", 9, "nand");
      OR = new bC("OR", 10, "or");
      NOR = new bC("NOR", 11, "nor");
      XOR = new bC("XOR", 12, "xor");
      XNOR = new bC("XNOR", 13, "xnor");
      o = l();
      CODEC = g9.c(values());
   }
}
