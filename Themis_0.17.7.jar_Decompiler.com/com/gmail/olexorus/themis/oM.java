package com.gmail.olexorus.themis;

class OM {
   public static final <T> void t(Appendable var0, T var1, Gg<? super T, ? extends CharSequence> var2) {
      if (var2 != null) {
         var0.append((CharSequence)var2.Y(var1));
      } else if (var1 == null ? true : var1 instanceof CharSequence) {
         var0.append((CharSequence)var1);
      } else if (var1 instanceof Character) {
         var0.append((Character)var1);
      } else {
         var0.append((CharSequence)String.valueOf(var1));
      }

   }
}
