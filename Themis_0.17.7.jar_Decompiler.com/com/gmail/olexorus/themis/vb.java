package com.gmail.olexorus.themis;

import com.gmail.olexorus.themis.api.CheckType;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffectType;

public final class Vb {
   private final Player M;
   private String V;
   private boolean K;
   private final long N;
   private Integer I;
   private short s;
   private Long W;
   private long q;
   private boolean R;
   private final EnumMap<CheckType, Boolean> o;
   private long x;
   private double h;
   private boolean c;
   private boolean z;
   private boolean Q;
   private boolean D;
   private boolean Y;
   public Location X;
   private HashMap<PotionEffectType, z0<Integer, Long>> T;
   private long w;
   private boolean j;
   private ArrayDeque<z0<Double, Long>> A;
   private z0<? extends Location, Integer> E;
   private z0<? extends Location, Integer> u;
   private Location P;
   private Location U;
   private boolean p;
   private long i;
   private long B;
   private static int[] n;
   private static final long a = kt.a(1395699553281849863L, 3093472858976970990L, MethodHandles.lookup().lookupClass()).a(83116208551929L);
   private static final String[] b;
   private static final String[] d;
   private static final Map e = new HashMap(13);

   public Vb(Player var1, short var2, short var3, int var4) {
      long var5 = ((long)var2 << 48 | (long)var3 << 48 >>> 16 | (long)var4 << 32 >>> 32) ^ a;
      super();
      this.M = var1;
      this.N = System.currentTimeMillis();
      this.R = true;
      this.o = new EnumMap(CheckType.class);
      this.c = this.M.isOnGround();
      this.z = this.M.isOnGround();
      this.T = new HashMap();
      m();
      this.A = new ArrayDeque();

      try {
         this.P = this.M.getLocation();
         if (vh.i() == null) {
            O(new int[4]);
         }

      } catch (RuntimeException var8) {
         throw a(var8);
      }
   }

   public final String r(Object[] var1) {
      return this.V;
   }

   public final void d(Object[] var1) {
      String var2 = (String)var1[0];
      this.V = var2;
   }

   public final boolean O(Object[] var1) {
      return this.K;
   }

   public final void k(Object[] var1) {
      boolean var2 = (Boolean)var1[0];
      this.K = var2;
   }

   public final long w(Object[] var1) {
      return this.N;
   }

   public final Integer l(Object[] var1) {
      return this.I;
   }

   public final long D(Object[] var1) {
      return this.q;
   }

   public final boolean L(Object[] var1) {
      return this.R;
   }

   public final void I(Object[] var1) {
      boolean var2 = (Boolean)var1[0];
      this.R = var2;
   }

   public final double R(Object[] var1) {
      return this.h;
   }

   public final boolean v(Object[] var1) {
      return this.c;
   }

   public final boolean i(Object[] var1) {
      return this.z;
   }

   public final boolean Y(Object[] var1) {
      return this.Q;
   }

   public final boolean c(Object[] var1) {
      return this.D;
   }

   public final boolean M(Object[] var1) {
      return this.Y;
   }

