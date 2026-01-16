package com.gmail.olexorus.themis;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.lang.invoke.MethodHandles;

final class tf extends TypeAdapter<Vd> {
   private final boolean A;
   private static final long a = kt.a(7619779566698821927L, 4954763420473964966L, MethodHandles.lookup().lookupClass()).a(63005537571720L);

   static TypeAdapter<Vd> c(C var0) {
      return (new tf(var0.x(ci.k) == n7.EMIT_ARRAY)).nullSafe();
   }

   private tf(boolean var1) {
      this.A = var1;
   }

   public void n(JsonWriter var1, Vd var2) {
      if (this.A) {
         var1.beginArray().value((double)Z(var2.k())).value((double)Z(var2.v())).value((double)Z(var2.D())).value((double)Z(var2.z())).endArray();
      } else {
         var1.value((long)var2.E());
      }

   }

   public Vd e(JsonReader var1) {
      long var2 = a ^ 117982427245487L;
      if (var1.peek() == JsonToken.BEGIN_ARRAY) {
         var1.beginArray();
         double var4 = var1.nextDouble();
         double var6 = var1.nextDouble();
         double var8 = var1.nextDouble();
         double var10 = var1.nextDouble();
         if (var1.peek() != JsonToken.END_ARRAY) {
            throw new JsonParseException("Failed to parse shadow colour at " + var1.getPath() + ": expected end of 4-element array but got " + var1.peek() + " instead.");
         } else {
            var1.endArray();
            return Vd.D(L(var4), L(var6), L(var8), L(var10));
         }
      } else {
         return Vd.C(var1.nextInt());
      }
   }

   static float Z(int var0) {
      return (float)var0 / 255.0F;
   }

   static int L(double var0) {
      return (int)((float)var0 * 255.0F);
   }
}
