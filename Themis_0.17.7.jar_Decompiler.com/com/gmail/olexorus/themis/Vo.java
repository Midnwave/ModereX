package com.gmail.olexorus.themis;

import com.gmail.olexorus.themis.api.CheckType;
import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.VehicleExitEvent;

public final class vO extends vh {
   private double Hh;
   private double Y = 0.4D;
   private boolean Hf;
   private boolean HM = true;
   private double Hz = 1.0D;
   private boolean P;
   private static String HK;

   public vO(Player var1, Vb var2) {
      super(CheckType.BOAT_MOVEMENT, var1, var2);
   }

   public void S(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public void P(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public void f(Object[] var1) {
      VehicleExitEvent var2 = (VehicleExitEvent)var1[0];
      this.P = false;
   }

   public static void L(String var0) {
      HK = var0;
   }

   public static String h() {
      return HK;
   }

   private static RuntimeException a(RuntimeException var0) {
      return var0;
   }

   static {
      if (h() == null) {
         L("iwnCC");
      }

   }
}
