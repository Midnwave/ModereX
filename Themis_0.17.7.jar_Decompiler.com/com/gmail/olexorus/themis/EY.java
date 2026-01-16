package com.gmail.olexorus.themis;

public interface Ey<S, O extends X> {
   O k(S var1);

   default O R(S var1) {
      return this.H(var1, (X)null);
   }

   default O H(S var1, O var2) {
      return var1 == null ? var2 : this.k(var1);
   }
}
