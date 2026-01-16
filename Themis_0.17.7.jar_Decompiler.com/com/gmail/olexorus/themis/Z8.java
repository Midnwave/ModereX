package com.gmail.olexorus.themis;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public final class z8 {
   private static final Object[] M;

   public static final Object[] t(Collection<?> var0) {
      boolean var1 = false;
      int var2 = var0.size();
      Object[] var10000;
      if (var2 == 0) {
         boolean var3 = false;
         var10000 = M;
      } else {
         Iterator var10 = var0.iterator();
         if (!var10.hasNext()) {
            boolean var4 = false;
            var10000 = M;
         } else {
            boolean var5 = false;
            Object[] var11 = new Object[var2];
            int var12 = 0;

            while(true) {
               while(true) {
                  var11[var12++] = var10.next();
                  if (var12 >= var11.length) {
                     if (!var10.hasNext()) {
                        var10000 = var11;
                        return var10000;
                     }

                     int var6 = var12 * 3 + 1 >>> 1;
                     if (var6 <= var12) {
                        if (var12 >= 2147483645) {
                           throw new OutOfMemoryError();
                        }

                        var6 = 2147483645;
                     }

                     var11 = Arrays.copyOf(var11, var6);
                  } else if (!var10.hasNext()) {
                     boolean var9 = false;
                     var10000 = Arrays.copyOf(var11, var12);
                     return var10000;
                  }
               }
            }
         }
      }

      return var10000;
   }

   public static final Object[] s(Collection<?> var0, Object[] var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         boolean var2 = false;
         int var3 = var0.size();
         Object[] var10000;
         boolean var4;
         if (var3 == 0) {
            var4 = false;
            if (var1.length > 0) {
               var1[0] = null;
            }

            var10000 = var1;
         } else {
            Iterator var5 = var0.iterator();
            if (!var5.hasNext()) {
               var4 = false;
               if (var1.length > 0) {
                  var1[0] = null;
               }

               var10000 = var1;
            } else {
               boolean var6 = false;
               Object[] var7 = var3 <= var1.length ? var1 : (Object[])Array.newInstance(var1.getClass().getComponentType(), var3);
               int var11 = 0;

               while(true) {
                  while(true) {
                     var7[var11++] = var5.next();
                     if (var11 >= var7.length) {
                        if (!var5.hasNext()) {
                           var10000 = var7;
                           return var10000;
                        }

                        int var12 = var11 * 3 + 1 >>> 1;
                        if (var12 <= var11) {
                           if (var11 >= 2147483645) {
                              throw new OutOfMemoryError();
                           }

                           var12 = 2147483645;
                        }

                        var7 = Arrays.copyOf(var7, var12);
                     } else if (!var5.hasNext()) {
                        boolean var10 = false;
                        if (var7 == var1) {
                           var1[var11] = null;
                           var10000 = var1;
                        } else {
                           var10000 = Arrays.copyOf(var7, var11);
                        }

                        return var10000;
                     }
                  }
               }
            }
         }

         return var10000;
      }
   }

   static {
      boolean var0 = false;
      M = new Object[0];
   }
}
