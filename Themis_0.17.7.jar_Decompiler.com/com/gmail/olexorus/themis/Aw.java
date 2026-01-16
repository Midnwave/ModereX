package com.gmail.olexorus.themis;

public class aw extends aK {
   protected aw(Object var1, Gs var2, Object var3, Object var4, boolean var5) {
      super(RW.CLIENT, var1, var2, var3, var4, var5);
   }

   protected aw(int var1, wC var2, zZ var3, Object var4, Gs var5, Object var6, Object var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   public void V(CJ var1) {
      var1.c(this);
   }

   public aw x() {
      try {
         Object var1 = NY.j(this.n());
         return new aw(this.K(), this.y(), this.M(), this.c(), this.Z(), this.O(), var1);
      } catch (Co var2) {
         var2.printStackTrace();
         return null;
      }
   }
}
