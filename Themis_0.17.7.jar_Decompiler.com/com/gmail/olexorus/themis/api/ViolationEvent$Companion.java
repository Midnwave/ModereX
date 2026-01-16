package com.gmail.olexorus.themis.api;

import com.gmail.olexorus.themis.MH;
import org.bukkit.event.HandlerList;

public final class ViolationEvent$Companion {
   private static int M;

   private ViolationEvent$Companion() {
   }

   public final HandlerList getHandlerList() {
      return ViolationEvent.access$getHANDLERS$cp();
   }

   public ViolationEvent$Companion(MH var1) {
      this();
   }

   public static void n(int var0) {
      M = var0;
   }

   public static int f() {
      return M;
   }

   public static int O() {
      int var0 = f();

      try {
         return var0 == 0 ? 49 : 0;
      } catch (RuntimeException var1) {
         throw a(var1);
      }
   }

   private static RuntimeException a(RuntimeException var0) {
      return var0;
   }

   static {
      if (O() != 0) {
         n(39);
      }

   }
}
