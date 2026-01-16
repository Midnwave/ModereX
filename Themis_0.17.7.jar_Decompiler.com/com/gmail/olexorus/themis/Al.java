package com.gmail.olexorus.themis;

public class aL extends lm<aL> {
   private long R;

   public void t() {
      this.R = (Long)this.t(ty.NEWER_THAN_OR_EQUALS, zZ.V_1_19, lm::Q, lm::l);
   }

   public void d() {
      this.o(ty.NEWER_THAN_OR_EQUALS, zZ.V_1_19, this.R, aL::lambda$write$0, lm::X);
   }

   public void T(aL var1) {
      this.R = var1.R;
   }

   private static void lambda$write$0(lm var0, Long var1) {
      var0.E(Math.toIntExact(var1));
   }
}
