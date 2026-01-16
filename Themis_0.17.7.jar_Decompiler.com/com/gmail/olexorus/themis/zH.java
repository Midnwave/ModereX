package com.gmail.olexorus.themis;

import java.util.Map;

public abstract class Zh<T extends Zh<T>> extends lm<T> {
   private Map<String, String> w;

   public void t() {
      this.w = this.U(Zh::lambda$read$0, Zh::lambda$read$1);
   }

   public void d() {
      this.o(this.w, Zh::lambda$write$2, Zh::lambda$write$3);
   }

   public void T(T var1) {
      this.w = var1.O();
   }

   public Map<String, String> O() {
      return this.w;
   }

   private static void lambda$write$3(lm var0, String var1) {
      var0.a(var1, 4096);
   }

   private static void lambda$write$2(lm var0, String var1) {
      var0.a(var1, 128);
   }

   private static String lambda$read$1(lm var0) {
      return var0.m(4096);
   }

   private static String lambda$read$0(lm var0) {
      return var0.m(128);
   }
}
