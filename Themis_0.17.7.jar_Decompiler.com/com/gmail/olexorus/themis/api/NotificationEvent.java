package com.gmail.olexorus.themis.api;

import com.gmail.olexorus.themis.MH;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class NotificationEvent extends Event implements Cancellable {
   public static final NotificationEvent$Companion Companion = new NotificationEvent$Companion((MH)null);
   private final String O;
   private boolean J;
   private static final HandlerList F = new HandlerList();
   private static String n;

   public NotificationEvent(String var1) {
      this.O = var1;
   }

   public final String getMessage() {
      return this.O;
   }

   public HandlerList getHandlers() {
      return F;
   }

   public boolean isCancelled() {
      return this.J;
   }

   public void setCancelled(boolean var1) {
      this.J = var1;
   }

   public static final HandlerList getHandlerList() {
      return Companion.getHandlerList();
   }

   public static final HandlerList access$getHANDLERS$cp() {
      return F;
   }

   static {
      z((String)null);
   }

   public static void z(String var0) {
      n = var0;
   }

   public static String Y() {
      return n;
   }
}
