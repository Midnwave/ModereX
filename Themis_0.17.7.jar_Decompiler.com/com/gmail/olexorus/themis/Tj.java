package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;

public class tJ {
   private final Map<Cs, Set<CJ>> Y = new ConcurrentHashMap();
   private volatile CJ[] B = new CJ[0];
   private static final long a = kt.a(-2274957372144058877L, 8475487798634034797L, MethodHandles.lookup().lookupClass()).a(5706114022110L);

   public void n(a0 var1) {
      this.B(var1, (Runnable)null);
   }

   public void B(a0 var1, Runnable var2) {
      long var3 = a ^ 12239479731987L;
      CJ[] var5 = this.B;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         CJ var8 = var5[var7];

         try {
            var1.V(var8);
         } catch (Exception var10) {
            if (var10.getClass() != Ta.class && (var10.getCause() == null || var10.getCause().getClass() != Ta.class)) {
               oS.J().s().log(Level.WARNING, "PacketEvents caught an unhandled exception while calling your listener.", var10);
            }
         }

         if (var2 != null) {
            var2.run();
         }
      }

      if (var1 instanceof aK && !((aK)var1).f()) {
         ((aK)var1).G((lm)null);
      }

   }

   public CJ r(na var1, Cs var2) {
      Cu var3 = var1.W(var2);
      return this.T(var3);
   }

   public CJ T(CJ var1) {
      this.H(var1);
      this.d();
      return var1;
   }

   public void O() {
      this.Y.clear();
      synchronized(this) {
         this.B = new CJ[0];
      }
   }

   private void d() {
      synchronized(this) {
         ArrayList var2 = new ArrayList();
         Cs[] var3 = Cs.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Cs var6 = var3[var5];
            Set var7 = (Set)this.Y.get(var6);
            if (var7 != null) {
               var2.addAll(var7);
            }
         }

         this.B = (CJ[])var2.toArray(new CJ[0]);
      }
   }

   private void H(CJ var1) {
      Set var2 = (Set)this.Y.computeIfAbsent(var1.l(), tJ::lambda$registerListenerNoRecalculation$0);
      var2.add(var1);
   }

   private static Set lambda$registerListenerNoRecalculation$0(Cs var0) {
      return new CopyOnWriteArraySet();
   }
}
