package com.gmail.olexorus.themis;

public class A1 implements g3 {
   private final Ev a;
   private MG L;
   private MG R;
   private MG u;
   private MG T;

   public A1(boolean var1, boolean var2) {
      this(new Ev(4096), new MG(4096), new MG(4096), var1 ? new MG(4096) : null, var2 ? new MG(4096) : null);
   }

   public A1(Ev var1, MG var2, MG var3, MG var4, MG var5) {
      this.a = var1;
      this.L = var2;
      this.R = var3;
      this.u = var4;
      this.T = var5;
   }

   public boolean B() {
      byte[] var1 = this.a.f();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         byte var4 = var1[var3];
         if (var4 != 0) {
            return false;
         }
      }

      return true;
   }

   public Ev U() {
      return this.a;
   }

   public MG T() {
      return this.L;
   }

   public MG F() {
      return this.R;
   }

   public MG X() {
      return this.u;
   }

   public MG b() {
      return this.T;
   }
}
