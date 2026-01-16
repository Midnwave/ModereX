package com.gmail.olexorus.themis;

public interface mG extends nc {
   static mG L(int var0) {
      return N(var0, ne.ABSOLUTE);
   }

   static mG J(int var0) {
      return N(var0, ne.RELATIVE);
   }

   static mG N(int var0, ne var1) {
      return new M7(var0, var1);
   }

   int x();

   ne t();
}
