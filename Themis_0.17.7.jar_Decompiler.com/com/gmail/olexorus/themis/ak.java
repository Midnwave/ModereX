package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

final class AK extends A5<s, vt> implements vt {
   private String v;
   private static final long c = kt.a(3355752218634787493L, 983874934314200645L, MethodHandles.lookup().lookupClass()).a(244537600026329L);

   AK() {
   }

   AK(s var1) {
      super(var1);
      this.v = var1.w();
   }

   public vt U(String var1) {
      long var2 = c ^ 17291291971995L;
      this.v = (String)Objects.requireNonNull(var1, "selector");
      return this;
   }

   public s s() {
      long var1 = c ^ 3674294781199L;
      if (this.o == null) {
         throw new IllegalStateException("nbt path must be set");
      } else if (this.v == null) {
         throw new IllegalStateException("selector must be set");
      } else {
         return EU.k(this.y, this.l(), this.o, this.B, this.R, this.v);
      }
   }
}
