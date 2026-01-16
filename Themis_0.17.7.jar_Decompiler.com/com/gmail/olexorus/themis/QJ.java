package com.gmail.olexorus.themis;

import java.util.Optional;
import java.util.UUID;

public class Qj extends lm<Qj> {
   private int Q;
   private Optional<UUID> q;
   private Vf W;
   private V U;
   private float v;
   private float B;
   private float O;
   private int e;
   private Optional<V> x;

   public void t() {
      this.Q = this.Q();
      boolean var1 = this.I.i(zZ.V_1_9);
      boolean var2 = this.I.i(zZ.V_1_15);
      boolean var3 = this.I.i(zZ.V_1_19);
      if (var1) {
         this.q = Optional.of(this.V());
      } else {
         this.q = Optional.empty();
      }

      if (this.I.i(zZ.V_1_14)) {
         this.W = Gp.C(this.I.u(), this.Q());
      } else {
         byte var4 = this.M();
         this.W = Gp.N(this.I.u(), var4);
         if (this.W == null) {
            this.W = Gp.C(this.I.u(), var4);
         }
      }

      double var16;
      double var6;
      double var8;
      if (var1) {
         var16 = this.o();
         var6 = this.o();
         var8 = this.o();
      } else {
         var16 = (double)this.f() / 32.0D;
         var6 = (double)this.f() / 32.0D;
         var8 = (double)this.f() / 32.0D;
      }

      this.U = new V(var16, var6, var8);
      if (this.I.i(zZ.V_1_21_9)) {
         this.x = Optional.of(aR.G(this));
      }

      if (var2) {
         this.v = (float)this.M() / 0.7111111F;
         this.B = (float)this.M() / 0.7111111F;
      } else {
         this.B = (float)this.M() / 0.7111111F;
         this.v = (float)this.M() / 0.7111111F;
      }

      if (var3) {
         this.O = (float)this.M() / 0.7111111F;
         this.e = this.Q();
      } else {
         this.e = this.f();
      }

      if (this.I.m(zZ.V_1_21_9)) {
         if (!var1 && this.e <= 0) {
            this.x = Optional.empty();
         } else {
            double var10 = (double)this.x() / 8000.0D;
            double var12 = (double)this.x() / 8000.0D;
            double var14 = (double)this.x() / 8000.0D;
            this.x = Optional.of(new V(var10, var12, var14));
         }
      }

   }

   public void d() {
      this.E(this.Q);
      boolean var1 = this.I.i(zZ.V_1_9);
      boolean var2 = this.I.i(zZ.V_1_15);
      boolean var3 = this.I.i(zZ.V_1_19);
      if (var1) {
         this.y((UUID)this.q.orElse(new UUID(0L, 0L)));
      }

      if (this.I.i(zZ.V_1_14)) {
         this.E(this.W.f(this.I.u()));
      } else if (this.W.J(this.I.u()) != -1) {
         this.u(this.W.J(this.I.u()));
      } else {
         this.u(this.W.f(this.I.u()));
      }

      if (var1) {
         this.v(this.U.f);
         this.v(this.U.K);
         this.v(this.U.r);
      } else {
         this.L(a8.J(this.U.f * 32.0D));
         this.L(a8.J(this.U.K * 32.0D));
         this.L(a8.J(this.U.r * 32.0D));
      }

      if (this.I.i(zZ.V_1_21_9)) {
         aR.M(this, (V)this.x.orElse(V.g()));
      }

      if (var2) {
         this.u(a8.a(this.v * 0.7111111F));
         this.u(a8.a(this.B * 0.7111111F));
      } else {
         this.u(a8.a(this.B * 0.7111111F));
         this.u(a8.a(this.v * 0.7111111F));
      }

      if (var3) {
         this.u(a8.a(this.O * 0.7111111F));
         this.E(this.e);
      } else {
         this.L(this.e);
      }

      if (this.I.m(zZ.V_1_21_9) && (var1 || this.e > 0)) {
         V var4 = (V)this.x.orElse(new V(-1.0D, -1.0D, -1.0D));
         int var5 = (int)(var4.f * 8000.0D);
         int var6 = (int)(var4.K * 8000.0D);
         int var7 = (int)(var4.r * 8000.0D);
         this.f(var5);
         this.f(var6);
         this.f(var7);
      }

   }

   public void i(Qj var1) {
      this.Q = var1.Q;
      this.q = var1.q;
      this.W = var1.W;
      this.U = var1.U;
      this.v = var1.v;
      this.B = var1.B;
      this.O = var1.O;
      this.e = var1.e;
      this.x = var1.x;
   }
}
