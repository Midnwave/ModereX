package com.gmail.olexorus.themis;

import java.io.DataInput;
import java.io.DataOutput;

class uB<T extends MC> extends uX<T> {
   final Class<T> Q;
   final byte m;
   private final T_<T> X;
   private final zF<T> G;

   uB(Class<T> var1, byte var2, T_<T> var3, zF<T> var4) {
      this.Q = var1;
      this.m = var2;
      this.X = var3;
      this.G = var4;
   }

   public final T s(DataInput var1) {
      return this.X.N(var1);
   }

   public final void l(T var1, DataOutput var2) {
      if (this.G != null) {
         this.G.k(var1, var2);
      }

   }

   public final byte B() {
      return this.m;
   }

   boolean o() {
      return false;
   }

   public String toString() {
      return uX.class.getSimpleName() + '[' + this.Q.getSimpleName() + " " + this.m + "]";
   }
}
