package com.gmail.olexorus.themis;

public class Gm implements n5 {
   private final int R;
   private final int[] I;
   private int e;

   public Gm(int var1) {
      this.R = var1;
      this.I = new int[1 << var1];
      this.e = 0;
   }

   public Gm(int var1, Br var2) {
      this(var1);
      int var3 = var2.G();

      for(int var4 = 0; var4 < var3; ++var4) {
         this.I[var4] = var2.G();
      }

      this.e = var3;
   }

   public int f() {
      return this.e;
   }

   public int e(int var1) {
      return var1 >= 0 && var1 < this.f() ? this.I[var1] : 0;
   }
}
