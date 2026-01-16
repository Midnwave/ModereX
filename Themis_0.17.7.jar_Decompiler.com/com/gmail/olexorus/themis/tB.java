package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum tb {
   NORMAL,
   COLD;

   private static final l3<String, tb> H;
   private final String b;
   private static final tb[] P;

   private tb(String var3) {
      this.b = var3;
   }

   public static tb e(String var0) {
      return (tb)rN.V(H, var0);
   }

   public String e() {
      return this.b;
   }

   private static tb[] P() {
      return new tb[]{NORMAL, COLD};
   }

   static {
      long var0 = kt.a(-378987802874770301L, 3118094513225861804L, MethodHandles.lookup().lookupClass()).a(21075055128346L) ^ 8086908463283L;
      NORMAL = new tb("NORMAL", 0, "normal");
      COLD = new tb("COLD", 1, "cold");
      P = P();
      H = l3.Q(tb.class, tb::e);
   }
}
