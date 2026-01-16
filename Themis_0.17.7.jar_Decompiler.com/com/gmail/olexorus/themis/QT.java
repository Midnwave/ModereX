package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public class Qt extends lm<Qt> {
   private b8<?> K;
   private boolean a;
   private V R;
   private r3 i;
   private float Q;
   private int c;
   private boolean f;
   private static final long b = kt.a(6637227629585923482L, -2728630455598546001L, MethodHandles.lookup().lookupClass()).a(189969008824382L);

   public void t() {
      long var1 = b ^ 133809378958494L;
      int var3 = 0;
      VI var4 = null;
      boolean var5 = this.I.i(zZ.V_1_20_5);
      if (this.I.R(zZ.V_1_7_10)) {
         String var6 = this.m(64);
         var4 = R3.W("minecraft:" + var6);
      } else if (!var5) {
         var3 = this.I.i(zZ.V_1_19) ? this.Q() : this.f();
         var4 = R3.k(this.I.u(), var3);
      }

      this.a = this.P();
      if (this.I.i(zZ.V_1_21_4)) {
         this.f = this.P();
      }

      if (this.I.i(zZ.V_1_15)) {
         this.R = new V(this.o(), this.o(), this.o());
      } else {
         this.R = new V((double)this.L(), (double)this.L(), (double)this.L());
      }

      this.i = new r3(this.L(), this.L(), this.L());
      this.Q = this.L();
      this.c = this.f();
      if (var5) {
         this.K = b8.Z(this);
      } else {
         Object var7;
         if (this.I.i(zZ.V_1_13)) {
            var7 = var4.J(this);
         } else {
            var7 = oh.z();
            if (this.I.i(zZ.V_1_8)) {
               var7 = oA.K(this, var3);
            }
         }

         this.K = new b8(var4, (oh)var7);
      }

   }

   public void d() {
      int var1;
      if (this.I.R(zZ.V_1_7_10)) {
         this.a(this.K.x().f().R(), 64);
      } else if (this.I.m(zZ.V_1_20_5)) {
         var1 = this.K.x().f(this.I.u());
         if (this.I.i(zZ.V_1_19)) {
            this.E(var1);
         } else {
            this.L(var1);
         }
      }

      this.I(this.a);
      if (this.I.i(zZ.V_1_21_4)) {
         this.I(this.f);
      }

      if (this.I.i(zZ.V_1_15)) {
         this.v(this.R.o());
         this.v(this.R.h());
         this.v(this.R.D());
      } else {
         this.S((float)this.R.o());
         this.S((float)this.R.h());
         this.S((float)this.R.D());
      }

      this.S(this.i.X());
      this.S(this.i.c());
      this.S(this.i.P());
      this.S(this.Q);
      this.L(this.c);
      if (this.I.i(zZ.V_1_20_5)) {
         b8.M(this, this.K);
      } else if (this.I.i(zZ.V_1_13)) {
         this.K.x().c(this, this.K.l());
      } else if (this.I.i(zZ.V_1_8)) {
         var1 = this.K.x().f(this.I.u());
         oA var2 = this.K.l() instanceof wG ? ((wG)this.K.l()).D(this.I.u()) : oA.N(var1);
         oA.N(this, var1, var2);
      }

   }

   public void C(Qt var1) {
      this.K = var1.K;
      this.a = var1.a;
      this.R = var1.R;
      this.i = var1.i;
      this.Q = var1.Q;
      this.c = var1.c;
      this.f = var1.f;
   }
}
