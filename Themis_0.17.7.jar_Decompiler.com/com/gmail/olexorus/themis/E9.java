package com.gmail.olexorus.themis;

class E9 extends E3 {
   public static final <T> boolean v(T[] var0, T var1) {
      return Ej.i(var0, var1) >= 0;
   }

   public static final boolean L(char[] var0, char var1) {
      return Ej.X(var0, var1) >= 0;
   }

   public static final <T> T J(T[] var0, int var1) {
      return var1 >= 0 && var1 <= Ej.M(var0) ? var0[var1] : null;
   }

   public static final <T> int i(T[] var0, T var1) {
      int var2;
      int var3;
      if (var1 == null) {
         var2 = 0;

         for(var3 = var0.length; var2 < var3; ++var2) {
            if (var0[var2] == null) {
               return var2;
            }
         }
      } else {
         var2 = 0;

         for(var3 = var0.length; var2 < var3; ++var2) {
            if (bU.I(var1, var0[var2])) {
               return var2;
            }
         }
      }

      return -1;
   }

   public static final int X(char[] var0, char var1) {
      int var2 = 0;

      for(int var3 = var0.length; var2 < var3; ++var2) {
         if (var1 == var0[var2]) {
            return var2;
         }
      }

      return -1;
   }

   public static final <T> T w(T[] var0) {
      return var0.length == 1 ? var0[0] : null;
   }

   public static final <T> int M(T[] var0) {
      return var0.length - 1;
   }
}
