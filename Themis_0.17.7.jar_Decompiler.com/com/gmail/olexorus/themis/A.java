package com.gmail.olexorus.themis;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

final class a implements Map, Serializable, lK {
   public static final a k = new a();
   private static final long serialVersionUID = 8246714829545688274L;
   private static final long a = kt.a(-504394815342338674L, 6142018601494004226L, MethodHandles.lookup().lookupClass()).a(120436793599975L);

   private a() {
   }

   public boolean equals(Object var1) {
      return var1 instanceof Map && ((Map)var1).isEmpty();
   }

   public int hashCode() {
      return 0;
   }

   public String toString() {
      long var1 = a ^ 48906655782291L;
      return "{}";
   }

   public int B() {
      return 0;
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean containsKey(Object var1) {
      return false;
   }

   public boolean c(Void var1) {
      return false;
   }

   public Void u(Object var1) {
      return null;
   }

   public Set<Entry> I() {
      return (Set)zi.B;
   }

   public Set<Object> H() {
      return (Set)zi.B;
   }

   public Collection p() {
      return (Collection)Jk.Q;
   }

   private final Object readResolve() {
      return k;
   }

   public void clear() {
      long var1 = a ^ 87302814024710L;
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   public void putAll(Map var1) {
      long var2 = a ^ 21943962812732L;
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   public Void h(Object var1) {
      long var2 = a ^ 51362513150273L;
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
