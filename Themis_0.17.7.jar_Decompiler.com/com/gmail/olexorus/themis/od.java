package com.gmail.olexorus.themis;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.lang.invoke.MethodHandles;
import java.util.UUID;

final class Od extends TypeAdapter<tY> {
   private final Gson q;
   private final boolean V;
   private static final long a = kt.a(-2662313869188441305L, 8541775098447502598L, MethodHandles.lookup().lookupClass()).a(31461181131500L);

   static TypeAdapter<tY> z(Gson var0, C var1) {
      return (new Od(var0, (Boolean)var1.x(ci.V))).nullSafe();
   }

   private Od(Gson var1, boolean var2) {
      this.q = var1;
      this.V = var2;
   }

   public tY K(JsonReader var1) {
      long var2 = a ^ 46163359157590L;
      var1.beginObject();
      v1 var4 = null;
      UUID var5 = null;
      X var6 = null;

      while(var1.hasNext()) {
         String var7 = var1.nextName();
         byte var9 = -1;
         switch(var7.hashCode()) {
         case 3355:
            if (var7.equals("id")) {
               var9 = 0;
            }
            break;
         case 3373707:
            if (var7.equals("name")) {
               var9 = 3;
            }
            break;
         case 3575610:
            if (var7.equals("type")) {
               var9 = 1;
            }
            break;
         case 3601339:
            if (var7.equals("uuid")) {
               var9 = 2;
            }
         }

         switch(var9) {
         case 0:
            if (var1.peek() == JsonToken.BEGIN_ARRAY) {
               var5 = (UUID)this.q.fromJson(var1, UUID.class);
            } else {
               String var10 = var1.nextString();
               if (var10.contains(":")) {
                  var4 = v1.t(var10);
               }

               try {
                  var5 = UUID.fromString(var10);
               } catch (IllegalArgumentException var14) {
                  try {
                     var4 = v1.t(var10);
                  } catch (uj var13) {
                  }
               }
            }
            break;
         case 1:
            var4 = (v1)this.q.fromJson(var1, v1.class);
            break;
         case 2:
            var5 = (UUID)this.q.fromJson(var1, UUID.class);
            break;
         case 3:
            var6 = (X)this.q.fromJson(var1, zK.N);
            break;
         default:
            var1.skipValue();
         }
      }

      if (var4 != null && var5 != null) {
         var1.endObject();
         return Tb.U(var4, var5, var6);
      } else {
         throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized");
      }
   }

   public void v(JsonWriter var1, tY var2) {
      long var3 = a ^ 81274912455907L;
      var1.beginObject();
      var1.name(this.V ? "type" : "id");
      this.q.toJson(var2.L(), zK.s, var1);
      var1.name(this.V ? "id" : "uuid");
      this.q.toJson(var2.H(), zK.z, var1);
      X var5 = var2.u();
      if (var5 != null) {
         var1.name("name");
         this.q.toJson(var5, zK.N, var1);
      }

      var1.endObject();
   }
}
