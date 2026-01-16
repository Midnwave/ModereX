package com.gmail.olexorus.themis;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

final class ES {
   private BX s;
   private final Set<ux> f;
   private boolean S;
   final o3 y;

   ES(o3 var1) {
      this.y = var1;
      this.f = EnumSet.noneOf(ux.class);
   }

   void s(ES var1) {
      this.s = var1.s;
      this.f.clear();
      this.f.addAll(var1.f);
   }

   public void B() {
      this.s = null;
      this.f.clear();
   }

   void p(WR var1) {
      // $FF: Couldn't be decompiled
   }

   void W() {
      boolean var1 = this.s != o3.e(this.y).s;
      if (this.S) {
         if (!var1) {
            this.y.N(bL.INSTANCE);
         }

         this.S = false;
      }

      if (!var1 && o3.h(this.y) != bL.INSTANCE) {
         if (!this.f.containsAll(o3.e(this.y).f)) {
            this.X();
         } else {
            Iterator var2 = this.f.iterator();

            while(var2.hasNext()) {
               ux var3 = (ux)var2.next();
               if (o3.e(this.y).f.add(var3)) {
                  this.y.N(var3);
               }
            }

         }
      } else {
         this.X();
      }
   }

   private void X() {
      if (this.s != null) {
         this.y.N(this.s);
      } else {
         this.y.N(bL.INSTANCE);
      }

      o3.e(this.y).s = this.s;
      Iterator var1 = this.f.iterator();

      while(var1.hasNext()) {
         ux var2 = (ux)var1.next();
         this.y.N(var2);
      }

      o3.e(this.y).f.clear();
      o3.e(this.y).f.addAll(this.f);
   }
}
