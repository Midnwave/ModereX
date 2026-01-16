package com.gmail.olexorus.themis;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.util.UUID;

final class Ef extends TypeAdapter<UUID> {
   private final boolean U;

   static TypeAdapter<UUID> S(C var0) {
      return (new Ef((Boolean)var0.x(ci.c))).nullSafe();
   }

   private Ef(boolean var1) {
      this.U = var1;
   }

   public void D(JsonWriter var1, UUID var2) {
      if (this.U) {
         int var3 = (int)(var2.getMostSignificantBits() >> 32);
         int var4 = (int)(var2.getMostSignificantBits() & 4294967295L);
         int var5 = (int)(var2.getLeastSignificantBits() >> 32);
         int var6 = (int)(var2.getLeastSignificantBits() & 4294967295L);
         var1.beginArray().value((long)var3).value((long)var4).value((long)var5).value((long)var6).endArray();
      } else {
         var1.value(var2.toString());
      }

   }

   public UUID Z(JsonReader var1) {
      if (var1.peek() == JsonToken.BEGIN_ARRAY) {
         var1.beginArray();
         int var2 = var1.nextInt();
         int var3 = var1.nextInt();
         int var4 = var1.nextInt();
         int var5 = var1.nextInt();
         var1.endArray();
         return new UUID((long)var2 << 32 | (long)var3 & 4294967295L, (long)var4 << 32 | (long)var5 & 4294967295L);
      } else {
         return UUID.fromString(var1.nextString());
      }
   }
}
