package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class E6 {
   private static final N8<wE<?>> y;
   public static final wE<GT> V;
   public static final wE<wx> T;
   public static final wE<gs> m;
   public static final wE<Og> C;

   public static N8<wE<?>> N() {
      return y;
   }

   public static <T extends Bi> wE<T> o(String var0, Ot<T> var1, ms<T> var2) {
      return (wE)y.h(var0, E6::lambda$define$0);
   }

   private static iF lambda$define$0(Ot var0, ms var1, z2 var2) {
      return new iF(var2, var0, var1);
   }

   static {
      long var0 = kt.a(4266021266242637541L, 7036249100177672682L, MethodHandles.lookup().lookupClass()).a(12896919711166L) ^ 111795015092279L;
      y = new N8("input_control_type");
      V = o("boolean", GT::n, GT::g);
      T = o("number_range", wx::U, wx::X);
      m = o("single_option", gs::l, gs::L);
      C = o("text", Og::n, Og::c);
      y.f();
   }
}
