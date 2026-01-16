package com.gmail.olexorus.themis;

import java.util.concurrent.atomic.AtomicInteger;

public class zX {
   private static final AtomicInteger D = new AtomicInteger(1);
   public static final zX Q = new zX();
   public static final zX y = new zX();
   public static final zX F = new zX();
   public static final zX i = new zX();
   private final int j;

   public zX() {
      this.j = D.getAndIncrement();
   }

   public int hashCode() {
      return this.j;
   }

   public boolean equals(Object var1) {
      return this == var1;
   }
}
