package com.gmail.olexorus.themis;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

final class TE extends TypeAdapter<or> {
   private final Gson t;
   private final boolean I;
   private final gk O;
   private static final long a = kt.a(-4160167708706772637L, -8809417183242292535L, MethodHandles.lookup().lookupClass()).a(219401619118298L);

   static TypeAdapter<or> O(Gson var0, C var1) {
      return (new TE(var0, (Boolean)var1.x(ci.o), (gk)var1.x(ci.M))).nullSafe();
   }

   private TE(Gson var1, boolean var2, gk var3) {
      this.t = var1;
      this.I = var2;
      this.O = var3;
   }

   public or Q(JsonReader var1) {
      long var2 = a ^ 24490174508556L;
      var1.beginObject();
      v1 var4 = null;
      int var5 = 1;
      Ae var6 = null;
      HashMap var7 = null;

      while(true) {
         while(var1.hasNext()) {
            String var8 = var1.nextName();
            if (var8.equals("id")) {
               var4 = (v1)this.t.fromJson(var1, zK.s);
            } else if (var8.equals("count")) {
               var5 = var1.nextInt();
            } else if (var8.equals("tag")) {
               JsonToken var13 = var1.peek();
               if (var13 != JsonToken.STRING && var13 != JsonToken.NUMBER) {
                  if (var13 == JsonToken.BOOLEAN) {
                     if (Tb.R) {
                        var6 = Ae.w(String.valueOf(var1.nextBoolean()));
                     } else {
                        var6 = Ae.k(String.valueOf(var1.nextBoolean()));
                     }
                  } else {
                     if (var13 != JsonToken.NULL) {
                        throw new JsonParseException("Expected tag to be a string");
                     }

                     var1.nextNull();
                  }
               } else if (Tb.R) {
                  var6 = Ae.w(var1.nextString());
               } else {
                  var6 = Ae.k(var1.nextString());
               }
            } else if (!var8.equals("components")) {
               var1.skipValue();
            } else {
               var1.beginObject();

               v1 var10;
               boolean var11;
               JsonElement var12;
               for(; var1.peek() != JsonToken.END_OBJECT; var7.put(var10, var11 ? Cl.b() : Rz.g(var12))) {
                  String var9 = var1.nextName();
                  if (var9.startsWith("!")) {
                     var10 = v1.t(var9.substring(1));
                     var11 = true;
                  } else {
                     var10 = v1.t(var9);
                     var11 = false;
                  }

                  var12 = (JsonElement)this.t.fromJson(var1, JsonElement.class);
                  if (var7 == null) {
                     var7 = new HashMap();
                  }
               }

               var1.endObject();
            }
         }

         if (var4 == null) {
            throw new JsonParseException("Not sure how to deserialize show_item hover event");
         }

         var1.endObject();
         if (var7 != null) {
            return or.T(var4, var5, var7);
         }

         return Tb.q(var4, var5, var6);
      }
   }

   public void H(JsonWriter var1, or var2) {
      long var3 = a ^ 17532348631871L;
      var1.beginObject();
      var1.name("id");
      this.t.toJson(var2.H(), zK.s, var1);
      int var5 = var2.z();
      if (var5 != 1 || this.I) {
         var1.name("count");
         var1.value((long)var5);
      }

      Map var6 = !Tb.m ? Collections.emptyMap() : var2.s();
      if (!var6.isEmpty() && this.O != gk.EMIT_LEGACY_NBT) {
         var1.name("components");
         var1.beginObject();
         Iterator var7 = var2.H(Rz.class).entrySet().iterator();

         while(var7.hasNext()) {
            Entry var8 = (Entry)var7.next();
            JsonElement var9 = ((Rz)var8.getValue()).r();
            if (var9 instanceof JsonNull) {
               var1.name("!" + ((v1)var8.getKey()).X());
               var1.beginObject().endObject();
            } else {
               var1.name(((v1)var8.getKey()).X());
               this.t.toJson(var9, var1);
            }
         }

         var1.endObject();
      } else if (this.O != gk.EMIT_DATA_COMPONENTS) {
         z(var1, var2);
      }

      var1.endObject();
   }

   private static void z(JsonWriter var0, or var1) {
      long var2 = a ^ 30210383558063L;
      Ae var4 = var1.i();
      if (var4 != null) {
         var0.name("tag");
         var0.value(var4.c());
      }

   }
}