   public final Location W(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final void h(Object[] var1) {
      Location var2 = (Location)var1[0];
      this.X = var2;
   }

   public final boolean k(Object[] var1) {
      return this.j;
   }

   public final Location b(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final boolean d(Object[] var1) {
      return this.p;
   }

   public final void c(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final void t(Object[] var1) {
      this.V = null;
      this.I = null;
   }

   public final void r(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   private final void v(Object[] var1) {
      this.s = (short)(new Random()).nextInt();
      ZQ var2 = new ZQ(this.s);
      oS.J().v().Q(this.M, (lm)var2);
      this.W = System.currentTimeMillis();
   }

   public final void F(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final void W(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final void M(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final void o(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final void U(Object[] var1) {
      PlayerTeleportEvent var2 = (PlayerTeleportEvent)var1[0];
      this.z = true;
      this.h(new Object[]{var2.getTo()});
      this.E = new z0(var2.getTo(), 0);
      this.u = null;
   }

   public final void G(Object[] var1) {
      PlayerRespawnEvent var2 = (PlayerRespawnEvent)var1[0];
      this.z = true;
      this.E = new z0(var2.getRespawnLocation(), 0);
      this.u = null;
   }

   public final void z(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final void E(Object[] var1) {
      PlayerRiptideEvent var2 = (PlayerRiptideEvent)var1[0];
      this.j = true;
   }

   public final void L(Object[] var1) {
      PlayerToggleFlightEvent var2 = (PlayerToggleFlightEvent)var1[0];
      long var3 = (Long)var1[1];
      long var10000 = a ^ var3;

      try {
         if (var2.isFlying()) {
            this.w = System.currentTimeMillis();
         }

      } catch (RuntimeException var5) {
         throw a(var5);
      }
   }

   public final boolean V(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final void T(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final boolean r(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final double D(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public final boolean T(Object[] var1) {
      long var2 = (Long)var1[0];
      long var10000 = a ^ var2;
      int[] var4 = m();

      int var6;
      label32: {
         try {
            long var7;
            var6 = (var7 = WB.u(new Object[]{this.w}) - 1000L) == 0L ? 0 : (var7 < 0L ? -1 : 1);
            if (var4 == null) {
               return (boolean)var6;
            }

            if (var6 <= 0) {
               break label32;
            }
         } catch (RuntimeException var5) {
            throw a(var5);
         }

         var6 = 0;
         return (boolean)var6;
      }

      var6 = 1;
      return (boolean)var6;
   }

   public final Integer x(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   private static final void K(Vb var0) {
      var0.v(new Object[0]);
   }

   public static void O(int[] var0) {
      n = var0;
   }

   public static int[] m() {
      return n;
   }

   static {
      int[] var10000 = new int[5];
      long var0 = a ^ 100633423104163L;
      O(var10000);
      Cipher var2;
      Cipher var11 = var2 = Cipher.getInstance("DES/CBC/PKCS5Padding");
      SecretKeyFactory var10002 = SecretKeyFactory.getInstance("DES");
      byte[] var10003 = new byte[]{(byte)((int)(var0 >>> 56)), 0, 0, 0, 0, 0, 0, 0};

      for(int var3 = 1; var3 < 8; ++var3) {
         var10003[var3] = (byte)((int)(var0 << var3 * 8 >>> 56));
      }

      var11.init(2, var10002.generateSecret(new DESKeySpec(var10003)), new IvParameterSpec(new byte[8]));
      String[] var9 = new String[2];
      int var7 = 0;
      String var6 = "Ä\u0018¥ö¶Û\u0091\u001d³\u0088\u0092~É*a×»\n\u0098Ú\"lõ\tG\u0007Àl«¶Ë$\u0003#~\u0080Bå\u001f> À\u0007*\u0003\tô1åìB6ââ\u0012Ø \u0094\u008cR\u0006ÿÎµ·\u000e\u0081@5á2|Õ\u009aìQQ¹6´¤ø_\u0089)\u001fçÅ\u0003";
      int var8 = "Ä\u0018¥ö¶Û\u0091\u001d³\u0088\u0092~É*a×»\n\u0098Ú\"lõ\tG\u0007Àl«¶Ë$\u0003#~\u0080Bå\u001f> À\u0007*\u0003\tô1åìB6ââ\u0012Ø \u0094\u008cR\u0006ÿÎµ·\u000e\u0081@5á2|Õ\u009aìQQ¹6´¤ø_\u0089)\u001fçÅ\u0003".length();
      char var5 = '8';
      int var4 = -1;

      while(true) {
         ++var4;
         byte[] var10 = var2.doFinal(var6.substring(var4, var4 + var5).getBytes("ISO-8859-1"));
         String var13 = a(var10).intern();
         boolean var10001 = true;
         var9[var7++] = var13;
         if ((var4 += var5) >= var8) {
            b = var9;
            d = new String[2];
            return;
         }

         var5 = var6.charAt(var4);
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
      int var5 = var0 ^ (int)(var1 & 32767L) ^ 31816;
      if (d[var5] == null) {
         Object[] var4;
         try {
            Long var3 = Thread.currentThread().getId();
            var4 = (Object[])e.get(var3);
            if (var4 == null) {
               var4 = new Object[]{Cipher.getInstance("DES/CBC/PKCS5Padding"), SecretKeyFactory.getInstance("DES"), new IvParameterSpec(new byte[8])};
               e.put(var3, var4);
            }
         } catch (Exception var10) {
            throw new RuntimeException("com/gmail/olexorus/themis/Vb", var10);
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
         d[var5] = a(((Cipher)var4[0]).doFinal(var9));
      }

      return d[var5];
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
         throw new RuntimeException("com/gmail/olexorus/themis/Vb" + " : " + var1 + " : " + var2.toString(), var5);
      }
   }
}
