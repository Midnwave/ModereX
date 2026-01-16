package com.gmail.olexorus.themis;

import java.util.RandomAccess;

final class gW<E> extends CU<E> implements RandomAccess {
   private final CU<E> X;
   private final int Y;
   private int j;

   public gW(CU<? extends E> var1, int var2, int var3) {
      this.X = var1;
      this.Y = var2;
      CU.k.K(this.Y, var3, this.X.size());
      this.j = var3 - this.Y;
   }

   public E get(int var1) {
      CU.k.G(var1, this.j);
      return this.X.get(this.Y + var1);
   }

   public int q() {
      return this.j;
   }
}
