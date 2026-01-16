package com.gmail.olexorus.themis;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

final class N2<E extends Enum<E>> extends Ns<E> {
   private final Map<String, E> c = new HashMap();

   N2(Class<E> var1) {
      super(var1);
      Enum[] var2 = (Enum[])var1.getEnumConstants();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Enum var5 = var2[var4];
         this.c.put(var5.name().toLowerCase(Locale.ROOT), var5);
      }

   }
}
