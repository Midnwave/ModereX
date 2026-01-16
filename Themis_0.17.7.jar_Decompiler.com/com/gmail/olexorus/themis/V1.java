package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;

public interface v1 extends Comparable<v1>, nc, v3, M8 {
   long a = kt.a(-7765131679723221022L, 552644457065065634L, MethodHandles.lookup().lookupClass()).a(248472257947017L);

   static v1 t(String var0) {
      return Q(var0, ':');
   }

   static v1 Q(String var0, char var1) {
      long var2 = a ^ 45029738147898L;
      Objects.requireNonNull(var0, "string");
      int var4 = var0.indexOf(var1);
      String var5 = var4 >= 1 ? var0.substring(0, var4) : "minecraft";
      String var6 = var4 >= 0 ? var0.substring(var4 + 1) : var0;
      return W(var5, var6);
   }

   static v1 W(String var0, String var1) {
      return new z5(var0, var1);
   }

   static Comparator<? super v1> f() {
      return z5.j;
   }

   static OptionalInt a(String var0) {
      long var1 = a ^ 28618858389854L;
      Objects.requireNonNull(var0, "namespace");
      int var3 = 0;

      for(int var4 = var0.length(); var3 < var4; ++var3) {
         if (!Q(var0.charAt(var3))) {
            return OptionalInt.of(var3);
         }
      }

      return OptionalInt.empty();
   }

   static OptionalInt n(String var0) {
      long var1 = a ^ 136589336714642L;
      Objects.requireNonNull(var0, "value");
      int var3 = 0;

      for(int var4 = var0.length(); var3 < var4; ++var3) {
         if (!I(var0.charAt(var3))) {
            return OptionalInt.of(var3);
         }
      }

      return OptionalInt.empty();
   }

   static boolean Q(char var0) {
      return z5.s(var0);
   }

   static boolean I(char var0) {
      return z5.l(var0);
   }

   String i();

   String a();

   String X();

   default String S() {
      long var1 = a ^ 135374912698842L;
      return this.i().equals("minecraft") ? this.a() : this.X();
   }

   default Stream<? extends rE> T() {
      long var1 = a ^ 33230626787962L;
      return Stream.of(rE.c("namespace", this.i()), rE.c("value", this.a()));
   }

   default int t(v1 var1) {
      return f().compare(this, var1);
   }

   default v1 m() {
      return this;
   }
}
