package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum EG implements bA<Boolean, Boolean> {
   AND,
   NAND,
   OR,
   NOR,
   XOR,
   XNOR;

   private static final EG[] C;

   private EG() {
   }

   public tw<Boolean> F(Jf<Boolean> var1) {
      return g9.k;
   }

   private static EG[] c() {
      return new EG[]{AND, NAND, OR, NOR, XOR, XNOR};
   }

   EG(ER var3) {
      this();
   }

   static {
      long var0 = kt.a(1314241251705619111L, -3518726235302695705L, MethodHandles.lookup().lookupClass()).a(210598369462510L) ^ 25260916201715L;
      AND = new ER("AND", 0);
      NAND = new En("NAND", 1);
      OR = new Em("OR", 2);
      NOR = new E8("NOR", 3);
      XOR = new Ek("XOR", 4);
      XNOR = new EN("XNOR", 5);
      C = c();
   }
}
