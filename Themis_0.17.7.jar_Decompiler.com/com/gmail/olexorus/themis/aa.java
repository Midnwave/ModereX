package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

public interface Aa extends d<Aa, nh>, Gl<Aa> {
   long b = kt.a(8649305065712478853L, -4807417755192076629L, MethodHandles.lookup().lookupClass()).a(101716607454731L);

   String H();

   Aa q(String var1);

   default Stream<? extends rE> T() {
      long var1 = b ^ 10511917506508L;
      return Stream.concat(Stream.of(rE.c("content", this.H())), d.super.T());
   }
}
