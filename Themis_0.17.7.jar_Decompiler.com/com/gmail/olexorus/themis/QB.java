package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class Qb extends lm<Qb> {
   private String t;
   private Es f;
   private Collection<String> B;
   private Optional<MU> T;

   public void t() {
      int var1 = this.I.i(zZ.V_1_18) ? 32767 : 16;
      this.t = this.m(var1);
      this.f = Es.values()[this.M()];
      MU var2 = null;
      if (this.f == Es.CREATE || this.f == Es.UPDATE) {
         nr var8 = null;
         Object var3;
         Object var4;
         Object var5;
         GJ var6;
         bV var7;
         oT var9;
         if (this.I.R(zZ.V_1_12_2)) {
            V2 var10 = this.x().k();
            var3 = var10.Q(this.m(32));
            var4 = var10.Q(this.m(16));
            var5 = var10.Q(this.m(16));
            var6 = GJ.values()[this.M()];
            if (this.I.R(zZ.V_1_7_10)) {
               var7 = bV.ALWAYS;
               var9 = oT.g;
            } else {
               var7 = bV.h(this.m(32));
               if (this.I.i(zZ.V_1_9)) {
                  var8 = nr.V(this.m(32));
               }

               if (this.I.i(zZ.V_1_17)) {
                  int var11 = this.Q();
                  if (var11 == 21) {
                     var11 = -1;
                  }

                  var9 = o4.i(var11);
               } else {
                  var9 = o4.i(this.M());
               }
            }
         } else {
            var3 = this.a();
            var6 = GJ.U(this.M());
            if (this.I.i(zZ.V_1_21_5)) {
               var7 = (bV)this.i(bV.class);
               var8 = (nr)this.i(nr.class);
            } else {
               var7 = bV.h(this.m(40));
               var8 = nr.V(this.m(40));
            }

            var9 = o4.i(this.M());
            var4 = this.a();
            var5 = this.a();
         }

         var2 = new MU((X)var3, (X)var4, (X)var5, var7, var8 == null ? nr.ALWAYS : var8, var9, var6);
      }

      this.T = Optional.ofNullable(var2);
      this.B = new ArrayList();
      if (this.f == Es.CREATE || this.f == Es.ADD_ENTITIES || this.f == Es.REMOVE_ENTITIES) {
         int var12;
         if (this.I.R(zZ.V_1_7_10)) {
            var12 = this.x();
         } else {
            var12 = this.Q();
         }

         for(int var13 = 0; var13 < var12; ++var13) {
            this.B.add(this.m(40));
         }
      }

   }

   public void d() {
      int var1 = this.I.i(zZ.V_1_18) ? 32767 : 16;
      this.a(this.t, var1);
      this.u(this.f.ordinal());
      if (this.f == Es.CREATE || this.f == Es.UPDATE) {
         MU var2 = (MU)this.T.orElse(new MU(X.f(), X.f(), X.f(), bV.ALWAYS, nr.ALWAYS, oT.g, GJ.NONE));
         if (this.I.R(zZ.V_1_12_2)) {
            V2 var3 = this.x().k();
            this.I(Et.D(var3.o(MU.v(var2)), 32));
            this.I(Et.D(var3.o(MU.E(var2)), 16));
            this.I(Et.D(var3.o(MU.h(var2)), 16));
            this.u(MU.N(var2).ordinal());
            if (this.I.R(zZ.V_1_7_10)) {
               this.a(bV.ALWAYS.O(), 32);
               this.u(15);
            } else {
               this.a(bV.I(MU.l(var2)), 32);
               if (this.I.i(zZ.V_1_9)) {
                  this.a(MU.g(var2).a(), 32);
               }

               this.u(o4.Q(MU.b(var2)));
            }
         } else {
            this.G(MU.v(var2));
            this.u(MU.N(var2).K());
            if (this.I.i(zZ.V_1_21_5)) {
               this.o(MU.l(var2));
               this.o(MU.g(var2));
            } else {
               this.I(bV.I(MU.l(var2)));
               this.I(MU.g(var2).a());
            }

            if (this.I.i(zZ.V_1_17)) {
               int var5 = o4.Q(MU.b(var2));
               if (var5 < 0) {
                  var5 = 21;
               }

               this.E(var5);
            } else {
               this.u(o4.Q(MU.b(var2)));
            }

            this.G(MU.E(var2));
            this.G(MU.h(var2));
         }
      }

      if (this.f == Es.CREATE || this.f == Es.ADD_ENTITIES || this.f == Es.REMOVE_ENTITIES) {
         if (this.I.R(zZ.V_1_7_10)) {
            this.f(this.B.size());
         } else {
            this.E(this.B.size());
         }

         Iterator var4 = this.B.iterator();

         while(var4.hasNext()) {
            String var6 = (String)var4.next();
            this.a(var6, 40);
         }
      }

   }

   public void H(Qb var1) {
      this.t = var1.t;
      this.f = var1.f;
      this.B = var1.B;
      this.T = var1.T;
   }
}
