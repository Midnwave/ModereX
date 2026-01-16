package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.List;

public class VY {
   private List<Float> s;
   private List<Boolean> i;
   private List<String> V;
   private List<v2> Z;

   public VY(List<Float> var1, List<Boolean> var2, List<String> var3, List<v2> var4) {
      this.s = var1;
      this.i = var2;
      this.V = var3;
      this.Z = var4;
   }

   public VY(int var1) {
      this.s = new ArrayList(1);
      this.i = new ArrayList(0);
      this.V = new ArrayList(0);
      this.Z = new ArrayList(0);
      this.p(var1);
   }

   public static VY V(lm<?> var0) {
      return var0.R().i(zZ.V_1_21_4) ? new VY(var0.j(lm::L), var0.j(lm::P), var0.j(lm::A), var0.j(v2::m)) : new VY(var0.Q());
   }

   public static void n(lm<?> var0, VY var1) {
      if (var0.R().i(zZ.V_1_21_4)) {
         var0.D(var1.s, lm::S);
         var0.D(var1.i, lm::I);
         var0.D(var1.V, lm::I);
         var0.D(var1.Z, v2::B);
      } else {
         var0.E(var1.S());
      }

   }

   public int S() {
      return !this.s.isEmpty() ? ((Float)this.s.get(0)).intValue() : 0;
   }

   public void p(int var1) {
      if (this.i.isEmpty()) {
         this.s.add((float)var1);
      } else {
         this.s.set(0, (float)var1);
      }

   }
}
