package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;

public final class Je {
   private static final boolean f;
   private static final long a = kt.a(7726533693601433545L, -8661220235761186548L, MethodHandles.lookup().lookupClass()).a(136926338832885L);

   public static <P> Optional<P> d(Class<P> var0) {
      long var1 = a ^ 30651777176682L;
      ServiceLoader var3 = NM.Z(var0);
      Iterator var4 = var3.iterator();

      while(true) {
         if (var4.hasNext()) {
            Object var5;
            try {
               var5 = var4.next();
            } catch (Throwable var7) {
               if (!f) {
                  continue;
               }

               throw new IllegalStateException("Encountered an exception loading service " + var0, var7);
            }

            if (var4.hasNext()) {
               throw new IllegalStateException("Expected to find one service " + var0 + ", found multiple");
            }

            return Optional.of(var5);
         }

         return Optional.empty();
      }
   }

   public static <P> Set<P> R(Class<? extends P> var0) {
      long var1 = a ^ 9409549406948L;
      ServiceLoader var3 = NM.Z(var0);
      HashSet var4 = new HashSet();
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         Object var6;
         try {
            var6 = var5.next();
         } catch (ServiceConfigurationError var8) {
            if (!f) {
               continue;
            }

            throw new IllegalStateException("Encountered an exception loading a provider for " + var0 + ": ", var8);
         }

         var4.add(var6);
      }

      return Collections.unmodifiableSet(var4);
   }

   static {
      f = Boolean.TRUE.equals(ud.z.i());
   }
}
