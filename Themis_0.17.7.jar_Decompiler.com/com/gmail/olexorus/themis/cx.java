package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import org.bukkit.entity.Player;

class CX extends C4 {
   final R6 j;
   private static final long a = kt.a(-1276632920184074774L, -5408829581826777173L, MethodHandles.lookup().lookupClass()).a(165549763259936L);

   CX(R6 var1, Cs var2) {
      super(var2);
      this.j = var1;
   }

   public void a(af var1) {
   }

   public void g(aF var1) {
   }

   public void U(aM var1) {
   }

   public void n(a9 var1) {
      if (var1.i() == lF.INTERACT_ENTITY) {
         ya var2 = new ya(var1);
         if (var2.U() == u_.ATTACK) {
            Player var3 = (Player)var1.O();
            y2 var4 = new y2(wd.f(var3.getLocation()).v().G().E(0, 1, 0), rS.cU.d().e());
            var1.Z().n(var4);
         }
      }

   }

   public void A(aY var1) {
      long var2 = a ^ 114128654992784L;
      if (var1.X() == rX.BLOCK_CHANGE) {
         y2 var4 = new y2(var1);
         ((Player)var1.O()).sendMessage("Type: " + var4.e().a().o());
      } else if (var1.X() == rX.SYSTEM_CHAT_MESSAGE) {
         new Qi(var1);
      }

   }

   public void m(ay var1) {
      long var2 = a ^ 27165393297242L;
      oS.J().m().n("User: (host-name) " + var1.Z().H().getHostString() + " connected...");
   }

   public void H(ai var1) {
      long var2 = a ^ 72648005412064L;
      oS.J().m().n("You logged in! User name: " + var1.Z().h().s());
   }

   public void I(aI var1) {
      long var2 = a ^ 111626688762145L;
      oS.J().m().n("User: (host-name) " + var1.Z().H().getHostString() + " disconnected...");
   }
}
