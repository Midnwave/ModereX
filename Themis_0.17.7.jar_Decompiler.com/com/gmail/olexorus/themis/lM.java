package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

public class lm<T extends lm<T>> {
   public Object g;
   public final Object l;
   protected vL F;
   protected zZ I;
   private Te o;
   protected Gs Z;
   private static final long ab = kt.a(-396557923452409746L, 1275707125087115846L, MethodHandles.lookup().lookupClass()).a(139688375444115L);

   public lm(vL var1, zZ var2, int var3) {
      long var4 = ab ^ 70968741977397L;
      super();
      this.l = new Object();
      if (var3 == -1) {
         throw new IllegalArgumentException("Packet does not exist on this protocol version!");
      } else {
         this.F = var1;
         this.I = var2;
         this.g = null;
         this.o = new Te((wC)null, var3);
      }
   }

   public lm(aw var1) {
      this(var1, true);
   }

   public lm(aw var1, boolean var2) {
      this.l = new Object();
      this.F = var1.Z().E();
      this.I = var1.M();
      this.Z = var1.Z();
      this.g = var1.n();
      this.o = new Te(var1.y(), var1.K());
      if (var2) {
         this.h((aK)var1);
      }

   }

   public lm(aE var1) {
      this(var1, true);
   }

   public lm(aE var1, boolean var2) {
      this.l = new Object();
      this.F = var1.Z().E();
      this.I = var1.M();
      this.g = var1.n();
      this.o = new Te(var1.y(), var1.K());
      this.Z = var1.Z();
      if (var2) {
         this.h((aK)var1);
      }

   }

   public lm(wC var1) {
      this.l = new Object();
      this.F = vL.UNKNOWN;
      this.I = oS.J().g().w();
      this.g = null;
      int var2 = var1.d(this.I.u());
      this.o = new Te(var1, var2);
   }

   public static lm<?> s(vL var0) {
      return new lm(var0, var0.J(), -2);
   }

   public static lm<?> z(Object var0) {
      return i(var0, oS.J().g().w());
   }

   public static lm<?> i(Object var0, zZ var1) {
      lm var2 = new lm(vL.UNKNOWN, var1, -2);
      var2.g = var0;
      return var2;
   }

   public static int G(long var0) {
      return (int)(var0 & 4294967295L);
   }

   public static int T(long var0) {
      return (int)(var0 >>> 32 & 4294967295L);
   }

   public static long z(int var0, int var1) {
      return (long)var0 & 4294967295L | ((long)var1 & 4294967295L) << 32;
   }

   public final void e(Object var1, boolean var2, boolean var3) {
      if (this.g == null || NY.N(this.g) == 0) {
         this.g = lC.z(var1);
      }

      if (var3) {
         Gs var4 = oS.J().X().p(var1);
         if (this.o.u() == null) {
            this.o.f(O5.w(var2 ? RW.SERVER : RW.CLIENT, var4.W(), this.I.u(), this.o.W()));
         }

         this.I = var4.E().J();
         int var5 = this.o.u().d(var4.E());
         this.E(var5);
      } else {
         this.E(this.o.W());
      }

      this.d();
   }

   public final void R(Object var1, boolean var2) {
      this.e(var1, var2, oS.J().L().T());
   }

   public void t() {
   }

   public void d() {
   }

   public void B(T var1) {
   }

   public final void h(aK var1) {
      lm var2 = var1.V();
      if (this.getClass().isInstance(var2)) {
         this.B(var2);
      } else {
         this.t();
      }

      var1.G(this);
   }

   public vL K() {
      return this.F;
   }

   public zZ R() {
      return this.I;
   }

   public Object H() {
      return this.g;
   }

   public int g() {
      return this.I.i(zZ.V_1_13) ? 262144 : 32767;
   }

   public byte M() {
      return NY.o(this.g);
   }

   public void u(int var1) {
      NY.Z(this.g, var1);
   }

   public short h() {
      return NY.C(this.g);
   }

   public boolean P() {
      return this.M() != 0;
   }

   public void I(boolean var1) {
      this.u(var1 ? 1 : 0);
   }

   public int f() {
      return NY.s(this.g);
   }

   public void L(int var1) {
      NY.p(this.g, var1);
   }

