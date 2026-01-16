package com.gmail.olexorus.themis;

import java.util.function.Function;

public class i4<T> extends id implements nW<T> {
   private final MO<T> F;
   private final Gw<T> k;
   private final Vl<T> L;
   private final bw<T> g;

   public i4(z2 var1, MO<T> var2, Gw<T> var3) {
      this(var1, var2, var3, (Vl)null, (bw)null);
   }

   public i4(z2 var1, Vl<T> var2, bw<T> var3) {
      this(var1, (MO)null, (Gw)null, var2, var3);
   }

   public i4(z2 var1, MO<T> var2, Gw<T> var3, Vl<T> var4, bw<T> var5) {
      super(var1);
      this.F = var2;
      this.k = var3;
      this.L = var4;
      this.g = var5;
   }

   public T M(lm<?> var1) {
      return this.F != null ? this.F.apply(var1) : null;
   }

   public void r(lm<?> var1, T var2) {
      if (this.k != null) {
         this.k.accept(var1, var2);
      }

   }

   public T E(Rc var1, vL var2) {
      if (this.L != null) {
         return this.L.q(var1, var2);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public Rc z(T var1, vL var2) {
      if (this.g != null) {
         return this.g.L(var1, var2);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public <Z> nW<Z> C(Function<T, Z> var1, Function<Z, T> var2) {
      MO var3 = this.F != null ? this::lambda$legacyMap$0 : null;
      Gw var4 = this.k != null ? this::lambda$legacyMap$1 : null;
      Vl var5 = this.L != null ? this::lambda$legacyMap$2 : null;
      bw var6 = this.g != null ? this::lambda$legacyMap$3 : null;
      return new i4(this.f, var3, var4, var5, var6);
   }

   private Rc lambda$legacyMap$3(Function var1, Object var2, vL var3) {
      return this.g.L(var1.apply(var2), var3);
   }

   private Object lambda$legacyMap$2(Function var1, Rc var2, vL var3) {
      return var1.apply(this.L.q(var2, var3));
   }

   private void lambda$legacyMap$1(Function var1, lm var2, Object var3) {
      this.k.accept(var2, var1.apply(var3));
   }

   private Object lambda$legacyMap$0(Function var1, lm var2) {
      return var1.apply(this.F.apply(var2));
   }
}
