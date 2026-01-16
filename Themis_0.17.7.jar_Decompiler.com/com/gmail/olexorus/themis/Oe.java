package com.gmail.olexorus.themis;

import java.util.List;
import java.util.concurrent.TimeUnit;

public final class OE<K, V> {
   private ml h;
   private List<TI<K, V>> Y;
   private List<TI<K, V>> m;
   private TimeUnit c;
   private boolean G;
   private long X;
   private int a;
   private NL<K, V> C;
   private J4<K, V> Q;
   private static final long b = kt.a(6895283348237104741L, -1564772824747219L, (Object)null).a(67039968697069L);

   private OE() {
      this.h = ml.CREATED;
      this.c = TimeUnit.SECONDS;
      this.X = 60L;
      this.a = Integer.MAX_VALUE;
   }

   public <K1 extends K, V1 extends V> zk<K1, V1> I() {
      return new zk(this, (Ji)null);
   }

   public OE<K, V> Z(long var1, TimeUnit var3) {
      long var4 = b ^ 23260512906676L;
      this.X = var1;
      this.c = (TimeUnit)lg.R(var3, "timeUnit");
      return this;
   }

   public OE<K, V> k(int var1) {
      long var2 = b ^ 73436756446105L;
      lg.H(var1 > 0, "maxSize");
      this.a = var1;
      return this;
   }

   public OE<K, V> L(ml var1) {
      long var2 = b ^ 2259995962889L;
      this.h = (ml)lg.R(var1, "expirationPolicy");
      return this;
   }

   static List l(OE var0) {
      return var0.m;
   }

   static boolean B(OE var0) {
      return var0.G;
   }

   static List B(OE var0) {
      return var0.Y;
   }

   static ml z(OE var0) {
      return var0.h;
   }

   static long x(OE var0) {
      return var0.X;
   }

   static TimeUnit Q(OE var0) {
      return var0.c;
   }

   static int d(OE var0) {
      return var0.a;
   }

   static NL k(OE var0) {
      return var0.C;
   }

   static J4 p(OE var0) {
      return var0.Q;
   }

   OE(Ji var1) {
      this();
   }
}
