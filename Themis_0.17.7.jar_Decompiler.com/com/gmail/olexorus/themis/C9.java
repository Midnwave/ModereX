package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.function.Supplier;

public interface c9<T extends GL> extends Supplier<T> {
   long a = kt.a(-9009448092304092479L, -4485246002702380222L, MethodHandles.lookup().lookupClass()).a(35233237209864L);

   T E();

   static <T extends GL> c9<T> n(Rc var0, VD<T> var1, Oz<T> var2, lm<?> var3) {
      if (var0 instanceof mZ) {
         al var4 = new al(((mZ)var0).b());
         return new un(var3, var1, var4);
      } else {
         return new n3((GL)var2.n(var0, var3));
      }
   }

   static <T extends GL> Rc q(lm<?> var0, EV<T> var1, c9<T> var2) {
      long var3 = a ^ 72243410057601L;
      if (var2 instanceof un) {
         return new mZ(un.z((un)var2).toString());
      } else if (var2 instanceof n3) {
         return var1.j(var0, n3.F((n3)var2));
      } else {
         throw new UnsupportedOperationException("Unsupported MappedEntityRef implementation: " + var2);
      }
   }
}
