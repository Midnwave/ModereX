package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.stream.Stream;

final class MS extends MN implements JR {
   private final v1 o;
   private final Ae v;
   private static final long b = kt.a(1370905831444707658L, -2968512077515432507L, MethodHandles.lookup().lookupClass()).a(136217684734963L);

   MS(v1 var1, Ae var2) {
      this.o = var1;
      this.v = var2;
   }

   public v1 m() {
      return this.o;
   }

   public String J() {
      return this.v.c();
   }

   public Ae D() {
      return this.v;
   }

   public Stream<? extends rE> T() {
      long var1 = b ^ 54880039046149L;
      return Stream.of(rE.E("key", this.o), rE.E("nbt", this.v));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MS var2 = (MS)var1;
         return Objects.equals(this.o, var2.o) && Objects.equals(this.v, var2.v);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.o.hashCode();
      var1 = 31 * var1 + this.v.hashCode();
      return var1;
   }
}