   public long u() {
      return NY.k(this.g);
   }

   public void d(int var1) {
      NY.M(this.g, var1);
   }

   public int Q() {
      long var1 = ab ^ 5031095471722L;
      int var3 = 0;
      int var4 = 0;

      byte var5;
      do {
         var5 = this.M();
         var3 |= (var5 & 127) << var4 * 7;
         ++var4;
         if (var4 > 5) {
            throw new RuntimeException("VarInt is too large. Must be smaller than 5 bytes.");
         }
      } while((var5 & 128) == 128);

      return var3;
   }

   public void E(int var1) {
      if ((var1 & -128) == 0) {
         this.u(var1);
      } else {
         int var2;
         if ((var1 & -16384) == 0) {
            var2 = (var1 & 127 | 128) << 8 | var1 >>> 7;
            this.f(var2);
         } else if ((var1 & -2097152) == 0) {
            var2 = (var1 & 127 | 128) << 16 | (var1 >>> 7 & 127 | 128) << 8 | var1 >>> 14;
            this.d(var2);
         } else if ((var1 & -268435456) == 0) {
            var2 = (var1 & 127 | 128) << 24 | (var1 >>> 7 & 127 | 128) << 16 | (var1 >>> 14 & 127 | 128) << 8 | var1 >>> 21;
            this.L(var2);
         } else {
            var2 = (var1 & 127 | 128) << 24 | (var1 >>> 7 & 127 | 128) << 16 | (var1 >>> 14 & 127 | 128) << 8 | var1 >>> 21 & 127 | 128;
            this.L(var2);
            this.u(var1 >>> 28);
         }
      }

   }

   public <K, V> Map<K, V> U(MO<K> var1, MO<V> var2) {
      return this.Q(var1, var2, Integer.MAX_VALUE);
   }

   public <K, V> Map<K, V> Q(MO<K> var1, MO<V> var2, int var3) {
      long var4 = ab ^ 75543499438140L;
      int var6 = this.Q();
      if (var6 > var3) {
         throw new RuntimeException(var6 + " elements exceeded max size of: " + var3);
      } else {
         HashMap var7 = new HashMap(var6);

         for(int var8 = 0; var8 < var6; ++var8) {
            Object var9 = var1.apply(this);
            Object var10 = var2.apply(this);
            var7.put(var9, var10);
         }

         return var7;
      }
   }

