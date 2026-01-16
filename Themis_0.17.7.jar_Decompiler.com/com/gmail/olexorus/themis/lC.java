package com.gmail.olexorus.themis;

public abstract class lc<T extends lc<T>> extends lm<T> {
   private al A;
   private byte[] w;

   public void t() {
      this.A = this.R();
      this.w = (byte[])this.u(lc::lambda$read$0);
   }

   public void d() {
      this.T(this.A);
      this.l(this.w, lm::N);
   }

   public void S(T var1) {
      this.A = var1.v();
      this.w = var1.s();
   }

   public al v() {
      return this.A;
   }

   public byte[] s() {
      return this.w;
   }

   private static byte[] lambda$read$0(lm var0) {
      return var0.j(5120);
   }
}
