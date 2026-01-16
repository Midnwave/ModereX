package com.gmail.olexorus.themis;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.regex.Pattern;

abstract class Th {
   boolean T(AnnotatedElement var1, Class<? extends Annotation> var2) {
      return this.L(var1, var2, 0) != null;
   }

   boolean n(AnnotatedElement var1, Class<? extends Annotation> var2, boolean var3) {
      return this.L(var1, var2, 0 | (var3 ? 0 : 8)) != null;
   }

   String[] n(AnnotatedElement var1, Class<? extends Annotation> var2, int var3) {
      return this.j(var1, var2, nQ.e, var3);
   }

   String[] j(AnnotatedElement var1, Class<? extends Annotation> var2, Pattern var3, int var4) {
      String var5 = this.L(var1, var2, var4);
      return var5 == null ? null : var3.split(var5);
   }

   String z(AnnotatedElement var1, Class<? extends Annotation> var2) {
      return this.L(var1, var2, 1);
   }

   abstract String L(AnnotatedElement var1, Class<? extends Annotation> var2, int var3);

   <T extends Annotation> T U(Class<?> var1, Class<T> var2) {
      while(var1 != null && Nk.class.isAssignableFrom(var1)) {
         Annotation var3 = var1.getAnnotation(var2);
         if (var3 != null) {
            return var3;
         }

         for(Class var4 = var1.getSuperclass(); var4 != null && Nk.class.isAssignableFrom(var4); var4 = var4.getSuperclass()) {
            var3 = var4.getAnnotation(var2);
            if (var3 != null) {
               return var3;
            }
         }

         var1 = var1.getEnclosingClass();
      }

      return null;
   }
}
