package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;

public class up extends ue {
   private final Callable<String> a;
   private static final long c = kt.a(-1760488458050741360L, 1487631687866184805L, MethodHandles.lookup().lookupClass()).a(18653342946117L);

   public up(String var1, Callable<String> var2) {
      super(var1);
      this.a = var2;
   }

   protected nl Y() {
      long var1 = c ^ 95539474073512L;
      String var3 = (String)this.a.call();
      return var3 != null && !var3.isEmpty() ? (new t3()).X("value", var3).I() : null;
   }
}
