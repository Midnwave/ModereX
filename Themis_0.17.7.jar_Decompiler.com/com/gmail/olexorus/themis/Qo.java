package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class QO extends lm<QO> {
   private bf s;
   private List<ld> a;

   public void t() {
      if (this.I.R(zZ.V_1_7_10)) {
         this.a = new ArrayList(1);
         String var1 = this.A();
         Aa var2 = X.N(var1);
         boolean var3 = this.P();
         short var4 = this.x();
         ld var5 = new ld(var2, (Ml)null, zE.SURVIVAL, var4);
         this.a.add(var5);
         if (var3) {
            this.s = null;
         } else {
            this.s = bf.REMOVE_PLAYER;
         }
      } else {
         this.s = bf.VALUES[this.Q()];
         int var13 = this.Q();
         this.a = new ArrayList(var13);

         for(int var14 = 0; var14 < var13; ++var14) {
            ld var15 = null;
            UUID var16 = this.V();
            switch(this.s.ordinal()) {
            case 0:
               String var20 = this.m(16);
               Ml var6 = new Ml(var16, var20);
               int var7 = this.Q();

               for(int var8 = 0; var8 < var7; ++var8) {
                  String var9 = this.A();
                  String var10 = this.A();
                  String var11 = (String)this.u(lm::A);
                  m8 var12 = new m8(var9, var10, var11);
                  var6.k().add(var12);
               }

               zE var21 = zE.o(this.Q());
               int var22 = this.Q();
               X var23 = this.P() ? this.a() : null;
               MM var24 = null;
               if (this.I.i(zZ.V_1_19)) {
                  var24 = (MM)this.u(lm::i);
               }

               var15 = new ld(var23, var6, var21, var24, var22);
               break;
            case 1:
               zE var19 = zE.o(this.Q());
               var15 = new ld((X)null, new Ml(var16, (String)null), var19, -1);
               break;
            case 2:
               int var18 = this.Q();
               var15 = new ld((X)null, new Ml(var16, (String)null), (zE)null, var18);
               break;
            case 3:
               X var17 = this.P() ? this.a() : null;
               var15 = new ld(var17, new Ml(var16, (String)null), (zE)null, -1);
               break;
            case 4:
               var15 = new ld((X)null, new Ml(var16, (String)null), (zE)null, -1);
            }

            if (var15 != null) {
               this.a.add(var15);
            }
         }
      }

   }

   public void d() {
      if (this.I.R(zZ.V_1_7_10)) {
         ld var1 = (ld)this.a.get(0);
         String var2 = ((Aa)ld.V(var1)).H();
         this.I(var2);
         this.I(this.s != bf.REMOVE_PLAYER);
         this.f(ld.c(var1));
      } else {
         this.E(this.s.ordinal());
         this.E(this.a.size());
         Iterator var3 = this.a.iterator();

         while(var3.hasNext()) {
            ld var4 = (ld)var3.next();
            this.y(ld.g(var4).J());
            switch(this.s.ordinal()) {
            case 0:
               this.a(ld.g(var4).s(), 16);
               this.D(ld.g(var4).k(), QO::lambda$write$0);
               this.E(ld.i(var4).ordinal());
               this.E(ld.c(var4));
               if (ld.V(var4) != null) {
                  this.I(true);
                  this.G(ld.V(var4));
               } else {
                  this.I(false);
               }

               if (this.I.i(zZ.V_1_19)) {
                  this.l(var4.I(), lm::H);
               }
               break;
            case 1:
               this.E(ld.i(var4).ordinal());
               break;
            case 2:
               this.E(ld.c(var4));
               break;
            case 3:
               if (ld.V(var4) != null) {
                  this.I(true);
                  this.G(ld.V(var4));
               } else {
                  this.I(false);
               }
            case 4:
            }
         }
      }

   }

   public void E(QO var1) {
      this.s = var1.s;
      this.a = var1.a;
   }

   private static void lambda$write$0(lm var0, m8 var1) {
      var0.I(var1.i());
      var0.I(var1.z());
      var0.l(var1.l(), lm::I);
   }
}
