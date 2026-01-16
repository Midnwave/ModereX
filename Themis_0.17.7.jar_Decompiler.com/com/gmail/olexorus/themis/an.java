package com.gmail.olexorus.themis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

public class AN {
   private Map<String, mH> o;

   public AN(Map<String, mH> var1) {
      this.o = var1;
   }

   public static AN R(lm<?> var0) {
      RT var1 = var0.u();
      HashMap var2 = new HashMap(var1.E());
      Iterator var3 = var1.q().entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         mH var5 = mH.G((RT)var4.getValue());
         var2.put((String)var4.getKey(), var5);
      }

      return new AN(var2);
   }

   public static void B(lm<?> var0, AN var1) {
      RT var2 = new RT();
      Iterator var3 = var1.o.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         RT var5 = new RT();
         mH.J(var5, (mH)var4.getValue());
         var2.j((String)var4.getKey(), var5);
      }

      var0.G(var2);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AN)) {
         return false;
      } else {
         AN var2 = (AN)var1;
         return this.o.equals(var2.o);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.o);
   }
}
