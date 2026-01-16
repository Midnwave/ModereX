package com.gmail.olexorus.themis;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public final class JH implements Listener, na {
   public static final Cb H;
   private final HashMap<UUID, ArrayList<vh>> Q;
   private static final KeySetView<vh, y> J;
   private static int p;
   private static final long a = kt.a(8597584609071307662L, -879972927705756369L, MethodHandles.lookup().lookupClass()).a(209071156844733L);
   private static final String[] b;
   private static final String[] c;
   private static final Map d = new HashMap(13);

   public JH() {
      // $FF: Couldn't be decompiled
   }

   private final void B(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public final void l(PlayerJoinEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler
   public final void h(PlayerMoveEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler
   public final void s(PlayerTeleportEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler
   public final void c(EntityDamageByEntityEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler
   public final void T(VehicleEnterEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler
   public final void L(VehicleExitEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler
   public final void a(PlayerRespawnEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void E(PlayerVelocityEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler
   public final void Z(EntitySpawnEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler
   public final void S(VehicleEnterEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler
   public final void T(VehicleExitEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public final void p(PlayerToggleFlightEvent param1) {
      // $FF: Couldn't be decompiled
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public final void Y(ProjectileHitEvent param1) {
      // $FF: Couldn't be decompiled
   }

   public void L(aw param1) {
      // $FF: Couldn't be decompiled
   }

   public void z(aE param1) {
      // $FF: Couldn't be decompiled
   }

   private static final void X(JH var0) {
      long var1 = a ^ 105365097184987L;
      long var3 = var1 ^ 82840259084134L;
      var0.B(new Object[]{var3});
   }

   public static final KeySetView L(Object[] var0) {
      return J;
   }

   static {
      long var0 = a ^ 29987297903908L;
      D(0);
      Cipher var2;
      Cipher var10000 = var2 = Cipher.getInstance("DES/CBC/PKCS5Padding");
      SecretKeyFactory var10002 = SecretKeyFactory.getInstance("DES");
      byte[] var10003 = new byte[]{(byte)((int)(var0 >>> 56)), 0, 0, 0, 0, 0, 0, 0};

      for(int var3 = 1; var3 < 8; ++var3) {
         var10003[var3] = (byte)((int)(var0 << var3 * 8 >>> 56));
      }

      var10000.init(2, var10002.generateSecret(new DESKeySpec(var10003)), new IvParameterSpec(new byte[8]));
      String[] var9 = new String[10];
      int var7 = 0;
      String var6 = "\r\u001b¨\u0011z'éñü\u0007¶±\u0002Àå< A\u0083)û\u001b\u00ad(ü\u0098\\!\u0015â\u0001aØeÈ¾\u009eÜ3'B7ÖÀÓ/\u0092\u000b\u0010 àS\u0006¯·âZz{\u0084\u0015Á\u000b\u000fà\u001fÈ\u0085øÑ°¬XhÌ)@X£tiâ n×m\u0007&²\u0094\u0094çËnë©`ÂÜ³ØÁø\u0080\\û\u001aG\u0084î\u0016öã\u0099H\u0018ßÍ ãìIÔ¦7\u009f~Y\u0000\f¬°CO\t>èAe# ¤à\u0099&y\u0094vÄ\u008f¡\u009a \u0084óðè\u0098¯\u0087×¤b\"\u009dÇ\u0006M\u0012ú\u0016ó\u000e\u0018á,\u0019ÿ\u000f\u0015ùÂ\u0089³=ÇIá¨^bkï\u0004\f!~²P¼\u0012\u0092!Á\u0004äë·HîD\u0084Ø{\u0095®>æ·×J\u009f=\"ÆG\u0095@9!\u0001\u000f²þî\u0006]\u008e\u00ad\u001d\u000fîæ8\u009fªËÀ¼Ud±\u00871 ìÜ\u0001ýV¥\u0019øMA\b¤Ûo]\u0010^©Se\u000eòXÂ";
      int var8 = "\r\u001b¨\u0011z'éñü\u0007¶±\u0002Àå< A\u0083)û\u001b\u00ad(ü\u0098\\!\u0015â\u0001aØeÈ¾\u009eÜ3'B7ÖÀÓ/\u0092\u000b\u0010 àS\u0006¯·âZz{\u0084\u0015Á\u000b\u000fà\u001fÈ\u0085øÑ°¬XhÌ)@X£tiâ n×m\u0007&²\u0094\u0094çËnë©`ÂÜ³ØÁø\u0080\\û\u001aG\u0084î\u0016öã\u0099H\u0018ßÍ ãìIÔ¦7\u009f~Y\u0000\f¬°CO\t>èAe# ¤à\u0099&y\u0094vÄ\u008f¡\u009a \u0084óðè\u0098¯\u0087×¤b\"\u009dÇ\u0006M\u0012ú\u0016ó\u000e\u0018á,\u0019ÿ\u000f\u0015ùÂ\u0089³=ÇIá¨^bkï\u0004\f!~²P¼\u0012\u0092!Á\u0004äë·HîD\u0084Ø{\u0095®>æ·×J\u009f=\"ÆG\u0095@9!\u0001\u000f²þî\u0006]\u008e\u00ad\u001d\u000fîæ8\u009fªËÀ¼Ud±\u00871 ìÜ\u0001ýV¥\u0019øMA\b¤Ûo]\u0010^©Se\u000eòXÂ".length();
      char var5 = 16;
      int var4 = -1;

      label27:
      while(true) {
         ++var4;
         String var11 = var6.substring(var4, var4 + var5);
         byte var10001 = -1;

         while(true) {
            byte[] var10 = var2.doFinal(var11.getBytes("ISO-8859-1"));
            String var15 = a(var10).intern();
            switch(var10001) {
            case 0:
               var9[var7++] = var15;
               if ((var4 += var5) >= var8) {
                  b = var9;
                  c = new String[10];
                  H = new Cb((MH)null);
                  J = (new ConcurrentHashMap()).keySet(y.u);
                  return;
               }

               var5 = var6.charAt(var4);
               break;
            default:
               var9[var7++] = var15;
               if ((var4 += var5) < var8) {
                  var5 = var6.charAt(var4);
                  continue label27;
               }

               var6 = "yÉ¬;_\fíkgZ\u00045\u0087r¾¸§ä\u0087\t\u009aºÚ7×\u0005ñ\u008d,u\u0085x\u0018Á0OÇ\b;Kþ\u0090²~¿\u0003\u0080rèæ¼3\u008eWQôt";
               var8 = "yÉ¬;_\fíkgZ\u00045\u0087r¾¸§ä\u0087\t\u009aºÚ7×\u0005ñ\u008d,u\u0085x\u0018Á0OÇ\b;Kþ\u0090²~¿\u0003\u0080rèæ¼3\u008eWQôt".length();
               var5 = ' ';
               var4 = -1;
            }

            ++var4;
            var11 = var6.substring(var4, var4 + var5);
            var10001 = 0;
         }
      }
   }

   public static void D(int var0) {
      p = var0;
   }

   public static int w() {
      return p;
   }

   public static int O() {
      int var0 = w();

      try {
         return var0 == 0 ? 20 : 0;
      } catch (RuntimeException var1) {
         throw a(var1);
      }
   }

   private static RuntimeException a(RuntimeException var0) {
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
      int var5 = var0 ^ (int)(var1 & 32767L) ^ 26814;
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
            throw new RuntimeException("com/gmail/olexorus/themis/JH", var10);
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
         throw new RuntimeException("com/gmail/olexorus/themis/JH" + " : " + var1 + " : " + var2.toString(), var5);
      }
   }
}
