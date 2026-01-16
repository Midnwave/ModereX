package com.gmail.olexorus.themis;

public class z9 {
   private final int S;
   private int t;
   private final boolean g;
   private final boolean D;
   private byte[] s;

   public z9(int var1, boolean var2, boolean var3, byte[] var4) {
      this.S = var1;
      this.g = var2;
      this.D = var3;
      this.s = var4;
   }

   public z9(int var1, int var2, boolean var3, boolean var4, byte[] var5) {
      this(var1, var3, var4, var5);
      this.t = var2;
   }

   public int i() {
      return this.S;
   }

   public int q() {
      return this.t;
   }

   public boolean q() {
      return this.D;
   }

   public byte[] u() {
      return this.s;
   }
}
