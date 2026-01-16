package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class TL {
   private String l;
   private UUID M;
   private List<bT> A;
   private u5 F;
   private static final long a = kt.a(-1796045598143836381L, 4927923460954865960L, MethodHandles.lookup().lookupClass()).a(155693944820290L);

   public TL(String var1, UUID var2, List<bT> var3) {
      this(var1, var2, var3, u5.U);
   }

   public TL(String var1, UUID var2, List<bT> var3, u5 var4) {
      this.l = var1;
      this.M = var2;
      this.A = var3;
      this.F = var4;
   }

   public static TL x(lm<?> var0) {
      boolean var3 = var0.R().m(zZ.V_1_21_9) || !var0.P();
      String var1;
      UUID var2;
      if (!var3) {
         var2 = var0.V();
         var1 = var0.m(16);
      } else {
         var1 = (String)var0.u(TL::lambda$read$0);
         var2 = (UUID)var0.u(lm::V);
      }

      List var4 = var0.j(bT::b);
      u5 var5 = var0.R().i(zZ.V_1_21_9) ? u5.G(var0) : u5.U;
      return new TL(var1, var2, var4, var5);
   }

   public static void i(lm<?> var0, TL var1) {
      boolean var2;
      if (var0.R().i(zZ.V_1_21_9)) {
         var2 = var1.l == null || var1.M == null;
         var0.I(!var2);
      } else {
         var2 = true;
      }

      if (!var2) {
         var0.y(var1.M);
         var0.a(var1.l, 16);
      } else {
         var0.l(var1.l, TL::lambda$write$1);
         var0.l(var1.M, lm::y);
      }

      var0.D(var1.A, bT::U);
      if (var0.R().i(zZ.V_1_21_9)) {
         u5.a(var0, var1.F);
      }

   }

   public static TL l(Rc var0, lm<?> var1) {
      long var2 = a ^ 114176953056160L;
      if (var0 instanceof mZ) {
         String var9 = ((mZ)var0).b();
         return new TL(var9, (UUID)null, new ArrayList());
      } else {
         RT var4 = (RT)var0;
         UUID var5 = (UUID)var4.M("id", g9.X, var1);
         String var6 = var4.c("name");
         List var7 = (List)var4.E("properties", bT.a, ArrayList::new, var1);
         u5 var8 = u5.W(var4, var1);
         return new TL(var6, var5, var7, var8);
      }
   }

   public static Rc P(lm<?> var0, TL var1) {
      long var2 = a ^ 21876583591287L;
      RT var4 = new RT();
      if (var1.M != null) {
         var4.X("id", var1.M, g9.X, var0);
      }

      if (var1.l != null) {
         var4.j("name", new mZ(var1.l));
      }

      if (!var1.A.isEmpty()) {
         var4.X("properties", var1.A, bT.a, var0);
      }

      u5.D(var4, var0, var1.F);
      return var4;
   }

   public static TL h(Rv var0) {
      List var1 = var0.d();
      ArrayList var2 = new ArrayList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         mx var4 = (mx)var3.next();
         var2.add(bT.i(var4));
      }

      return new TL(var0.j(), var0.Y(), var2);
   }

   public List<mx> A() {
      if (this.A.isEmpty()) {
         return Collections.emptyList();
      } else {
         ArrayList var1 = new ArrayList(this.A.size());
         Iterator var2 = this.A.iterator();

         while(var2.hasNext()) {
            bT var3 = (bT)var2.next();
            var1.add(var3.o());
         }

         return Collections.unmodifiableList(var1);
      }
   }

   public String z() {
      return this.l;
   }

   public UUID r() {
      return this.M;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TL)) {
         return false;
      } else {
         TL var2 = (TL)var1;
         if (!Objects.equals(this.l, var2.l)) {
            return false;
         } else if (!Objects.equals(this.M, var2.M)) {
            return false;
         } else {
            return !this.A.equals(var2.A) ? false : this.F.equals(var2.F);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.l, this.M, this.A, this.F});
   }

   private static void lambda$write$1(lm var0, String var1) {
      var0.a(var1, 16);
   }

   private static String lambda$read$0(lm var0) {
      return var0.m(16);
   }
}
