package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

final class W6 implements A2 {
   private String q;
   private UUID V;
   private final List<mx> n = new ArrayList();
   private boolean U = true;
   private v1 D;
   private static final long a = kt.a(229350448299462188L, 7136738152583232651L, MethodHandles.lookup().lookupClass()).a(113747796194435L);

   public A2 g(String var1) {
      this.q = var1;
      return this;
   }

   public A2 Y(UUID var1) {
      this.V = var1;
      return this;
   }

   public A2 p(mx var1) {
      long var2 = a ^ 32404843476652L;
      this.n.add((mx)Objects.requireNonNull(var1, "property"));
      return this;
   }

   public A2 F(Collection<mx> var1) {
      long var2 = a ^ 32317216912614L;
      Iterator var4 = ((Collection)Objects.requireNonNull(var1, "properties")).iterator();

      while(var4.hasNext()) {
         mx var5 = (mx)var4.next();
         this.p(var5);
      }

      return this;
   }

   public A2 Q(boolean var1) {
      this.U = var1;
      return this;
   }

   public A2 S(v1 var1) {
      this.D = var1;
      return this;
   }

   public Rv S() {
      return new AL(this.q, this.V, this.n, this.U, this.D);
   }
}
