package com.gmail.olexorus.themis;

import com.gmail.olexorus.themis.api.CheckType;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.util.BoundingBox;

public final class Ea implements Listener, na, PluginMessageListener {
   public static final wR N;
   private boolean S;
   private static Ea O;
   private static final HashMap<UUID, EnumMap<CheckType, oG>> I;
   private static final ConcurrentHashMap<UUID, Vb> B;
   private static final ConcurrentHashMap<Integer, z0<Entity, ConcurrentLinkedQueue<z0<Integer, BoundingBox>>>> u;
   private static z0<Integer, Integer> k;
   private static final BC<z0<Location, Long>> Z;
   private static int F;
   private static final EP<EnumMap<CheckType, oG>> h;
   private static boolean V;
   private static final long a = kt.a(-7959578968448651956L, -4178654648562108114L, MethodHandles.lookup().lookupClass()).a(205855091030862L);
   private static final String[] b;
   private static final String[] c;
   private static final Map d = new HashMap(13);

   private Ea() {
      long var1 = a ^ 84296456858518L;
      long var3 = var1 ^ 127428496741610L;
      long var10001 = var1 ^ 119507297149415L;
      int var5 = (int)((var1 ^ 119507297149415L) >>> 48);
      int var6 = (int)(var10001 << 16 >>> 48);
      int var7 = (int)(var10001 << 32 >>> 32);
      long var8 = var1 ^ 101457205818341L;
      int[] var10000 = Vb.m();
      super();
      Iterable var11 = (Iterable)Bukkit.getOnlinePlayers();
      boolean var12 = false;
      int[] var10 = var10000;
      Iterator var13 = var11.iterator();

      while(true) {
         if (var13.hasNext()) {
            Object var14 = var13.next();
            Player var15 = (Player)var14;
            boolean var16 = false;
            Vb var17 = new Vb(var15, (short)var5, (short)var6, var7);
            Map var18 = (Map)B;

            try {
               var18.put(var15.getUniqueId(), var17);
               var17.c(new Object[]{var8});
               if (var10 == null) {
                  break;
               }

               if (var10 != null) {
                  continue;
               }
            } catch (NullPointerException var19) {
               throw a(var19);
            }
         }

         Bukkit.getServer().getMessenger().registerIncomingPluginChannel((Plugin)Themis.g.n(new Object[]{var3}), true.k<invokedynamic>(8802, 7145789043624180583L ^ var1), (PluginMessageListener)this);
         Bukkit.getScheduler().runTaskTimer((Plugin)Themis.g.n(new Object[]{var3}), Ea::P, 0L, 1L);
         Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)Themis.g.n(new Object[]{var3}), Ea::t, 0L, 200L);
         break;
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public final void r(PlayerLoginEvent var1) {
      long var2 = a ^ 132653417995673L;
      long var10001 = var2 ^ 97581631330280L;
      int var4 = (int)((var2 ^ 97581631330280L) >>> 48);
      int var5 = (int)(var10001 << 16 >>> 48);
      int var6 = (int)(var10001 << 32 >>> 32);
      if (var1.getResult() == Result.ALLOWED) {
         Map var7 = (Map)B;
         UUID var8 = var1.getPlayer().getUniqueId();
         Vb var9 = new Vb(var1.getPlayer(), (short)var4, (short)var5, var6);
         var7.put(var8, var9);
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public final void t(PlayerJoinEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public final void k(PlayerQuitEvent param1) {
      // $FF: Couldn't be decompiled
   }

   public void onPluginMessageReceived(String param1, Player param2, byte[] param3) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void c(PlayerMoveEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public final void m(PlayerMoveEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void a(PlayerTeleportEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void K(PlayerRespawnEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void K(PlayerRiptideEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public final void U(PlayerToggleFlightEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void N(EntityPotionEffectEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void S(BlockPistonExtendEvent var1) {
      long var2 = a ^ 11008512689120L;
      long var4 = var2 ^ 100469623973579L;
      long var6 = var2 ^ 71959393129070L;
      int[] var10000 = Vb.m();
      N.q(new Object[]{var4});
      Z.addFirst(new z0(var1.getBlock().getLocation(), System.currentTimeMillis()));
      int[] var8 = var10000;
      Iterator var9 = var1.getBlocks().iterator();

      while(var9.hasNext()) {
         Block var10 = (Block)var9.next();
         Jh.x(new Object[]{var10, var6});
         if (var8 == null) {
            break;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void m(BlockPistonRetractEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void h(BlockBreakEvent var1) {
      long var2 = a ^ 76101222461123L;
      long var4 = var2 ^ 15702236413773L;
      Jh.x(new Object[]{var1.getBlock(), var4});
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void Q(BlockExplodeEvent var1) {
      long var2 = a ^ 44515700285531L;
      long var4 = var2 ^ 108911158718933L;
      int[] var6 = Vb.m();
      Iterator var7 = var1.blockList().iterator();

      while(var7.hasNext()) {
         Block var8 = (Block)var7.next();
         Jh.x(new Object[]{var8, var4});
         if (var6 == null) {
            break;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void v(EntityExplodeEvent var1) {
      long var2 = a ^ 133824201509986L;
      long var4 = var2 ^ 55830742539756L;
      int[] var10000 = Vb.m();
      Iterator var7 = var1.blockList().iterator();
      int[] var6 = var10000;

      while(var7.hasNext()) {
         Block var8 = (Block)var7.next();
         Jh.x(new Object[]{var8, var4});
         if (var6 == null) {
            break;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void Q(BlockBurnEvent var1) {
      long var2 = a ^ 47880145125574L;
      long var4 = var2 ^ 106629867966792L;
      Jh.x(new Object[]{var1.getBlock(), var4});
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void K(BlockFadeEvent var1) {
      long var2 = a ^ 112173523484653L;
      long var4 = var2 ^ 49976967753315L;
      Jh.x(new Object[]{var1.getBlock(), var4});
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void C(LeavesDecayEvent var1) {
      long var2 = a ^ 4427140821759L;
      long var4 = var2 ^ 87374705599345L;
      Jh.x(new Object[]{var1.getBlock(), var4});
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public final void w(BlockPlaceEvent param1) {
      // $FF: Couldn't be decompiled
   }

   public void L(aw param1) {
      // $FF: Couldn't be decompiled
   }

   public void z(aE param1) {
      // $FF: Couldn't be decompiled
   }

   private final void S(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   private final void l(Object[] var1) {
      u.values().removeIf(Ea::q);
   }

   private static final void P() {
      wR var10000 = N;
      ++F;
   }

   private static final void t(Ea var0) {
      var0.l(new Object[0]);
   }

   private static final z0 u(Gg var0, Object var1) {
      return (z0)var0.Y(var1);
   }

   private static final boolean q(Gg var0, Object var1) {
      return (Boolean)var0.Y(var1);
   }

   public static final Ea Y(Object[] var0) {
      return O;
   }

   public static final BC B(Object[] var0) {
      return Z;
   }

   public static final int Y(Object[] var0) {
      return F;
   }

   public static final void V(Object[] var0) {
      Ea var1 = (Ea)var0[0];
      O = var1;
   }

   public Ea(MH var1) {
      this();
   }

   public static final ConcurrentHashMap D(Object[] var0) {
      return B;
   }

   public static final HashMap m(Object[] var0) {
      return I;
   }

   public static final EP t(Object[] var0) {
      return h;
   }

   public static final ConcurrentHashMap R(Object[] var0) {
      return u;
   }

   static {
      h(true);
      long var0 = a ^ 38491366502468L;
      Cipher var2;
      Cipher var10000 = var2 = Cipher.getInstance("DES/CBC/PKCS5Padding");
      SecretKeyFactory var10002 = SecretKeyFactory.getInstance("DES");
      byte[] var10003 = new byte[]{(byte)((int)(var0 >>> 56)), 0, 0, 0, 0, 0, 0, 0};

      for(int var3 = 1; var3 < 8; ++var3) {
         var10003[var3] = (byte)((int)(var0 << var3 * 8 >>> 56));
      }

      var10000.init(2, var10002.generateSecret(new DESKeySpec(var10003)), new IvParameterSpec(new byte[8]));
      String[] var9 = new String[3];
      int var7 = 0;
      String var6 = "\u0090qH\t\u0000üL\u0018R\u0014e\u0087½}bß L\u0015\u0007÷8\u009f%\u009c\u0001\u0087ïý\u000eæOk8ñ\\[íJ\u0013F\u008b\u0019{O/{ÁR8\u00146DøÇi¸&\u0015úÙ\"3\u009b2Þâl\fòã/TÊP26\u0015SØr°ó\u0090\u001d\u0019¡ò´Ó\u0082?J¹\"Å\u0093í·a4c\u008b\u0080b&";
      int var8 = "\u0090qH\t\u0000üL\u0018R\u0014e\u0087½}bß L\u0015\u0007÷8\u009f%\u009c\u0001\u0087ïý\u000eæOk8ñ\\[íJ\u0013F\u008b\u0019{O/{ÁR8\u00146DøÇi¸&\u0015úÙ\"3\u009b2Þâl\fòã/TÊP26\u0015SØr°ó\u0090\u001d\u0019¡ò´Ó\u0082?J¹\"Å\u0093í·a4c\u008b\u0080b&".length();
      char var5 = 16;
      int var4 = -1;

      while(true) {
         ++var4;
         byte[] var10 = var2.doFinal(var6.substring(var4, var4 + var5).getBytes("ISO-8859-1"));
         String var12 = a(var10).intern();
         boolean var10001 = true;
         var9[var7++] = var12;
         if ((var4 += var5) >= var8) {
            b = var9;
            c = new String[3];
            N = new wR((MH)null);
            I = new HashMap();
            B = new ConcurrentHashMap();
            u = new ConcurrentHashMap();
            Z = new BC();
            h = (EP)nM.m;
            return;
         }

         var5 = var6.charAt(var4);
      }
   }

   public static void h(boolean var0) {
      V = var0;
   }

   public static boolean R() {
      return V;
   }

   public static boolean B() {
      boolean var0 = R();

      try {
         return !var0;
      } catch (NullPointerException var1) {
         throw a(var1);
      }
   }

   private static NullPointerException a(NullPointerException var0) {
      return var0;
   }

   private static String a(byte[] var0) {
      int var1 = 0;
      int var2;
      char[] var3 = new char[var2 = var0.length];

      for(int var4 = 0; var4 < var2; ++var4) {
         int var5;
         if ((var5 = 255 & var0[var4]) < 192) {
            var3[var1++] = (char)var5;
         } else {
            char var6;
            byte var7;
            if (var5 < 224) {
               var6 = (char)((char)(var5 & 31) << 6);
               ++var4;
               var7 = var0[var4];
               var6 |= (char)(var7 & 63);
               var3[var1++] = var6;
            } else if (var4 < var2 - 2) {
               var6 = (char)((char)(var5 & 15) << 12);
               ++var4;
               var7 = var0[var4];
               var6 = (char)(var6 | (char)(var7 & 63) << 6);
               ++var4;
               var7 = var0[var4];
               var6 |= (char)(var7 & 63);
               var3[var1++] = var6;
            }
         }
      }

      return new String(var3, 0, var1);
   }

   private static String a(int var0, long var1) {
      int var5 = var0 ^ (int)(var1 & 32767L) ^ 24973;
      if (c[var5] == null) {
         Object[] var4;
         try {
            Long var3 = Thread.currentThread().getId();
            var4 = (Object[])d.get(var3);
            if (var4 == null) {
               var4 = new Object[]{Cipher.getInstance("DES/CBC/PKCS5Padding"), SecretKeyFactory.getInstance("DES"), new IvParameterSpec(new byte[8])};
               d.put(var3, var4);
            }
         } catch (Exception var10) {
            throw new RuntimeException("com/gmail/olexorus/themis/Ea", var10);
         }

         byte[] var6 = new byte[8];
         var6[0] = (byte)((int)(var1 >>> 56));

         for(int var7 = 1; var7 < 8; ++var7) {
            var6[var7] = (byte)((int)(var1 << var7 * 8 >>> 56));
         }

         DESKeySpec var11 = new DESKeySpec(var6);
         SecretKey var8 = ((SecretKeyFactory)var4[1]).generateSecret(var11);
         ((Cipher)var4[0]).init(2, var8, (IvParameterSpec)var4[2]);
         byte[] var9 = b[var5].getBytes("ISO-8859-1");
         c[var5] = a(((Cipher)var4[0]).doFinal(var9));
      }

      return c[var5];
   }

   private static Object a(Lookup var0, MutableCallSite var1, String var2, Object[] var3) {
      int var4 = (Integer)var3[0];
      long var5 = (Long)var3[1];
      String var7 = a(var4, var5);
      MethodHandle var8 = MethodHandles.constant(String.class, var7);
      var1.setTarget(MethodHandles.dropArguments(var8, 0, new Class[]{Integer.TYPE, Long.TYPE}));
      return var7;
   }

   private static CallSite a(Lookup var0, String var1, MethodType var2) {
      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(MethodHandles.explicitCastArguments(MethodHandles.insertArguments("a".asCollector(Object[].class, var2.parameterCount()), 0, new Object[]{var0, var3, var1}), var2));
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("com/gmail/olexorus/themis/Ea" + " : " + var1 + " : " + var2.toString(), var5);
      }
   }
}
