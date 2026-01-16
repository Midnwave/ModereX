package com.gmail.olexorus.themis;

import java.util.Locale;

public class Nz {
   al n;
   float W = 0.0F;
   float J = 0.0F;
   boolean Q;
   boolean c;
   boolean a;
   boolean u = false;
   boolean A;
   zd h;

   public Nz e(String var1) {
      this.n = new al(var1.toLowerCase(Locale.ROOT));
      return this;
   }

   public Nz A(float var1) {
      this.W = var1;
      return this;
   }

   public Nz Z(float var1) {
      this.J = var1;
      return this;
   }

   public Nz c(boolean var1) {
      this.Q = var1;
      return this;
   }

   public Nz U(boolean var1) {
      this.c = var1;
      return this;
   }

   public Nz V(boolean var1) {
      this.a = var1;
      return this;
   }

   public Nz M(boolean var1) {
      this.u = var1;
      return this;
   }

   public Nz R(zd var1) {
      this.h = var1;
      return this;
   }

   public Nz L(boolean var1) {
      this.A = var1;
      return this;
   }

   public vM e() {
      return ((cB)rS.u().h(this.n.R(), this::lambda$build$0)).X();
   }

   private cB lambda$build$0(z2 var1) {
      return (new vM(var1, this.W, this.J, this.Q, this.c, this.a, this.u, this.A, this.h)).s();
   }
}
