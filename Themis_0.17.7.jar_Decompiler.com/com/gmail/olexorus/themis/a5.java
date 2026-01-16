package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

abstract class A5<C extends mm<C, B>, B extends zB<C, B>> extends Av<C, B> implements zB<C, B> {
   protected String o;
   protected boolean B = false;
   protected X R;
   private static final long b = kt.a(2234647094709740175L, 9078477441857843912L, MethodHandles.lookup().lookupClass()).a(21873731086872L);

   A5() {
   }

   A5(C var1) {
      super(var1);
      this.o = var1.n();
      this.B = var1.o();
      this.R = var1.V();
   }

   public B b(String var1) {
      long var2 = b ^ 98134381869065L;
      this.o = (String)Objects.requireNonNull(var1, "nbtPath");
      return this;
   }

   public B W(boolean var1) {
      this.B = var1;
      return this;
   }

   public B G(lv var1) {
      this.R = lv.z(var1);
      return this;
   }
}
