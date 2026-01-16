package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

abstract class Av<C extends d<C, B>, B extends GH<C, B>> implements GH<C, B> {
   protected List<X> y = Collections.emptyList();
   private WR x;
   private Nr z;
   private static final long a = kt.a(-6020500940705951666L, 831772924158039503L, MethodHandles.lookup().lookupClass()).a(163753364792456L);

   protected Av() {
   }

   protected Av(C var1) {
      List var2 = var1.C();
      if (!var2.isEmpty()) {
         this.y = new ArrayList(var2);
      }

      if (var1.R()) {
         this.x = var1.o();
      }

   }

   public B U(X var1) {
      long var2 = a ^ 137262244192035L;
      if (var1 == X.f()) {
         return this;
      } else {
         this.C();
         this.y.add((X)Objects.requireNonNull(var1, "component"));
         return this;
      }
   }

   public B y(Iterable<? extends lv> var1) {
      long var2 = a ^ 22142028699239L;
      Objects.requireNonNull(var1, "components");
      boolean var4 = false;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         lv var6 = (lv)var5.next();
         X var7 = ((lv)Objects.requireNonNull(var6, "components[?]")).T();
         if (var7 != X.f()) {
            if (!var4) {
               this.C();
               var4 = true;
            }

            this.y.add((X)Objects.requireNonNull(var7, "components[?]"));
         }
      }

      return this;
   }

   private void C() {
      if (this.y == Collections.emptyList()) {
         this.y = new ArrayList();
      }

   }

   public List<X> X() {
      return Collections.unmodifiableList(this.y);
   }

   public B r(WR var1) {
      this.x = var1;
      this.z = null;
      return this;
   }

   public B D(BX var1) {
      this.O().X(var1);
      return this;
   }

   public B Z(MJ var1) {
      this.O().C(var1);
      return this;
   }

   public B Q(ux var1, NW var2) {
      this.O().e(var1, var2);
      return this;
   }

   public B k(Eb var1) {
      this.O().W(var1);
      return this;
   }

   private Nr O() {
      if (this.z == null) {
         if (this.x != null) {
            this.z = this.x.y();
            this.x = null;
         } else {
            this.z = WR.s();
         }
      }

      return this.z;
   }

   protected final boolean R() {
      return this.z != null || this.x != null;
   }

   protected WR l() {
      if (this.z != null) {
         return this.z.C();
      } else {
         return this.x != null ? this.x : WR.O();
      }
   }
}
