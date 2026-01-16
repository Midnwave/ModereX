package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;

final class Bq extends BG implements zj {
   static final zj u;
   private final List<MC> T;
   private final uX<? extends MC> t;
   private final int c;
   private static final long b = kt.a(-2424617699465211413L, 8390356364610455658L, MethodHandles.lookup().lookupClass()).a(237376729345447L);

   Bq(uX<? extends MC> var1, List<MC> var2) {
      this.T = Collections.unmodifiableList(var2);
      this.t = var1;
      this.c = var2.hashCode();
   }

   public uX<? extends MC> N() {
      return this.t;
   }

   public int o() {
      return this.T.size();
   }

   public zj Z(MC var1) {
      j(var1);
      if (this.t != tq.j) {
         Z(var1, this.t);
      }

      return this.e(Bq::lambda$add$2, var1.y());
   }

   static void j(MC var0) {
      long var1 = b ^ 41293609541201L;
      if (var0.y() == tq.j) {
         throw new IllegalArgumentException(String.format("Cannot add a %s to a %s", tq.j, tq.x));
      }
   }

   static void Z(MC var0, uX<? extends MC> var1) {
      long var2 = b ^ 115301084998721L;
      if (var0.y() != var1) {
         throw new IllegalArgumentException(String.format("Trying to add tag of type %s to list of %s", var0.y(), var1));
      }
   }

   private zj e(Consumer<List<MC>> var1, uX<? extends MC> var2) {
      ArrayList var3 = new ArrayList(this.T);
      var1.accept(var3);
      uX var4 = this.t;
      if (var2 != null && var4 == tq.j) {
         var4 = var2;
      }

      return new Bq(var4, var3);
   }

   public Iterator<MC> iterator() {
      Iterator var1 = this.T.iterator();
      return new Bo(this, var1);
   }

   public void forEach(Consumer<? super MC> var1) {
      this.T.forEach(var1);
   }

   public Spliterator<MC> spliterator() {
      return Spliterators.spliterator(this.T, 1040);
   }

   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof Bq && this.T.equals(((Bq)var1).T);
   }

   public int hashCode() {
      return this.c;
   }

   public Stream<? extends rE> T() {
      long var1 = b ^ 127746837103129L;
      return Stream.of(rE.E("tags", this.T), rE.E("type", this.t));
   }

   private static void lambda$add$3(Iterable var0, List var1) {
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         MC var3 = (MC)var2.next();
         var1.add(var3);
      }

   }

   private static void lambda$add$2(MC var0, List var1) {
      var1.add(var0);
   }

   private static void lambda$remove$1(int var0, Consumer var1, List var2) {
      MC var3 = (MC)var2.remove(var0);
      if (var1 != null) {
         var1.accept(var3);
      }

   }

   private static void lambda$set$0(int var0, MC var1, Consumer var2, List var3) {
      MC var4 = (MC)var3.set(var0, var1);
      if (var2 != null) {
         var2.accept(var4);
      }

   }

   static {
      u = new Bq(tq.j, Collections.emptyList());
   }
}
