package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class vx {
   private AP k;
   private CW B;
   private al m;
   private al y;
   private gC<Vf> A;
   private boolean o;
   private boolean c;
   private boolean D;
   private boolean Z;
   private boolean f;
   private CW K;
   private static final long a = kt.a(3894487422438069152L, 7853053864566127875L, MethodHandles.lookup().lookupClass()).a(256721739341969L);

   public vx(AP var1, CW var2, al var3, al var4, gC<Vf> var5, boolean var6, boolean var7, boolean var8, boolean var9, boolean var10, CW var11) {
      this.k = var1;
      this.B = var2;
      this.m = var3;
      this.y = var4;
      this.A = var5;
      this.o = var6;
      this.c = var7;
      this.D = var8;
      this.Z = var9;
      this.f = var10;
      this.K = var11;
   }

   public static vx j(lm<?> var0) {
      AP var1 = (AP)var0.w((Enum[])AP.values());
      CW var2 = CW.B(var0);
      al var3 = (al)var0.u(lm::R);
      al var4 = (al)var0.u(lm::R);
      gC var5 = (gC)var0.u(vx::lambda$read$0);
      boolean var6 = var0.P();
      boolean var7 = var0.P();
      boolean var8 = var0.P();
      boolean var9 = false;
      boolean var10 = false;
      CW var11 = b1.F9;
      if (var0.R().i(zZ.V_1_21_5)) {
         var9 = var0.P();
         if (var0.R().i(zZ.V_1_21_6)) {
            var10 = var0.P();
            var11 = CW.B(var0);
         }
      }

      return new vx(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static void k(lm<?> var0, vx var1) {
      var0.o((Enum)var1.k);
      CW.O(var0, var1.B);
      var0.l(var1.m, lm::T);
      var0.l(var1.y, lm::T);
      var0.l(var1.A, gC::n);
      var0.I(var1.o);
      var0.I(var1.c);
      var0.I(var1.D);
      if (var0.R().i(zZ.V_1_21_5)) {
         var0.I(var1.Z);
         if (var0.R().i(zZ.V_1_21_6)) {
            var0.I(var1.f);
            CW.O(var0, var1.K);
         }
      }

   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof vx)) {
         return false;
      } else {
         vx var2 = (vx)var1;
         if (this.o != var2.o) {
            return false;
         } else if (this.c != var2.c) {
            return false;
         } else if (this.D != var2.D) {
            return false;
         } else if (this.Z != var2.Z) {
            return false;
         } else if (this.f != var2.f) {
            return false;
         } else if (this.k != var2.k) {
            return false;
         } else if (!this.B.equals(var2.B)) {
            return false;
         } else if (!Objects.equals(this.m, var2.m)) {
            return false;
         } else if (!Objects.equals(this.y, var2.y)) {
            return false;
         } else {
            return !Objects.equals(this.A, var2.A) ? false : this.K.equals(var2.K);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.k, this.B, this.m, this.y, this.A, this.o, this.c, this.D, this.Z, this.f, this.K});
   }

   public String toString() {
      long var1 = a ^ 107139639632413L;
      return "ItemEquippable{slot=" + this.k + ", equipSound=" + this.B + ", assetId=" + this.m + ", cameraOverlay=" + this.y + ", allowedEntities=" + this.A + ", dispensable=" + this.o + ", swappable=" + this.c + ", damageOnHurt=" + this.D + ", equipOnInteract=" + this.Z + ", canBeSheared=" + this.f + ", shearingSound=" + this.K + '}';
   }

   private static gC lambda$read$0(lm var0) {
      return gC.P(var0, Gp::C);
   }
}
