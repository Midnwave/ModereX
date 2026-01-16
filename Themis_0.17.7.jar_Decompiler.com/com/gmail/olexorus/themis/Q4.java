package com.gmail.olexorus.themis;

import java.util.Optional;

public class Q4 extends lm<Q4> {
   private TO A;
   private Optional<String> b;
   private aX K;
   private long M;
   private zE O;
   private zE N;
   private boolean j;
   private boolean w;
   private byte W;
   private Nt e;
   private Integer m;
   private int H;
   private String Q;

   public Q4(aE var1) {
      super(var1);
   }

   public void t() {
      boolean var1 = this.I.i(zZ.V_1_14);
      boolean var2 = this.I.i(zZ.V_1_15);
      boolean var3 = this.I.i(zZ.V_1_16);
      boolean var4 = this.I.i(zZ.V_1_19);
      boolean var5 = this.I.i(zZ.V_1_19_3);
      boolean var6 = this.I.i(zZ.V_1_20_2);
      this.A = TO.p(this);
      if (var3) {
         this.b = Optional.of(this.A());
         this.M = this.k();
         if (var6) {
            this.O = this.S();
         } else {
            this.O = zE.o(this.h());
         }

         this.N = this.S();
         this.j = this.P();
         this.w = this.P();
         if (var5) {
            if (!var6) {
               this.W = this.M();
            }
         } else {
            this.W = (byte)(this.P() ? 3 : 2);
         }

         if (var4) {
            this.e = (Nt)this.u(lm::E);
         }

         if (this.I.i(zZ.V_1_20)) {
            this.m = this.Q();
         }

         if (this.I.i(zZ.V_1_21_2)) {
            this.H = this.Q();
         }

         if (var6) {
            this.W = this.M();
         }
      } else {
         this.b = Optional.empty();
         this.M = 0L;
         if (var2) {
            this.M = this.k();
         } else if (!var1) {
            this.K = aX.B(this.M());
         }

         this.O = zE.o(this.M());
         this.Q = this.m(16);
         this.w = wf.d(this.Q);
         this.j = wf.j(this.Q);
      }

   }

   public void d() {
      boolean var1 = this.I.i(zZ.V_1_14);
      boolean var2 = this.I.i(zZ.V_1_15);
      boolean var3 = this.I.i(zZ.V_1_16);
      boolean var4 = this.I.i(zZ.V_1_19);
      boolean var5 = this.I.i(zZ.V_1_19_3);
      boolean var6 = this.I.i(zZ.V_1_20_2);
      TO.x(this, this.A);
      int var7;
      if (var3) {
         this.I((String)this.b.orElse(""));
         this.A(this.M);
         this.O(this.O);
         this.O(this.N);
         this.I(this.j);
         this.I(this.w);
         if (var5) {
            if (!var6) {
               this.u(this.W);
            }
         } else {
            this.I((this.W & 1) != 0);
         }

         if (var4) {
            this.l(this.e, lm::c);
         }

         if (this.I.i(zZ.V_1_20)) {
            var7 = this.m != null ? this.m : 0;
            this.E(var7);
         }

         if (this.I.i(zZ.V_1_21_2)) {
            this.E(this.H);
         }

         if (var6) {
            this.u(this.W);
         }
      } else {
         if (var2) {
            this.A(this.M);
         } else if (!var1) {
            var7 = this.K == null ? aX.NORMAL.W() : this.K.W();
            this.u(var7);
         }

         this.u(this.O.ordinal());
         if (this.w) {
            this.I(RP.FLAT.w());
         } else if (this.j) {
            this.I(RP.DEBUG_ALL_BLOCK_STATES.w());
         } else {
            this.a(this.Q == null ? RP.DEFAULT.w() : this.Q, 16);
         }
      }

   }

   public void l(Q4 var1) {
      this.A = var1.A;
      this.b = var1.b;
      this.K = var1.K;
      this.M = var1.M;
      this.O = var1.O;
      this.N = var1.N;
      this.j = var1.j;
      this.w = var1.w;
      this.W = var1.W;
      this.e = var1.e;
      this.m = var1.m;
      this.H = var1.H;
      this.Q = var1.Q;
   }

   public AR c() {
      VD var1 = this.w(BW.N());
      return this.A.C(var1, this);
   }
}
