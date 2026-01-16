package com.gmail.olexorus.themis;

public class aM extends aE {
   public aM(Object var1, Gs var2, Object var3, Object var4, boolean var5) {
      super(var1, var2, var3, var4, var5);
   }

   protected aM(int var1, wC var2, zZ var3, Object var4, Gs var5, Object var6, Object var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   public aM Q() {
      try {
         Object var1 = NY.j(this.n());
         return new aM(this.K(), this.m(), this.M(), this.c(), this.Z(), this.O(), var1);
      } catch (Co var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public gt m() {
      return (gt)super.y();
   }
}
