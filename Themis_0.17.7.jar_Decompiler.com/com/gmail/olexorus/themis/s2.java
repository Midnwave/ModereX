package com.gmail.olexorus.themis;

import java.util.List;

public class s2 extends lm<s2> {
   private int T;
   private List<re> Q;

   public void t() {
      this.T = this.Q();
      this.Q = this.j(re::Q);
   }

   public void d() {
      this.E(this.T);
      this.D(this.Q, re::j);
   }

   public void E(s2 var1) {
      this.T = var1.T;
      this.Q = var1.Q;
   }
}
