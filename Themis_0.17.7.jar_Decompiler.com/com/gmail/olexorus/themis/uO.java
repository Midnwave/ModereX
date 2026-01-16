package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class uo implements Comparable<uo> {
   private final int d;
   private final int F;
   private final int x;
   private final boolean L;
   private final String r;
   private static final long a = kt.a(-6533946134442612565L, -1115805157677506675L, MethodHandles.lookup().lookupClass()).a(25206066904112L);

   public uo(int var1, int var2, int var3, boolean var4, String var5) {
      this.d = var1;
      this.F = var2;
      this.x = var3;
      this.L = var4;
      this.r = var5;
   }

   public uo(int var1, int var2, int var3, String var4) {
      this(var1, var2, var3, var4 != null, var4);
   }

   public uo(int var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, (String)null);
   }

   public uo(int var1, int var2, int var3) {
      this(var1, var2, var3, false);
   }

   public uo(String var1) {
      long var2 = a ^ 126789090822103L;
      super();
      String var4 = var1.replace("-SNAPSHOT", "");
      String[] var5 = var4.split("\\+");
      String[] var6 = var5.length > 0 ? var5[0].split("\\.") : null;
      if (var5.length >= 1 && var5.length <= 2 && var6.length >= 2 && var6.length <= 3) {
         this.d = Integer.parseInt(var6[0]);
         this.F = Integer.parseInt(var6[1]);
         this.x = var6.length > 2 ? Integer.parseInt(var6[2]) : 0;
         this.L = var1.contains("-SNAPSHOT");
         this.r = var5.length > 1 ? var5[1] : null;
      } else {
         throw new IllegalArgumentException("Version string must be in the format 'major.minor[.patch][+commit][-SNAPSHOT]', found '" + var1 + "' instead");
      }
   }

   public static uo F(String var0) {
      return new uo(var0);
   }

   public int b(uo var1) {
      int var2 = Integer.compare(this.d, var1.d);
      if (var2 != 0) {
         return var2;
      } else {
         int var3 = Integer.compare(this.F, var1.F);
         if (var3 != 0) {
            return var3;
         } else {
            int var4 = Integer.compare(this.x, var1.x);
            return var4 != 0 ? var4 : Boolean.compare(var1.L, this.L);
         }
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof uo)) {
         return false;
      } else {
         uo var2 = (uo)var1;
         return this.d == var2.d && this.F == var2.F && this.x == var2.x && this.L == var2.L && Objects.equals(this.r, var2.r);
      }
   }

   public boolean L(uo var1) {
      return this.b(var1) > 0;
   }

   public boolean X(uo var1) {
      return this.b(var1) < 0;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.d, this.F, this.x, this.L, this.r});
   }

   public uo l() {
      return new uo(this.d, this.F, this.x, this.L, this.r);
   }

   public String toString() {
      long var1 = a ^ 11847154523453L;
      return this.d + "." + this.F + "." + this.x + (this.L && this.r != null ? "+" + this.r + "-SNAPSHOT" : "");
   }

   public String v() {
      return this.d + "." + this.F + "." + this.x;
   }
}
