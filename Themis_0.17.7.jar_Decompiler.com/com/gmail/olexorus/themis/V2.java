package com.gmail.olexorus.themis;

import java.util.Objects;

public class v2 implements r9 {
   public static final v2 e = new v2(-1);
   public static final v2 q = new v2(-16777216);
   protected final int F;
   protected final int n;
   protected final int Z;

   public v2(int var1, int var2, int var3) {
      this.F = a8.c(var1, 0, 255);
      this.n = a8.c(var2, 0, 255);
      this.Z = a8.c(var3, 0, 255);
   }

   public v2(float var1, float var2, float var3) {
      this(a8.a(var1 * 255.0F), a8.a(var2 * 255.0F), a8.a(var3 * 255.0F));
   }

   public v2(int var1) {
      this(var1 >> 16 & 255, var1 >> 8 & 255, var1 & 255);
   }

   public static v2 m(lm<?> var0) {
      return new v2(var0.f());
   }

   public static void B(lm<?> var0, v2 var1) {
      var0.L(var1.V());
   }

   public static v2 M(lm<?> var0) {
      return new v2(var0.h(), var0.h(), var0.h());
   }

   public static void X(lm<?> var0, v2 var1) {
      var0.u(var1.F);
      var0.u(var1.n);
      var0.u(var1.Z);
   }

   public static v2 u(Rc var0, lm<?> var1) {
      return o(var0, var1.R().u());
   }

   public static v2 o(Rc var0, vL var1) {
      if (var0 instanceof mh) {
         return new v2(((mh)var0).P());
      } else {
         m_ var2 = (m_)var0;
         float var3 = ((mh)var2.t(0)).b();
         float var4 = ((mh)var2.t(1)).b();
         float var5 = ((mh)var2.t(2)).b();
         return new v2(var3, var4, var5);
      }
   }

   public static Rc j(v2 var0, vL var1) {
      if (var1.K(vL.V_1_21_2)) {
         return new mz(var0.V());
      } else {
         m_ var2 = new m_(Ay.W, 3);
         var2.Z(new m6((float)var0.F));
         var2.Z(new m6((float)var0.n));
         var2.Z(new m6((float)var0.Z));
         return var2;
      }
   }

   public vc d() {
      return this.o(255);
   }

   public vc o(int var1) {
      return new vc(var1, this.F, this.n, this.Z);
   }

   public v2 u(int var1) {
      return new v2(var1, this.n, this.Z);
   }

   public v2 s(int var1) {
      return new v2(this.F, var1, this.Z);
   }

   public v2 x(int var1) {
      return new v2(this.F, this.n, var1);
   }

   public int V() {
      return this.F << 16 | this.n << 8 | this.Z;
   }

   public v2 o(v2 var1) {
      return new v2(this.F + var1.F, this.n + var1.n, this.Z + var1.Z);
   }

   public v2 q(v2 var1) {
      return new v2(this.F - var1.F, this.n - var1.n, this.Z - var1.Z);
   }

   public v2 X(v2 var1) {
      if (var1 instanceof vc) {
         return var1.X(this);
      } else if (this.F == 255 && this.n == 255 && this.Z == 255) {
         return var1;
      } else {
         return var1.F == 255 && var1.n == 255 && var1.Z == 255 ? this : new v2(this.F * var1.F / 255, this.n * var1.n / 255, this.Z * var1.Z / 255);
      }
   }

   public vc R(vc var1) {
      int var2 = var1.y();
      if (var2 == 255) {
         return var1;
      } else if (var2 == 0) {
         return this.d();
      } else {
         int var3 = var2 + (255 - var2);
         return new vc(var3, o(var3, var2, this.F, var1.F), o(var3, var2, this.n, var1.n), o(var3, var2, this.Z, var1.Z));
      }
   }

   protected static int o(int var0, int var1, int var2, int var3) {
      return (var3 * var1 + var2 * (var0 - var1)) / var0;
   }

   public v2 s() {
      int var1 = (int)((float)this.F * 0.3F + (float)this.n * 0.59F + (float)this.Z * 0.11F);
      return new v2(var1, var1, var1);
   }

   public v2 z(float var1) {
      return this.E(var1, var1, var1);
   }

   public v2 E(float var1, float var2, float var3) {
      return new v2((int)((float)this.F * var1), (int)((float)this.n * var2), (int)((float)this.Z * var3));
   }

   public v2 W(v2 var1, float var2) {
      return new v2(a8.s(var2, this.F, var1.F), a8.s(var2, this.n, var1.n), a8.s(var2, this.Z, var1.Z));
   }

   public int y() {
      return 255;
   }

   public int k() {
      return this.F;
   }

   public int v() {
      return this.n;
   }

   public int D() {
      return this.Z;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof v2) {
         return this.V() == ((v2)var1).V();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.V()});
   }
}
