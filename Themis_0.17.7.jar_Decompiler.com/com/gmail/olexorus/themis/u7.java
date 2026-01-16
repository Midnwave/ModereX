package com.gmail.olexorus.themis;

import io.netty.channel.Channel;
import java.net.SocketAddress;
import java.util.List;

public class u7 implements uJ {
   public SocketAddress D(Object var1) {
      return ((Channel)var1).remoteAddress();
   }

   public boolean t(Object var1) {
      return ((Channel)var1).isOpen();
   }

   public Object K(Object var1, Object var2) {
      return ((Channel)var1).writeAndFlush(var2);
   }

   public Object K(Object var1) {
      return ((Channel)var1).pipeline();
   }

   public List<String> P(Object var1) {
      return ((Channel)var1).pipeline().names();
   }

   public Object y(Object var1) {
      return ((Channel)var1).alloc().buffer();
   }
}
