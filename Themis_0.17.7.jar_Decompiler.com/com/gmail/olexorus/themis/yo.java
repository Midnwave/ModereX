package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class yO extends lm<yO> {
   private int a;
   private List<String> p;
   private String B;
   private TU c;
   private Boolean P;
   private static final long b = kt.a(5570420510886177483L, -3202362551922650719L, MethodHandles.lookup().lookupClass()).a(185224970936444L);

   public void t() {
      long var1 = b ^ 114387964913429L;
      if (this.I.i(zZ.V_1_17_1)) {
         boolean var3 = this.I.i(zZ.V_1_21_2);
         int var4 = var3 ? 100 : 200;
         int var5 = var3 ? 1024 : 8192;
         this.a = this.Q();
         int var6 = this.Q();
         if (var6 > var4) {
            throw new IllegalStateException("Page count " + var6 + " is larger than limit of " + var4);
         }

         this.p = new ArrayList(var6);

         for(int var7 = 0; var7 < var6; ++var7) {
            this.p.add(this.m(var5));
         }

         this.B = (String)this.u(yO::lambda$read$0);
      } else {
         this.c = this.u();
         this.P = this.P();
         this.a = this.Q();
      }

   }

   public void d() {
      if (this.I.i(zZ.V_1_17_1)) {
         boolean var1 = this.I.i(zZ.V_1_21_2);
         int var2 = var1 ? 1024 : 8192;
         this.E(this.a);
         this.E(this.p.size());
         Iterator var3 = this.p.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            this.a(var4, var2);
         }

         this.l(this.B, yO::lambda$write$1);
      } else {
         this.m(this.c);
         this.I(this.P);
         this.E(this.a);
      }

   }

   public void v(yO var1) {
      this.a = var1.a;
      this.p = var1.p;
      this.B = var1.B;
      this.c = var1.c;
      this.P = var1.P;
   }

   private static void lambda$write$1(boolean var0, lm var1, String var2) {
      int var3 = var0 ? 32 : 128;
      var1.a(var2, var3);
   }

   private static String lambda$read$0(boolean var0, lm var1) {
      int var2 = var0 ? 32 : 128;
      return var1.m(var2);
   }
}
