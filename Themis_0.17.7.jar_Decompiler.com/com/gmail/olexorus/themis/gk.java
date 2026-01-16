package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public final class GK extends G0 implements CL<Integer>, O0<Integer> {
   public static final O2 F = new O2((MH)null);
   private static final GK o = new GK(1, 0);
   private static final long b = kt.a(5634469224324429787L, 3258326121818735464L, MethodHandles.lookup().lookupClass()).a(109706198215788L);

   public GK(int var1, int var2) {
      super(var1, var2, 1);
   }

   public Integer f() {
      return this.Y();
   }

   public Integer y() {
      return this.W();
   }

   public boolean u() {
      return this.Y() > this.W();
   }

   public boolean equals(Object var1) {
      return var1 instanceof GK && (this.u() && ((GK)var1).u() || this.Y() == ((GK)var1).Y() && this.W() == ((GK)var1).W());
   }

   public int hashCode() {
      return this.u() ? -1 : 31 * this.Y() + this.W();
   }

   public String toString() {
      long var1 = b ^ 51129460068532L;
      return this.Y() + ".." + this.W();
   }

   public static final GK S() {
      return o;
   }
}
