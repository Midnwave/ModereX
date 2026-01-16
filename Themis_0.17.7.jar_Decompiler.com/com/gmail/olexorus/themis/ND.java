package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class nD implements ra {
   public static final nD W = new nD(Collections.emptyMap(), Collections.emptyMap());
   private final Map<nW<?>, ?> c;
   private final Map<nW<?>, Optional<?>> r;
   private static final long b = kt.a(7881935328809179919L, 3982488292151731373L, MethodHandles.lookup().lookupClass()).a(4122420085634L);

   public nD(zQ var1) {
      this((Map)var1.o(), new HashMap());
   }

   public nD(zQ var1, Map<nW<?>, Optional<?>> var2) {
      this(var1.o(), var2);
   }

   public nD(Map<nW<?>, ?> var1, Map<nW<?>, Optional<?>> var2) {
      this.c = Collections.unmodifiableMap(new HashMap(var1));
      this.r = var2;
   }

   public <T> T v(nW<T> var1) {
      Optional var2 = (Optional)this.r.get(var1);
      return var2 != null ? var2.orElse((Object)null) : this.c.get(var1);
   }

   public <T> void S(nW<T> var1, Optional<T> var2) {
      Object var3 = this.c.get(var1);
      Object var4 = var2.orElse((Object)null);
      if (Objects.equals(var3, var4)) {
         this.r.remove(var1);
      } else {
         this.r.put(var1, var2);
      }

   }

   public Map<nW<?>, Optional<?>> N() {
      return this.r;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof nD)) {
         return false;
      } else {
         nD var2 = (nD)var1;
         return !this.c.equals(var2.c) ? false : this.r.equals(var2.r);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.c, this.r});
   }

   public String toString() {
      long var1 = b ^ 38285168408280L;
      return "PatchableComponentMap{base=" + this.c + ", patches=" + this.r + '}';
   }
}
