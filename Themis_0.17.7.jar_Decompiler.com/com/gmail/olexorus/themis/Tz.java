package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

public class tz extends tO<tz> {
   private List<tO<?>> u;
   private static final long a = kt.a(-6604937418721731646L, -7849358281871649143L, MethodHandles.lookup().lookupClass()).a(106273525039441L);

   public tz(List<tO<?>> var1) {
      super(B_.H);
      this.u = var1;
   }

   public static tz s(lm<?> var0) {
      List var1 = var0.j(tO::X);
      return new tz(var1);
   }

   public static void a(lm<?> var0, tz var1) {
      var0.D(var1.u, tO::A);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof tz)) {
         return false;
      } else {
         tz var2 = (tz)var1;
         return this.u.equals(var2.u);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.u);
   }

   public String toString() {
      long var1 = a ^ 31091869241269L;
      return "CompositeSlotDisplay{contents=" + this.u + '}';
   }
}
