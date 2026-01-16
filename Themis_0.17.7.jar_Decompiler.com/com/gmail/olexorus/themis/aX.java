package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class Ax {
   private final String c;
   private final X R;
   private final boolean h;
   private static final long a = kt.a(93952688204086595L, -942299568022001848L, MethodHandles.lookup().lookupClass()).a(17726042681982L);

   public Ax(String var1, X var2, boolean var3) {
      this.c = var1;
      this.R = var2;
      this.h = var3;
   }

   public static Ax H(Rc var0, lm<?> var1) {
      long var2 = a ^ 6362445195910L;
      if (var0 instanceof mZ) {
         return new Ax(((mZ)var0).b(), (X)null, false);
      } else {
         RT var4 = (RT)var0;
         String var5 = var4.N("id");
         X var6 = (X)var4.M("display", com.gmail.olexorus.themis.h.z(var1), var1);
         boolean var7 = var4.v("initial", false);
         return new Ax(var5, var6, var7);
      }
   }

   public static Rc R(lm<?> var0, Ax var1) {
      long var2 = a ^ 1712422010740L;
      RT var4 = new RT();
      var4.j("id", new mZ(var1.c));
      if (var1.R != null) {
         var4.X("display", var1.R, com.gmail.olexorus.themis.h.z(var0), var0);
      }

      if (var1.h) {
         var4.j("initial", new mA(true));
      }

      return var4;
   }

   static boolean e(Ax var0) {
      return var0.h;
   }
}
