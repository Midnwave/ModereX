package com.gmail.olexorus.themis;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.lang.invoke.MethodHandles;

final class B6 extends TypeAdapter<Ma> {
   static final TypeAdapter<Ma> O = (new B6()).nullSafe();
   private static final long a = kt.a(7986055707590177483L, 3552033561826641166L, MethodHandles.lookup().lookupClass()).a(131079422320363L);

   private B6() {
   }

   public Ma w(JsonReader var1) {
      long var2 = a ^ 136813534570995L;
      String var4 = var1.nextString();

      try {
         return Ma.b(var4);
      } catch (IllegalArgumentException var6) {
         throw new JsonParseException("Don't know how to turn " + var4 + " into a Position");
      }
   }

   public void R(JsonWriter var1, Ma var2) {
      var1.value(var2.G());
   }
}
