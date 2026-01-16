package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

final class V0<V> implements Va<V> {
   private final String y;
   private final ob<V> P;
   private final V A;
   private static final long a = kt.a(-9212225020257354038L, -3538036214033079584L, MethodHandles.lookup().lookupClass()).a(217550504716102L);

   V0(String var1, ob<V> var2, V var3) {
      this.y = var1;
      this.P = var2;
      this.A = var3;
   }

   public String T() {
      return this.y;
   }

   public ob<V> D() {
      return this.P;
   }

   public V A() {
      return this.A;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         V0 var2 = (V0)var1;
         return Objects.equals(this.y, var2.y) && Objects.equals(this.P, var2.P);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.y, this.P});
   }

   public String toString() {
      long var1 = a ^ 52021798942439L;
      return this.getClass().getSimpleName() + "{id=" + this.y + ",type=" + this.P + ",defaultValue=" + this.A + '}';
   }
}
