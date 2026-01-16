package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class O_ implements tw<List<T>> {
   final tw S;

   O_(tw var1) {
      this.S = var1;
   }

   public List<T> m(Rc var1, lm<?> var2) {
      try {
         List var3 = (List)g9.z.n(var1, var2);
         ArrayList var9 = new ArrayList(var3.size());
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            Rc var6 = (Rc)var5.next();
            var9.add(this.S.n(var6, var2));
         }

         return var9;
      } catch (to var8) {
         try {
            Object var4 = this.S.n(var1, var2);
            return Collections.singletonList(var4);
         } catch (to var7) {
            var8.addSuppressed(var7);
            throw var8;
         }
      }
   }

   public Rc p(lm<?> var1, List<T> var2) {
      ArrayList var3 = new ArrayList(var2.size());
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Object var5 = var4.next();
         var3.add(this.S.j(var1, var5));
      }

      return g9.z.j(var1, var3);
   }
}
