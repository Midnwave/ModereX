package com.gmail.olexorus.themis;

import com.gmail.olexorus.themis.api.CheckType;
import java.lang.invoke.MethodHandles;
import java.util.ArrayDeque;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class v6 extends vh {
   private ArrayDeque<z0<Integer, Long>> P = new ArrayDeque();
   private int wb = 1;
   private static String Y;
   private static final long bb = kt.a(2933370750520763536L, 7394447957946295375L, MethodHandles.lookup().lookupClass()).a(133237659956402L);

   public v6(Player var1, Vb var2) {
      super(CheckType.ELYTRA_FLIGHT, var1, var2);
   }

   public void S(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   private final z0 E(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public void p(Object[] var1) {
      long var2 = (Long)var1[0];
      PlayerTeleportEvent var4 = (PlayerTeleportEvent)var1[1];
      this.wb = 2;
   }

   public void F(Object[] var1) {
      PlayerRespawnEvent var2 = (PlayerRespawnEvent)var1[0];
      this.wb = 2;
   }

   public void d(Object[] var1) {
      EntitySpawnEvent var2 = (EntitySpawnEvent)var1[0];

      int var4;
      try {
         var4 = ((Firework)var2.getEntity()).getFireworkMeta().getPower();
      } catch (NullPointerException var6) {
         var4 = 3;
      }

      var4 = (Math.max(var4, 1) + 1) * 10 + 5 + 6;
      this.P.add(new z0(var4, System.currentTimeMillis() + (long)(var4 * 50) + (long)1000));
   }

   public static void U(String var0) {
      Y = var0;
   }

   public static String N() {
      return Y;
   }

   static {
      if (N() != null) {
         U("FSvVqc");
      }

   }

   private static NullPointerException a(NullPointerException var0) {
      return var0;
   }
}
