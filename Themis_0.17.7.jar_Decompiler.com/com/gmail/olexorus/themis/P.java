package com.gmail.olexorus.themis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class p {
   private final Map<nW<?>, Object> I = new HashMap();

   public zQ t() {
      return new zQ(this.I);
   }

   public p Y(zQ var1) {
      return this.K(var1.o());
   }

   public p K(Map<nW<?>, ?> var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.i((nW)var3.getKey(), var3.getValue());
      }

      return this;
   }

   public <T> p i(nW<T> var1, T var2) {
      if (var2 == null) {
         this.I.remove(var1);
      } else {
         this.I.put(var1, var2);
      }

      return this;
   }
}
