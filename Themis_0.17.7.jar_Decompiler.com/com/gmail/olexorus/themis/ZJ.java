package com.gmail.olexorus.themis;

public class Zj extends lm<Zj> {
   private int B;
   private vL j;
   private String E;
   private int T;
   private Md K;

   public Zj(aw var1) {
      super(var1);
   }

   public void t() {
      try {
         this.B = this.Q();
         this.j = vL.e(this.B);
         this.E = this.m(32767);
         this.T = this.C();
         int var1 = this.Q();
         this.K = Md.h(var1);
      } catch (Exception var2) {
         throw new Ta();
      }
   }

   public void d() {
      this.E(this.B);
      this.a(this.E, 32767);
      this.f(this.T);
      this.E(this.K.t());
   }

   public void G(Zj var1) {
      this.B = var1.B;
      this.j = var1.j;
      this.E = var1.E;
      this.T = var1.T;
      this.K = var1.K;
   }

   public vL K() {
      return this.j;
   }

   public uy u() {
      return this.K.K();
   }
}
