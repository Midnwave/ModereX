package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class vD {
   private static final N8<Wu<?>> g;
   public static final Wu<uq> I;
   public static final Wu<uc> a;
   public static final Wu<u2> J;
   public static final Wu<uU> O;
   public static final Wu<uT> C;

   private static <T extends us<?>> Wu<T> g(String var0, MO<T> var1, Gw<T> var2) {
      return (Wu)g.h(var0, vD::lambda$register$0);
   }

   public static N8<Wu<?>> c() {
      return g;
   }

   private static cz lambda$register$0(MO var0, Gw var1, z2 var2) {
      return new cz(var2, var0, var1);
   }

   static {
      long var0 = kt.a(-2388985441285584763L, -7174957875303692538L, MethodHandles.lookup().lookupClass()).a(270471563633090L) ^ 66829438904057L;
      g = new N8("recipe_display");
      I = g("crafting_shapeless", uq::s, uq::i);
      a = g("crafting_shaped", uc::G, uc::G);
      J = g("furnace", u2::G, u2::K);
      O = g("stonecutter", uU::n, uU::b);
      C = g("smithing", uT::B, uT::Y);
      g.f();
   }
}
