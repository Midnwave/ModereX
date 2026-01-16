package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class Q_ extends lm<Q_> {
   private int i;
   private int G;
   private String W;
   private int N;
   private int q;
   private X s;
   private boolean e;
   private static final long a = kt.a(-2552145805730579677L, -6111164486085578612L, MethodHandles.lookup().lookupClass()).a(239138602479120L);

   public void t() {
      long var1 = a ^ 87096635521847L;
      if (!this.I.i(zZ.V_1_21_2) && !this.I.m(zZ.V_1_14)) {
         this.i = this.Q();
      } else {
         this.i = this.a();
      }

      if (this.I.R(zZ.V_1_7_10)) {
         this.G = this.h();
         this.s = this.x().c(this.m(32));
         this.N = this.h();
         this.e = this.P();
         if (this.G == 11) {
            this.q = this.f();
         }

      } else {
         if (this.I.i(zZ.V_1_14)) {
            this.G = this.Q();
            this.s = this.a();
         } else {
            this.W = this.A();
            this.s = this.a();
            this.N = this.h();
            if (this.W.equals("EntityHorse")) {
               this.q = this.f();
            }
         }

      }
   }

   public void d() {
      long var1 = a ^ 18519393368988L;
      if (!this.I.i(zZ.V_1_21_2) && !this.I.m(zZ.V_1_14)) {
         this.E(this.i);
      } else {
         this.y(this.i);
      }

      if (this.I.R(zZ.V_1_7_10)) {
         this.u(this.G);
         this.I(this.x().v(this.s));
         this.u(this.N);
         this.I(this.e);
         if (this.G == 11) {
            this.L(this.q);
         }

      } else {
         if (this.I.i(zZ.V_1_14)) {
            this.E(this.G);
            this.G(this.s);
         } else {
            this.I(this.W);
            this.G(this.s);
            this.u(this.N);
            if (this.W.equals("EntityHorse")) {
               this.L(this.q);
            }
         }

      }
   }

   public void U(Q_ var1) {
      this.i = var1.i;
      this.G = var1.G;
      this.W = var1.W;
      this.N = var1.N;
      this.q = var1.q;
      this.s = var1.s;
      this.e = var1.e;
   }
}
