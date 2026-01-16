package com.gmail.olexorus.themis;

import java.io.InputStream;
import java.util.function.Function;

public class lo {
   private Vs F;
   private boolean E;
   private boolean p;
   private boolean q;
   private boolean b;
   private boolean i;
   private boolean J;
   private boolean S;
   private Function<String, InputStream> a;

   public lo() {
      this.F = Vs.MILLIS;
      this.E = true;
      this.p = true;
      this.q = false;
      this.b = false;
      this.i = false;
      this.J = true;
      this.S = true;
      this.a = lo::lambda$new$0;
   }

   public lo D(Vs var1) {
      this.F = var1;
      return this;
   }

   public lo x(boolean var1) {
      this.E = var1;
      return this;
   }

   public lo Z(boolean var1) {
      this.p = var1;
      return this;
   }

   public lo s(boolean var1) {
      this.b = var1;
      return this;
   }

   public boolean J() {
      return this.E;
   }

   public boolean j() {
      return this.p;
   }

   public boolean D() {
      return this.q;
   }

   public boolean P() {
      return this.b;
   }

   public boolean Z() {
      return this.i;
   }

   public boolean o() {
      return this.J;
   }

   public boolean b() {
      return this.S;
   }

   public Function<String, InputStream> M() {
      return this.a;
   }

   public Vs S() {
      return this.F;
   }

   private static InputStream lambda$new$0(String var0) {
      return lo.class.getClassLoader().getResourceAsStream(var0);
   }
}
