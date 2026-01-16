package com.gmail.olexorus.themis;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface i6 {
   i6 J = i6::lambda$static$0;
   i6 j = i6::lambda$static$1;

   void O(X var1, Deque<X> var2, Set<gG> var3);

   private static void lambda$static$1(X var0, Deque var1, Set var2) {
      if (var2.contains(gG.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS) && var0 instanceof GY) {
         Iterator var3 = ((GY)var0).n().iterator();

         while(var3.hasNext()) {
            VE var4 = (VE)var3.next();
            var1.add(var4.T());
         }
      }

      mk var5 = var0.U();
      if (var5 != null) {
         bj var6 = var5.S();
         if (var2.contains(gG.INCLUDE_HOVER_SHOW_ENTITY_NAME) && var6 == bj.P) {
            var1.addLast(((tY)var5.E()).u());
         } else if (var2.contains(gG.INCLUDE_HOVER_SHOW_TEXT_COMPONENT) && var6 == bj.C) {
            var1.addLast((X)var5.E());
         }
      }

      var1.addAll(var0.C());
   }

   private static void lambda$static$0(X var0, Deque var1, Set var2) {
      List var4;
      int var5;
      if (var2.contains(gG.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS) && var0 instanceof GY) {
         GY var3 = (GY)var0;
         var4 = var3.n();

         for(var5 = var4.size() - 1; var5 >= 0; --var5) {
            var1.addFirst(((lv)var4.get(var5)).T());
         }
      }

      mk var6 = var0.U();
      if (var6 != null) {
         bj var7 = var6.S();
         if (var2.contains(gG.INCLUDE_HOVER_SHOW_ENTITY_NAME) && var7 == bj.P) {
            var1.addFirst(((tY)var6.E()).u());
         } else if (var2.contains(gG.INCLUDE_HOVER_SHOW_TEXT_COMPONENT) && var7 == bj.C) {
            var1.addFirst((X)var6.E());
         }
      }

      var4 = var0.C();

      for(var5 = var4.size() - 1; var5 >= 0; --var5) {
         var1.addFirst((X)var4.get(var5));
      }

   }
}
