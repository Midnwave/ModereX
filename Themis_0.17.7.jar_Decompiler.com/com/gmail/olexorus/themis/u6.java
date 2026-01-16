package com.gmail.olexorus.themis;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.lang.invoke.MethodHandles;

final class u6<E> extends TypeAdapter<E> {
   private final String L;
   private final l3<String, E> b;
   private final boolean D;
   private static final long a = kt.a(-3059787109446143226L, -6009009864049464830L, MethodHandles.lookup().lookupClass()).a(206214290389224L);

   public static <E> TypeAdapter<E> x(String var0, l3<String, E> var1) {
      return (new u6(var0, var1, true)).nullSafe();
   }

   public static <E> TypeAdapter<E> q(String var0, l3<String, E> var1) {
      return (new u6(var0, var1, false)).nullSafe();
   }

   private u6(String var1, l3<String, E> var2, boolean var3) {
      this.L = var1;
      this.b = var2;
      this.D = var3;
   }

   public void write(JsonWriter var1, E var2) {
      var1.value((String)this.b.Q(var2));
   }

   public E read(JsonReader var1) {
      long var2 = a ^ 56301861852740L;
      String var4 = var1.nextString();
      Object var5 = this.b.P(var4);
      if (var5 != null) {
         return var5;
      } else if (this.D) {
         throw new JsonParseException("invalid " + this.L + ":  " + var4);
      } else {
         return null;
      }
   }
}
