package com.gmail.olexorus.themis;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.invoke.MethodHandles;

public class Wz extends ChannelInboundHandlerAdapter {
   private static final InternalLogger D = InternalLoggerFactory.getInstance(ChannelInitializer.class);
   private static final long a = kt.a(-6762630865280550239L, 3033223066396499394L, MethodHandles.lookup().lookupClass()).a(12491281948353L);

   public void channelRegistered(ChannelHandlerContext var1) {
      boolean var7 = false;

      label60: {
         ChannelPipeline var2;
         label59: {
            try {
               var7 = true;
               GR.K(var1.channel(), uy.HANDSHAKING);
               var7 = false;
               break label59;
            } catch (Throwable var8) {
               this.exceptionCaught(var1, var8);
               var7 = false;
            } finally {
               if (var7) {
                  ChannelPipeline var4 = var1.pipeline();
                  if (var4.context(this) != null) {
                     var4.remove(this);
                  }

               }
            }

            var2 = var1.pipeline();
            if (var2.context(this) != null) {
               var2.remove(this);
            }
            break label60;
         }

         var2 = var1.pipeline();
         if (var2.context(this) != null) {
            var2.remove(this);
         }
      }

      var1.pipeline().fireChannelRegistered();
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
      long var3 = a ^ 27928828874903L;
      D.warn("Failed to initialize a channel. Closing: " + var1.channel(), var2);
      var1.close();
   }
}
