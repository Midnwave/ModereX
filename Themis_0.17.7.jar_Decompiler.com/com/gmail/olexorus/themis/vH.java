package com.gmail.olexorus.themis;

import com.gmail.olexorus.themis.api.CheckType;
import com.gmail.olexorus.themis.api.ViolationEvent;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public abstract class vh {
   private final CheckType Il;
   private final Player o;
   private final Vb If;
   private final AtomicInteger s;
   private boolean zJ;
   private static String zb;
   private static String[] zB;
   private static boolean Ij;
   private static String[] IC;
   private static vh[] zr;
   private static vh[] z_;
   private static String[] zZ;
   private static vh[] z1;
   private static String[] zQ;
   private static String I5;
   private static String IQ;
   private static String T;
   private static int Ic;
   private static String IV;
   private static int[] Iu;
   private static int zI;
   private static String[] R;
   private static String zl;
   private static String U;
   private static String[] w;
   private static boolean zc;
   private static int[] zx;
   private static String p;
   private static int zf;
   private static boolean zk;
   private static int[] zH;
   private static boolean Iy;
   private static String IA;
   private static String x;
   private static int[] Ih;
   private static String g;
   private static boolean el;
   private static int[] zN;
   private static int k;
   private static String[] Ib;
   private static String[] zu;
   private static int zF;
   private static vh[] n;
   private static String[] I;
   private static vh[] eB;
   private static String D;
   private static String[] eH;
   private static int[] zg;
   private static String[] I1;
   private static int q;
   private static vh[] IE;
   private static String F;
   private static int IM;
   private static int[] a;
   private static vh[] C;
   private static String[] t;
   private static int[] Ii;
   private static boolean z0;
   private static String[] IZ;
   private static int[] Ie;
   private static boolean er;
   private static int G;
   private static String eD;
   private static String IH;
   private static int[] Ia;
   private static boolean u;
   private static vh[] ej;
   private static String[] B;
   private static vh[] b;
   private static int[] zG;
   private static String[] L;
   private static String[] Id;
   private static String z5;
   private static String[] h;
   private static String[] Iq;
   private static vh[] IG;
   private static int IF;
   private static int[] z8;
   private static int[] z4;
   private static String zi;
   private static String[] za;
   private static int[] zn;
   private static boolean z7;
   private static int IW;
   private static vh[] H;
   private static String M;
   private static String[] j;
   private static int[] I7;
   private static vh[] m;
   private static vh[] eG;
   private static int[] c;
   private static String A;
   private static int[] eR;
   private static int r;
   private static boolean zD;
   private static int[] IO;
   private static String zs;
   private static String IR;
   private static int V;
   private static boolean ze;
   private static String IB;
   private static int[] K;
   private static int IT;
   private static int I_;
   private static vh[] O;
   private static String[] zT;
   private static int y;
   private static vh[] Io;
   private static int Ix;
   private static String[] zO;
   private static String[] z9;
   private static boolean IP;
   private static int It;
   private static String zC;
   private static String[] zY;
   private static String Im;
   private static String[] Z;
   private static boolean IJ;
   private static int z3;
   private static int i;
   private static String ID;
   private static int[] ek;
   private static int I4;
   private static int zj;
   private static boolean Iz;
   private static String[] f;
   private static boolean IS;
   private static int[] N;
   private static boolean zP;
   private static int[] e2;
   private static String[] zW;
   private static vh[] IX;
   private static vh[] z2;
   private static String eq;
   private static int zA;
   private static vh[] eX;
   private static vh[] I6;
   private static String E;
   private static String Ir;
   private static String X;
   private static String[] Iw;
   private static boolean d;
   private static int[] zL;
   private static String Ip;
   private static vh[] IU;
   private static String zm;
   private static String[] zE;
   private static int zM;
   private static String zt;
   private static vh[] eT;
   private static String Q;
   private static int[] In;
   private static int zp;
   private static String eo;
   private static int zU;
   private static int[] v;
   private static String[] e;
   private static String[] Iv;
   private static boolean zh;
   private static int[] ev;
   private static String IK;
   private static String[] I0;
   private static int I3;
   private static String[] J;
   private static boolean IN;
   private static String[] eM;
   private static boolean z6;
   private static String[] zV;
   private static int[] W;
   private static String zv;
   private static String zo;
   private static vh[] S;
   private static String[] II;
   private static boolean zS;
   private static boolean I8;
   private static String[] zK;
   private static String l;
   private static int IL;
   private static String[] Ig;
   private static String[] zz;
   private static boolean e9;
   private static String[] zR;
   private static int I2;
   private static String[] zX;
   private static String Ik;
   private static int[] zy;
   private static boolean zq;
   private static vh[] I9;
   private static String[] z;
   private static int[] zd;
   private static String[] IY;
   private static String zw;
   private static boolean Is;
   private static final long ab = kt.a(-1526315558893268793L, 6869607831553555144L, MethodHandles.lookup().lookupClass()).a(239355719403910L);
   private static final String cb;

   public vh(CheckType var1, Player var2, Vb var3) {
      this.Il = var1;
      this.o = var2;
      this.If = var3;
      this.s = new AtomicInteger();
   }

   public final CheckType q(Object[] var1) {
      return this.Il;
   }

   protected final Player H(Object[] var1) {
      return this.o;
   }

   protected final Vb t(Object[] var1) {
      return this.If;
   }

   public final void n(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public static void w(Object[] var0) {
      vh var8 = (vh)var0[0];
      double var4 = (Double)var0[1];
      int var1 = (Boolean)var0[2];
      long var6 = (Long)var0[3];
      int var2 = (Integer)var0[4];
      Object var3 = (Object)var0[5];
      var6 ^= ab;
      long var9 = var6 ^ 51322093839907L;
      int var11 = JH.w();

      try {
         if (var3 != null) {
            throw new UnsupportedOperationException(cb);
         }
      } catch (UnsupportedOperationException var12) {
         throw a(var12);
      }

      label28: {
         int var10000;
         label27: {
            try {
               var10000 = var2 & 2;
               if (var11 != 0) {
                  break label27;
               }

               if (var10000 == 0) {
                  break label28;
               }
            } catch (UnsupportedOperationException var13) {
               throw a(var13);
            }

            var10000 = 1;
         }

         var1 = var10000;
      }

      var8.n(new Object[]{var4, var9, Boolean.valueOf((boolean)var1)});
   }

   public final void o(Object[] var1) {
      long var2 = (Long)var1[0];
      long var10000 = ab ^ var2;
      int var5 = this.s.getAndSet(0);
      int var7 = JH.O();
      Bukkit.getPluginManager().callEvent((Event)(new ViolationEvent(this.o, this.Il, (double)var5 * 0.001D, this.zJ)));
      int var4 = var7;

      try {
         this.zJ = false;
         if (i() == null) {
            ++var4;
            JH.D(var4);
         }

      } catch (UnsupportedOperationException var6) {
         throw a(var6);
      }
   }

   public final boolean m(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public void S(Object[] var1) {
      PlayerMoveEvent var4 = (PlayerMoveEvent)var1[0];
      long var2 = (Long)var1[1];
   }

   public void p(Object[] var1) {
      long var3 = (Long)var1[0];
      PlayerTeleportEvent var2 = (PlayerTeleportEvent)var1[1];
   }

   public void O(Object[] var1) {
      long var3 = (Long)var1[0];
      EntityDamageByEntityEvent var2 = (EntityDamageByEntityEvent)var1[1];
   }

   public void P(Object[] var1) {
      long var3 = (Long)var1[0];
      VehicleEnterEvent var2 = (VehicleEnterEvent)var1[1];
   }

   public void g(Object[] var1) {
      VehicleExitEvent var2 = (VehicleExitEvent)var1[0];
   }

   public void F(Object[] var1) {
      PlayerRespawnEvent var2 = (PlayerRespawnEvent)var1[0];
   }

   public void D(Object[] var1) {
      PlayerVelocityEvent var2 = (PlayerVelocityEvent)var1[0];
   }

   public void d(Object[] var1) {
      EntitySpawnEvent var2 = (EntitySpawnEvent)var1[0];
   }

   public void b(Object[] var1) {
      VehicleEnterEvent var2 = (VehicleEnterEvent)var1[0];
   }

   public void f(Object[] var1) {
      VehicleExitEvent var2 = (VehicleExitEvent)var1[0];
   }

   public void y(Object[] var1) {
      PlayerToggleFlightEvent var2 = (PlayerToggleFlightEvent)var1[0];
      long var3 = (Long)var1[1];
   }

   public void C(Object[] var1) {
   }

   public void T(Object[] var1) {
      yx var2 = (yx)var1[0];
      long var3 = (Long)var1[1];
   }

   public void x(Object[] var1) {
      long var2 = (Long)var1[0];
      sb var4 = (sb)var1[1];
   }

   public void L(Object[] var1) {
      long var2 = (Long)var1[0];
      yu var4 = (yu)var1[1];
   }

   public static void j(String var0) {
      zb = var0;
   }

   public static String i() {
      return zb;
   }

   public static void f(String[] var0) {
      zB = var0;
   }

   public static String[] f() {
      return zB;
   }

   public static void b(boolean var0) {
      Ij = var0;
   }

   public static boolean G() {
      return Ij;
   }

   public static boolean F() {
      boolean var0 = G();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void N(String[] var0) {
      IC = var0;
   }

   public static String[] V() {
      return IC;
   }

   public static void O(vh[] var0) {
      zr = var0;
   }

   public static vh[] M() {
      return zr;
   }

   public static void s(vh[] var0) {
      z_ = var0;
   }

   public static vh[] p() {
      return z_;
   }

   public static void h(String[] var0) {
      zZ = var0;
   }

   public static String[] v() {
      return zZ;
   }

   public static void t(vh[] var0) {
      z1 = var0;
   }

   public static vh[] J() {
      return z1;
   }

   public static void m(String[] var0) {
      zQ = var0;
   }

   public static String[] w() {
      return zQ;
   }

   public static void Z(String var0) {
      I5 = var0;
   }

   public static String L() {
      return I5;
   }

   public static void p(String var0) {
      IQ = var0;
   }

   public static String U() {
      return IQ;
   }

   public static void b(String var0) {
      T = var0;
   }

   public static String r() {
      return T;
   }

   public static void F(int var0) {
      Ic = var0;
   }

   public static int E() {
      return Ic;
   }

   public static int H() {
      int var0 = E();

      try {
         return var0 == 0 ? 43 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void y(String var0) {
      IV = var0;
   }

   public static String S() {
      return IV;
   }

   public static void o(int[] var0) {
      Iu = var0;
   }

   public static int[] Y() {
      return Iu;
   }

   public static void V(int var0) {
      zI = var0;
   }

   public static int g() {
      return zI;
   }

   public static int J() {
      int var0 = g();

      try {
         return var0 == 0 ? 63 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void a(String[] var0) {
      R = var0;
   }

   public static String[] T() {
      return R;
   }

   public static void F(String var0) {
      zl = var0;
   }

   public static String o() {
      return zl;
   }

   public static void s(String var0) {
      U = var0;
   }

   public static String A() {
      return U;
   }

   public static void T(String[] var0) {
      w = var0;
   }

   public static String[] c() {
      return w;
   }

   public static void T(boolean var0) {
      zc = var0;
   }

   public static boolean r() {
      return zc;
   }

   public static boolean k() {
      boolean var0 = r();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void x(int[] var0) {
      zx = var0;
   }

   public static int[] Z() {
      return zx;
   }

   public static void R(String var0) {
      p = var0;
   }

   public static String k() {
      return p;
   }

   public static void a(int var0) {
      zf = var0;
   }

   public static int I() {
      return zf;
   }

   public static int X() {
      int var0 = I();

      try {
         return var0 == 0 ? 36 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void C(boolean var0) {
      zk = var0;
   }

   public static boolean g() {
      return zk;
   }

   public static boolean y() {
      boolean var0 = g();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void H(int[] var0) {
      zH = var0;
   }

   public static int[] y() {
      return zH;
   }

   public static void c(boolean var0) {
      Iy = var0;
   }

   public static boolean W() {
      return Iy;
   }

   public static boolean K() {
      boolean var0 = W();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void f(String var0) {
      IA = var0;
   }

   public static String M() {
      return IA;
   }

   public static void M(String var0) {
      x = var0;
   }

   public static String f() {
      return x;
   }

   public static void e(int[] var0) {
      Ih = var0;
   }

   public static int[] e() {
      return Ih;
   }

   public static void v(String var0) {
      g = var0;
   }

   public static String Z() {
      return g;
   }

   public static void x(boolean var0) {
      el = var0;
   }

   public static boolean I() {
      return el;
   }

   public static boolean j() {
      boolean var0 = I();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void T(int[] var0) {
      zN = var0;
   }

   public static int[] X() {
      return zN;
   }

   public static void v(int var0) {
      k = var0;
   }

   public static int Zy() {
      return k;
   }

   public static int b() {
      int var0 = Zy();

      try {
         return var0 == 0 ? 17 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void g(String[] var0) {
      Ib = var0;
   }

   public static String[] A() {
      return Ib;
   }

   public static void k(String[] var0) {
      zu = var0;
   }

   public static String[] W() {
      return zu;
   }

   public static void J(int var0) {
      zF = var0;
   }

   public static int k() {
      return zF;
   }

   public static int s() {
      int var0 = k();

      try {
         return var0 == 0 ? 90 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void l(vh[] var0) {
      n = var0;
   }

   public static vh[] d() {
      return n;
   }

   public static void l(String[] var0) {
      I = var0;
   }

   public static String[] b() {
      return I;
   }

   public static void U(vh[] var0) {
      eB = var0;
   }

   public static vh[] a() {
      return eB;
   }

   public static void w(String var0) {
      D = var0;
   }

   public static String w() {
      return D;
   }

   public static void y(String[] var0) {
      eH = var0;
   }

   public static String[] B() {
      return eH;
   }

   public static void E(int[] var0) {
      zg = var0;
   }

   public static int[] O() {
      return zg;
   }

   public static void n(String[] var0) {
      I1 = var0;
   }

   public static String[] j() {
      return I1;
   }

   public static void E(int var0) {
      q = var0;
   }

   public static int h() {
      return q;
   }

   public static int Q() {
      int var0 = h();

      try {
         return var0 == 0 ? 45 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void Y(vh[] var0) {
      IE = var0;
   }

   public static vh[] c() {
      return IE;
   }

   public static void Q(String var0) {
      F = var0;
   }

   public static String z() {
      return F;
   }

   public static void Q(int var0) {
      IM = var0;
   }

   public static int Z7() {
      return IM;
   }

   public static int F() {
      int var0 = Z7();

      try {
         return var0 == 0 ? 35 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void S(int[] var0) {
      a = var0;
   }

   public static int[] P() {
      return a;
   }

   public static void D(vh[] var0) {
      C = var0;
   }

   public static vh[] n() {
      return C;
   }

   public static void c(String[] var0) {
      t = var0;
   }

   public static String[] n() {
      return t;
   }

   public static void g(int[] var0) {
      Ii = var0;
   }

   public static int[] G() {
      return Ii;
   }

   public static void r(boolean var0) {
      z0 = var0;
   }

   public static boolean T() {
      return z0;
   }

   public static boolean M() {
      boolean var0 = T();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void A(String[] var0) {
      IZ = var0;
   }

   public static String[] X() {
      return IZ;
   }

   public static void U(int[] var0) {
      Ie = var0;
   }

   public static int[] b() {
      return Ie;
   }

   public static void y(boolean var0) {
      er = var0;
   }

   public static boolean J() {
      return er;
   }

   public static boolean q() {
      boolean var0 = J();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void Z(int var0) {
      G = var0;
   }

   public static int j() {
      return G;
   }

   public static int m() {
      int var0 = j();

      try {
         return var0 == 0 ? 102 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void c(String var0) {
      eD = var0;
   }

   public static String n() {
      return eD;
   }

   public static void q(String var0) {
      IH = var0;
   }

   public static String c() {
      return IH;
   }

   public static void Q(int[] var0) {
      Ia = var0;
   }

   public static int[] l() {
      return Ia;
   }

   public static void e(boolean var0) {
      u = var0;
   }

   public static boolean N() {
      return u;
   }

   public static boolean Q() {
      boolean var0 = N();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void N(vh[] var0) {
      ej = var0;
   }

   public static vh[] P() {
      return ej;
   }

   public static void i(String[] var0) {
      B = var0;
   }

   public static String[] q() {
      return B;
   }

   public static void a(vh[] var0) {
      b = var0;
   }

   public static vh[] f() {
      return b;
   }

   public static void m(int[] var0) {
      zG = var0;
   }

   public static int[] K() {
      return zG;
   }

   public static void V(String[] var0) {
      L = var0;
   }

   public static String[] e() {
      return L;
   }

   public static void C(String[] var0) {
      Id = var0;
   }

   public static String[] k() {
      return Id;
   }

   public static void K(String var0) {
      z5 = var0;
   }

   public static String b() {
      return z5;
   }

   public static void e(String[] var0) {
      h = var0;
   }

   public static String[] s() {
      return h;
   }

   public static void G(String[] var0) {
      Iq = var0;
   }

   public static String[] d() {
      return Iq;
   }

   public static void A(vh[] var0) {
      IG = var0;
   }

   public static vh[] w() {
      return IG;
   }

   public static void W(int var0) {
      IF = var0;
   }

   public static int a() {
      return IF;
   }

   public static int d() {
      int var0 = a();

      try {
         return var0 == 0 ? 61 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void X(int[] var0) {
      z8 = var0;
   }

   public static int[] a() {
      return z8;
   }

   public static void y(int[] var0) {
      z4 = var0;
   }

   public static int[] D() {
      return z4;
   }

   public static void S(String var0) {
      zi = var0;
   }

   public static String V() {
      return zi;
   }

   public static void J(String[] var0) {
      za = var0;
   }

   public static String[] S() {
      return za;
   }

   public static void j(int[] var0) {
      zn = var0;
   }

   public static int[] T() {
      return zn;
   }

   public static void I(boolean var0) {
      z7 = var0;
   }

   public static boolean d() {
      return z7;
   }

   public static boolean t() {
      boolean var0 = d();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void u(int var0) {
      IW = var0;
   }

   public static int W() {
      return IW;
   }

   public static int v() {
      int var0 = W();

      try {
         return var0 == 0 ? 47 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void e(vh[] var0) {
      H = var0;
   }

   public static vh[] r() {
      return H;
   }

   public static void B(String var0) {
      M = var0;
   }

   public static String J() {
      return M;
   }

   public static void z(String[] var0) {
      j = var0;
   }

   public static String[] y() {
      return j;
   }

   public static void w(int[] var0) {
      I7 = var0;
   }

   public static int[] z() {
      return I7;
   }

   public static void K(vh[] var0) {
      m = var0;
   }

   public static vh[] g() {
      return m;
   }

   public static void T(vh[] var0) {
      eG = var0;
   }

   public static vh[] L() {
      return eG;
   }

   public static void n(int[] var0) {
      c = var0;
   }

   public static int[] c() {
      return c;
   }

   public static void J(String var0) {
      A = var0;
   }

   public static String O() {
      return A;
   }

   public static void D(int[] var0) {
      eR = var0;
   }

   public static int[] q() {
      return eR;
   }

   public static void d(int var0) {
      r = var0;
   }

   public static int C() {
      return r;
   }

   public static int y() {
      int var0 = C();

      try {
         return var0 == 0 ? 58 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void N(boolean var0) {
      zD = var0;
   }

   public static boolean C() {
      return zD;
   }

   public static boolean h() {
      boolean var0 = C();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void M(int[] var0) {
      IO = var0;
   }

   public static int[] u() {
      return IO;
   }

   public static void h(String var0) {
      zs = var0;
   }

   public static String Q() {
      return zs;
   }

   public static void X(String var0) {
      IR = var0;
   }

   public static String R() {
      return IR;
   }

   public static void r(int var0) {
      V = var0;
   }

   public static int x() {
      return V;
   }

   public static int z() {
      int var0 = x();

      try {
         return var0 == 0 ? 42 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void U(boolean var0) {
      ze = var0;
   }

   public static boolean O() {
      return ze;
   }

   public static boolean L() {
      boolean var0 = O();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void m(String var0) {
      IB = var0;
   }

   public static String X() {
      return IB;
   }

   public static void J(int[] var0) {
      K = var0;
   }

   public static int[] m() {
      return K;
   }

   public static void z(int var0) {
      IT = var0;
   }

   public static int L() {
      return IT;
   }

   public static int n() {
      int var0 = L();

      try {
         return var0 == 0 ? 87 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void g(int var0) {
      I_ = var0;
   }

   public static int w() {
      return I_;
   }

   public static int u() {
      int var0 = w();

      try {
         return var0 == 0 ? 96 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void m(vh[] var0) {
      O = var0;
   }

   public static vh[] H() {
      return O;
   }

   public static void W(String[] var0) {
      zT = var0;
   }

   public static String[] P() {
      return zT;
   }

   public static void G(int var0) {
      y = var0;
   }

   public static int B() {
      return y;
   }

   public static int P() {
      int var0 = B();

      try {
         return var0 == 0 ? 5 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void L(vh[] var0) {
      Io = var0;
   }

   public static vh[] T() {
      return Io;
   }

   public static void w(int var0) {
      Ix = var0;
   }

   public static int N() {
      return Ix;
   }

   public static int p() {
      int var0 = N();

      try {
         return var0 == 0 ? 40 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void H(String[] var0) {
      zO = var0;
   }

   public static String[] Q() {
      return zO;
   }

   public static void p(String[] var0) {
      z9 = var0;
   }

   public static String[] F() {
      return z9;
   }

   public static void Q(boolean var0) {
      IP = var0;
   }

   public static boolean S() {
      return IP;
   }

   public static boolean a() {
      boolean var0 = S();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void C(int var0) {
      It = var0;
   }

   public static int t() {
      return It;
   }

   public static int c() {
      int var0 = t();

      try {
         return var0 == 0 ? 78 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void T(String var0) {
      zC = var0;
   }

   public static String g() {
      return zC;
   }

   public static void X(String[] var0) {
      zY = var0;
   }

   public static String[] R() {
      return zY;
   }

   public static void k(String var0) {
      Im = var0;
   }

   public static String H() {
      return Im;
   }

   public static void r(String[] var0) {
      Z = var0;
   }

   public static String[] z() {
      return Z;
   }

   public static void l(boolean var0) {
      IJ = var0;
   }

   public static boolean H() {
      return IJ;
   }

   public static boolean B() {
      boolean var0 = H();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void i(int var0) {
      z3 = var0;
   }

   public static int Z() {
      return z3;
   }

   public static int Y() {
      int var0 = Z();

      try {
         return var0 == 0 ? 15 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void Y(int var0) {
      i = var0;
   }

   public static int r() {
      return i;
   }

   public static int e() {
      int var0 = r();

      try {
         return var0 == 0 ? 118 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void g(String var0) {
      ID = var0;
   }

   public static String W() {
      return ID;
   }

   public static void G(int[] var0) {
      ek = var0;
   }

   public static int[] g() {
      return ek;
   }

   public static void e(int var0) {
      I4 = var0;
   }

   public static int V() {
      return I4;
   }

   public static int l() {
      int var0 = V();

      try {
         return var0 == 0 ? 125 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void H(int var0) {
      zj = var0;
   }

   public static int R() {
      return zj;
   }

   public static int f() {
      int var0 = R();

      try {
         return var0 == 0 ? 106 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void O(boolean var0) {
      Iz = var0;
   }

   public static boolean o() {
      return Iz;
   }

   public static boolean V() {
      boolean var0 = o();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void E(String[] var0) {
      f = var0;
   }

   public static String[] N() {
      return f;
   }

   public static void H(boolean var0) {
      IS = var0;
   }

   public static boolean i() {
      return IS;
   }

   public static boolean Z() {
      boolean var0 = i();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void f(int[] var0) {
      N = var0;
   }

   public static int[] p() {
      return N;
   }

   public static void o(boolean var0) {
      zP = var0;
   }

   public static boolean e() {
      return zP;
   }

   public static boolean D() {
      boolean var0 = e();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void B(int[] var0) {
      e2 = var0;
   }

   public static int[] E() {
      return e2;
   }

   public static void u(String[] var0) {
      zW = var0;
   }

   public static String[] C() {
      return zW;
   }

   public static void W(vh[] var0) {
      IX = var0;
   }

   public static vh[] h() {
      return IX;
   }

   public static void g(vh[] var0) {
      z2 = var0;
   }

   public static vh[] X() {
      return z2;
   }

   public static void W(String var0) {
      eq = var0;
   }

   public static String a() {
      return eq;
   }

   public static void U(int var0) {
      zA = var0;
   }

   public static int S() {
      return zA;
   }

   public static int i() {
      int var0 = S();

      try {
         return var0 == 0 ? 15 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void u(vh[] var0) {
      eX = var0;
   }

   public static vh[] C() {
      return eX;
   }

   public static void d(vh[] var0) {
      I6 = var0;
   }

   public static vh[] K() {
      return I6;
   }

   public static void A(String var0) {
      E = var0;
   }

   public static String s() {
      return E;
   }

   public static void z(String var0) {
      Ir = var0;
   }

   public static String y() {
      return Ir;
   }

   public static void Y(String var0) {
      X = var0;
   }

   public static String E() {
      return X;
   }

   public static void v(String[] var0) {
      Iw = var0;
   }

   public static String[] t() {
      return Iw;
   }

   public static void i(boolean var0) {
      d = var0;
   }

   public static boolean z() {
      return d;
   }

   public static boolean p() {
      boolean var0 = z();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void z(int[] var0) {
      zL = var0;
   }

   public static int[] o() {
      return zL;
   }

   public static void P(String var0) {
      Ip = var0;
   }

   public static String q() {
      return Ip;
   }

   public static void J(vh[] var0) {
      IU = var0;
   }

   public static vh[] E() {
      return IU;
   }

   public static void l(String var0) {
      zm = var0;
   }

   public static String x() {
      return zm;
   }

   public static void O(String[] var0) {
      zE = var0;
   }

   public static String[] a() {
      return zE;
   }

   public static void l(int var0) {
      zM = var0;
   }

   public static int ZN() {
      return zM;
   }

   public static int G() {
      int var0 = ZN();

      try {
         return var0 == 0 ? 21 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void i(String var0) {
      zt = var0;
   }

   public static String l() {
      return zt;
   }

   public static void c(vh[] var0) {
      eT = var0;
   }

   public static vh[] I() {
      return eT;
   }

   public static void x(String var0) {
      Q = var0;
   }

   public static String m() {
      return Q;
   }

   public static void l(int[] var0) {
      In = var0;
   }

   public static int[] N() {
      return In;
   }

   public static void o(int var0) {
      zp = var0;
   }

   public static int M() {
      return zp;
   }

   public static int A() {
      int var0 = M();

      try {
         return var0 == 0 ? 116 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void O(String var0) {
      eo = var0;
   }

   public static String v() {
      return eo;
   }

   public static void I(int var0) {
      zU = var0;
   }

   public static int o() {
      return zU;
   }

   public static int K() {
      int var0 = o();

      try {
         return var0 == 0 ? 126 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void v(int[] var0) {
      v = var0;
   }

   public static int[] V() {
      return v;
   }

   public static void D(String[] var0) {
      e = var0;
   }

   public static String[] E() {
      return e;
   }

   public static void b(String[] var0) {
      Iv = var0;
   }

   public static String[] H() {
      return Iv;
   }

   public static void F(boolean var0) {
      zh = var0;
   }

   public static boolean x() {
      return zh;
   }

   public static boolean P() {
      boolean var0 = x();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void W(int[] var0) {
      ev = var0;
   }

   public static int[] M() {
      return ev;
   }

   public static void r(String var0) {
      IK = var0;
   }

   public static String P() {
      return IK;
   }

   public static void j(String[] var0) {
      I0 = var0;
   }

   public static String[] g() {
      return I0;
   }

   public static void X(int var0) {
      I3 = var0;
   }

   public static int T() {
      return I3;
   }

   public static int Zn() {
      int var0 = T();

      try {
         return var0 == 0 ? 117 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void F(String[] var0) {
      J = var0;
   }

   public static String[] r() {
      return J;
   }

   public static void J(boolean var0) {
      IN = var0;
   }

   public static boolean f() {
      return IN;
   }

   public static boolean b() {
      boolean var0 = f();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void L(String[] var0) {
      eM = var0;
   }

   public static String[] m() {
      return eM;
   }

   public static void p(boolean var0) {
      z6 = var0;
   }

   public static boolean n() {
      return z6;
   }

   public static boolean X() {
      boolean var0 = n();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void w(String[] var0) {
      zV = var0;
   }

   public static String[] i() {
      return zV;
   }

   public static void t(int[] var0) {
      W = var0;
   }

   public static int[] R() {
      return W;
   }

   public static void d(String var0) {
      zv = var0;
   }

   public static String D() {
      return zv;
   }

   public static void D(String var0) {
      zo = var0;
   }

   public static String p() {
      return zo;
   }

   public static void C(vh[] var0) {
      S = var0;
   }

   public static vh[] W() {
      return S;
   }

   public static void R(String[] var0) {
      II = var0;
   }

   public static String[] G() {
      return II;
   }

   public static void S(boolean var0) {
      zS = var0;
   }

   public static boolean R() {
      return zS;
   }

   public static boolean c() {
      boolean var0 = R();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void q(boolean var0) {
      I8 = var0;
   }

   public static boolean A() {
      return I8;
   }

   public static boolean l() {
      boolean var0 = A();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void P(String[] var0) {
      zK = var0;
   }

   public static String[] L() {
      return zK;
   }

   public static void o(String var0) {
      l = var0;
   }

   public static String T() {
      return l;
   }

   public static void q(int var0) {
      IL = var0;
   }

   public static int q() {
      return IL;
   }

   public static int D() {
      int var0 = q();

      try {
         return var0 == 0 ? 94 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void I(String[] var0) {
      Ig = var0;
   }

   public static String[] u() {
      return Ig;
   }

   public static void q(String[] var0) {
      zz = var0;
   }

   public static String[] I() {
      return zz;
   }

   public static void f(boolean var0) {
      e9 = var0;
   }

   public static boolean Y() {
      return e9;
   }

   public static boolean u() {
      boolean var0 = Y();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void d(String[] var0) {
      zR = var0;
   }

   public static String[] x() {
      return zR;
   }

   public static void t(int var0) {
      I2 = var0;
   }

   public static int U() {
      return I2;
   }

   public static int O() {
      int var0 = U();

      try {
         return var0 == 0 ? 20 : 0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void x(String[] var0) {
      zX = var0;
   }

   public static String[] D() {
      return zX;
   }

   public static void u(String var0) {
      Ik = var0;
   }

   public static String C() {
      return Ik;
   }

   public static void k(int[] var0) {
      zy = var0;
   }

   public static int[] L() {
      return zy;
   }

   public static void g(boolean var0) {
      zq = var0;
   }

   public static boolean w() {
      return zq;
   }

   public static boolean E() {
      boolean var0 = w();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   public static void k(vh[] var0) {
      I9 = var0;
   }

   public static vh[] Y() {
      return I9;
   }

   public static void t(String[] var0) {
      z = var0;
   }

   public static String[] Y() {
      return z;
   }

   public static void u(int[] var0) {
      zd = var0;
   }

   public static int[] U() {
      return zd;
   }

   public static void S(String[] var0) {
      IY = var0;
   }

   public static String[] U() {
      return IY;
   }

   public static void G(String var0) {
      zw = var0;
   }

   public static String j() {
      return zw;
   }

   public static void k(boolean var0) {
      Is = var0;
   }

   public static boolean s() {
      return Is;
   }

   public static boolean U() {
      boolean var0 = s();

      try {
         return !var0;
      } catch (UnsupportedOperationException var1) {
         throw a(var1);
      }
   }

   static {
      int[] var10000 = new int[3];
      vh[] var10005 = new vh[5];
      String[] var10011 = new String[2];
      String[] var10012 = new String[3];
      int[] var10015 = new int[5];
      int[] var10022 = new int[5];
      int[] var10030 = new int[2];
      String[] var10031 = new String[4];
      int[] var10033 = new int[4];
      String[] var10038 = new String[1];
      vh[] var10040 = new vh[5];
      vh[] var10041 = new vh[2];
      vh[] var10049 = new vh[1];
      int[] var10051 = new int[1];
      String[] var10054 = new String[1];
      int[] var10060 = new int[5];
      String[] var10062 = new String[4];
      String[] var10074 = new String[5];
      String[] var10076 = new String[5];
      String[] var10077 = new String[3];
      vh[] var10081 = new vh[5];
      String[] var10084 = new String[2];
      String[] var10088 = new String[2];
      int[] var10089 = new int[3];
      String[] var10094 = new String[3];
      int[] var10096 = new int[4];
      int[] var10098 = new int[4];
      String[] var10099 = new String[3];
      String[] var10100 = new String[4];
      int[] var10104 = new int[3];
      int[] var10112 = new int[5];
      vh[] var10114 = new vh[2];
      String[] var10119 = new String[5];
      String[] var10122 = new String[3];
      vh[] var10131 = new vh[2];
      vh[] var10136 = new vh[3];
      String[] var10137 = new String[2];
      String[] var10140 = new String[1];
      vh[] var10141 = new vh[1];
      vh[] var10142 = new vh[2];
      vh[] var10146 = new vh[4];
      vh[] var10148 = new vh[3];
      String[] var10149 = new String[4];
      vh[] var10152 = new vh[2];
      vh[] var10155 = new vh[4];
      String[] var10163 = new String[4];
      int[] var10164 = new int[4];
      String[] var10169 = new String[2];
      vh[] var10170 = new vh[1];
      int[] var10171 = new int[2];
      String[] var10173 = new String[4];
      int[] var10176 = new int[3];
      String[] var10182 = new String[2];
      long var0 = ab ^ 58326743649366L;
      f("NoA0Nc");
      g(false);
      c(false);
      U(2);
      j((String[])null);
      H(var10182);
      H((int[])null);
      I(true);
      r(false);
      x(false);
      v((String)null);
      e(var10176);
      M("WOgrQb");
      k((String[])null);
      g(var10173);
      v(49);
      T(var10171);
      U(var10170);
      G(var10169);
      l((String[])null);
      l((vh[])null);
      J(0);
      n((String[])null);
      E(var10164);
      I(var10163);
      l((int[])null);
      y((String[])null);
      H(false);
      w((String)null);
      U(true);
      Q(0);
      Q("YTQ3Wb");
      Y(var10155);
      E(93);
      c((String[])null);
      D(var10152);
      S((int[])null);
      A((String[])null);
      f(var10149);
      k(var10148);
      j("SIlYgb");
      u(var10146);
      X(0);
      u(18);
      p((String[])null);
      s(var10142);
      O(var10141);
      N(var10140);
      b(true);
      Z("wEjsi");
      m(var10137);
      t(var10136);
      h((String[])null);
      f((int[])null);
      y("w2wbXc");
      m("NovVi");
      A(var10131);
      F(48);
      b("I0kADc");
      p("ncTgxc");
      F("hmb4Bb");
      a((String[])null);
      V(70);
      o((int[])null);
      o(28);
      q(var10122);
      x((int[])null);
      T(false);
      T(var10119);
      s("PT7BRb");
      C(true);
      a(25);
      R("ZyfU4b");
      e(var10114);
      Q(false);
      U(var10112);
      c((vh[])null);
      o("NPnhFb");
      O(false);
      X("WstAGc");
      K("JvBpEb");
      r((String)null);
      w(26);
      g(var10104);
      x((String)null);
      q(0);
      r(0);
      E(var10100);
      e(var10099);
      y(var10098);
      t(0);
      v(var10096);
      g(68);
      u(var10094);
      c("z3wJv");
      G((String)null);
      Y((String)null);
      p(false);
      w(var10089);
      X(var10088);
      W((vh[])null);
      m((vh[])null);
      S("aDIog");
      x(var10084);
      D((String[])null);
      k((String)null);
      K(var10081);
      q("RitJgb");
      k(true);
      v((String[])null);
      w(var10077);
      t(var10076);
      d((vh[])null);
      F(var10074);
      O("h52Dic");
      f(false);
      o(true);
      J((int[])null);
      W(0);
      J(true);
      A("yfJZhb");
      B("nh4fFc");
      C(0);
      y(false);
      u((int[])null);
      d(var10062);
      I(99);
      B(var10060);
      z(39);
      X((int[])null);
      S((String[])null);
      L((String[])null);
      z((String)null);
      z(var10054);
      T((String)null);
      Z(68);
      W(var10051);
      L((vh[])null);
      N(var10049);
      P((String)null);
      D((String)null);
      J((String)null);
      i(73);
      D((int[])null);
      Y(2);
      i((String[])null);
      C(var10041);
      J(var10040);
      u((String)null);
      b(var10038);
      g((vh[])null);
      W((String[])null);
      J((String[])null);
      i(true);
      t(var10033);
      T((vh[])null);
      r(var10031);
      Q(var10030);
      k((int[])null);
      F(false);
      W((String)null);
      G(0);
      j((int[])null);
      d((String)null);
      z((int[])null);
      n(var10022);
      l(false);
      e(true);
      V((String[])null);
      q(false);
      l(0);
      e(83);
      M(var10015);
      H(0);
      h("vDkCEb");
      C(var10012);
      P(var10011);
      i("ZZspc");
      l("v01QAb");
      R((String[])null);
      g("CInXjc");
      d(0);
      a(var10005);
      O((String[])null);
      S(true);
      G((int[])null);
      N(true);
      m(var10000);
      Cipher var2;
      Cipher var5 = var2 = Cipher.getInstance("DES/CBC/PKCS5Padding");
      SecretKeyFactory var10002 = SecretKeyFactory.getInstance("DES");
      byte[] var10003 = new byte[]{(byte)((int)(var0 >>> 56)), 0, 0, 0, 0, 0, 0, 0};

      for(int var3 = 1; var3 < 8; ++var3) {
         var10003[var3] = (byte)((int)(var0 << var3 * 8 >>> 56));
      }

      var5.init(2, var10002.generateSecret(new DESKeySpec(var10003)), new IvParameterSpec(new byte[8]));
      byte[] var4 = var2.doFinal("ê\u0084\u0012sæ\u008d¿ÀÅ[p¢\u0002ÂS\u0018\u0090N¸1\u008a¦÷D\u009f\u0015îhÉ\u008aN\u0019ÿO®J\u0010<²Hx\u0003$ls±\u0001ÐÞY/(÷\u0088öä\u000b\n~2\\9\u0086\f\u001b×ükAéH\u0087\u0099¬>±\u001d³«KH¼ó\u000fkÅx§\u0006?RW+¨®J".getBytes("ISO-8859-1"));
      String var6 = a(var4).intern();
      boolean var10001 = true;
      cb = var6;
   }

   private static UnsupportedOperationException a(UnsupportedOperationException var0) {
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
}
