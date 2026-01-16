package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.stream.Stream;

public final class Eb implements nc, rA {
   private final Bb o;
   private final gK b;
   private static final long a = kt.a(459131152954075147L, -6123818224284310714L, MethodHandles.lookup().lookupClass()).a(255104544003442L);

   public static Eb z(String var0) {
      return new Eb(Bb.OPEN_URL, gK.m(var0));
   }

   public static Eb C(String var0) {
      return new Eb(Bb.OPEN_FILE, gK.m(var0));
   }

   public static Eb E(String var0) {
      return new Eb(Bb.RUN_COMMAND, gK.m(var0));
   }

   public static Eb o(String var0) {
      return new Eb(Bb.SUGGEST_COMMAND, gK.m(var0));
   }

   public static Eb Q(String var0) {
      long var1 = a ^ 134859473691395L;
      Objects.requireNonNull(var0, "page");
      return new Eb(Bb.CHANGE_PAGE, gK.j(Integer.parseInt(var0)));
   }

   public static Eb t(int var0) {
      return new Eb(Bb.CHANGE_PAGE, gK.j(var0));
   }

   public static Eb d(String var0) {
      return new Eb(Bb.COPY_TO_CLIPBOARD, gK.m(var0));
   }

   public static Eb O(u var0) {
      long var1 = a ^ 19787822380746L;
      Objects.requireNonNull(var0, "dialog");
      return new Eb(Bb.SHOW_DIALOG, gK.J(var0));
   }

   public static Eb s(v1 var0, String var1) {
      return A(var0, Ae.w(var1));
   }

   public static Eb A(v1 var0, Ae var1) {
      long var2 = a ^ 36772077737594L;
      Objects.requireNonNull(var0, "key");
      Objects.requireNonNull(var1, "nbt");
      return new Eb(Bb.CUSTOM, gK.B(var0, var1));
   }

   public static Eb C(Bb var0, String var1) {
      long var2 = a ^ 131876978364765L;
      if (var0 == Bb.CHANGE_PAGE) {
         return Q(var1);
      } else if (!var0.M().equals(EM.class)) {
         throw new IllegalArgumentException("Action " + var0 + " does not support string payloads");
      } else {
         return new Eb(var0, gK.m(var1));
      }
   }

   private Eb(Bb var1, gK var2) {
      long var3 = a ^ 97958358896722L;
      super();
      if (!var1.F(var2)) {
         throw new IllegalArgumentException("Action " + var1 + " does not support payload " + var2);
      } else {
         this.o = (Bb)Objects.requireNonNull(var1, "action");
         this.b = (gK)Objects.requireNonNull(var2, "payload");
      }
   }

   public Bb v() {
      return this.o;
   }

   public String a() {
      long var1 = a ^ 15010277735852L;
      if (this.b instanceof EM) {
         return ((EM)this.b).I();
      } else if (this.o == Bb.CHANGE_PAGE) {
         return String.valueOf(((l4)this.b).c());
      } else {
         throw new IllegalStateException("Payload is not a string payload, is " + this.b);
      }
   }

   public gK k() {
      return this.b;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Eb var2 = (Eb)var1;
         return this.o == var2.o && Objects.equals(this.b, var2.b);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.o.hashCode();
      var1 = 31 * var1 + this.b.hashCode();
      return var1;
   }

   public Stream<? extends rE> T() {
      long var1 = a ^ 119139280519026L;
      return Stream.of(rE.E("action", this.o), rE.E("payload", this.b));
   }

   public String toString() {
      return cH.M(this);
   }
}
