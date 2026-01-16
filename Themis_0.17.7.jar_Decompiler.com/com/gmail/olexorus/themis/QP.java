package com.gmail.olexorus.themis;

import java.util.UUID;

public class Qp extends lm<Qp> {
   private UUID L;

   public void t() {
      this.L = (UUID)this.u(lm::V);
   }

   public void d() {
      this.l(this.L, lm::y);
   }

   public void l(Qp var1) {
      this.L = var1.L;
   }
}
