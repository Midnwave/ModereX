package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class c_ extends id implements i {
   private final int X;
   private final int M;
   private final i R;
   private final vM Q;
   private final Set<Mz> Y;
   private final Map<vL, zQ> m;
   private static final long a = kt.a(-8094724810938014868L, -126216929302847541L, MethodHandles.lookup().lookupClass()).a(234037237885012L);

   public c_(z2 var1, int var2, int var3, i var4, vM var5, Set<Mz> var6) {
      super(var1);
      this.X = var2;
      this.M = var3;
      this.R = var4;
      this.Q = var5;
      this.Y = var6;
      this.m = new EnumMap(vL.class);
   }

   public int u() {
      return this.X;
   }

   public int R() {
      return this.M;
   }

   public i u() {
      return this.R;
   }

   public vM X() {
      return this.Q;
   }

   public Set<Mz> T() {
      return this.Y;
   }

   public zQ B(vL var1) {
      long var2 = a ^ 129598304650337L;
      if (!var1.x()) {
         throw new IllegalArgumentException("Unsupported version for getting components of " + this.f() + ": " + var1);
      } else {
         return (zQ)this.m.getOrDefault(var1, zQ.i);
      }
   }

   void v(vL var1, zQ var2) {
      long var3 = a ^ 117869257689577L;
      if (this.m.containsKey(var1)) {
         throw new IllegalStateException("Components are already defined for " + this.f() + " in version " + var1);
      } else if (!var1.x()) {
         throw new IllegalArgumentException("Unsupported version for setting components of " + this.f() + ": " + var1);
      } else {
         this.m.put(var1, var2);
      }
   }

   boolean c(vL var1) {
      return this.m.containsKey(var1);
   }

   void y() {
      zQ var1 = null;
      vL[] var2 = vL.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         vL var5 = var2[var4];
         if (var5.x()) {
            zQ var6 = (zQ)this.m.get(var5);
            if (var6 == null) {
               if (var1 != null) {
                  this.m.put(var5, var1);
               }
            } else {
               if (var1 == null) {
                  vL[] var7 = vL.values();
                  int var8 = var7.length;

                  for(int var9 = 0; var9 < var8; ++var9) {
                     vL var10 = var7[var9];
                     if (var10 == var5) {
                        break;
                     }

                     this.m.put(var10, var6);
                  }
               }

               var1 = var6;
            }
         }
      }

   }
}
