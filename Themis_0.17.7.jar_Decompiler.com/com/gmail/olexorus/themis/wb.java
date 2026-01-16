package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public interface Wb extends GL, uW<Wb>, rp {
   long c = kt.a(-2622705990008147072L, -1270328953495578464L, MethodHandles.lookup().lookupClass()).a(183714113260506L);

   rt N();

   al M();

   static Wb u(lm<?> var0) {
      return (Wb)var0.y((VD)Jd.W());
   }

   static void M(lm<?> var0, Wb var1) {
      var0.j((GL)var1);
   }

   static Wb W(Rc var0, vL var1, z2 var2) {
      long var3 = c ^ 111386531324556L;
      RT var5 = (RT)var0;
      String var6 = var5.c("model");
      rt var7 = var6 != null ? rt.I(var6) : rt.NORMAL;
      al var8 = new al(var5.N("asset_id"));
      return new i9(var2, var7, var8);
   }
}
