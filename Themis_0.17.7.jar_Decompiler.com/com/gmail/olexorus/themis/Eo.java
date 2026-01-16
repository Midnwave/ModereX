package com.gmail.olexorus.themis;

import com.google.common.collect.MapMaker;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class EO {
   private static final String qz;
   public static final String N1;
   public static final String m;
   public static zZ Nq;
   public static boolean NW;
   public static boolean q2;
   public static boolean NY;
   public static Class<?> N6;
   public static Class<?> U;
   public static Class<?> f;
   public static Class<?> Nv;
   public static Class<?> B;
   public static Class<?> a;
   public static Class<?> O;
   public static Class<?> C;
   public static Class<?> h;
   public static Class<?> qT;
   public static Class<?> b;
   public static Class<?> i;
   public static Class<?> E;
   public static Class<?> qv;
   public static Class<?> qX;
   public static Class<?> qk;
   public static Class<?> qc;
   public static Class<?> q3;
   public static Class<?> r;
   public static Class<?> q7;
   public static Class<?> q8;
   public static Class<?> NO;
   public static Class<?> qg;
   public static Class<?> Ny;
   public static Class<?> S;
   public static Class<?> qo;
   public static Class<?> qw;
   public static Class<?> NX;
   public static Class<?> qZ;
   public static Class<?> q_;
   public static Class<?> z;
   public static Class<?> qr;
   public static Class<?> qj;
   public static Class<?> qy;
   public static Class<?> W;
   public static Class<?> Z;
   public static Class<?> qq;
   public static Class<?> p;
   public static Class<?> ND;
   public static Class<?> Nd;
   public static Class<?> qK;
   public static Class<?> qN;
   public static Class<?> Q;
   public static Class<?> qp;
   public static Class<?> Ni;
   public static Class<?> Nx;
   public static Class<?> qM;
   public static Class<?> j;
   public static Class<?> u;
   public static Class<?> I;
   public static Class<?> NC;
   public static Class<?> qR;
   public static Class<?> Nk;
   public static Class<?> Nh;
   public static Class<?> NR;
   public static Class<?> D;
   public static Class<?> N3;
   public static Class<?> qO;
   public static Class<?> NH;
   public static Class<?> w;
   public static Class<?> qs;
   public static Class<?> M;
   public static Class<?> q9;
   public static Class<?> qD;
   public static Class<?> qu;
   public static Class<?> qQ;
   public static Class<?> Nw;
   public static Class<?> qn;
   public static Class<?> K;
   public static Class<?> H;
   public static Class<?> k;
   public static Class<?> P;
   public static Class<?> q1;
   public static Class<?> q5;
   public static Field NS;
   public static Field qF;
   public static Field q0;
   public static Field V;
   public static Field N_;
   public static Field e;
   public static Field Ng;
   public static Field qL;
   public static Field qP;
   public static Field l;
   public static Field qe;
   public static Field Y;
   public static Field NI;
   public static Field Np;
   public static Field qI;
   public static Field qi;
   public static Method Nm;
   public static Method qm;
   public static Method F;
   public static Method t;
   public static Method qB;
   public static Method qE;
   public static Method R;
   public static Method NG;
   public static Method NU;
   public static Method x;
   public static Method ql;
   public static Method NZ;
   public static Method N9;
   public static Method L;
   public static Method d;
   public static Method J;
   public static Method qH;
   public static Method No;
   public static Method n;
   public static Method qV;
   public static Method Nr;
   public static Method qf;
   public static Method qS;
   public static Method qJ;
   public static Method NF;
   public static Method qU;
   public static Method qG;
   public static Method qa;
   public static Method Ne;
   public static Method qA;
   public static Method qh;
   public static Method qd;
   public static Method y;
   public static Method NJ;
   public static Method T;
   public static Method A;
   public static Method NE;
   public static Method NT;
   public static Method N;
   public static Method X;
   public static Method qW;
   public static Method qY;
   public static Method q6;
   public static Method qx;
   public static Method s;
   private static Constructor<?> qt;
   private static Constructor<?> g;
   private static Constructor<?> qb;
   private static Constructor<?> v;
   private static Constructor<?> o;
   private static Object qC;
   private static Object NQ;
   private static Object NA;
   private static boolean G;
   private static boolean q;
   private static boolean c;
   public static Map<Integer, Entity> q4;
   private static final long ab = kt.a(-6077309474213589681L, 4658983447653139173L, MethodHandles.lookup().lookupClass()).a(224682526409692L);

   private static void N() {
      Class var0 = Nv != null ? Nv : q3;

      try {
         qt = f.getConstructor(var0, Integer.TYPE);
         g = U.getConstructor(P);
         if (Nq.i(zZ.V_1_9)) {
            qb = C.getDeclaredConstructor(String.class, String.class);
            qb.setAccessible(true);
         }

         if (Nq.i(zZ.V_1_20_5)) {
            v = u.getConstructor(P, I);
         }

         if (NH != null) {
            o = NH.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
         }
      } catch (NoSuchMethodException var2) {
         var2.printStackTrace();
      }

   }

   private static void u() {
      long var0 = ab ^ 23363804647125L;
      Nm = rB.N(N6, "isDebugging", 0);
      NU = rB.w(B, qw, 0);
      qm = rB.N(qo, "getHandle", 0);
      F = rB.N(qw, "getHandle", 0);
      t = rB.N(Ny, "getHandle", 0);
      qB = rB.N(qc, NW ? "g" : "getId", 0);
      qE = rB.N(qc, NW ? "a" : "fromId", 0);
      R = rB.N(q3, NW ? "g" : "getId", 0);
      NG = rB.w(q3, q3, 0);
      if (q2) {
         x = rB.w(q_, Iterable.class, 0);
         ql = rB.y(q_, z, 0, Integer.TYPE);
      }

      if (p != null) {
         qJ = rB.N(q7, "getTypeKey", 0);
         qf = rB.w(q7, p, 0);
         qS = rB.w(p, Integer.TYPE, 0);
      }

      NF = rB.N(Nd, "encodeStart", 0);
      qU = rB.N(qK, "result", 0);
      String var2 = Nq.m(zZ.V_1_9) ? "a" : (Nq.m(zZ.V_1_17) ? "getEntity" : "b");
      NZ = rB.O(q8, var2, B, Integer.TYPE);
      if (NZ == null) {
         NZ = rB.O(q8, "getEntity", B, Integer.TYPE);
      }

      if (oS.J().g().w().R(zZ.V_1_12_2)) {
         d = rB.d(qZ, "toNMS", qk);
         if (oS.J().g().w().i(zZ.V_1_9)) {
            Class var3 = rB.e("org.bukkit.Particle");
            J = rB.d(qZ, "toBukkit", var3);
         }
      }

      N9 = rB.N(NX, "asBukkitCopy", 0);
      L = rB.d(NX, "asNMSCopy", ItemStack.class);
      qH = rB.O(U, "k", f);
      if (qH == null) {
         qH = rB.w(U, f, 0);
      }

      No = rB.O(U, "a", U, f);
      if (No == null) {
         No = rB.E(U, 0, f);
      }

      n = rB.y(Z, Integer.TYPE, 0, W);
      qV = rB.y(Z, W, 0, Integer.TYPE);
      if (qq != null) {
         Nr = rB.O(qq, "fromData", qq, W);
      }

      qG = rB.E(Ni, 0, DataInputStream.class);
      if (qG == null) {
         if (Nq.i(zZ.V_1_20_2)) {
            qG = rB.E(Ni, 0, DataInput.class, NR);
         } else {
            qG = rB.E(Ni, 0, DataInput.class);
         }
      }

      if (Nq.i(zZ.V_1_20_2) && Nq.m(zZ.V_1_20_5)) {
         qa = rB.d(Ni, "a", qp, DataOutput.class);
      } else {
         qa = rB.E(Ni, 0, Nq.i(zZ.V_1_20_2) ? qp : Q, DataOutput.class);
      }

      if (Nq.i(zZ.V_1_20_2)) {
         T = rB.w(NR, NR, 0);
      }

      if (Nq.i(zZ.V_1_20_5)) {
         Ne = qM.getMethods()[0];
         qA = j.getMethods()[0];
      }

      qh = rB.E(qR, 0, C);
      qd = rB.y(I, Nq.i(zZ.V_1_17) ? Nk : Nh, 0, qR);
      y = rB.w(NC, Nk, 0);
      NJ = rB.y(Nk, Integer.TYPE, 0, Object.class);
      if (q9 != null) {
         A = rB.y(D, q9, 0, Integer.TYPE, Integer.TYPE);
         NT = rB.w(q9, W, 0);
      }

      if (qD != null) {
         NE = rB.y(D, qD, 0, Integer.TYPE, Integer.TYPE, Boolean.TYPE);
      }

      if (W != null) {
         N = rB.y(M, W, 0, NH);
      }

      if (qs != null) {
         X = rB.y(w, qs, 0, Long.TYPE);
      }

      if (M != null) {
         qW = rB.w(qs, M, 0);
      }

      qY = rB.y(qQ, Void.TYPE, 0, U);
      q6 = rB.E(Nw, 0, List.class, u);
      qx = rB.w(qn, K, 0);
      s = rB.d(Server.class, "getTPS");
   }

   private static void M() {
      long var0 = ab ^ 28285692220759L;
      qF = rB.B(B, O, 0, true);
      NS = rB.x(a, "ping");
      q0 = rB.B(U, P, 0, true);
      Ng = rB.x(qZ, "particles");
      qL = rB.x(C, "key");
      if (oS.J().g().w().R(zZ.V_1_12_2)) {
         qP = rB.x(qk, "X");
         l = rB.x(qk, "ac");
      }

      V = rB.t(p, ND, 0);
      N_ = rB.t(qN, qN, 0);
      e = rB.t(q8, D, 0);
      if (e == null) {
         e = rB.t(q8, N3, 0);
      }

      G = rB.t(q8, qj, 0) != null;
      if (G) {
         q = rB.t(q7, qj, 0) == null;
      }

      qe = rB.t(a, qu, 0);
      Y = rB.t(qR, C, 1);
      NI = rB.B(B, qQ, 0, true);
      Np = rB.t(H, E, 0);
      qI = rB.t(E, qX, 0);
      qi = rB.t(qX, k, 0);
   }

   private static void R() {
      long var0 = ab ^ 130285766752005L;
      c = rB.e("net.minecraft.server.network.PlayerConnection") != null;
      N6 = z("server.MinecraftServer", "MinecraftServer");
      U = z(c ? "network.PacketDataSerializer" : "network.FriendlyByteBuf", "PacketDataSerializer");
      f = z("world.item.ItemStack", "ItemStack");
      Nv = z(c ? "world.level.IMaterial" : "world.level.ItemLike", "IMaterial");
      B = z("world.entity.Entity", "Entity");
      a = z(c ? "server.level.EntityPlayer" : "server.level.ServerPlayer", "EntityPlayer");
      O = z(c ? "world.phys.AxisAlignedBB" : "world.phys.AABB", "AxisAlignedBB");
      String var2 = Nq.i(zZ.V_1_21_11) ? "resources.Identifier" : "resources.ResourceLocation";
      C = z(c ? "resources.MinecraftKey" : var2, "MinecraftKey");
      h = z(c ? "world.entity.player.EntityHuman" : "world.entity.player.Player", "EntityHuman");
      qT = z(c ? "server.network.PlayerConnection" : "server.network.ServerGamePacketListenerImpl", "PlayerConnection");
      i = z(c ? "server.network.LoginListener" : "server.network.ServerLoginPacketListenerImpl", "LoginListener");
      E = z("server.network.ServerCommonPacketListenerImpl", "ServerCommonPacketListenerImpl");
      b = l("entity.CraftPlayer$TransferCookieConnection");
      H = rB.e("io.papermc.paper.connection.PaperCommonConnection");
      qv = z(c ? "server.network.ServerConnection" : "server.network.ServerConnectionListener", "ServerConnection");
      qX = z(c ? "network.NetworkManager" : "network.Connection", "NetworkManager");
      qc = z(c ? "world.effect.MobEffectList" : "world.effect.MobEffect", "MobEffectList");
      q3 = z("world.item.Item", "Item");
      r = z("server.dedicated.DedicatedServer", "DedicatedServer");
      q7 = z(c ? "world.level.World" : "world.level.Level", "World");
      q8 = z(c ? "server.level.WorldServer" : "server.level.ServerLevel", "WorldServer");
      NO = z(c ? "network.protocol.EnumProtocolDirection" : "network.protocol.PacketFlow", "EnumProtocolDirection");
      if (q2) {
         q_ = z("world.level.entity.LevelEntityGetter", "");
         qr = z("world.level.entity.PersistentEntitySectionManager", "");
         qj = rB.e("ca.spottedleaf.moonrise.patches.chunk_system.level.entity.EntityLookup");
         if (qj == null) {
            qj = rB.e("io.papermc.paper.chunk.system.entity.EntityLookup");
         }

         z = z("world.level.entity.EntityAccess", "");
      }

      p = z(c ? "world.level.dimension.DimensionManager" : "world.level.dimension.DimensionType", "DimensionManager");
      ND = rB.e("com.mojang.serialization.Codec");
      Nd = rB.e("com.mojang.serialization.Encoder");
      qK = rB.e("com.mojang.serialization.DataResult");
      qN = z(c ? "nbt.DynamicOpsNBT" : "nbt.NbtOps", "DynamicOpsNBT");
      if (oS.J().g().w().R(zZ.V_1_12_2)) {
         qk = z((String)null, "EnumParticle");
      }

      qy = l("util.CraftMagicNumbers");
      W = z(c ? "world.level.block.state.IBlockData" : "world.level.block.state.BlockState", "IBlockData");
      Z = z("world.level.block.Block", "Block");
      qq = l("block.data.CraftBlockData");
      qg = rB.e("com.mojang.authlib.GameProfile");
      Ny = l("CraftWorld");
      qo = l("entity.CraftPlayer");
      S = l("CraftServer");
      qw = l("entity.CraftEntity");
      NX = l("inventory.CraftItemStack");
      qZ = l("CraftParticle");
      k = A("channel.Channel");
      P = A("buffer.ByteBuf");
      q1 = A("handler.codec.ByteToMessageDecoder");
      q5 = A("handler.codec.MessageToByteEncoder");
      Q = z(c ? "nbt.NBTTagCompound" : "nbt.CompoundTag", "NBTTagCompound");
      qp = z(c ? "nbt.NBTBase" : "nbt.Tag", "NBTBase");
      Ni = z(c ? "nbt.NBTCompressedStreamTools" : "nbt.NbtIo", "NBTCompressedStreamTools");
      NR = z(c ? "nbt.NBTReadLimiter" : "nbt.NbtAccounter", "NBTReadLimiter");
      D = z(c ? "server.level.ChunkProviderServer" : "server.level.ServerChunkCache", "ChunkProviderServer");
      N3 = z(c ? "world.level.chunk.IChunkProvider" : "world.level.chunk.ChunkSource", "IChunkProvider");
      qO = z("world.level.chunk.status.ChunkStatus", "");
      if (qO == null) {
         qO = z("world.level.ChunkStatus", "");
      }

      NH = z(c ? "core.BlockPosition" : "core.BlockPos", "BlockPosition");
      w = z(c ? "server.level.PlayerChunkMap" : "server.level.ChunkMap", "");
      qs = z(c ? "server.level.PlayerChunk" : "server.level.ChunkHolder", "");
      M = z(c ? "world.level.chunk.Chunk" : "world.level.chunk.LevelChunk", "Chunk");
      q9 = z(c ? "world.level.IBlockAccess" : "world.level.BlockGetter", "IBlockAccess");
      qD = z(c ? "world.level.chunk.IChunkAccess" : "world.level.chunk.ChunkAccess", "IChunkAccess");
      if (Nq.i(zZ.V_1_20_5)) {
         Nx = rB.e("net.minecraft.network.codec.StreamCodec");
         qM = rB.e("net.minecraft.network.codec.StreamDecoder");
         j = rB.e("net.minecraft.network.codec.StreamEncoder");
         u = rB.e("net.minecraft.network.RegistryFriendlyByteBuf");
      }

      I = z(c ? "core.IRegistryCustom" : "core.RegistryAccess", "IRegistryCustom");
      NC = z(c ? "core.IRegistryCustom$Dimension" : "core.RegistryAccess$Frozen", "IRegistryCustom$Dimension");
      qR = z("resources.ResourceKey", "ResourceKey");
      Nk = z(c ? "core.IRegistry" : "core.Registry", "IRegistry");
      Nh = z(c ? "core.IRegistryWritable" : "core.WritableRegistry", "IRegistryWritable");
      qu = rB.e("net.minecraft.network.chat.RemoteChatSession");
      qQ = z("network.syncher.DataWatcher", "DataWatcher");
      if (qQ == null) {
         qQ = z("network.syncher.SynchedEntityData", "DataWatcher");
      }

      Nw = z("network.protocol.game.ClientboundSetEntityDataPacket", "PacketPlayOutEntityMetadata");
      qn = Ry.C(qQ, 0);
      K = Ry.C(qQ, 1);
   }

   private static void V() {
      try {
         if (Nq.i(zZ.V_1_20_5)) {
            NA = rB.t(f, Nx, 0).get((Object)null);
         }
      } catch (IllegalAccessException var1) {
         var1.printStackTrace();
      }

   }

   public static void j() {
      Nq = oS.J().g().w();
      NW = Nq.i(zZ.V_1_19);
      q2 = Nq.i(zZ.V_1_17);
      NY = Nq.i(zZ.V_1_12);
      R();
      M();
      u();
      N();
      V();
   }

   public static Class<?> z(String var0, String var1) {
      long var2 = ab ^ 7022448759864L;
      return q2 ? rB.e("net.minecraft." + var0) : rB.e(N1 + var1);
   }

   public static boolean P() {
      Object var0 = j(Bukkit.getServer());
      if (var0 != null && Nm != null) {
         try {
            return (Boolean)Nm.invoke(var0);
         } catch (InvocationTargetException | IllegalAccessException var2) {
            Nm = null;
            return false;
         }
      } else {
         return false;
      }
   }

   public static Object j(Server var0) {
      if (qC == null) {
         try {
            Field var1 = rB.t(S, N6, 0);
            if (var1 == null) {
               qC = rB.t(N6, N6, 0).get((Object)null);
            } else {
               qC = var1.get(var0);
            }
         } catch (IllegalAccessException var2) {
            var2.printStackTrace();
         }
      }

      return qC;
   }

   public static Object o() {
      if (NQ == null) {
         try {
            NQ = rB.t(N6, qv, 0).get(j(Bukkit.getServer()));
         } catch (IllegalAccessException var1) {
            var1.printStackTrace();
         }
      }

      return NQ;
   }

   public static Class<?> l(String var0) {
      return rB.e(m + var0);
   }

   public static Class<?> A(String var0) {
      long var1 = ab ^ 13416267325847L;
      return rB.e("io.netty." + var0);
   }

   public static Entity O(Object var0) {
      Object var1 = null;

      try {
         var1 = NU.invoke(var0);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         var3.printStackTrace();
      }

      return (Entity)var1;
   }

   public static Object G(Player var0) {
      return qo.cast(var0);
   }

   public static Object I(Player var0) {
      Object var1 = G(var0);

      try {
         return qm.invoke(var1);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static Object Q(Player var0) {
      Object var1 = I(var0);
      if (var1 == null) {
         return null;
      } else {
         vJ var2 = new vJ(var1, a);
         return b != null ? var2.Q(0, b) : var2.Q(0, qT);
      }
   }

   public static Object y(Player var0) {
      Object var1 = Q(var0);
      if (var1 == null) {
         return null;
      } else {
         Class var2;
         if (E != null) {
            var2 = var1.getClass() == i ? i : E;
         } else {
            var2 = qT;
         }

         vJ var3 = new vJ(var1, var2);

         try {
            return var3.Q(0, qX);
         } catch (Exception var7) {
            try {
               var1 = var3.s(0, qT);
               var3 = new vJ(var1, qT);
               return var3.Q(0, qX);
            } catch (Exception var6) {
               var7.printStackTrace();
               return null;
            }
         }
      }
   }

   public static Object L(Player var0) {
      Object var1 = y(var0);
      if (var1 == null) {
         return null;
      } else {
         vJ var2 = new vJ(var1, qX);
         return var2.Q(0, k);
      }
   }

   public static Object W(Object var0) {
      try {
         Object var1 = Np.get(var0);
         Object var2 = qI.get(var1);
         return qi.get(var2);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static List<Object> T() {
      vJ var0 = new vJ(o());
      int var1 = 0;

      while(true) {
         try {
            List var2 = (List)var0.Q(var1, List.class);
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               Object var4 = var3.next();
               if (var4.getClass().isAssignableFrom(qX)) {
                  return var2;
               }
            }
         } catch (Exception var5) {
            return (List)var0.Q(1, List.class);
         }

         ++var1;
      }
   }

   private static Entity J(World var0, int var1) {
      long var2 = ab ^ 23715146236070L;
      if (var0 == null) {
         return null;
      } else {
         Entity var4 = (Entity)q4.getOrDefault(var1, (Object)null);
         if (var4 != null) {
            return var4;
         } else {
            try {
               Object var5 = t.invoke(var0);
               Object var6;
               if (oS.J().g().w().i(zZ.V_1_17)) {
                  vJ var7 = q ? new vJ(var5, q8) : new vJ(var5, q7);
                  Object var8;
                  if (G) {
                     var8 = var7.Q(0, qj);
                  } else {
                     Object var9 = var7.Q(0, qr);
                     vJ var10 = new vJ(var9);
                     var8 = var10.Q(0, q_);
                  }

                  var6 = ql.invoke(var8, var1);
               } else {
                  var6 = NZ.invoke(var5, var1);
               }

               if (var6 == null) {
                  return null;
               } else {
                  Entity var12 = O(var6);
                  q4.put(var1, var12);
                  return var12;
               }
            } catch (InvocationTargetException | IllegalAccessException var11) {
               throw new RuntimeException("Error while looking up entity by id " + var1 + " in " + var0, var11);
            }
         }
      }
   }

   public static Entity I(World var0, int var1) {
      if (var0 != null) {
         Entity var2 = J(var0, var1);
         if (var2 != null) {
            return var2;
         }
      }

      Iterator var5 = Bukkit.getWorlds().iterator();

      Entity var4;
      do {
         if (!var5.hasNext()) {
            return null;
         }

         World var3 = (World)var5.next();
         var4 = J(var3, var1);
      } while(var4 == null);

      return var4;
   }

   private static Object lambda$convertWorldServerDimensionToNMSNbt$0(Object var0) {
      return var0;
   }

   static {
      long var0 = ab ^ 56405360649062L;
      String var2 = Bukkit.getServer().getClass().getPackage().getName();

      String var3;
      try {
         var3 = var2.replace(".", ",").split(",")[3];
      } catch (Exception var5) {
         var3 = "";
      }

      qz = var3;
      N1 = "net.minecraft.server." + qz + ".";
      m = var2 + ".";
      G = false;
      q = true;
      q4 = (new MapMaker()).weakValues().makeMap();
   }
}
