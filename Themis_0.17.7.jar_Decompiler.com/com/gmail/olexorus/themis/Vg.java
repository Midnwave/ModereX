package com.gmail.olexorus.themis;

import java.util.Objects;

public class VG {
   private gb j;

   public VG(gb var1) {
      this.j = var1;
   }

   public static VG A(lm<?> var0) {
      gb var1 = (gb)var0.y((VD)c1.z());
      return new VG(var1);
   }

   public static void k(lm<?> var0, VG var1) {
      var0.j((GL)var1.j);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof VG)) {
         return false;
      } else {
         VG var2 = (VG)var1;
         return this.j.equals(var2.j);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.j);
   }
}
