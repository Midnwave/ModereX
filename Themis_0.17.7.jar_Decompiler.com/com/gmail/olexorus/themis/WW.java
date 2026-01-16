package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class ww extends wK {
   private static final long a = kt.a(-5017234726423889344L, 2562215502467680359L, MethodHandles.lookup().lookupClass()).a(226372293412019L);

   public static final <T> T o(Iterable<? extends T> var0) {
      long var1 = a ^ 47941794696409L;
      if (var0 instanceof List) {
         return wF.N((List)var0);
      } else {
         Iterator var3 = var0.iterator();
         if (!var3.hasNext()) {
            throw new NoSuchElementException("Collection is empty.");
         } else {
            return var3.next();
         }
      }
   }

   public static final <T> T N(List<? extends T> var0) {
      long var1 = a ^ 95778454751626L;
      if (var0.isEmpty()) {
         throw new NoSuchElementException("List is empty.");
      } else {
         return var0.get(0);
      }
   }

   public static final <T> T z(Iterable<? extends T> var0) {
      if (var0 instanceof List) {
         return ((List)var0).isEmpty() ? null : ((List)var0).get(((List)var0).size() - 1);
      } else {
         Iterator var1 = var0.iterator();
         if (!var1.hasNext()) {
            return null;
         } else {
            Object var2;
            for(var2 = var1.next(); var1.hasNext(); var2 = var1.next()) {
            }

            return var2;
         }
      }
   }

   public static final <T> T R(Iterable<? extends T> var0) {
      long var1 = a ^ 35532154240243L;
      if (var0 instanceof List) {
         return wF.J((List)var0);
      } else {
         Iterator var3 = var0.iterator();
         if (!var3.hasNext()) {
            throw new NoSuchElementException("Collection is empty.");
         } else {
            Object var4 = var3.next();
            if (var3.hasNext()) {
               throw new IllegalArgumentException("Collection has more than one element.");
            } else {
               return var4;
            }
         }
      }
   }

   public static final <T> T J(List<? extends T> var0) {
      long var1 = a ^ 86406777204681L;
      switch(var0.size()) {
      case 0:
         throw new NoSuchElementException("List is empty.");
      case 1:
         return var0.get(0);
      default:
         throw new IllegalArgumentException("List has more than one element.");
      }
   }

   public static final <T, A extends Appendable> A m(Iterable<? extends T> var0, A var1, CharSequence var2, CharSequence var3, CharSequence var4, int var5, CharSequence var6, Gg<? super T, ? extends CharSequence> var7) {
      var1.append(var3);
      int var8 = 0;
      Iterator var9 = var0.iterator();

      while(var9.hasNext()) {
         Object var10 = var9.next();
         ++var8;
         if (var8 > 1) {
            var1.append(var2);
         }

         if (var5 >= 0 && var8 > var5) {
            break;
         }

         OR.t(var1, var10, var7);
      }

      if (var5 >= 0 && var8 > var5) {
         var1.append(var6);
      }

      var1.append(var4);
      return var1;
   }

   public static final <T> String W(Iterable<? extends T> var0, CharSequence var1, CharSequence var2, CharSequence var3, int var4, CharSequence var5, Gg<? super T, ? extends CharSequence> var6) {
      return ((StringBuilder)wF.m(var0, (Appendable)(new StringBuilder()), var1, var2, var3, var4, var5, var6)).toString();
   }

   // $FF: synthetic method
   public static String e(Iterable var0, CharSequence var1, CharSequence var2, CharSequence var3, int var4, CharSequence var5, Gg var6, int var7, Object var8) {
      long var9 = a ^ 46613100135613L;
      if ((var7 & 1) != 0) {
         var1 = (CharSequence)", ";
      }

      if ((var7 & 2) != 0) {
         var2 = (CharSequence)"";
      }

      if ((var7 & 4) != 0) {
         var3 = (CharSequence)"";
      }

      if ((var7 & 8) != 0) {
         var4 = -1;
      }

      if ((var7 & 16) != 0) {
         var5 = (CharSequence)"...";
      }

      if ((var7 & 32) != 0) {
         var6 = null;
      }

      return wF.W(var0, var1, var2, var3, var4, var5, var6);
   }
}
