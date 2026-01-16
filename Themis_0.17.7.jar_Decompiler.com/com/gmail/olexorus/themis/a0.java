package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

final class A0 extends A5<rF, NO> implements NO {
   private Ma T;
   private static final long c = kt.a(-6811011917929104229L, 353843141290762661L, MethodHandles.lookup().lookupClass()).a(140773273997273L);

   A0() {
   }

   A0(rF var1) {
      super(var1);
      this.T = var1.Z();
   }

   public NO y(Ma var1) {
      long var2 = c ^ 1631021840396L;
      this.T = (Ma)Objects.requireNonNull(var1, "pos");
      return this;
   }

   public rF G() {
      long var1 = c ^ 61316217801158L;
      if (this.o == null) {
         throw new IllegalStateException("nbt path must be set");
      } else if (this.T == null) {
         throw new IllegalStateException("pos must be set");
      } else {
         return ET.Y(this.y, this.l(), this.o, this.B, this.R, this.T);
      }
   }
}
