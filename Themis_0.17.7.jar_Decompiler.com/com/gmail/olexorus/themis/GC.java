package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class gC<T extends GL> implements Ng<T> {
   private final al Z;
   private final List<T> W;
   static final boolean p = !gC.class.desiredAssertionStatus();
   private static final long a = kt.a(-228259866160247121L, 6805627635607706056L, MethodHandles.lookup().lookupClass()).a(244201848479593L);

   public gC(al var1) {
      this(var1, (List)null);
   }

   public gC(List<T> var1) {
      this((al)null, var1);
   }

   public gC(al var1, List<T> var2) {
      long var3 = a ^ 69740904685458L;
      super();
      if (var1 == null && var2 == null) {
         throw new IllegalArgumentException("Illegal generic holder set: either tag key or holder ids have to be set");
      } else {
         this.Z = var1;
         this.W = var2;
      }
   }

   public static <Z extends GL> gC<Z> l() {
      return new gC(new ArrayList(0));
   }

   public static <Z extends GL> gC<Z> P(lm<?> var0, BiFunction<vL, Integer, Z> var1) {
      int var2 = var0.Q() - 1;
      if (var2 == -1) {
         return new gC(var0.R(), (List)null);
      } else {
         ArrayList var3 = new ArrayList(Math.min(var2, 65536));

         for(int var4 = 0; var4 < var2; ++var4) {
            var3.add(var0.e(var1));
         }

         return new gC((al)null, var3);
      }
   }

   public static <Z extends GL> void n(lm<?> var0, gC<Z> var1) {
      if (var1.Z != null) {
         var0.E(0);
         var0.T(var1.Z);
      } else if (!p && var1.W == null) {
         throw new AssertionError();
      } else {
         var0.E(var1.W.size() + 1);
         Iterator var2 = var1.W.iterator();

         while(var2.hasNext()) {
            GL var3 = (GL)var2.next();
            var0.j(var3);
         }

      }
   }

   public static <Z extends GL> gC<Z> T(Rc var0, lm<?> var1, VD<Z> var2) {
      vL var3 = var1.R().u();
      ArrayList var4;
      if (var0 instanceof mZ) {
         String var5 = ((mZ)var0).b();
         if (!var5.isEmpty() && var5.charAt(0) == '#') {
            String var10 = var5.substring(1);
            al var7 = new al(var10);
            return new gC(var7);
         }

         var4 = new ArrayList(1);
         al var6 = new al(var5);
         var4.add(var2.y(var3, var6));
      } else {
         m_ var9 = (m_)var0;
         var4 = new ArrayList(var9.N());
         Iterator var11 = var9.N().iterator();

         while(var11.hasNext()) {
            Rc var12 = (Rc)var11.next();
            al var8 = new al(((mZ)var12).b());
            var4.add(var2.y(var3, var8));
         }
      }

      return new gC(var4);
   }

   public static <Z extends GL> Rc k(gC<Z> var0, vL var1) {
      return w(lm.s(var1), var0);
   }

   public static <Z extends GL> Rc O(lm<?> var0, gC<Z> var1) {
      if (var1.Z != null) {
         return new mZ("#" + var1.Z);
      } else if (!p && var1.W == null) {
         throw new AssertionError();
      } else {
         m_ var2 = m_.L();
         Iterator var3 = var1.W.iterator();

         while(var3.hasNext()) {
            GL var4 = (GL)var3.next();
            var2.Z(new mZ(var4.f().toString()));
         }

         return var2;
      }
   }

   public static <Z extends GL> Ng<Z> P(Rc var0, lm<?> var1) {
      return P(var0, var1.R().u());
   }

   public static <Z extends GL> Ng<Z> P(Rc var0, vL var1) {
      Object var2;
      if (var0 instanceof mZ) {
         String var3 = ((mZ)var0).b();
         if (!var3.isEmpty() && var3.charAt(0) == '#') {
            String var4 = var3.substring(1);
            al var5 = new al(var4);
            return new gC(var5);
         }

         var2 = Collections.singletonList(var3);
      } else {
         m_ var6 = (m_)var0;
         var2 = new ArrayList(var6.N());
         Iterator var7 = var6.N().iterator();

         while(var7.hasNext()) {
            Rc var8 = (Rc)var7.next();
            ((List)var2).add(((mZ)var8).b());
         }
      }

      return new vP((List)var2);
   }

   public static <Z extends GL> Rc w(lm<?> var0, Ng<Z> var1) {
      return q(var1, var0.R().u());
   }

   public static <Z extends GL> Rc q(Ng<Z> var0, vL var1) {
      long var2 = a ^ 104174550210462L;
      if (!(var0 instanceof vP)) {
         if (var0 instanceof gC) {
            return k((gC)var0, var1);
         } else {
            throw new UnsupportedOperationException("Unsupported mapped entity reference set implementation: " + var0);
         }
      } else {
         vP var4 = (vP)var0;
         m_ var5 = m_.L();
         Iterator var6 = vP.V(var4).iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            var5.Z(new mZ(var7));
         }

         return var5;
      }
   }

   public gC<T> o(lm<?> var1, VD<T> var2) {
      return this;
   }

   public gC<T> C(vL var1, m2 var2, VD<T> var3) {
      return this;
   }

   public gC<T> V(vL var1, VD<T> var2) {
      return this;
   }

   public boolean u() {
      return this.W != null && this.W.isEmpty();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof gC)) {
         return false;
      } else {
         gC var2 = (gC)var1;
         return !Objects.equals(this.Z, var2.Z) ? false : Objects.equals(this.W, var2.W);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.Z, this.W});
   }

   public String toString() {
      long var1 = a ^ 3990199262685L;
      return "MappedEntitySet{tagKey=" + this.Z + ", entities=" + this.W + '}';
   }
}
