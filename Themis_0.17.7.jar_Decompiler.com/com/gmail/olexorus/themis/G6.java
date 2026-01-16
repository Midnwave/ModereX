package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class g6 {
   private static final N8<BM> Z;
   public static final BM g;
   public static final BM S;
   public static final BM D;
   public static final BM C;
   public static final BM y;
   public static final BM u;
   public static final BM L;

   public static BM K(String var0) {
      return (BM)Z.h(var0, i1::new);
   }

   public static N8<BM> c() {
      return Z;
   }

   static {
      long var0 = kt.a(2242973878103012288L, 8218838696776599044L, MethodHandles.lookup().lookupClass()).a(244788476507122L) ^ 59079606751721L;
      Z = new N8("rabbit_variant");
      g = K("brown");
      S = K("white");
      D = K("black");
      C = K("white_splotched");
      y = K("gold");
      u = K("salt");
      L = K("evil");
      Z.f();
   }
}
