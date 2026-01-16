package com.gmail.olexorus.themis;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class t_ {
   private final String n;
   private Map<vL, Map<String, Integer>> w;
   private zw O;
   N8<?> G;
   private static final long a = kt.a(-1832501743202299252L, 8148934883168325930L, MethodHandles.lookup().lookupClass()).a(66826184636933L);

   public t_(String var1, boolean var2) {
      this.w = new HashMap();
      this.n = var1;
      if (!var2) {
         this.N();
      }

   }

   public t_(String var1) {
      this(var1, false);
   }

   public void N() {
      long var1 = a ^ 70097180174130L;
      if (this.w == null) {
         this.w = new HashMap();
      }

      try {
         mC var3 = M.v("mappings/" + this.n);

         try {
            var3.p();
            mC var4 = (mC)var3.m().getValue();
            int var5 = ((mh)var4.m().getValue()).P();
            mC var6 = (mC)var4.m().getValue();
            vL[] var7 = new vL[var5];
            Entry var8 = var6.m();
            if (((Rc)var8.getValue()).b() == Ay.P) {
               this.O(var8, var6, var7);
            } else {
               this.h(var8, var6, var7);
            }

            this.O = new zw(var7);
         } catch (Throwable var10) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (var3 != null) {
            var3.close();
         }

      } catch (IOException var11) {
         throw new RuntimeException("Unable to load mapping files.", var11);
      }
   }

   private void O(Entry<String, Rc> var1, mC var2, vL[] var3) {
      vL var4 = vL.valueOf((String)var1.getKey());
      var3[0] = var4;
      ArrayList var5 = new ArrayList();
      Iterator var6 = ((mD)var1.getValue()).iterator();

      while(var6.hasNext()) {
         Rc var7 = (Rc)var6.next();
         var5.add(((mZ)var7).b());
      }

      Consumer var13 = this::lambda$loadAsArray$0;
      var13.accept(var4);
      int var14 = 1;
      Iterator var8 = var2.iterator();

      while(var8.hasNext()) {
         Entry var9 = (Entry)var8.next();
         vL var10 = vL.valueOf((String)var9.getKey());
         var3[var14++] = var10;
         List var11 = M.k((mC)var9.getValue());

         for(int var12 = var11.size() - 1; var12 >= 0; --var12) {
            ((m5)var11.get(var12)).V(var5);
         }

         var13.accept(var10);
      }

   }

   private void h(Entry<String, Rc> var1, mC var2, vL[] var3) {
      vL var4 = vL.valueOf((String)var1.getKey());
      var3[0] = var4;
      Map var5 = (Map)StreamSupport.stream(((mC)var1.getValue()).spliterator(), false).collect(Collectors.toMap(Entry::getKey, t_::lambda$loadAsMap$1));
      Consumer var6 = this::lambda$loadAsMap$2;
      var6.accept(var4);
      int var7 = 1;
      Iterator var8 = var2.iterator();

      while(var8.hasNext()) {
         Entry var9 = (Entry)var8.next();
         vL var10 = vL.valueOf((String)var9.getKey());
         var3[var7++] = var10;
         List var11 = M.L((mC)var9.getValue());
         Iterator var12 = var11.iterator();

         while(var12.hasNext()) {
            WN var13 = (WN)var12.next();
            var13.G(var5);
         }

         var6.accept(var10);
      }

   }

   public vL[] l() {
      return this.O.x();
   }

   public int w(vL var1) {
      return this.O.H(var1);
   }

   public zw A() {
      return this.O;
   }

   public void U(vL var1) {
      zw var2 = this.O.g(var1);
      if (this.O != var2) {
         int var3 = this.O.H(var1);
         vL var4 = this.O.x()[var3];
         this.w.put(var1, (Map)this.w.get(var4));
         this.O = var2;
      }

   }

   public void l() {
      this.w.clear();
      this.w = null;
   }

   public z2 y(String var1, RZ var2) {
      al var3 = new al(var1);
      int[] var4 = new int[this.l().length];
      int var5 = 0;
      vL[] var6 = this.l();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         vL var9 = var6[var8];
         Map var10 = (Map)this.w.get(var9);
         if (var10.containsKey(var1)) {
            int var11 = (Integer)var10.get(var1);
            var4[var5] = var11;
         } else {
            var4[var5] = -1;
         }

         ++var5;
      }

      return new z2(var3, var4, this, var2);
   }

   private void lambda$loadAsMap$2(Map var1, vL var2) {
      HashMap var3 = new HashMap(var1);
      this.w.put(var2, var3);
   }

   private static Integer lambda$loadAsMap$1(Entry var0) {
      return ((mh)var0.getValue()).P();
   }

   private void lambda$loadAsArray$0(List var1, vL var2) {
      HashMap var3 = new HashMap();
      int var4 = var1.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         var3.put((String)var1.get(var5), var5);
      }

      this.w.put(var2, var3);
   }
}
