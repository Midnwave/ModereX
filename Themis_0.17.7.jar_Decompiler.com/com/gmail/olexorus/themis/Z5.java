package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;

final class z5 implements v1 {
   static final Comparator<? super v1> j = Comparator.comparing(v1::a).thenComparing(v1::i);
   private final String v;
   private final String E;
   private static final long b = kt.a(207929607934465447L, -2677286323350625239L, MethodHandles.lookup().lookupClass()).a(86878231641763L);

   z5(String var1, String var2) {
      long var3 = b ^ 111341725182242L;
      super();
      a("namespace", var1, var1, var2, v1.a(var1), "[a-z0-9_\\-.]+");
      a("value", var2, var1, var2, v1.n(var2), "[a-z0-9_\\-./]+");
      this.v = (String)Objects.requireNonNull(var1, "namespace");
      this.E = (String)Objects.requireNonNull(var2, "value");
   }

   private static void a(String var0, String var1, String var2, String var3, OptionalInt var4, String var5) {
      long var6 = b ^ 117526595742665L;
      if (var4.isPresent()) {
         int var8 = var4.getAsInt();
         char var9 = var1.charAt(var8);
         throw new uj(var2, var3, String.format("Non " + var5 + " character in %s of Key[%s] at index %d ('%s', bytes: %s)", var0, u(var2, var3), var8, var9, Arrays.toString(String.valueOf(var9).getBytes(StandardCharsets.UTF_8))));
      }
   }

   static boolean s(char var0) {
      return var0 == '_' || var0 == '-' || var0 >= 'a' && var0 <= 'z' || var0 >= '0' && var0 <= '9' || var0 == '.';
   }

   static boolean l(char var0) {
      return var0 == '_' || var0 == '-' || var0 >= 'a' && var0 <= 'z' || var0 >= '0' && var0 <= '9' || var0 == '.' || var0 == '/';
   }

   public String i() {
      return this.v;
   }

   public String a() {
      return this.E;
   }

   public String X() {
      return u(this.v, this.E);
   }

   private static String u(String var0, String var1) {
      return var0 + ':' + var1;
   }

   public String toString() {
      return this.X();
   }

   public Stream<? extends rE> T() {
      long var1 = b ^ 44725916189737L;
      return Stream.of(rE.c("namespace", this.v), rE.c("value", this.E));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof v1)) {
         return false;
      } else {
         v1 var2 = (v1)var1;
         return Objects.equals(this.v, var2.i()) && Objects.equals(this.E, var2.a());
      }
   }

   public int hashCode() {
      int var1 = this.v.hashCode();
      var1 = 31 * var1 + this.E.hashCode();
      return var1;
   }

   public int t(v1 var1) {
      return v1.super.t(var1);
   }
}
