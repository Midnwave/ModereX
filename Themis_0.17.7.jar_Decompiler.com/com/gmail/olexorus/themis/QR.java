package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Qr extends lm<Qr> {
   private Optional<Integer> r;
   private Optional<Wp> c;
   private List<x> Y;

   public void t() {
      int var1;
      int var2;
      if (this.I.i(zZ.V_1_13)) {
         this.r = Optional.of(this.Q());
         var1 = this.Q();
         var2 = this.Q();
         int var3 = this.Q();
         this.c = Optional.of(new Wp(var1, var1 + var2));
         this.Y = new ArrayList(var3);

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = this.A();
            X var6 = (X)this.u(lm::a);
            x var7 = new x(var5, var6);
            this.Y.add(var7);
         }
      } else {
         var1 = this.Q();
         this.Y = new ArrayList(var1);

         for(var2 = 0; var2 < var1; ++var2) {
            String var8 = this.A();
            x var9 = new x(var8, (X)null);
            this.Y.add(var9);
         }
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_13)) {
         this.E((Integer)this.r.orElse(-1));
         Wp var1 = (Wp)this.c.get();
         this.E(var1.S());
         this.E(var1.C());
         this.E(this.Y.size());
         Iterator var2 = this.Y.iterator();

         while(var2.hasNext()) {
            x var3 = (x)var2.next();
            this.I(var3.l());
            boolean var4 = var3.v().isPresent();
            this.I(var4);
            if (var4) {
               this.G((X)var3.v().get());
            }
         }
      } else {
         this.E(this.Y.size());
         Iterator var5 = this.Y.iterator();

         while(var5.hasNext()) {
            x var6 = (x)var5.next();
            this.I(var6.l());
         }
      }

   }

   public void g(Qr var1) {
      this.r = var1.r;
      this.c = var1.c;
      this.Y = var1.Y;
   }
}
