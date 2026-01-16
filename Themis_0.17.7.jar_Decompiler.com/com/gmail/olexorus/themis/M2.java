package com.gmail.olexorus.themis;

public interface m2 {
   default <T extends GL> VD<T> W(VD<T> var1, vL var2) {
      VD var3 = this.z(var1.S(), var2);
      return var3 != null ? var3 : var1;
   }

   VD<?> z(al var1, vL var2);
}
