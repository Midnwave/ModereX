package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import org.bukkit.Bukkit;

public class u9 extends u3 {
   private final String x;
   private static final long a = kt.a(5155973049150313610L, -6667754720901694622L, MethodHandles.lookup().lookupClass()).a(48008619627969L);

   public u9() {
      long var1 = a ^ 56090156460755L;
      super();
      this.x = o4.p(oT.F) + "[packetevents] " + o4.p(oT.g);
   }

   protected void g(Level var1, oT var2, String var3) {
      Bukkit.getConsoleSender().sendMessage(this.x + o4.p(var2) + var3);
   }

   public void F(String var1) {
      this.g(Level.INFO, oT.g, var1);
   }

   public void A(String var1) {
      this.g(Level.WARNING, oT.N, var1);
   }

   public void Q(String var1) {
      this.g(Level.SEVERE, oT.T, var1);
   }

   public void n(String var1) {
      if (oS.J().z().P()) {
         this.g(Level.FINE, oT.q, var1);
      }

   }
}
