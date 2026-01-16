package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Q1 extends lm<Q1> {
   private Map<al, List<lJ>> w;
   private static final long a = kt.a(5325537511361504628L, 429922137471468964L, MethodHandles.lookup().lookupClass()).a(66832969736444L);

   public void t() {
      long var1 = a ^ 122654355693834L;
      if (this.I.i(zZ.V_1_17)) {
         this.w = this.U(lm::R, Q1::lambda$read$0);
      } else {
         this.w = new HashMap(4);
         this.w.put(al.z("block"), this.j(lJ::G));
         this.w.put(al.z("item"), this.j(lJ::G));
         this.w.put(al.z("fluid"), this.j(lJ::G));
         if (this.I.i(zZ.V_1_14)) {
            this.w.put(al.z("entity_type"), this.j(lJ::G));
         }
      }

   }

   public void d() {
      long var1 = a ^ 53242907369889L;
      if (this.I.i(zZ.V_1_17)) {
         this.o(this.w, lm::T, Q1::lambda$write$1);
      } else {
         this.D((List)this.w.get(al.z("block")), lJ::E);
         this.D((List)this.w.get(al.z("item")), lJ::E);
         this.D((List)this.w.get(al.z("fluid")), lJ::E);
         if (this.I.i(zZ.V_1_14)) {
            this.D((List)this.w.get(al.z("entity_type")), lJ::E);
         }
      }

   }

   public void m(Q1 var1) {
      this.w = var1.w;
   }

   private static void lambda$write$1(lm var0, List var1) {
      var0.D(var1, lJ::E);
   }

   private static List lambda$read$0(lm var0) {
      return var0.j(lJ::G);
   }
}
