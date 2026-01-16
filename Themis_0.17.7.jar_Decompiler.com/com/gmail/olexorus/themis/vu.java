package com.gmail.olexorus.themis;

public class Vu extends RuntimeException {
   final boolean Y;
   final Oh L;
   final String[] G;

   public Vu() {
      this((String)null, true);
   }

   public Vu(boolean var1) {
      this((String)null, var1);
   }

   public Vu(t var1, String... var2) {
      this(var1.n(), var2);
   }

   public Vu(Oh var1, String... var2) {
      this(var1, true, var2);
   }

   public Vu(t var1, boolean var2, String... var3) {
      this(var1.n(), var2, var3);
   }

   public Vu(Oh var1, boolean var2, String... var3) {
      super(var1.E(), (Throwable)null, false, false);
      this.Y = var2;
      this.L = var1;
      this.G = var3;
   }

   public Vu(String var1, boolean var2) {
      super(var1, (Throwable)null, false, false);
      this.Y = var2;
      this.G = null;
      this.L = null;
   }
}
