package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public interface Cm extends GL, uW<Cm>, rp {
   long a = kt.a(-1603430495615398551L, -396231523513862107L, MethodHandles.lookup().lookupClass()).a(154311417359631L);

   oK E();

   oK X();

   oK c();

   zY R();

   static Cm X(lm<?> var0) {
      oK var1 = oK.g(var0);
      oK var2 = oK.g(var0);
      return new ip(var1, var2);
   }

   static void K(lm<?> var0, Cm var1) {
      oK.A(var0, var1.E());
      oK.A(var0, var1.c());
   }

   static Cm q(Rc var0, vL var1, z2 var2) {
      long var3 = a ^ 84588353202769L;
      RT var5 = (RT)var0;
      RT var6 = var5.J("chat");
      RT var7 = var5.J("narration");
      oK var8 = null;
      zY var9 = null;
      if (var1.X(vL.V_1_19_1)) {
         RT var10 = var5.J("overlay");
         if (var10 != null) {
            var10 = var10.J("description");
            if (var10 != null) {
               var8 = oK.i(var10, var1);
            }
         }

         if (var6 != null) {
            var6 = var6.J("description");
         }

         if (var7 != null) {
            var9 = (zY)rN.V(zY.ID_INDEX, var7.N("priority"));
            var7 = var7.J("description");
         }
      } else {
         Objects.requireNonNull(var6, "NBT chat does not exist");
         Objects.requireNonNull(var7, "NBT narration does not exist");
      }

      oK var12 = var6 == null ? null : oK.i(var6, var1);
      oK var11 = var7 == null ? null : oK.i(var7, var1);
      return new ip(var2, var12, var8, var11, var9);
   }
}
