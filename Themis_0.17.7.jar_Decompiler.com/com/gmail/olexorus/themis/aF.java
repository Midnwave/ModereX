package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

final class AF extends Av<TG, W_> implements W_ {
   private String T;
   private static final long b = kt.a(5939202498807027928L, 4466441776293293764L, MethodHandles.lookup().lookupClass()).a(183074531836011L);

   AF() {
   }

   AF(TG var1) {
      super(var1);
      this.T = var1.T();
   }

   public W_ a(String var1) {
      long var2 = b ^ 133399361034623L;
      this.T = (String)Objects.requireNonNull(var1, "keybind");
      return this;
   }

   public TG a() {
      long var1 = b ^ 45070125687412L;
      if (this.T == null) {
         throw new IllegalStateException("keybind must be set");
      } else {
         return Ec.F(this.y, this.l(), this.T);
      }
   }
}
