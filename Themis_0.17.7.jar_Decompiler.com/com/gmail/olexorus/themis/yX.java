package com.gmail.olexorus.themis;

public class yx extends lm<yx> {
   private boolean b;
   private boolean q;
   private V4 T;
   private boolean n;
   private boolean C;

   public yx(aw var1) {
      super(var1, false);
      this.b = var1.y() == lF.PLAYER_POSITION || var1.y() == lF.PLAYER_POSITION_AND_ROTATION;
      this.q = var1.y() == lF.PLAYER_ROTATION || var1.y() == lF.PLAYER_POSITION_AND_ROTATION;
      this.h(var1);
   }

   public void t() {
      V var1 = new V();
      float var2 = 0.0F;
      float var3 = 0.0F;
      if (this.b) {
         double var4 = this.o();
         double var6 = this.o();
         double var8;
         if (this.I.R(zZ.V_1_7_10)) {
            var8 = this.o();
         }

         var8 = this.o();
         var1 = new V(var4, var6, var8);
      }

      if (this.q) {
         var2 = this.L();
         var3 = this.L();
      }

      this.T = new V4(var1, var2, var3);
      byte var10 = this.M();
      this.n = (var10 & 1) == 1;
      this.C = (var10 & 2) == 2;
   }

   public void d() {
      if (this.b) {
         this.v(this.T.v().o());
         if (this.I.R(zZ.V_1_7_10)) {
            this.v(this.T.v().h() + 1.62D);
         }

         this.v(this.T.v().h());
         this.v(this.T.v().D());
      }

      if (this.q) {
         this.S(this.T.I());
         this.S(this.T.T());
      }

      this.u((this.n ? 1 : 0) | (this.C ? 2 : 0));
   }

   public void m(yx var1) {
      this.b = var1.b;
      this.q = var1.q;
      this.T = var1.T;
      this.n = var1.n;
      this.C = var1.C;
   }

   public V4 o() {
      return this.T;
   }

   public boolean Z() {
      return this.b;
   }

   public boolean Q() {
      return this.q;
   }

   public boolean q() {
      return this.n;
   }
}
