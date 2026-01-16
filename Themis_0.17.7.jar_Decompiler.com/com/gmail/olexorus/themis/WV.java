package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class wV {
   private static final N8<ap> N;
   public static final ap l;
   public static final ap D;
   public static final ap M;
   public static final ap d;
   public static final ap v;

   public static ap V(String var0) {
      return (ap)N.h(var0, ii::new);
   }

   public static N8<ap> B() {
      return N;
   }

   static {
      long var0 = kt.a(2226850235634863503L, 6259096311704094112L, MethodHandles.lookup().lookupClass()).a(194122829829785L) ^ 5418497183090L;
      N = new N8("parrot_variant");
      l = V("red_blue");
      D = V("blue");
      M = V("green");
      d = V("yellow_blue");
      v = V("gray");
      N.f();
   }
}
