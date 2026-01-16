package com.gmail.olexorus.themis;

import com.gmail.olexorus.themis.api.CheckType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class vZ extends vh {
   private int Y;
   private static vh[] P;

   public vZ(Player var1, Vb var2) {
      super(CheckType.PACKET_SPOOF, var1, var2);
   }

   public void S(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public void p(Object[] var1) {
      long var3 = (Long)var1[0];
      PlayerTeleportEvent var2 = (PlayerTeleportEvent)var1[1];
      this.Y = 1;
   }

   public void F(Object[] var1) {
      PlayerRespawnEvent var2 = (PlayerRespawnEvent)var1[0];
      this.Y = 1;
   }

   public static void p(vh[] var0) {
      P = var0;
   }

   public static vh[] U() {
      return P;
   }

   private static RuntimeException a(RuntimeException var0) {
      return var0;
   }

   static {
      if (U() == null) {
         p(new vh[4]);
      }

   }
}
