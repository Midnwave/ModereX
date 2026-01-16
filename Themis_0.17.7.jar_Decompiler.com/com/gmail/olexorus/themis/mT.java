package com.gmail.olexorus.themis;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import org.bukkit.entity.Player;

public class Mt implements RF {
   private static Field s;
   private static final long a = kt.a(6823289816628331818L, 5910348427020514284L, MethodHandles.lookup().lookupClass()).a(9337650404924L);

   public int g(Player var1) {
      return Via.getAPI().getPlayerVersion(var1);
   }

   public int L(Gs var1) {
      long var2 = a ^ 73293861405146L;

      try {
         ChannelHandler var4 = ((Channel)var1.b()).pipeline().get("via-encoder");
         if (s == null) {
            s = rB.x(var4.getClass(), "connection");
         }

         UserConnection var5 = (UserConnection)s.get(var4);
         return var5.getProtocolInfo().getProtocolVersion();
      } catch (IllegalAccessException var6) {
         oS.J().m().A("Unable to grab ViaVersion client version for player!");
         return -1;
      }
   }
}
