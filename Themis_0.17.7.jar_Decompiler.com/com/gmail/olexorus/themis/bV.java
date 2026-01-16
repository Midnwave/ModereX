package com.gmail.olexorus.themis;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

public class BV extends ChannelInitializer<Channel> {
   protected void initChannel(Channel var1) {
      var1.pipeline().addLast(new ChannelHandler[]{new VM(this)});
   }
}
