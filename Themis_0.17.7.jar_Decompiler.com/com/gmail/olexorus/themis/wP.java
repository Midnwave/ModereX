package com.gmail.olexorus.themis;

public class Wp {
   private int b;
   private int p;

   public Wp(int var1, int var2) {
      this.b = var1;
      this.p = var2;
   }

   public int S() {
      return this.b;
   }

   public int C() {
      return this.p - this.b;
   }
}
