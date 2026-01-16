package com.gmail.olexorus.themis;

import java.util.Arrays;

public class C2 {
   private final short[] g;

   public C2(int var1) {
      this.g = new short[var1];
   }

   public short[] U() {
      return this.g;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         C2 var2 = (C2)var1;
         return Arrays.equals(this.g, var2.g);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.g);
   }
}
