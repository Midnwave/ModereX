package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;

public class iU extends id implements TN {
   private final String X;
   private final i u;
   private final float m;
   private final Map<TY, String> x;
   private final X y;
   private static final long a = kt.a(-580233170706119063L, 657874112592628516L, MethodHandles.lookup().lookupClass()).a(96271275307393L);

   public iU(String var1, i var2, Map<TY, String> var3, X var4) {
      this((z2)null, var1, var2, 0.0F, var3, var4);
   }

   public iU(z2 var1, String var2, i var3, Map<TY, String> var4, X var5) {
      this(var1, var2, var3, 0.0F, var4, var5);
   }

   public iU(String var1, i var2, float var3, Map<TY, String> var4, X var5) {
      this((z2)null, var1, var2, var3, var4, var5);
   }

   public iU(z2 var1, String var2, i var3, float var4, Map<TY, String> var5, X var6) {
      super(var1);
      this.X = var2;
      this.u = var3;
      this.m = var4;
      this.x = var5;
      this.y = var6;
   }

   public TN S(z2 var1) {
      return new iU(var1, this.X, this.u, this.m, this.x, this.y);
   }

   public String q() {
      return this.X;
   }

   public i H() {
      return this.u;
   }

   public float X() {
      return this.m;
   }

   public Map<TY, String> G() {
      return this.x;
   }

   public X a() {
      return this.y;
   }

   public boolean I(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof iU)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         iU var2 = (iU)var1;
         if (Float.compare(var2.m, this.m) != 0) {
            return false;
         } else if (!this.X.equals(var2.X)) {
            return false;
         } else if (!Objects.equals(this.u, var2.u)) {
            return false;
         } else {
            return !this.x.equals(var2.x) ? false : this.y.equals(var2.y);
         }
      }
   }

   public int z() {
      return Objects.hash(new Object[]{super.hashCode(), this.X, this.u, this.m, this.x, this.y});
   }

   public String toString() {
      long var1 = a ^ 55113803862479L;
      return "StaticTrimMaterial{assetName='" + this.X + '\'' + ", ingredient=" + this.u + ", itemModelIndex=" + this.m + ", overrideArmorMaterials=" + this.x + ", description=" + this.y + '}';
   }
}
