package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class TU {
   public static final TU B = g().k(new RT()).a();
   private final vL t;
   private final m2 v;
   private final i S;
   private int W;
   private RT M;
   private nD L;
   private int i;
   private static final long a = kt.a(1085439302450729036L, -5549502179025311965L, MethodHandles.lookup().lookupClass()).a(9311590527122L);

   private TU(i var1, int var2, RT var3, nD var4, int var5, vL var6, m2 var7) {
      this.S = var1;
      this.W = var2;
      this.M = var3;
      this.L = var4;
      this.i = var5;
      this.t = var6;
      this.v = var7;
   }

   public static TU Z(Rc var0, lm<?> var1) {
      return z(var0, var1.R().u());
   }

   public static TU z(Rc var0, vL var1) {
      long var2 = a ^ 102400474418557L;
      if (var0 instanceof mZ) {
         al var7 = new al(((mZ)var0).b());
         return g().s(z1.x(var7.toString())).a();
      } else {
         RT var4 = (RT)var0;
         Bx var5 = g();
         al var6 = (al)((Optional)Optional.ofNullable(var4.c("id")).map(Optional::of).orElseGet(TU::lambda$decode$0)).map(al::new).orElseThrow(TU::lambda$decode$1);
         var5.s(z1.x(var6.toString()));
         var5.k(var4.J("tag"));
         Optional var10000 = ((Optional)Optional.ofNullable(var4.r("Count")).map(Optional::of).orElseGet(TU::lambda$decode$2)).map(mh::P);
         Objects.requireNonNull(var5);
         var10000.ifPresent(var5::A);
         return var5.a();
      }
   }

   public static Rc J(lm<?> var0, TU var1) {
      return r(var1, var0.R().u());
   }

   public static Rc r(TU var0, vL var1) {
      long var2 = a ^ 132589599064843L;
      if (var1.K(vL.V_1_20_5)) {
         boolean var4 = var0.h() || var0.L == null || var0.L.N().isEmpty();
         if (var4) {
            return new mZ(var0.S.f().toString());
         }
      }

      RT var5 = new RT();
      var5.j("id", new mZ(var0.S.f().toString()));
      if (var1.X(vL.V_1_20_5)) {
         var5.j("Count", new mz(var0.o()));
         if (var0.M != null) {
            var5.j("tag", var0.M);
         }
      }

      return var5;
   }

   public int V() {
      return this.t.K(vL.V_1_20_5) ? (Integer)this.a(gZ.uW, 1) : this.v().u();
   }

   public i v() {
      if (this.t.K(vL.V_1_11)) {
         return this.h() ? z1.B6 : this.S;
      } else {
         return this.S;
      }
   }

   public int o() {
      if (this.t.K(vL.V_1_11)) {
         return this.h() ? 0 : this.W;
      } else {
         return this.W;
      }
   }

   public RT m() {
      return this.M;
   }

   public <T> T a(nW<T> var1, T var2) {
      return this.R() ? this.B().L(var1, var2) : this.v().b().L(var1, var2);
   }

   public boolean R() {
      return this.L != null && !this.L.N().isEmpty();
   }

   public nD B() {
      if (this.L == null) {
         this.L = new nD(this.S.b(), new HashMap(4));
      }

      return this.L;
   }

   public int U() {
      return this.i;
   }

   public boolean h() {
      boolean var1 = this.S == z1.B6 || this.W <= 0;
      if (!this.t.a(vL.V_1_12_2)) {
         return var1;
      } else {
         return var1 || this.i < -32768 || this.i > 65536;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TU)) {
         return false;
      } else {
         TU var2 = (TU)var1;
         return this.S.equals(var2.S) && this.W == var2.W && Objects.equals(this.M, var2.M) && Objects.equals(this.L, var2.L) && this.i == var2.i;
      }
   }

   public String toString() {
      long var1 = a ^ 67033746592510L;
      return this.h() ? "ItemStack[EMPTY]" : "ItemStack[" + this.o() + "x/" + this.V() + "x " + this.S.f() + (this.M != null ? ", nbt tag names=" + this.M.d() : "") + (this.i != -1 ? ", legacy data=" + this.i : "") + (this.L != null ? ", components=" + this.L.N() : "") + "]";
   }

   public static Bx g() {
      return new Bx();
   }

   private static Optional lambda$decode$2(RT var0) {
      long var1 = a ^ 47138882217233L;
      return Optional.ofNullable(var0.r("count"));
   }

   private static IllegalArgumentException lambda$decode$1(RT var0) {
      long var1 = a ^ 9134113035088L;
      return new IllegalArgumentException("No item type specified: " + var0.q().keySet());
   }

   private static Optional lambda$decode$0(RT var0) {
      long var1 = a ^ 27122103030201L;
      return Optional.ofNullable(var0.c("item"));
   }

   TU(i var1, int var2, RT var3, nD var4, int var5, vL var6, m2 var7, Td var8) {
      this(var1, var2, var3, var4, var5, var6, var7);
   }
}
