package com.gmail.olexorus.themis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

public final class nN {
   public static boolean G(Object var0, Object var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         if (var0 instanceof rp) {
            return ((rp)var0).I(var1);
         } else {
            return var1 instanceof rp ? ((rp)var1).I(var1) : Objects.equals(var0, var1);
         }
      } else {
         return false;
      }
   }

   public static <K> boolean K(Map<K, ?> var0, Map<K, ?> var1) {
      if (var0.isEmpty() && var1.isEmpty()) {
         return true;
      } else if (var0.size() != var1.size()) {
         return false;
      } else {
         Iterator var2 = var0.entrySet().iterator();

         Entry var3;
         Object var4;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (Entry)var2.next();
            var4 = var1.get(var3.getKey());
         } while(G(var3.getValue(), var4));

         return false;
      }
   }

   public static <K, V> Map<K, V> I(Entry<? extends K, ? extends V>... var0) {
      if (var0.length == 0) {
         return Collections.emptyMap();
      } else {
         HashMap var1 = new HashMap(var0.length);
         Entry[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Entry var5 = var2[var4];
            var1.put(var5.getKey(), var5.getValue());
         }

         return Collections.unmodifiableMap(var1);
      }
   }
}
