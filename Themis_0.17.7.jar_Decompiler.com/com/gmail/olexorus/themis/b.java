package com.gmail.olexorus.themis;

import java.util.Set;
import java.util.Map.Entry;

public interface B extends MC, Gn<B>, Iterable<Entry<String, ? extends MC>> {
   static B j() {
      return BN.S;
   }

   static No S() {
      return new We();
   }

   default uX<B> y() {
      return tq.w;
   }

   Set<String> W();

   MC E(String var1);

   byte Z(String var1, byte var2);

   default String r(String var1) {
      return this.C(var1, "");
   }

   String C(String var1, String var2);

   default B I(String var1) {
      return this.H(var1, j());
   }

   B H(String var1, B var2);
}
