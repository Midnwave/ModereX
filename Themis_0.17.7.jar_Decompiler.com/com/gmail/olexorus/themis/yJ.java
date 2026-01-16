package com.gmail.olexorus.themis;

public class yj extends lm<yj> {
   private VC R;
   private VU t;
   private g5 p;
   private String h;
   private VC U;
   private VC W;
   private b4 A;
   private bZ z;
   private String T;
   private boolean v;
   private boolean S;
   private boolean Q;
   private boolean u;
   private float x;
   private long J;

   public void t() {
      this.R = this.M();
      this.t = (VU)this.i(VU.class);
      this.p = (g5)this.i(g5.class);
      this.h = this.A();
      int var1 = this.I.i(zZ.V_1_16_2) ? 48 : 32;
      this.U = new VC(a8.c(this.M(), -var1, var1), a8.c(this.M(), -var1, var1), a8.c(this.M(), -var1, var1));
      this.W = new VC(a8.c(this.M(), 0, var1), a8.c(this.M(), 0, var1), a8.c(this.M(), 0, var1));
      this.A = (b4)this.i(b4.class);
      this.z = (bZ)this.i(bZ.class);
      this.T = this.m(this.I.i(zZ.V_1_17) ? 128 : 12);
      this.x = a8.s(this.L(), 0.0F, 1.0F);
      this.J = this.l();
      byte var2 = this.M();
      this.v = (var2 & 1) != 0;
      this.S = (var2 & 8) != 0;
      this.Q = (var2 & 2) != 0;
      this.u = (var2 & 4) != 0;
   }

   public void d() {
      this.o(this.R);
      this.o(this.t);
      this.o(this.p);
      this.I(this.h);
      this.u(this.U.Q);
      this.u(this.U.R);
      this.u(this.U.e);
      this.u(this.W.Q);
      this.u(this.W.R);
      this.u(this.W.e);
      this.o(this.A);
      this.o(this.z);
      this.I(this.T);
      this.S(this.x);
      this.X(this.J);
      this.u(0 | (this.v ? 1 : 0) | (this.Q ? 2 : 0) | (this.u ? 4 : 0) | (this.S ? 8 : 0));
   }

   public void G(yj var1) {
      this.R = var1.R;
      this.t = var1.t;
      this.p = var1.p;
      this.h = var1.h;
      this.U = var1.U;
      this.W = var1.W;
      this.A = var1.A;
      this.z = var1.z;
      this.T = var1.T;
      this.v = var1.v;
      this.S = var1.S;
      this.Q = var1.Q;
      this.u = var1.u;
      this.x = var1.x;
      this.J = var1.J;
   }
}
