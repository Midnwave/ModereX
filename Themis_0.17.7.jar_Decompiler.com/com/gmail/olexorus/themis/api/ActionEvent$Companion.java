package com.gmail.olexorus.themis.api;

import com.gmail.olexorus.themis.MH;
import org.bukkit.event.HandlerList;

public final class ActionEvent$Companion {
   private static String r;

   private ActionEvent$Companion() {
   }

   public final HandlerList getHandlerList() {
      return ActionEvent.access$getHANDLERS$cp();
   }

   public ActionEvent$Companion(MH var1) {
      this();
   }

   public static void f(String var0) {
      r = var0;
   }

   public static String I() {
      return r;
   }

   static {
      if (I() != null) {
         f("sUw1fb");
      }

   }
}
