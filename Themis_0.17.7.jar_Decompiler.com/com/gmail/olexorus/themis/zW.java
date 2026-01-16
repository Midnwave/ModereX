package com.gmail.olexorus.themis;

import java.util.Arrays;

public class zw {
   private final vL[] Z;
   private final vL[] V;

   public zw(vL... var1) {
      this.Z = (vL[])var1.clone();
      Arrays.sort(this.Z);
      this.V = new vL[this.Z.length];
      int var2 = this.Z.length - 1;

      for(int var3 = 0; var2 >= 0; ++var3) {
         this.V[var3] = this.Z[var2];
         --var2;
      }

   }

   public zw g(vL var1) {
      if (Arrays.binarySearch(this.Z, var1) >= 0) {
         return this;
      } else {
         vL[] var2 = (vL[])Arrays.copyOf(this.Z, this.Z.length + 1);
         var2[var2.length - 1] = var1;
         return new zw(var2);
      }
   }

   public vL[] x() {
      return this.Z;
   }

   public int H(vL var1) {
      int var2 = this.V.length - 1;
      vL[] var3 = this.V;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         vL var6 = var3[var5];
         if (var1.K(var6)) {
            return var2;
         }

         --var2;
      }

      return 0;
   }

   public int H() {
      return this.Z.length;
   }
}
