package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.List;

public final class wu<T> {
   private final List<P<T>> c;

   public wu() {
      this(new ArrayList());
   }

   public wu(List<P<T>> var1) {
      this.c = var1;
   }

   public static <T> wu<T> J(lm<?> var0, MO<T> var1) {
      List var2 = var0.j(wu::lambda$read$0);
      return new wu(var2);
   }

   public static <T> void W(lm<?> var0, wu<T> var1, Gw<T> var2) {
      var0.D(var1.c, wu::lambda$write$1);
   }

   private static void lambda$write$1(Gw var0, lm var1, P var2) {
      P.a(var1, var2, var0);
   }

   private static P lambda$read$0(lm var0, MO var1, lm var2) {
      return P.S(var0, var1);
   }
}
