package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ss extends lm<ss> {
   private int w;
   private List<aG> X;
   private int t;
   private int u;
   private boolean A;
   private boolean E;

   public void t() {
      this.w = this.I.i(zZ.V_1_21_2) ? this.a() : this.Q();
      if (this.I.i(zZ.V_1_19)) {
         this.X = this.j(lm::F);
      } else {
         int var1 = this.M() & 255;
         this.X = new ArrayList(var1);

         for(int var2 = 0; var2 < var1; ++var2) {
            this.X.add(this.F());
         }
      }

      this.t = this.Q();
      this.u = this.Q();
      this.A = this.P();
      this.E = this.P();
   }

   public void d() {
      if (this.I.i(zZ.V_1_21_2)) {
         this.y(this.w);
      } else {
         this.E(this.w);
      }

      if (this.I.i(zZ.V_1_19)) {
         this.D(this.X, lm::W);
      } else {
         this.u(this.X.size() & 255);
         Iterator var1 = this.X.iterator();

         while(var1.hasNext()) {
            aG var2 = (aG)var1.next();
            this.W(var2);
         }
      }

      this.E(this.t);
      this.E(this.u);
      this.I(this.A);
      this.I(this.E);
   }

   public void B(ss var1) {
      this.w = var1.w;
      this.X = var1.X;
      this.t = var1.t;
      this.u = var1.u;
      this.A = var1.A;
      this.E = var1.E;
   }
}
