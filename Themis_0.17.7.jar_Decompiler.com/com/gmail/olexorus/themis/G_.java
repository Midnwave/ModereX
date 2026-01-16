package com.gmail.olexorus.themis;

import java.io.Serializable;

final class g_<T extends Enum<T>> extends CU<T> implements O4<T>, Serializable {
   private final T[] V;

   public g_(T[] var1) {
      this.V = var1;
   }

   public int q() {
      return this.V.length;
   }

   public T D(int var1) {
      CU.k.G(var1, this.V.length);
      return this.V[var1];
   }

   public boolean O(T var1) {
      Enum var2 = (Enum)Ej.J(this.V, var1.ordinal());
      return var2 == var1;
   }

   public int Z(T var1) {
      int var2 = var1.ordinal();
      Enum var3 = (Enum)Ej.J(this.V, var2);
      return var3 == var1 ? var2 : -1;
   }

   public int F(T var1) {
      return this.indexOf(var1);
   }

   private final Object writeReplace() {
      return new nZ(this.V);
   }
}
