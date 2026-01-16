package com.gmail.olexorus.themis;

public class at extends lm<at> {
   private int j;
   private VC a;

   public void t() {
      if (this.I.i(zZ.V_1_8)) {
         this.j = this.Q();
         this.a = this.M();
      } else {
         this.j = this.f();
         int var1 = this.f();
         short var2 = this.h();
         int var3 = this.f();
         this.a = new VC(var1, var2, var3);
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_8)) {
         this.E(this.j);
         this.o(this.a);
      } else {
         this.L(this.j);
         this.L(this.a.e());
         this.u(this.a.I());
         this.L(this.a.z());
      }

   }

   public void S(at var1) {
      var1.j = this.j;
      var1.a = this.a;
   }
}
