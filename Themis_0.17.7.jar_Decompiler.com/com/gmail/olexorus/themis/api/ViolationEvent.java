package com.gmail.olexorus.themis.api;

import com.gmail.olexorus.themis.MH;
import com.gmail.olexorus.themis.kt;
import com.gmail.olexorus.themis.vh;
import java.lang.invoke.MethodHandles;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ViolationEvent extends Event implements Cancellable {
   public static final ViolationEvent$Companion Companion;
   private final Player y;
   private final CheckType b;
   private final double q;
   private final boolean D;
   private boolean w;
   private static final HandlerList G;
   private static String[] P;
   private static final long a = kt.a(-4627310704018510901L, -5048199770114386934L, MethodHandles.lookup().lookupClass()).a(132583401453817L);

   public ViolationEvent(Player var1, CheckType var2, double var3, boolean var5) {
      long var6 = a ^ 94938834440260L;
      String[] var8 = I();
      super();
      this.y = var1;
      this.b = var2;
      this.q = var3;
      this.D = var5;
      if (vh.i() == null) {
         s(new String[5]);
      }

   }

   public final Player getPlayer() {
      return this.y;
   }

   public final CheckType getType() {
      return this.b;
   }

   public final double getSeverity() {
      return this.q;
   }

   public final boolean getDragback() {
      return this.D;
   }

   public HandlerList getHandlers() {
      return G;
   }

   public boolean isCancelled() {
      return this.w;
   }

   public void setCancelled(boolean var1) {
      this.w = var1;
   }

   public static final HandlerList getHandlerList() {
      return Companion.getHandlerList();
   }

   public static final HandlerList access$getHANDLERS$cp() {
      return G;
   }

   static {
      String[] var10000 = new String[3];
      Companion = new ViolationEvent$Companion((MH)null);
      G = new HandlerList();
      s(var10000);
   }

   public static void s(String[] var0) {
      P = var0;
   }

   public static String[] I() {
      return P;
   }
}
