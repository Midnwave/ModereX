package com.gmail.olexorus.themis;

public class ld {
   private X P;
   private Ml g;
   private zE R;
   private MM r;
   private int k;

   public ld(X var1, Ml var2, zE var3, MM var4, int var5) {
      this.P = var1;
      this.g = var2;
      this.R = var3;
      this.r = var4;
      this.k = var5;
   }

   public ld(X var1, Ml var2, zE var3, int var4) {
      this(var1, var2, var3, (MM)null, var4);
   }

   public MM I() {
      return this.r;
   }

   static X V(ld var0) {
      return var0.P;
   }

   static int c(ld var0) {
      return var0.k;
   }

   static Ml g(ld var0) {
      return var0.g;
   }

   static zE i(ld var0) {
      return var0.R;
   }
}
