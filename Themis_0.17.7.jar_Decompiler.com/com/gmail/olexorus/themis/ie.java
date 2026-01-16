package com.gmail.olexorus.themis;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class iE<T> extends id {
   private final MO<? extends T> Y;
   private final Gw<T> o;

   public iE(z2 var1, MO<? extends T> var2, Gw<T> var3) {
      super(var1);
      this.Y = var2;
      this.o = var3;
   }

   public T N(lm<?> var1) {
      return this.Y.apply(var1);
   }

   public void a(lm<?> var1, T var2) {
      this.o.accept(var1, var2);
   }

   public Function<lm<?>, T> a() {
      MO var10000 = this.Y;
      Objects.requireNonNull(var10000);
      return var10000::apply;
   }

   public BiConsumer<lm<?>, T> t() {
      return this.o;
   }
}
