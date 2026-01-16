package com.gmail.olexorus.themis;

import java.util.Iterator;
import java.util.function.Consumer;

class Bo implements Iterator<MC> {
   final Iterator P;
   final Bq Q;

   Bo(Bq var1, Iterator var2) {
      this.Q = var1;
      this.P = var2;
   }

   public boolean hasNext() {
      return this.P.hasNext();
   }

   public MC q() {
      return (MC)this.P.next();
   }

   public void forEachRemaining(Consumer<? super MC> var1) {
      this.P.forEachRemaining(var1);
   }
}
