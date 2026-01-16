package com.gmail.olexorus.themis;

public class sg extends lm<sg> {
   private int[] B;

   public sg(int var1) {
      super((wC)rX.DESTROY_ENTITIES);
      this.B = new int[]{var1};
   }

   public void t() {
      if (this.I == zZ.V_1_17) {
         this.B = new int[]{this.Q()};
      } else {
         int var2;
         if (this.I.R(zZ.V_1_7_10)) {
            short var1 = this.h();
            this.B = new int[var1];

            for(var2 = 0; var2 < var1; ++var2) {
               this.B[var2] = this.f();
            }
         } else {
            int var3 = this.Q();
            this.B = new int[var3];

            for(var2 = 0; var2 < var3; ++var2) {
               this.B[var2] = this.Q();
            }
         }
      }

   }

   public void d() {
      if (this.I == zZ.V_1_17) {
         this.E(this.B[0]);
      } else {
         int[] var1;
         int var2;
         int var3;
         int var4;
         if (this.I.R(zZ.V_1_7_10)) {
            this.u(this.B.length);
            var1 = this.B;
            var2 = var1.length;

            for(var3 = 0; var3 < var2; ++var3) {
               var4 = var1[var3];
               this.L(var4);
            }
         } else {
            this.E(this.B.length);
            var1 = this.B;
            var2 = var1.length;

            for(var3 = 0; var3 < var2; ++var3) {
               var4 = var1[var3];
               this.E(var4);
            }
         }
      }

   }

   public void b(sg var1) {
      this.B = var1.B;
   }

   public int[] e() {
      return this.B;
   }
}
