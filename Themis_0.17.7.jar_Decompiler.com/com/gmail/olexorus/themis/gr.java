package com.gmail.olexorus.themis;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import java.lang.invoke.MethodHandles;
import java.util.NoSuchElementException;
import java.util.UUID;

public class GR {
   private static final long a = kt.a(5963082532238165089L, -3895963643430899795L, MethodHandles.lookup().lookupClass()).a(240143142269237L);

   public static void K(Object var0, uy var1) {
      long var2 = a ^ 95693913092301L;
      Channel var4 = (Channel)var0;
      if (!rz.y(var4)) {
         Gs var5 = new Gs(var4, var1, (vL)null, new Ml((UUID)null, (String)null));
         if (var1 == uy.PLAY) {
            var5.H(oS.J().g().w().u());
            oS.J().m().A("Late injection detected, we missed packets so some functionality may break!");
         }

         synchronized(var4) {
            if (var4.pipeline().get("splitter") == null) {
               var4.close();
            } else {
               ay var7 = new ay(var5);
               oS.J().u().n(var7);
               if (var7.Q()) {
                  var4.unsafe().closeForcibly();
               } else {
                  w(var4, (Nu)null, var5);
                  var4.closeFuture().addListener(GR::lambda$initChannel$0);
                  oS.J().X().o(var4, var5);
               }
            }
         }
      }
   }

   public static void N(Object var0) {
      long var1 = a ^ 114101002076590L;
      Channel var3 = (Channel)var0;
      if (var3.pipeline().get(oS.z) != null) {
         var3.pipeline().remove(oS.z);
      } else {
         oS.J().s().warning("Could not find decoder handler in channel pipeline!");
      }

      if (var3.pipeline().get(oS.H) != null) {
         var3.pipeline().remove(oS.H);
      } else {
         oS.J().s().warning("Could not find encoder handler in channel pipeline!");
      }

   }

   public static void w(Channel var0, Nu var1, Gs var2) {
      long var3 = a ^ 2298157633915L;

      String var6;
      try {
         CP var9;
         if (var1 != null) {
            if (var1.C) {
               return;
            }

            var1.C = true;
            var1 = (Nu)var0.pipeline().remove(oS.z);
            ChannelHandler var5 = var0.pipeline().remove(oS.H);
            var1 = new Nu(var1);
            var9 = new CP(var5);
         } else {
            var9 = new CP(var2);
            var1 = new Nu(var2);
         }

         var6 = var0.pipeline().names().contains("inbound_config") ? "inbound_config" : "decoder";
         var0.pipeline().addBefore(var6, oS.z, var1);
         String var7 = var0.pipeline().names().contains("outbound_config") ? "outbound_config" : "encoder";
         var0.pipeline().addBefore(var7, oS.H, var9);
      } catch (NoSuchElementException var8) {
         var6 = lC.M(var0);
         throw new IllegalStateException("PacketEvents failed to add a decoder to the netty pipeline. Pipeline handlers: " + var6, var8);
      }
   }

   private static void lambda$initChannel$0(Gs var0, ChannelFuture var1) {
      JO.Y(var0.b(), var0.e());
   }
}
