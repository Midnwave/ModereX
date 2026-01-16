package com.gmail.olexorus.themis;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.lang.invoke.MethodHandles;

final class mF extends TypeAdapter<mx> {
   static final TypeAdapter<mx> R = (new mF()).nullSafe();
   private static final long a = kt.a(8022691786521507937L, -5075140608826540567L, MethodHandles.lookup().lookupClass()).a(207214086204627L);

   private mF() {
   }

   public void Y(JsonWriter var1, mx var2) {
      long var3 = a ^ 77933398195424L;
      var1.beginObject();
      var1.name("name");
      var1.value(var2.g());
      var1.name("value");
      var1.value(var2.l());
      if (var2.K() != null) {
         var1.name("signature");
         var1.value(var2.K());
      }

      var1.endObject();
   }

   public mx b(JsonReader var1) {
      long var2 = a ^ 132414554528402L;
      var1.beginObject();
      String var4 = null;
      String var5 = null;
      String var6 = null;

      while(var1.hasNext()) {
         String var7 = var1.nextName();
         if (var7.equals("name")) {
            var4 = var1.nextString();
         } else if (var7.equals("value")) {
            var5 = var1.nextString();
         } else if (var7.equals("signature")) {
            var6 = var1.nextString();
         } else {
            var1.skipValue();
         }
      }

      var1.endObject();
      if (var4 != null && var5 != null) {
         return Rv.G(var4, var5, var6);
      } else {
         throw new JsonParseException("A profile property requires both a name and value");
      }
   }
}
