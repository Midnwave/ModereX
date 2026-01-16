package com.gmail.olexorus.themis;

public class QX extends lm<QX> {
   private String Y;
   private c w;
   private X O;
   private EX N;
   private Mn j;

   public void t() {
      if (this.I.i(zZ.V_1_18)) {
         this.Y = this.A();
      } else {
         this.Y = this.m(16);
      }

      this.w = c.B(this.M());
      if (this.w != c.CREATE && this.w != c.UPDATE) {
         this.O = X.f();
         this.N = EX.INTEGER;
         if (this.I.i(zZ.V_1_20_3)) {
            this.j = null;
         }
      } else if (this.I.m(zZ.V_1_13)) {
         this.O = this.x().c(this.m(32));
         this.N = EX.S(this.A());
      } else {
         this.O = this.a();
         this.N = EX.V(this.Q());
         if (this.I.i(zZ.V_1_20_3)) {
            this.j = (Mn)this.u(Mn::Y);
         }
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_18)) {
         this.I(this.Y);
      } else {
         this.a(this.Y, 16);
      }

      this.u((byte)this.w.ordinal());
      if (this.w == c.CREATE || this.w == c.UPDATE) {
         if (this.I.m(zZ.V_1_13)) {
            String var1 = this.x().v(this.O);
            this.I(Et.D(var1, 32));
            if (this.N != null) {
               this.I(this.N.name().toLowerCase());
            } else {
               this.I(EX.INTEGER.name().toLowerCase());
            }
         } else {
            this.G(this.O);
            if (this.N != null) {
               this.E(this.N.ordinal());
            } else {
               this.E(EX.INTEGER.ordinal());
            }

            if (this.I.i(zZ.V_1_20_3)) {
               this.l(this.j, Mn::F);
            }
         }
      }

   }

   public void K(QX var1) {
      this.Y = var1.Y;
      this.w = var1.w;
      this.O = var1.O;
      this.N = var1.N;
      this.j = var1.j;
   }
}
