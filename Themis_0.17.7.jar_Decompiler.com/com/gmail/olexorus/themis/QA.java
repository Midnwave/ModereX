package com.gmail.olexorus.themis;

public class Qa extends lm<Qa> {
   private int m;
   private bG S;
   private Gi v;
   private boolean V;

   public void t() {
      if (this.I.i(zZ.V_1_21_2)) {
         this.m = this.Q();
         this.S = bG.B(this);
         this.v = new Gi(this.f());
      } else {
         V var1 = com.gmail.olexorus.themis.V.c(this);
         float var2 = this.L();
         float var3 = this.L();
         this.S = new bG(var1, com.gmail.olexorus.themis.V.g(), var2, var3);
         this.v = new Gi(this.h());
         if (this.I.i(zZ.V_1_9)) {
            this.m = this.Q();
            if (this.I.i(zZ.V_1_17) && this.I.R(zZ.V_1_19_3)) {
               this.V = this.P();
            }
         }
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_21_2)) {
         this.E(this.m);
         bG.s(this, this.S);
         this.L(this.v.a());
      } else {
         com.gmail.olexorus.themis.V.q(this, this.S.V());
         this.S(this.S.K());
         this.S(this.S.m());
         this.u(this.v.a());
         if (this.I.i(zZ.V_1_9)) {
            this.E(this.m);
            if (this.I.i(zZ.V_1_17) && this.I.R(zZ.V_1_19_3)) {
               this.I(this.V);
            }
         }
      }

   }

   public void Z(Qa var1) {
      this.m = var1.m;
      this.S = var1.S;
      this.v = var1.v;
      this.V = var1.V;
   }
}
