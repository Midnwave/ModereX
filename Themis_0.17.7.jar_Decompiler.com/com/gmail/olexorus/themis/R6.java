package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

class r6 implements tw<Af> {
   private static final long a = kt.a(375305096357939009L, -3032764302407770686L, MethodHandles.lookup().lookupClass()).a(34499522313174L);

   public Af l(Rc var1, lm<?> var2) {
      long var3 = a ^ 55373657809449L;
      if (var1 instanceof mh) {
         return new Af(((mh)var1).b(), 1.0F);
      } else {
         RT var5 = (RT)var1.s(RT.class);
         float var6 = var5.p("value").floatValue();
         float var7 = var5.L("alpha", 1.0F).floatValue();
         return new Af(var6, var7);
      }
   }

   public Rc p(lm<?> var1, Af var2) {
      long var3 = a ^ 116961141852756L;
      if (Af.W(var2) == 1.0F) {
         return new m6(Af.Y(var2));
      } else {
         RT var5 = new RT();
         var5.j("value", new m6(Af.Y(var2)));
         var5.j("alpha", new m6(Af.W(var2)));
         return var5;
      }
   }
}
