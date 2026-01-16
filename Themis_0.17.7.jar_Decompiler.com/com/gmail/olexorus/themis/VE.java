package com.gmail.olexorus.themis;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.lang.invoke.MethodHandles;
import java.util.Locale;

final class vE extends TypeAdapter<BX> {
   static final TypeAdapter<BX> n = (new vE(false)).nullSafe();
   static final TypeAdapter<BX> M = (new vE(true)).nullSafe();
   private final boolean F;
   private static final long a = kt.a(7189255573091952168L, -6541372131875895414L, MethodHandles.lookup().lookupClass()).a(220850269407982L);

   private vE(boolean var1) {
      this.F = var1;
   }

   public void S(JsonWriter var1, BX var2) {
      if (var2 instanceof oT) {
         var1.value((String)oT.x.Q((oT)var2));
      } else if (this.F) {
         var1.value((String)oT.x.Q(oT.W(var2)));
      } else {
         var1.value(B(var2));
      }

   }

   private static String B(BX var0) {
      long var1 = a ^ 88887723901473L;
      return String.format(Locale.ROOT, "%c%06X", '#', var0.Y());
   }

   public BX o(JsonReader var1) {
      BX var2 = A(var1.nextString());
      if (var2 == null) {
         return null;
      } else {
         return (BX)(this.F ? oT.W(var2) : var2);
      }
   }

   static BX A(String var0) {
      return var0.startsWith("#") ? BX.u(var0) : (BX)oT.x.P(var0);
   }
}
