package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QM extends lm<QM> {
   private int V;
   private UUID L;
   private Vf J;
   private V d;
   private float N;
   private float x;
   private float f;
   private V K;
   private List<Tw<?>> e;

   public void t() {
      this.V = this.Q();
      int var1;
      if (this.I.m(zZ.V_1_9)) {
         this.L = new UUID(0L, 0L);
         var1 = this.M() & 255;
         this.J = Gp.C(this.I.u(), var1);
         this.d = new V((double)this.f() / 32.0D, (double)this.f() / 32.0D, (double)this.f() / 32.0D);
      } else {
         this.L = this.V();
         if (this.I.i(zZ.V_1_11)) {
            var1 = this.Q();
         } else {
            var1 = this.h();
         }

         this.J = Gp.C(this.I.u(), var1);
         this.d = new V(this.o(), this.o(), this.o());
      }

      this.N = (float)this.M() / 0.7111111F;
      this.x = (float)this.M() / 0.7111111F;
      this.f = (float)this.M() / 0.7111111F;
      double var7 = (double)this.x() / 8000.0D;
      double var3 = (double)this.x() / 8000.0D;
      double var5 = (double)this.x() / 8000.0D;
      this.K = new V(var7, var3, var5);
      if (this.I.m(zZ.V_1_15)) {
         this.e = this.n();
      } else {
         this.e = new ArrayList();
      }

   }

   public void d() {
      this.E(this.V);
      if (this.I.m(zZ.V_1_9)) {
         this.u(this.J.f(this.I.u()) & 255);
         this.L(a8.J(this.d.f * 32.0D));
         this.L(a8.J(this.d.K * 32.0D));
         this.L(a8.J(this.d.r * 32.0D));
      } else {
         this.y(this.L);
         if (this.I.i(zZ.V_1_11)) {
            this.E(this.J.f(this.I.u()));
         } else {
            this.u(this.J.f(this.I.u()) & 255);
         }

         this.v(this.d.f);
         this.v(this.d.K);
         this.v(this.d.r);
      }

      this.u((int)(this.N * 0.7111111F));
      this.u((int)(this.x * 0.7111111F));
      this.u((int)(this.f * 0.7111111F));
      this.f((int)(this.K.f * 8000.0D));
      this.f((int)(this.K.K * 8000.0D));
      this.f((int)(this.K.r * 8000.0D));
      if (this.I.m(zZ.V_1_15)) {
         this.R(this.e);
      }

   }

   public void m(QM var1) {
      this.V = var1.V;
      this.L = var1.L;
      this.J = var1.J;
      this.d = var1.d;
      this.N = var1.N;
      this.x = var1.x;
      this.f = var1.f;
      this.K = var1.K;
      this.e = var1.e;
   }
}
