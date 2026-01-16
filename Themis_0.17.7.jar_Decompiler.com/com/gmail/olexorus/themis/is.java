package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class iS extends id implements RA {
   private final al l;
   private final String c;
   private static final long b = kt.a(-525964627033369462L, 6107758422868400520L, MethodHandles.lookup().lookupClass()).a(249314329116034L);

   public iS(al var1, String var2) {
      this((z2)null, var1, var2);
   }

   public iS(z2 var1, al var2, String var3) {
      super(var1);
      this.l = var2;
      this.c = var3;
   }

   public RA c(z2 var1) {
      return new iS(var1, this.l, this.c);
   }

   public al L() {
      return this.l;
   }

   public String J() {
      return this.c;
   }

   public boolean I(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof iS)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         iS var2 = (iS)var1;
         return !this.l.equals(var2.l) ? false : this.c.equals(var2.c);
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.l, this.c});
   }

   public String toString() {
      long var1 = b ^ 62909323602868L;
      return "StaticBannerPattern{assetId=" + this.l + ", translationKey='" + this.c + '\'' + '}';
   }
}
