package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

public final class n1 {
   private static final long a = kt.a(-7031500551402904189L, -1265938048384789402L, MethodHandles.lookup().lookupClass()).a(13393398415147L);

   public static TU A(lm<?> var0) {
      return var0.R().i(zZ.V_1_20_5) ? V(var0) : D(var0);
   }

   public static void w(lm<?> var0, TU var1) {
      TU var2 = var1 == null ? TU.B : var1;
      if (var0.R().i(zZ.V_1_20_5)) {
         k(var0, var2);
      } else {
         o(var0, var2);
      }

   }

   private static TU D(lm<?> var0) {
      boolean var1 = var0.R().i(zZ.V_1_13_2);
      if (var1 && !var0.P()) {
         return TU.B;
      } else {
         int var2 = var1 ? var0.Q() : var0.x();
         if (var2 < 0 && !var1) {
            return TU.B;
         } else {
            vL var3 = var0.R().u();
            i var4 = (i)z1.N().C(var3, var2);
            byte var5 = var0.M();
            short var6 = var3.X(vL.V_1_13) ? var0.x() : -1;
            RT var7 = var0.u();
            return TU.g().s(var4).A(var5).k(var7).k(var6).j(var0).a();
         }
      }
   }

   private static void o(lm<?> var0, TU var1) {
      if (var0.R().m(zZ.V_1_13_2)) {
         int var2 = var1.h() ? -1 : var1.v().f(var0.R().u());
         var0.f(var2);
         if (var2 != -1) {
            var0.u(var1.o());
            if (var0.R().m(zZ.V_1_13)) {
               var0.f(var1.U());
            }

            var0.G(var1.m());
         }
      } else if (var1.h()) {
         var0.I(false);
      } else {
         var0.I(true);
         var0.j((GL)var1.v());
         var0.u(var1.o());
         var0.G(var1.m());
      }

   }

   public static TU V(lm<?> var0) {
      return w(var0, false);
   }

   public static TU S(lm<?> var0) {
      return w(var0, true);
   }

   private static TU w(lm<?> var0, boolean var1) {
      long var2 = a ^ 71875843865532L;
      int var4 = var0.Q();
      if (var4 <= 0) {
         return TU.B;
      } else {
         i var5 = (i)var0.y((VD)z1.N());
         int var6 = var0.Q();
         int var7 = var0.Q();
         if (var6 == 0 && var7 == 0) {
            return TU.g().s(var5).A(var4).j(var0).a();
         } else {
            nD var8 = new nD(var5.B(var0.R().u()), new HashMap(var6 + var7));

            int var9;
            for(var9 = 0; var9 < var6; ++var9) {
               nW var10 = (nW)var0.y((VD)gZ.u());
               int var11;
               if (var1) {
                  int var12 = var0.Q();
                  if (var12 > NY.r(var0.g)) {
                     throw new RuntimeException("Component size " + var12 + " for " + var10.f() + " out of bounds");
                  }

                  var11 = NY.p(var0.g) + var12;
               } else {
                  var11 = -1;
               }

               Object var14 = var10.M(var0);
               if (var11 != -1) {
                  int var13 = NY.p(var0.g);
                  if (var13 != var11) {
                     throw new RuntimeException("Invalid component read for " + var10.f() + "; expected reader index " + var11 + ", got reader index " + var13);
                  }
               }

               var8.M(var10, var14);
            }

            for(var9 = 0; var9 < var7; ++var9) {
               var8.G((nW)var0.y((VD)gZ.u()));
            }

            return TU.g().s(var5).A(var4).B(var8).j(var0).a();
         }
      }
   }

   public static void k(lm<?> var0, TU var1) {
      K(var0, var1, false);
   }

   public static void L(lm<?> var0, TU var1) {
      K(var0, var1, true);
   }

   private static void K(lm<?> var0, TU var1, boolean var2) {
      if (var1.h()) {
         var0.u(0);
      } else {
         var0.E(var1.o());
         var0.j((GL)var1.v());
         if (!var1.R()) {
            var0.f(0);
         } else {
            Map var3 = var1.B().N();
            int var4 = 0;
            int var5 = 0;
            Iterator var6 = var3.entrySet().iterator();

            Entry var7;
            while(var6.hasNext()) {
               var7 = (Entry)var6.next();
               if (((Optional)var7.getValue()).isPresent()) {
                  ++var4;
               } else {
                  ++var5;
               }
            }

            var0.E(var4);
            var0.E(var5);
            var6 = var3.entrySet().iterator();

            while(var6.hasNext()) {
               var7 = (Entry)var6.next();
               if (((Optional)var7.getValue()).isPresent()) {
                  var0.E(((nW)var7.getKey()).f(var0.R().u()));
                  Runnable var8 = n1::lambda$writeModern$0;
                  if (var2) {
                     Object var9 = var0.g;
                     var0.g = NY.o(var9);
                     var8.run();
                     Object var10 = var0.g;
                     var0.g = var9;
                     var0.E(NY.r(var10));
                     NY.I(var0.g, var10);
                     NY.o(var10);
                  } else {
                     var8.run();
                  }
               }
            }

            var6 = var3.entrySet().iterator();

            while(var6.hasNext()) {
               var7 = (Entry)var6.next();
               if (!((Optional)var7.getValue()).isPresent()) {
                  var0.E(((nW)var7.getKey()).f(var0.R().u()));
               }
            }

         }
      }
   }

   private static void lambda$writeModern$0(Entry var0, lm var1) {
      ((nW)var0.getKey()).r(var1, ((Optional)var0.getValue()).get());
   }
}
