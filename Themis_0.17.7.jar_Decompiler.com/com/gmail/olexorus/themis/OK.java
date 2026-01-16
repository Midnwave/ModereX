package com.gmail.olexorus.themis;

public class Ok implements RI {
   private final Nl P;
   private final Nl p;
   private final Nl w;

   public Ok(Nl var1, Nl var2, Nl var3) {
      this.P = var1;
      this.p = var2;
      this.w = var3;
   }

   public static Ok B(lm<?> var0) {
      Nl var1 = Nl.v(var0);
      Nl var2 = Nl.v(var0);
      Nl var3 = Nl.v(var0);
      return new Ok(var1, var2, var3);
   }

   public static void Y(lm<?> var0, Ok var1) {
      Nl.F(var0, var1.P);
      Nl.F(var0, var1.p);
      Nl.F(var0, var1.w);
   }
}
