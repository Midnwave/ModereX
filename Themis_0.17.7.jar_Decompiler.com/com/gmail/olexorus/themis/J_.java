package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum J_ {
   SNAKE_CASE,
   CAMEL_CASE,
   VALUE_FIELD,
   ALL;

   public static final J_ MODERN_ONLY;
   public static final J_ LEGACY_ONLY;
   public static final J_ BOTH;
   private static final J_[] Y;

   private static J_[] i() {
      return new J_[]{SNAKE_CASE, CAMEL_CASE, VALUE_FIELD, ALL};
   }

   static {
      long var0 = kt.a(-7903107500655399827L, -66682570899016215L, MethodHandles.lookup().lookupClass()).a(144064871889934L) ^ 35851001063361L;
      SNAKE_CASE = new J_("SNAKE_CASE", 0);
      CAMEL_CASE = new J_("CAMEL_CASE", 1);
      VALUE_FIELD = new J_("VALUE_FIELD", 2);
      ALL = new J_("ALL", 3);
      Y = i();
      MODERN_ONLY = CAMEL_CASE;
      LEGACY_ONLY = VALUE_FIELD;
      BOTH = ALL;
   }
}
