package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class ik extends id implements GI {
   private final al G;
   private final al D;
   private final al C;
   private final gC<Wa> x;
   private static final long a = kt.a(-2500471167877514098L, 2290842398402146216L, MethodHandles.lookup().lookupClass()).a(198334013624276L);

   public ik(al var1, al var2, al var3, gC<Wa> var4) {
      this((z2)null, var1, var2, var3, var4);
   }

   public ik(al var1, al var2, al var3) {
      this((z2)null, var1, var2, var3, gC.l());
   }

   public ik(z2 var1, al var2, al var3, al var4, gC<Wa> var5) {
      super(var1);
      this.G = var2;
      this.D = var3;
      this.C = var4;
      this.x = var5;
   }

   public GI B(z2 var1) {
      return new ik(var1, this.G, this.D, this.C, this.x);
   }

   public al e() {
      return this.G;
   }

   public al D() {
      return this.D;
   }

   public al Y() {
      return this.C;
   }

   public gC<Wa> u() {
      return this.x;
   }

   public boolean I(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ik)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         ik var2 = (ik)var1;
         if (!this.G.equals(var2.G)) {
            return false;
         } else if (!this.D.equals(var2.D)) {
            return false;
         } else {
            return !this.C.equals(var2.C) ? false : this.x.equals(var2.x);
         }
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.G, this.D, this.C, this.x});
   }

   public String toString() {
      long var1 = a ^ 1358879011150L;
      return "StaticWolfVariant{wildTexture=" + this.G + ", tameTexture=" + this.D + ", angryTexture=" + this.C + ", biomes=" + this.x + '}';
   }
}
