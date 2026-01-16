package com.gmail.olexorus.themis;

import com.gmail.olexorus.themis.api.CheckType;
import java.lang.invoke.MethodHandles;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public final class vA extends vh {
   private static String[] Y;
   private static final long P = kt.a(-5494744328308659779L, -4142566380948079772L, MethodHandles.lookup().lookupClass()).a(52992526067930L);

   public vA(Player var1, long var2, Vb var4) {
      long var10000 = P ^ var2;
      String[] var5 = O();
      super(CheckType.REACH, var1, var4);
      if (var5 == null) {
         vh.j("JxdWB");
      }

   }

   public void O(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   private final double J(Object[] var1) {
      Location var2 = (Location)var1[0];
      BoundingBox var3 = (BoundingBox)var1[1];
      Location var4 = new Location(var2.getWorld(), WB.E(new Object[]{var2.getX(), var3.getMinX(), var3.getMaxX()}), WB.E(new Object[]{var2.getY(), var3.getMinY(), var3.getMaxY()}), WB.E(new Object[]{var2.getZ(), var3.getMinZ(), var3.getMaxZ()}));
      return var2.distanceSquared(var4);
   }

   public static void U(String[] var0) {
      Y = var0;
   }

   public static String[] O() {
      return Y;
   }

   static {
      if (O() == null) {
         U(new String[1]);
      }

   }

   private static RuntimeException a(RuntimeException var0) {
      return var0;
   }
}
