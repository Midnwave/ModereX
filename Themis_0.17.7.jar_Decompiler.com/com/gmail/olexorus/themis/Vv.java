package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class vV<T> implements Iterable<rq<T>> {
   private List<rq<T>> K;

   public vV() {
      this((List)(new ArrayList()));
   }

   public vV(List<rq<T>> var1) {
      this.K = var1;
   }

   public vV(T var1, int var2) {
      this(new rq(var1, var2));
   }

   public vV(rq<T> var1) {
      this.K = new ArrayList(1);
      this.K.add(var1);
   }

   public static <T> tw<vV<T>> e(tw<T> var0) {
      return rq.G(var0).c().K(vV::new, vV::f);
   }

   public List<rq<T>> f() {
      return this.K;
   }

   public boolean o() {
      return this.K.isEmpty();
   }

   public Iterator<rq<T>> iterator() {
      return this.K.iterator();
   }
}
