package com.gmail.olexorus.themis;

import io.netty.channel.Channel;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class ED implements Listener {
   private final AG u;

   public ED(Plugin var1) {
      this.u = new AG(var1);
   }

   private void o(Channel var1, boolean var2) {
      var1.eventLoop().execute(ED::lambda$setChannelFreeze$0);
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void y(PlayerConnectionValidateLoginEvent var1) {
      if (var1.isAllowed()) {
         if (var1.getConnection() instanceof PlayerConfigurationConnection) {
            Channel var2 = (Channel)EO.W(var1.getConnection());
            this.o(var2, true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void B(PlayerJoinEvent var1) {
      this.u.W(var1.getPlayer());
      Channel var2 = (Channel)EO.L(var1.getPlayer());
      if (var2 != null) {
         this.o(var2, false);
      }

   }

   private static void lambda$setChannelFreeze$0(Channel var0, boolean var1) {
      try {
         NZ var2 = (NZ)oS.J().L();
         CP var3 = var2.N(var0);
         if (var3 != null) {
            var3.h(var0, var1);
         }

      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }
}
