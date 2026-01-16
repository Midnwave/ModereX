package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Iterator;

final class Nv<T> implements Collection<T>, lK {
   private final T[] v;
   private final boolean f;
   private static final long a = kt.a(5333799702655645510L, -4537976609214246944L, MethodHandles.lookup().lookupClass()).a(43303077471942L);

   public Nv(T[] var1, boolean var2) {
      this.v = var1;
      this.f = var2;
   }

   public int f() {
      return this.v.length;
   }

   public boolean isEmpty() {
      return this.v.length == 0;
   }

   public boolean contains(Object var1) {
      return Ej.v(this.v, var1);
   }

   public boolean containsAll(Collection<? extends Object> var1) {
      Iterable var2 = (Iterable)var1;
      boolean var3 = false;
      boolean var10000;
      if (((Collection)var2).isEmpty()) {
         var10000 = true;
      } else {
         Iterator var4 = var2.iterator();

         while(true) {
            if (!var4.hasNext()) {
               var10000 = true;
               break;
            }

            Object var5 = var4.next();
            boolean var7 = false;
            if (!this.contains(var5)) {
               var10000 = false;
               break;
            }
         }
      }

      return var10000;
   }

   public Iterator<T> iterator() {
      return v0.N(this.v);
   }

   public final Object[] toArray() {
      return wF.l(this.v, this.f);
   }

   public boolean add(T var1) {
      long var2 = a ^ 74397386488909L;
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   public boolean addAll(Collection<? extends T> var1) {
      long var2 = a ^ 88362450274068L;
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   public void clear() {
      long var1 = a ^ 17111575852308L;
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   public boolean remove(Object var1) {
      long var2 = a ^ 113622225098953L;
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   public boolean removeAll(Collection<? extends Object> var1) {
      long var2 = a ^ 85317241526938L;
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   public boolean retainAll(Collection<? extends Object> var1) {
      long var2 = a ^ 57317162982353L;
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   public <T> T[] toArray(T[] var1) {
      return z8.s((Collection)this, var1);
   }
}
