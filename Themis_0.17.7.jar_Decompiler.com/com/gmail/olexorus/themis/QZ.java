package com.gmail.olexorus.themis;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Qz extends lm<Qz> {
   private EnumSet<ub> S;
   private List<M6> t;

   public void t() {
      this.S = this.J(ub.class);
      this.t = this.j(this::lambda$read$0);
   }

   public void d() {
      this.r(this.S, ub.class);
      this.D(this.t, this::lambda$write$2);
   }

   public void N(Qz var1) {
      this.S = var1.S;
      this.t = var1.t;
   }

   private void lambda$write$2(lm var1, M6 var2) {
      var1.y(var2.E());
      Iterator var3 = this.S.iterator();

      while(var3.hasNext()) {
         ub var4 = (ub)var3.next();
         switch(var4.ordinal()) {
         case 0:
            var1.a(var2.n().s(), 16);
            this.D(var2.n().k(), Qz::lambda$write$1);
            break;
         case 1:
            var1.l(var2.f(), lm::h);
            break;
         case 2:
            var1.E(var2.K().q());
            break;
         case 3:
            var1.I(var2.y());
            break;
         case 4:
            var1.E(var2.w());
            break;
         case 5:
            var1.l(var2.D(), lm::G);
            break;
         case 6:
            if (this.I.i(zZ.V_1_21_2)) {
               var1.E(var2.u());
            }
            break;
         case 7:
            if (this.I.i(zZ.V_1_21_4)) {
               var1.I(var2.f());
            }
         }
      }

   }

   private static void lambda$write$1(lm var0, m8 var1) {
      var0.I(var1.i());
      var0.I(var1.z());
      var0.l(var1.l(), lm::I);
   }

   private M6 lambda$read$0(lm var1) {
      UUID var2 = var1.V();
      Ml var3 = new Ml(var2, (String)null);
      zE var4 = zE.N();
      boolean var5 = false;
      int var6 = 0;
      q var7 = null;
      X var8 = null;
      int var9 = 0;
      boolean var10 = false;
      Iterator var11 = this.S.iterator();

      while(true) {
         label36:
         while(var11.hasNext()) {
            ub var12 = (ub)var11.next();
            switch(var12.ordinal()) {
            case 0:
               var3.G(var2);
               var3.s(var1.m(16));
               int var13 = var1.Q();
               int var14 = 0;

               while(true) {
                  if (var14 >= var13) {
                     continue label36;
                  }

                  String var15 = var1.A();
                  String var16 = var1.A();
                  String var17 = (String)var1.u(lm::A);
                  m8 var18 = new m8(var15, var16, var17);
                  var3.k().add(var18);
                  ++var14;
               }
            case 1:
               var7 = (q)var1.u(lm::M);
               break;
            case 2:
               var4 = zE.o(var1.Q());
               break;
            case 3:
               var5 = var1.P();
               break;
            case 4:
               var6 = var1.Q();
               break;
            case 5:
               var8 = (X)var1.u(lm::a);
               break;
            case 6:
               if (this.I.i(zZ.V_1_21_2)) {
                  var9 = var1.Q();
               }
               break;
            case 7:
               if (this.I.i(zZ.V_1_21_4)) {
                  var10 = var1.P();
               }
            }
         }

         return new M6(var3, var5, var6, var4, var8, var7, var9, var10);
      }
   }
}
