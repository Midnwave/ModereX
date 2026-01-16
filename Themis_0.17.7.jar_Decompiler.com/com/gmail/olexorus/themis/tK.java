package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum TK {
   SOURCE,
   BINARY,
   RUNTIME;

   // $FF: synthetic field
   private static final O4 D;

   // $FF: synthetic method
   private static final TK[] k() {
      TK[] var0 = new TK[]{SOURCE, BINARY, RUNTIME};
      return var0;
   }

   static {
      long var0 = kt.a(-8744160299828165532L, -5965260364740131126L, MethodHandles.lookup().lookupClass()).a(154769278900104L) ^ 66239021108254L;
      SOURCE = new TK("SOURCE", 0);
      BINARY = new TK("BINARY", 1);
      RUNTIME = new TK("RUNTIME", 2);
      D = Cz.x((Enum[])q);
   }
}
