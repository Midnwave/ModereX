package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

class bl implements tw<CW> {
   private static final long a = kt.a(-4480459700096616029L, 5140448565710045126L, MethodHandles.lookup().lookupClass()).a(162312731467878L);

   public CW i(Rc var1, lm<?> var2) {
      long var3 = a ^ 128879331043733L;
      if (var1 instanceof mZ) {
         return b1.P(((mZ)var1).b());
      } else {
         RT var5 = (RT)var1.s(RT.class);
         al var6 = (al)var5.g("sound_is", al.F, var2);
         Float var7 = (Float)var5.M("range", g9.x, var2);
         return new cZ(var6, var7);
      }
   }

   public Rc A(lm<?> var1, CW var2) {
      long var3 = a ^ 79005044185456L;
      if (var2.z()) {
         return new mZ(var2.f().toString());
      } else {
         RT var5 = new RT();
         var5.X("sound_id", var2.l(), al.F, var1);
         if (var2.S() != null) {
            var5.X("range", var2.S(), g9.x, var1);
         }

         return var5;
      }
   }
}
