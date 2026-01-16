package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class u3 {
   protected static final Pattern H;

   protected void g(Level var1, oT var2, String var3) {
      var3 = H.matcher(var3).replaceAll("");
      oS.J().s().log(var1, var2 != null ? var2.toString() : "" + var3);
   }

   public void F(String var1) {
      this.g(Level.INFO, (oT)null, var1);
   }

   public void A(String var1) {
      this.g(Level.WARNING, (oT)null, var1);
   }

   public void Q(String var1) {
      this.g(Level.SEVERE, (oT)null, var1);
   }

   public void n(String var1) {
      if (this.t()) {
         this.g(Level.FINE, (oT)null, var1);
      }

   }

   public boolean t() {
      return oS.J().z().P();
   }

   static {
      long var0 = kt.a(-1717021743243065207L, -8928224804918973564L, MethodHandles.lookup().lookupClass()).a(160473795832216L) ^ 53514462593204L;
      H = Pattern.compile("(?i)§[0-9A-FK-ORX]");
   }
}
