package com.gmail.olexorus.themis;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GZ implements Listener {
   @EventHandler
   public void s(PlayerJoinEvent var1) {
      vX var2 = oS.J();
      Gs var3 = var2.v().c(var1.getPlayer());
      oS.J().u().n(new ai(var3, var1.getPlayer()));
   }
}
