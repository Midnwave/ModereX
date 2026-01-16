package com.gmail.olexorus.themis;

public final class Gi {
   public static final Gi I = new Gi(0);
   public static final Gi v = new Gi(1);
   public static final Gi R = new Gi(2);
   public static final Gi l = new Gi(4);
   public static final Gi K = new Gi(8);
   public static final Gi b = new Gi(16);
   public static final Gi L = new Gi(32);
   public static final Gi H = new Gi(64);
   public static final Gi f = new Gi(128);
   public static final Gi S = new Gi(256);
   private final int C;

   public Gi(int var1) {
      this.C = var1;
   }

   public int a() {
      return this.C;
   }
}
