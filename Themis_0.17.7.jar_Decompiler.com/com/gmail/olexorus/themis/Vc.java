package com.gmail.olexorus.themis;

public final class vc extends v2 {
   public static final vc J = new vc(-1);
   public static final vc c = new vc(-16777216);
   public static final vc t = new vc(0);
   private final int r;

   public vc(int var1, int var2, int var3) {
      this(255, var1, var2, var3);
   }

   public vc(int var1, int var2, int var3, int var4) {
      super(var2, var3, var4);
      this.r = a8.c(var1, 0, 255);
   }

   public vc(float var1, float var2, float var3) {
      this(1.0F, var1, var2, var3);
   }

   public vc(float var1, float var2, float var3, float var4) {
      super(var2, var3, var4);
      this.r = a8.a(var1 * 255.0F);
   }

   public vc(int var1) {
      this(var1 >> 24 & 255, var1 >> 16 & 255, var1 >> 8 & 255, var1 & 255);
   }

   public static vc p(Rc var0, vL var1) {
      if (var0 instanceof mh) {
         return new vc(((mh)var0).P());
      } else {
         m_ var2 = (m_)var0;
         float var3 = ((mh)var2.t(0)).b();
         float var4 = ((mh)var2.t(1)).b();
         float var5 = ((mh)var2.t(2)).b();
         float var6 = ((mh)var2.t(3)).b();
         return new vc(var6, var3, var4, var5);
      }
   }

   public static Rc y(vc var0, vL var1) {
      if (var1.K(vL.V_1_21_2)) {
         return new mz(var0.V());
      } else {
         m_ var2 = new m_(Ay.W, 4);
         var2.Z(new m6((float)var0.F));
         var2.Z(new m6((float)var0.n));
         var2.Z(new m6((float)var0.Z));
         var2.Z(new m6((float)var0.r));
         return var2;
      }
   }

   public vc d() {
      return this;
   }

   public vc j(int var1) {
      return new vc(this.r, var1, this.n, this.Z);
   }

   public vc S(int var1) {
      return new vc(this.r, this.F, var1, this.Z);
   }

   public vc C(int var1) {
      return new vc(this.r, this.F, this.n, var1);
   }

   public int V() {
      return this.r << 24 | this.F << 16 | this.n << 8 | this.Z;
   }

   public vc j(v2 var1) {
      return new vc(this.r, this.F + var1.F, this.n + var1.n, this.Z + var1.Z);
   }

   public vc l(v2 var1) {
      return new vc(this.r, this.F - var1.F, this.n - var1.n, this.Z - var1.Z);
   }

   public vc f(v2 var1) {
      return var1.y() == 255 && var1.F == 255 && var1.n == 255 && var1.Z == 255 ? this : new vc(this.r * var1.y() / 255, this.F * var1.F / 255, this.n * var1.n / 255, this.Z * var1.Z / 255);
   }

   public vc R(vc var1) {
      int var2 = var1.r;
      if (var2 == 255) {
         return var1;
      } else if (var2 == 0) {
         return this;
      } else {
         int var3 = var2 + this.r * (255 - var2) / 255;
         return new vc(var3, L(var3, var2, this.F, var1.F), L(var3, var2, this.n, var1.n), L(var3, var2, this.Z, var1.Z));
      }
   }

   protected static int L(int var0, int var1, int var2, int var3) {
      return (var3 * var1 + var2 * (var0 - var1)) / var0;
   }

   public vc Q() {
      int var1 = (int)((float)this.F * 0.3F + (float)this.n * 0.59F + (float)this.Z * 0.11F);
      return new vc(this.r, var1, var1, var1);
   }

   public vc H(float var1) {
      return this.s(var1, var1, var1);
   }

   public vc s(float var1, float var2, float var3) {
      return new vc(this.r, (int)((float)this.F * var1), (int)((float)this.n * var2), (int)((float)this.Z * var3));
   }

   public vc Q(v2 var1, float var2) {
      return new vc(a8.s(var2, this.r, var1.y()), a8.s(var2, this.F, var1.F), a8.s(var2, this.n, var1.n), a8.s(var2, this.Z, var1.Z));
   }

   public int y() {
      return this.r;
   }
}
