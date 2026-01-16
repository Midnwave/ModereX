package com.gmail.olexorus.themis.api;

import com.gmail.olexorus.themis.MH;
import com.gmail.olexorus.themis.vh;
import org.bukkit.event.HandlerList;

public final class NotificationEvent$Companion {
   private static vh[] c;

   private NotificationEvent$Companion() {
   }

   public final HandlerList getHandlerList() {
      return NotificationEvent.access$getHANDLERS$cp();
   }

   public NotificationEvent$Companion(MH var1) {
      this();
   }

   public static void s(vh[] var0) {
      c = var0;
   }

   public static vh[] s() {
      return c;
   }

   static {
      if (s() != null) {
         s(new vh[5]);
      }

   }
}
