package com.gmail.olexorus.themis;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

final class tn extends TypeAdapter<v1> {
   static final TypeAdapter<v1> B = (new tn()).nullSafe();

   private tn() {
   }

   public void d(JsonWriter var1, v1 var2) {
      var1.value(var2.X());
   }

   public v1 y(JsonReader var1) {
      return v1.t(var1.nextString());
   }
}
