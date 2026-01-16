package com.gmail.olexorus.themis;

import java.util.Objects;

public class JQ {
   private boolean h;
   private boolean a;
   private CW d;
   private CW q;

   public JQ(boolean var1, boolean var2, CW var3, CW var4) {
      this.h = var1;
      this.a = var2;
      this.d = var3;
      this.q = var4;
   }

   public static JQ O(lm<?> var0) {
      boolean var1 = var0.P();
      boolean var2 = var0.P();
      CW var3 = (CW)var0.u(CW::B);
      CW var4 = (CW)var0.u(CW::B);
      return new JQ(var1, var2, var3, var4);
   }

   public static void I(lm<?> var0, JQ var1) {
      var0.I(var1.h);
      var0.I(var1.a);
      var0.l(var1.d, CW::O);
      var0.l(var1.q, CW::O);
   }

   public boolean equals(Object var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         JQ var2 = (JQ)var1;
         if (this.h != var2.h) {
            return false;
         } else if (this.a != var2.a) {
            return false;
         } else {
            return !Objects.equals(this.d, var2.d) ? false : Objects.equals(this.q, var2.q);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.h, this.a, this.d, this.q});
   }
}