   public <K, V> void o(Map<K, V> var1, Gw<K> var2, Gw<V> var3) {
      this.E(var1.size());
      Iterator var4 = var1.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         Object var6 = var5.getKey();
         Object var7 = var5.getValue();
         var2.accept(this, var6);
         var3.accept(this, var7);
      }

   }

   public BH V() {
      Nf var1 = (Nf)this.y((VD)ge.B());
      cP var2 = (cP)this.y((VD)mB.h());
      int var3 = this.Q();
      return new BH(var1, var2, var3);
   }

   public void B(BH var1) {
      this.j((GL)var1.O());
      this.j((GL)var1.O());
      this.E(var1.p());
   }

   public TU c() {
      long var1 = ab ^ 89122450795838L;
      TU var3 = this.u();
      if (var3.h()) {
         throw new RuntimeException("Empty ItemStack not allowed");
      } else {
         return var3;
      }
   }

   public TU u() {
      return n1.A(this);
   }

   public void M(TU var1) {
      long var2 = ab ^ 17054438478949L;
      if (var1 != null && !var1.h()) {
         this.m(var1);
      } else {
         throw new RuntimeException("Empty ItemStack not allowed");
      }
   }

   public void m(TU var1) {
      n1.w(this, var1);
   }

   public RT u() {
      return (RT)this.v();
   }

   public Rc Q() {
      Rc var1 = this.v();
      return var1 == RU.v ? null : var1;
   }

   public Rc v() {
      return g1.O(this.g, this.I);
   }

   public RT M() {
      return (RT)this.o();
   }

   public Rc o() {
      return g1.X(this.g, this.I, Gq.d());
   }

   public void G(RT var1) {
      this.b(var1);
   }

   public void b(Rc var1) {
      g1.A(this.g, this.I, var1);
   }

   public String A() {
      return this.m(32767);
   }

   public String m(int var1) {
      long var2 = ab ^ 62993895927605L;
      int var4 = this.Q();
      if (var4 > var1 * 4) {
         throw new RuntimeException("The received encoded string buffer length is longer than maximum allowed (" + var4 + " > " + var1 * 4 + ")");
      } else if (var4 < 0) {
         throw new RuntimeException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         String var5 = NY.B(this.g, NY.p(this.g), var4, StandardCharsets.UTF_8);
         NY.n(this.g, NY.p(this.g) + var4);
         if (var5.length() > var1) {
            throw new RuntimeException("The received string length is longer than maximum allowed (" + var4 + " > " + var1 + ")");
         } else {
            return var5;
         }
      }
   }

   public String n() {
      return this.x().I(this.a());
   }

   public void I(String var1) {
      this.a(var1, 32767);
   }

   public void a(String var1, int var2) {
      this.f(var1, var2, true);
   }

   public void f(String var1, int var2, boolean var3) {
      long var4 = ab ^ 99346905402592L;
      if (var3) {
         var1 = Cc.b(var1, var2);
      }

      byte[] var6 = var1.getBytes(StandardCharsets.UTF_8);
      if (!var3 && var6.length > var2) {
         throw new IllegalStateException("String too big (was " + var6.length + " bytes encoded, max " + var2 + ")");
      } else {
         this.E(var6.length);
         NY.N(this.g, var6);
      }
   }

   public h x() {
      return h.z(this);
   }

   public void f(String var1) {
      this.G(this.x().k(var1));
   }

   public X a() {
      return this.I.i(zZ.V_1_20_3) ? this.n() : this.K();
   }

   public X n() {
      return this.x().z(this.v(), this);
   }

   public X K() {
      String var1 = this.m(this.g());
      return this.x().k(var1);
   }

   public void G(X var1) {
      if (this.I.i(zZ.V_1_20_3)) {
         this.N(var1);
      } else {
         this.X(var1);
      }

   }

   public void N(X var1) {
      this.b(this.x().e(var1, this));
   }

   public void X(X var1) {
      String var2 = this.x().I(var1);
      this.a(var2, this.g());
   }

   public WR V() {
      return this.x().R().A(this.u(), this);
   }

   public void a(WR var1) {
      this.G(this.x().R().D(var1, this));
   }

   public al z(int var1) {
      return new al(this.m(var1));
   }

   public al R() {
      return this.z(32767);
   }

   public void p(al var1, int var2) {
      this.a(var1.toString(), var2);
   }

   public void T(al var1) {
      this.p((al)var1, 32767);
   }

   public int C() {
      return NY.Y(this.g);
   }

   public short x() {
      return NY.O(this.g);
   }

   public void f(int var1) {
      NY.D(this.g, var1);
   }

   public void B(int var1) {
      NY.V(this.g, var1);
   }

   public long k() {
      return NY.Q(this.g);
   }

   public void A(long var1) {
      NY.U(this.g, var1);
   }

   public long l() {
      long var1 = 0L;

      int var3;
      byte var4;
      for(var3 = 0; ((var4 = this.M()) & 128) == 128; var1 |= (long)(var4 & 127) << var3++ * 7) {
      }

      return var1 | (long)(var4 & 127) << var3 * 7;
   }

   public void X(long var1) {
      while((var1 & -128L) != 0L) {
         this.u((int)(var1 & 127L) | 128);
         var1 >>>= 7;
      }

      this.u((int)var1);
   }

   public float L() {
      return NY.W(this.g);
   }

   public void S(float var1) {
      NY.Z(this.g, var1);
   }

   public double o() {
      return NY.O(this.g);
   }

   public void v(double var1) {
      NY.r(this.g, var1);
   }

   public byte[] y() {
      return this.E(NY.r(this.g));
   }

   public byte[] E(int var1) {
      byte[] var2 = new byte[var1];
      NY.U(this.g, var2);
      return var2;
   }

   public void d(byte[] var1) {
      NY.N(this.g, var1);
   }

   public byte[] j(int var1) {
      long var2 = ab ^ 101592332480495L;
      int var4 = this.Q();
      if (var4 > var1) {
         throw new RuntimeException("The received byte array length is longer than maximum allowed (" + var4 + " > " + var1 + ")");
      } else {
         return this.E(var4);
      }
   }

   public byte[] w() {
      return this.j(NY.r(this.g));
   }

   public void N(byte[] var1) {
      this.E(var1.length);
      this.d(var1);
   }

   public int[] k() {
      long var1 = ab ^ 32576561376502L;
      int var3 = NY.r(this.g);
      int var4 = this.Q();
      if (var4 > var3) {
         throw new IllegalStateException("VarIntArray with size " + var4 + " is bigger than allowed " + var3);
      } else {
         int[] var5 = new int[var4];

         for(int var6 = 0; var6 < var4; ++var6) {
            var5[var6] = this.Q();
         }

         return var5;
      }
   }

   public void B(int[] var1) {
      this.E(var1.length);
      int[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         this.E(var5);
      }

   }

   public byte[] G(int var1) {
      byte[] var2 = new byte[var1];
      NY.U(this.g, var2);
      return var2;
   }

   public long[] J() {
      long var1 = ab ^ 45541494590444L;
      int var3 = NY.r(this.g) / 8;
      int var4 = this.Q();
      if (var4 > var3) {
         throw new IllegalStateException("LongArray with size " + var4 + " is bigger than allowed " + var3);
      } else {
         long[] var5 = new long[var4];

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = this.k();
         }

         return var5;
      }
   }

   public void Q(long[] var1) {
      this.E(var1.length);
      long[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long var5 = var2[var4];
         this.A(var5);
      }

   }

   public UUID V() {
      long var1 = this.k();
      long var3 = this.k();
      return new UUID(var1, var3);
   }

   public void y(UUID var1) {
      this.A(var1.getMostSignificantBits());
      this.A(var1.getLeastSignificantBits());
   }

   public VC M() {
      long var1 = this.k();
      return new VC(var1, this.I);
   }

   public void o(VC var1) {
      long var2 = var1.E(this.I);
      this.A(var2);
   }

   public zE S() {
      return zE.o(this.M());
   }

   public void O(zE var1) {
      int var2 = var1 == null ? -1 : var1.q();
      this.u(var2);
   }

   public List<Tw<?>> n() {
      long var1 = ab ^ 30952253185359L;
      ArrayList var3 = new ArrayList();
      int var6;
      iE var7;
      if (this.I.i(zZ.V_1_9)) {
         boolean var4 = this.I.i(zZ.V_1_10);

         short var5;
         while((var5 = this.h()) != 255) {
            var6 = var4 ? this.Q() : this.h();
            var7 = Tf.f(this.I.u(), var6);
            if (var7 == null) {
               throw new IllegalStateException("Unknown entity metadata type id: " + var6 + " version " + this.I.u());
            }

            var3.add(new Tw(var5, var7, var7.N(this)));
         }
      } else {
         for(byte var9 = this.M(); var9 != 127; var9 = this.M()) {
            int var10 = (var9 & 224) >> 5;
            var6 = var9 & 31;
            var7 = Tf.f(this.I.u(), var10);
            Tw var8 = new Tw(var6, var7, var7.N(this));
            var3.add(var8);
         }
      }

      return var3;
   }

   public void R(List<Tw<?>> var1) {
      if (var1 == null) {
         var1 = new ArrayList();
      }

      if (this.I.i(zZ.V_1_9)) {
         boolean var2 = this.I.i(zZ.V_1_10);

         Tw var4;
         for(Iterator var3 = ((List)var1).iterator(); var3.hasNext(); var4.X().a(this, var4.U())) {
            var4 = (Tw)var3.next();
            this.u(var4.p());
            if (var2) {
               this.E(var4.X().f(this.I.u()));
            } else {
               this.u(var4.X().f(this.I.u()));
            }
         }

         this.u(255);
      } else {
         Iterator var7 = ((List)var1).iterator();

         while(var7.hasNext()) {
            Tw var8 = (Tw)var7.next();
            int var9 = var8.X().f(this.I.u());
            int var5 = var8.p();
            int var6 = (var9 << 5 | var5 & 31) & 255;
            this.u(var6);
            var8.X().a(this, var8.U());
         }

         this.u(127);
      }

   }

   public Wi x() {
      long var1 = this.k();
      byte[] var3;
      if (this.I.i(zZ.V_1_19_3)) {
         if (this.P()) {
            var3 = this.E(256);
         } else {
            var3 = new byte[0];
         }
      } else {
         var3 = this.j(256);
      }

      return new Wi(var1, var3);
   }

   public void e(Wi var1) {
      this.A(var1.e());
      if (this.I.i(zZ.V_1_19_3)) {
         boolean var2 = var1.P().length != 0;
         this.I(var2);
         if (var2) {
            this.d(var1.P());
         }
      } else {
         this.N(var1.P());
      }

   }

   public PublicKey J() {
      return Ni.F(this.j(512));
   }

   public void J(PublicKey var1) {
      this.N(var1.getEncoded());
   }

   public BQ E() {
      Instant var1 = this.V();
      PublicKey var2 = this.J();
      byte[] var3 = this.j(4096);
      return new BQ(var1, var2, var3);
   }

   public void N(BQ var1) {
      this.e(var1.t());
      this.J(var1.g());
      this.N(var1.T());
   }

   public q M() {
      return new q(this.V(), this.E());
   }

   public void h(q var1) {
      this.y(var1.V());
      this.N(var1.W());
   }

   public Instant V() {
      return Instant.ofEpochMilli(this.k());
   }

   public void e(Instant var1) {
      this.A(var1.toEpochMilli());
   }

   public MM i() {
      return new MM(this.V(), this.J(), this.j(4096));
   }

   public void H(MM var1) {
      this.e(var1.o());
      this.J(var1.Z());
      this.N(var1.y());
   }

   public static <K> IntFunction<K> p(IntFunction<K> var0, int var1) {
      return lm::lambda$limitValue$0;
   }

   public Nt E() {
      return new Nt(this.R(), this.M());
   }

   public void c(Nt var1) {
      this.T(var1.U());
      this.o(var1.z());
   }

   public TR Z() {
      return new TR(this.V(), this.w());
   }

   public void x(TR var1) {
      this.y(var1.N());
      this.N(var1.T());
   }

   public zf C() {
      int var1 = this.Q();
      BitSet var2 = BitSet.valueOf(this.E(3));
      byte var3 = this.I.i(zZ.V_1_21_5) ? this.M() : 0;
      return new zf(var1, var2, var3);
   }

   public void C(zf var1) {
      this.E(var1.G());
      this.d(Arrays.copyOf(var1.M().toByteArray(), 3));
      if (this.I.i(zZ.V_1_21_5)) {
         this.u(var1.E());
      }

   }

   public GA N() {
      MZ var1 = this.S();
      TR var2 = (TR)this.u(lm::Z);
      return new GA(var1, var2);
   }

   public void f(GA var1) {
      this.L(var1.i());
      this.l(var1.v(), lm::x);
   }

   public cG t() {
      return this.I.i(zZ.V_1_19_3) ? new cG(this.E(256)) : new cG(this.w());
   }

   public void g(cG var1) {
      this.d(var1.B());
   }

   public JL U() {
      int var1 = this.Q() - 1;
      return var1 == -1 ? new JL(new cG(this.E(256))) : new JL(var1);
   }

   public void v(JL var1) {
      this.E(var1.N() + 1);
      if (var1.k().isPresent()) {
         this.d(((cG)var1.k().get()).B());
      }

   }

   public oX E() {
      List var1 = (List)this.U(p((IntFunction)(ArrayList::new), 20), lm::U);
      return new oX(var1);
   }

   public void M(oX var1) {
      this.d(var1.p(), lm::v);
   }

   public MZ S() {
      List var1 = (List)this.U(p((IntFunction)(ArrayList::new), 5), lm::Z);
      return new MZ(var1);
   }

   public void L(MZ var1) {
      this.d(var1.z(), lm::x);
   }

   public List<cv> a() {
      return (List)this.U(p((IntFunction)(ArrayList::new), 8), this::lambda$readSignedCommandArguments$1);
   }

   public void y(List<cv> var1) {
      this.d(var1, this::lambda$writeSignedCommandArguments$2);
   }

   public BitSet F() {
      return BitSet.valueOf(this.J());
   }

   public void W(BitSet var1) {
      this.Q(var1.toLongArray());
   }

   public Tx q() {
      // $FF: Couldn't be decompiled
   }

   public void L(Tx var1) {
      this.E(var1.V().W());
      if (var1.V() == ax.PARTIALLY_FILTERED) {
         this.W(var1.H());
      }

   }

   public aG F() {
      TU var1 = OW.b(this);
      TU var2 = this.u();
      TU var3 = !this.R().i(zZ.V_1_20_5) && !this.R().m(zZ.V_1_19) ? this.u() : (TU)this.u(OW::b);
      boolean var4 = this.P();
      int var5 = this.f();
      int var6 = this.f();
      int var7 = this.f();
      int var8 = this.f();
      float var9 = this.L();
      int var10 = this.f();
      aG var11 = aG.w(var1, var3, var2, var5, var6, var7, var8, var9, var10);
      if (var4) {
         var11.n(var11.W());
      }

      return var11;
   }

   public void W(aG var1) {
      OW.O(this, var1.L());
      this.m(var1.O());
      TU var2 = var1.v();
      if (var2 != null && var2.h()) {
         var2 = null;
      }

      if (!this.R().i(zZ.V_1_20_5) && !this.R().m(zZ.V_1_19)) {
         this.m(var2);
      } else {
         this.l(var2, OW::O);
      }

      this.I(var1.B() >= var1.W());
      this.L(var1.B());
      this.L(var1.W());
      this.L(var1.X());
      this.L(var1.E());
      this.S(var1.v());
      this.L(var1.K());
   }

   public r0 f() {
      Cm var1 = this.I.i(zZ.V_1_21) ? (Cm)this.i((VD)mg.o(), (MO)(Cm::X)) : (Cm)this.y((VD)mg.o());
      X var2 = this.a();
      X var3 = (X)this.u(lm::a);
      return new r0(var1, var2, var3);
   }

   public void B(r0 var1) {
      if (this.I.i(zZ.V_1_21)) {
         this.M(var1.x(), Cm::K);
      } else {
         this.j((GL)var1.x());
      }

      this.G(var1.z());
      this.l(var1.k(), lm::G);
   }

   public Mr G() {
      byte var1 = this.M();
      int var2 = var1 & 3;
      List var3 = this.j(lm::Q);
      int var4 = (var1 & 8) != 0 ? this.Q() : 0;
      String var5;
      if (var2 == 2) {
         var5 = this.A();
         ie var6 = this.I.i(zZ.V_1_19) ? (ie)this.e(Ow::A) : Ow.J(this.R().toString());
         List var7 = (List)var6.S(this).orElse((Object)null);
         al var8 = (var1 & 16) != 0 ? this.R() : null;
         return new Mr(var1, var3, var4, var5, var6, var7, var8);
      } else if (var2 == 1) {
         var5 = this.A();
         return new Mr(var1, var3, var4, var5, (ie)null, (List)null, (al)null);
      } else {
         return new Mr(var1, var3, var4, (String)null, (ie)null, (List)null, (al)null);
      }
   }

   public void O(Mr var1) {
      this.u(var1.F());
      this.D(var1.c(), lm::E);
      if ((var1.F() & 8) != 0) {
         this.E(var1.w());
      }

      var1.K().ifPresent(this::I);
      if (var1.i().isPresent()) {
         ie var2 = (ie)var1.i().get();
         if (this.I.i(zZ.V_1_19)) {
            this.j((GL)var2);
         } else {
            this.T(var2.f());
         }

         if (var1.f().isPresent()) {
            var2.m(this, (List)var1.f().get());
         }
      }

      var1.t().ifPresent(this::T);
   }

   public uK b() {
      String var1 = this.A();
      String var2 = this.A();
      String var3 = this.A();
      return new uK(var1, var2, var3);
   }

   public void j(uK var1) {
      this.I(var1.h());
      this.I(var1.B());
      this.I(var1.P());
   }

   public <T extends Enum<T>> EnumSet<T> J(Class<T> var1) {
      Enum[] var2 = (Enum[])var1.getEnumConstants();
      byte[] var3 = new byte[-Math.floorDiv(-var2.length, 8)];
      NY.U(this.H(), var3);
      BitSet var4 = BitSet.valueOf(var3);
      EnumSet var5 = EnumSet.noneOf(var1);

      for(int var6 = 0; var6 < var2.length; ++var6) {
         if (var4.get(var6)) {
            var5.add(var2[var6]);
         }
      }

      return var5;
   }

   public <T extends Enum<T>> void r(EnumSet<T> var1, Class<T> var2) {
      Enum[] var3 = (Enum[])var2.getEnumConstants();
      BitSet var4 = new BitSet(var3.length);

      for(int var5 = 0; var5 < var3.length; ++var5) {
         if (var1.contains(var3[var5])) {
            var4.set(var5);
         }
      }

      this.d(Arrays.copyOf(var4.toByteArray(), -Math.floorDiv(-var3.length, 8)));
   }

   public <U, V, R> U t(ty var1, zZ var2, MO<V> var3, MO<R> var4) {
      return this.I.K(var1, var2) ? var3.apply(this) : var4.apply(this);
   }

   public <V> void o(ty var1, zZ var2, V var3, Gw<V> var4, Gw<V> var5) {
      if (this.I.K(var1, var2)) {
         var4.accept(this, var3);
      } else {
         var5.accept(this, var3);
      }

   }

   public <R> R u(MO<R> var1) {
      return this.P() ? var1.apply(this) : null;
   }

   public <V> void l(V var1, Gw<V> var2) {
      if (var1 != null) {
         this.I(true);
         var2.accept(this, var1);
      } else {
         this.I(false);
      }

   }

   public <R> Optional<R> h(MO<R> var1) {
      return this.P() ? Optional.of(var1.apply(this)) : Optional.empty();
   }

   public <V> void F(Optional<V> var1, Gw<V> var2) {
      if (var1.isPresent()) {
         this.I(true);
         var2.accept(this, var1.get());
      } else {
         this.I(false);
      }

   }

   public <K, C extends Collection<K>> C U(IntFunction<C> var1, MO<K> var2) {
      int var3 = this.Q();
      return this.R(var1, var2, var3);
   }

   private <K, C extends Collection<K>> C R(IntFunction<C> var1, MO<K> var2, int var3) {
      Collection var4 = (Collection)var1.apply(var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         var4.add(var2.apply(this));
      }

      return var4;
   }

   public <K> void d(Collection<K> var1, Gw<K> var2) {
      this.E(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         var2.accept(this, var4);
      }

   }

   public <K> List<K> j(MO<K> var1) {
      return (List)this.U(ArrayList::new, var1);
   }

   public <K> void D(List<K> var1, Gw<K> var2) {
      this.E(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         var2.accept(this, var4);
      }

   }

   public <K> Set<K> u(MO<K> var1) {
      return (Set)this.U(HashSet::new, var1);
   }

   public <K> void P(Set<K> var1, Gw<K> var2) {
      this.E(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         var2.accept(this, var4);
      }

   }

   public <K> K[] t(MO<K> var1, Class<K> var2) {
      int var3 = this.Q();
      Object[] var4 = (Object[])Array.newInstance(var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         var4[var5] = var1.apply(this);
      }

      return var4;
   }

   public <K> void c(K[] var1, Gw<K> var2) {
      this.E(var1.length);
      Object[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         var2.accept(this, var6);
      }

   }

   public <Z extends Enum<?>> Z i(Class<Z> var1) {
      return this.w((Enum[])var1.getEnumConstants());
   }

   public <Z extends Enum<?>> Z w(Z[] var1) {
      return var1[this.Q()];
   }

   public <Z extends Enum<?>> Z n(Z[] var1, Z var2) {
      int var3 = this.Q();
      return var3 >= 0 && var3 < var1.length ? var1[var3] : var2;
   }

   public void o(Enum<?> var1) {
      this.E(var1.ordinal());
   }

   public <Z extends GL> Z e(BiFunction<vL, Integer, Z> var1) {
      long var2 = ab ^ 59660345927987L;
      int var4 = this.Q();
      GL var5 = (GL)var1.apply(this.I.u(), var4);
      if (var5 == null) {
         throw new IllegalStateException("Can't find mapped entity with id " + var4 + " using " + var1);
      } else {
         return var5;
      }
   }

   public <Z extends GL> VD<Z> w(VD<Z> var1) {
      return this.T().W(var1, this.I.u());
   }

   public m2 T() {
      return (m2)(this.Z != null ? this.Z : CC.k);
   }

   public <Z extends GL> Z W(BiFunction<vL, Integer, Z> var1, MO<Z> var2) {
      long var3 = ab ^ 70929669178610L;
      int var5 = this.Q();
      if (var5 == 0) {
         return (GL)var2.apply(this);
      } else {
         GL var6 = (GL)var1.apply(this.I.u(), var5 - 1);
         if (var6 == null) {
            throw new IllegalStateException("Can't find mapped entity with id " + var5 + " using " + var1);
         } else {
            return var6;
         }
      }
   }

   public <Z extends GL> Z y(VD<Z> var1) {
      VD var2 = this.T().W(var1, this.I.u());
      return this.e((BiFunction)var2);
   }

   public <Z extends GL> Z i(VD<Z> var1, MO<Z> var2) {
      VD var3 = this.T().W(var1, this.I.u());
      return this.W(var3, var2);
   }

   public void j(GL var1) {
      long var2 = ab ^ 109831465601238L;
      if (!var1.z()) {
         throw new IllegalArgumentException("Can't write id of unregistered entity " + var1.f() + " (" + var1 + ")");
      } else {
         this.E(var1.f(this.I.u()));
      }
   }

   public <Z extends GL> void M(Z var1, Gw<Z> var2) {
      if (!var1.z()) {
         this.E(0);
         var2.accept(this, var1);
      } else {
         int var3 = var1.f(this.I.u());
         this.E(var3 + 1);
      }
   }

   public int a() {
      return this.I.i(zZ.V_1_21_2) ? this.Q() : this.h();
   }

   public void y(int var1) {
      if (this.I.i(zZ.V_1_21_2)) {
         this.E(var1);
      } else {
         this.u(var1);
      }

   }

   public <L, R> E_<L, R> e(MO<L> var1, MO<R> var2) {
      return this.P() ? E_.u(var1.apply(this)) : E_.Y(var2.apply(this));
   }

   public <L, R> void w(E_<L, R> var1, Gw<L> var2, Gw<R> var3) {
      if (var1.m()) {
         this.I(true);
         var2.accept(this, var1.l());
      } else {
         this.I(false);
         var3.accept(this, var1.J());
      }

   }

   public void Z(float var1) {
      this.u((byte)a8.a(var1 * 256.0F / 360.0F));
   }

   public float x() {
      return (float)(this.M() * 360) / 256.0F;
   }

   public Integer t() {
      int var1 = this.Q();
      return var1 == 0 ? null : var1 - 1;
   }

   public void l(Integer var1) {
      this.E(var1 == null ? 0 : var1 + 1);
   }

   public <Z> Z y(int var1, MO<Z> var2) {
      long var3 = ab ^ 57040328293093L;
      int var5 = this.Q();
      if (var5 > var1) {
         throw new RuntimeException("Buffer size " + var5 + " is larger than allowed limit of " + var1);
      } else {
         Object var6 = this.g;

         Object var7;
         try {
            this.g = NY.g(var6, var5);
            var7 = var2.apply(this);
         } finally {
            this.g = var6;
         }

         return var7;
      }
   }

   public <Z> void w(Z var1, Gw<Z> var2) {
      Object var3 = NY.o(this.g);
      Object var4 = this.g;

      try {
         this.g = var3;
         var2.accept(this, var1);
      } finally {
         this.g = var4;
      }

      this.E(NY.r(var3));
      NY.I(var4, var3);
   }

   private void lambda$writeSignedCommandArguments$2(lm var1, cv var2) {
      this.a(var2.b(), 16);
      this.g(var2.x());
   }

   private cv lambda$readSignedCommandArguments$1(lm var1) {
      return new cv(this.m(16), this.t());
   }

   private static Object lambda$limitValue$0(int var0, IntFunction var1, int var2) {
      long var3 = ab ^ 78814258593008L;
      if (var2 > var0) {
         throw new RuntimeException("Value " + var2 + " is larger than limit " + var0);
      } else {
         return var1.apply(var2);
      }
   }
}
