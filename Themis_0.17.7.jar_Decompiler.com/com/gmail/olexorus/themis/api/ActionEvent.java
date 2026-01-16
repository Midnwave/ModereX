package com.gmail.olexorus.themis.api;

import com.gmail.olexorus.themis.MH;
import com.gmail.olexorus.themis.kt;
import com.gmail.olexorus.themis.vh;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ActionEvent extends Event implements Cancellable {
   public static final ActionEvent$Companion Companion;
   private final Player l;
   private final CheckType S;
   private final String o;
   private final List<String> A;
   private boolean Q;
   private static final HandlerList b;
   private static vh[] c;
   private static final long a = kt.a(3128036681036394729L, 5741866592069524609L, MethodHandles.lookup().lookupClass()).a(194783327957212L);

   public ActionEvent(Player var1, CheckType var2, String var3, List<String> var4) {
      long var5 = a ^ 104227367951072L;
      String[] var10000 = ViolationEvent.I();
      super();
      this.l = var1;
      this.S = var2;
      String[] var7 = var10000;

      try {
         this.o = var3;
         this.A = var4;
         if (var7 == null) {
            vh.j("bbqH8b");
         }

      } catch (RuntimeException var8) {
         throw a(var8);
      }
   }

   public final Player getPlayer() {
      return this.l;
   }

   public final CheckType getType() {
      return this.S;
   }

   public final String getActionName() {
      return this.o;
   }

   public final List<String> getCommands() {
      return this.A;
   }

   public HandlerList getHandlers() {
      return b;
   }

   public boolean isCancelled() {
      return this.Q;
   }

   public void setCancelled(boolean var1) {
      this.Q = var1;
   }

   public static final HandlerList getHandlerList() {
      return Companion.getHandlerList();
   }

   public static final HandlerList access$getHANDLERS$cp() {
      return b;
   }

   static {
      vh[] var10000 = new vh[1];
      Companion = new ActionEvent$Companion((MH)null);
      Q(var10000);
      b = new HandlerList();
   }

   public static void Q(vh[] var0) {
      c = var0;
   }

   public static vh[] c() {
      return c;
   }

   private static RuntimeException a(RuntimeException var0) {
      return var0;
   }
}
