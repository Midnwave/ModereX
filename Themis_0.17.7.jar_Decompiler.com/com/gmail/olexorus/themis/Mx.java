package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

public interface mx extends nc {
   long a = kt.a(6484553279784585960L, -7010790825131929335L, MethodHandles.lookup().lookupClass()).a(111991570953753L);

   String g();

   String l();

   String K();

   default Stream<? extends rE> T() {
      long var1 = a ^ 115040677328359L;
      return Stream.of(rE.c("name", this.g()), rE.c("value", this.l()), rE.c("signature", this.K()));
   }
}
