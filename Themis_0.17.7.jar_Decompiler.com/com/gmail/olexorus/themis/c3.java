package com.gmail.olexorus.themis;

import java.util.HashMap;

public class C3 implements n5 {
   private final int E;
   private final int[] k;
   private final HashMap<Object, Integer> I;
   private int j;

   public C3(int var1) {
      this.I = new HashMap();
      this.j = 0;
      this.E = var1;
      this.k = new int[1 << var1];
   }

   public C3(int var1, Br var2) {
      this(var1);
      int var3 = var2.G();

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2.G();
         this.k[var4] = var5;
         this.I.putIfAbsent(var5, var4);
      }

      this.j = var3;
   }

   public int f() {
      return this.j;
   }

   public int e(int var1) {
      return var1 >= 0 && var1 < this.f() ? this.k[var1] : 0;
   }
}
