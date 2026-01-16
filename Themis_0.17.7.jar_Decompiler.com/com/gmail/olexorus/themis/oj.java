package com.gmail.olexorus.themis;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public final class Oj implements B7<Rc, DataInputStream> {
   public static final Oj X = new Oj();
   private static final Map<Ay<?>, CK> y = new HashMap(16);
   private static final Map<Ay<?>, JG> f = new HashMap(16);
   private static final long a = kt.a(1211317412650016485L, 4918639733920088814L, MethodHandles.lookup().lookupClass()).a(55417662350789L);

   public Rc A(Gq var1, DataInputStream var2, boolean var3) {
      Ay var4 = gq.O.O(var1, var2);
      if (var3) {
         int var5 = var2.readUnsignedShort();
         var2.skipBytes(var5);
      }

      Object var6;
      if (var4 == Ay.K) {
         var6 = new mC(var2, var1, Oj::lambda$deserializeTag$0, (S)null);
      } else if (var4 == Ay.P) {
         var6 = new mD(var2, var1, Oj::lambda$deserializeTag$1, (S)null);
      } else {
         var6 = gq.O.R(var1, var2, var4);
      }

      return (Rc)var6;
   }

   private static void K(Rc var0) {
      long var1 = a ^ 30363439225419L;
      if (var0 != null) {
         if (var0 instanceof Iterator && ((Iterator)var0).hasNext()) {
            throw new IllegalStateException("Previous nbt has not been read completely");
         }
      }
   }

   public static void T(byte[] var0, int var1, int var2) {
      var0[var2] = (byte)(var1 >>> 24 & 255);
      var0[var2 + 1] = (byte)(var1 >>> 16 & 255);
      var0[var2 + 2] = (byte)(var1 >>> 8 & 255);
      var0[var2 + 3] = (byte)(var1 & 255);
   }

   private static byte[] lambda$static$25(Gq var0, DataInput var1) {
      var0.e(24);
      int var2 = var1.readInt();
      var0.e(var2 * 8);
      var0.r(var2 * 8);
      byte[] var3 = new byte[4 + var2 * 8];
      T(var3, var2, 0);
      var1.readFully(var3, 4, var2 * 8);
      return var3;
   }

   private static byte[] lambda$static$24(Gq var0, DataInput var1) {
      var0.e(24);
      int var2 = var1.readInt();
      var0.e(var2 * 4);
      var0.r(var2 * 4);
      byte[] var3 = new byte[4 + var2 * 4];
      T(var3, var2, 0);
      var1.readFully(var3, 4, var2 * 4);
      return var3;
   }

   private static byte[] lambda$static$23(Gq var0, DataInput var1) {
      var0.s();

      byte[] var18;
      try {
         var0.e(48);
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();

         try {
            DataOutputStream var3 = new DataOutputStream(var2);

            try {
               HashSet var4 = new HashSet();

               byte var5;
               while((var5 = var1.readByte()) != 0) {
                  var3.write(var5);
                  String var6 = gq.F(var0, var1);
                  var3.writeUTF(var6);
                  var3.write(((JG)f.get(gq.O.v.get(Integer.valueOf(var5)))).w(var0, var1));
                  if (var4.add(var6)) {
                     var0.e(36);
                  }
               }

               var3.write(var5);
               var18 = var2.toByteArray();
            } catch (Throwable var15) {
               try {
                  var3.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }

               throw var15;
            }

            var3.close();
         } catch (Throwable var16) {
            try {
               var2.close();
            } catch (Throwable var13) {
               var16.addSuppressed(var13);
            }

            throw var16;
         }

         var2.close();
      } finally {
         var0.d();
      }

      return var18;
   }

   private static byte[] lambda$static$22(Gq var0, DataInput var1) {
      var0.s();

      byte[] var20;
      try {
         var0.e(36);
         byte var2 = var1.readByte();
         Ay var3 = (Ay)gq.O.v.get(Integer.valueOf(var2));
         int var4 = var1.readInt();
         var0.e(var4 * 4);
         ByteArrayOutputStream var5 = new ByteArrayOutputStream();

         try {
            DataOutputStream var6 = new DataOutputStream(var5);

            try {
               var6.write(var2);
               var6.writeInt(var4);
               int var7 = 0;

               while(true) {
                  if (var7 >= var4) {
                     var20 = var5.toByteArray();
                     break;
                  }

                  var6.write(((JG)f.get(var3)).w(var0, var1));
                  ++var7;
               }
            } catch (Throwable var17) {
               try {
                  var6.close();
               } catch (Throwable var16) {
                  var17.addSuppressed(var16);
               }

               throw var17;
            }

            var6.close();
         } catch (Throwable var18) {
            try {
               var5.close();
            } catch (Throwable var15) {
               var18.addSuppressed(var15);
            }

            throw var18;
         }

         var5.close();
      } finally {
         var0.d();
      }

      return var20;
   }

   private static byte[] lambda$static$21(Gq var0, DataInput var1) {
      var0.e(36);
      String var2 = var1.readUTF();
      var0.e(var2.length() * 2);
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();

      byte[] var5;
      try {
         DataOutputStream var4 = new DataOutputStream(var3);

         try {
            var4.writeUTF(var2);
            var5 = var3.toByteArray();
         } catch (Throwable var9) {
            try {
               var4.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         var4.close();
      } catch (Throwable var10) {
         try {
            var3.close();
         } catch (Throwable var7) {
            var10.addSuppressed(var7);
         }

         throw var10;
      }

      var3.close();
      return var5;
   }

   private static byte[] lambda$static$20(Gq var0, DataInput var1) {
      long var2 = a ^ 16229250940484L;
      var0.e(24);
      int var4 = var1.readInt();
      if (var4 >= 16777216) {
         throw new IllegalArgumentException("Byte array length is too large: " + var4);
      } else {
         var0.e(var4 * 1);
         var0.r(var4 * 1);
         byte[] var5 = new byte[4 + var4];
         T(var5, var4, 0);
         var1.readFully(var5, 4, var4);
         return var5;
      }
   }

   private static byte[] lambda$static$19(Gq var0, DataInput var1) {
      var0.e(16);
      byte[] var2 = new byte[8];
      var1.readFully(var2);
      return var2;
   }

   private static byte[] lambda$static$18(Gq var0, DataInput var1) {
      var0.e(12);
      byte[] var2 = new byte[4];
      var1.readFully(var2);
      return var2;
   }

   private static byte[] lambda$static$17(Gq var0, DataInput var1) {
      var0.e(16);
      byte[] var2 = new byte[8];
      var1.readFully(var2);
      return var2;
   }

   private static byte[] lambda$static$16(Gq var0, DataInput var1) {
      var0.e(12);
      byte[] var2 = new byte[4];
      var1.readFully(var2);
      return var2;
   }

   private static byte[] lambda$static$15(Gq var0, DataInput var1) {
      var0.e(10);
      return new byte[]{var1.readByte(), var1.readByte()};
   }

   private static byte[] lambda$static$14(Gq var0, DataInput var1) {
      var0.e(9);
      return new byte[]{var1.readByte()};
   }

   private static void lambda$static$13(Gq var0, DataInput var1) {
      var0.e(24);
      int var2 = var1.readInt();
      var0.e(var2 * 8);
      var0.r(var2 * 8);
      var1.skipBytes(var2 * 8);
   }

   private static void lambda$static$12(Gq var0, DataInput var1) {
      var0.e(24);
      int var2 = var1.readInt();
      var0.e(var2 * 4);
      var0.r(var2 * 4);
      var1.skipBytes(var2 * 4);
   }

   private static void lambda$static$11(Gq var0, DataInput var1) {
      var0.s();

      try {
         var0.e(48);

         Ay var3;
         for(HashSet var2 = new HashSet(); (var3 = gq.O.O(var0, var1)) != Ay.Q; ((CK)y.get(var3)).X(var0, var1)) {
            String var4 = gq.F(var0, var1);
            if (var2.add(var4)) {
               var0.e(36);
            }
         }
      } finally {
         var0.d();
      }

   }

   private static void lambda$static$10(Gq var0, DataInput var1) {
      var0.s();

      try {
         var0.e(36);
         Ay var2 = gq.O.O(var0, var1);
         int var3 = var1.readInt();
         var0.e(var3 * 4);

         for(int var4 = 0; var4 < var3; ++var4) {
            ((CK)y.get(var2)).X(var0, var1);
         }
      } finally {
         var0.d();
      }

   }

   private static void lambda$static$9(Gq var0, DataInput var1) {
      var0.e(36);
      int var2 = var1.readUnsignedShort();
      var0.e(var2 * 2);
      var1.skipBytes(var2);
   }

   private static void lambda$static$8(Gq var0, DataInput var1) {
      var0.e(24);
      int var2 = var1.readInt();
      var0.e(var2 * 1);
      var0.r(var2 * 1);
      var1.skipBytes(var2);
   }

   private static void lambda$static$7(Gq var0, DataInput var1) {
      var0.e(16);
      var1.skipBytes(8);
   }

   private static void lambda$static$6(Gq var0, DataInput var1) {
      var0.e(12);
      var1.skipBytes(4);
   }

   private static void lambda$static$5(Gq var0, DataInput var1) {
      var0.e(16);
      var1.skipBytes(8);
   }

   private static void lambda$static$4(Gq var0, DataInput var1) {
      var0.e(12);
      var1.skipBytes(4);
   }

   private static void lambda$static$3(Gq var0, DataInput var1) {
      var0.e(10);
      var1.skipBytes(2);
   }

   private static void lambda$static$2(Gq var0, DataInput var1) {
      var0.e(9);
      var1.skipBytes(1);
   }

   private static void lambda$deserializeTag$1() {
   }

   private static void lambda$deserializeTag$0() {
   }

   static void z(Rc var0) {
      K(var0);
   }

   static Map Q() {
      return y;
   }

   static Map W() {
      return f;
   }

   static {
      y.put(Ay.I, Oj::lambda$static$2);
      y.put(Ay.o, Oj::lambda$static$3);
      y.put(Ay.X, Oj::lambda$static$4);
      y.put(Ay.C, Oj::lambda$static$5);
      y.put(Ay.W, Oj::lambda$static$6);
      y.put(Ay.h, Oj::lambda$static$7);
      y.put(Ay.j, Oj::lambda$static$8);
      y.put(Ay.m, Oj::lambda$static$9);
      y.put(Ay.P, Oj::lambda$static$10);
      y.put(Ay.K, Oj::lambda$static$11);
      y.put(Ay.R, Oj::lambda$static$12);
      y.put(Ay.p, Oj::lambda$static$13);
      f.put(Ay.I, Oj::lambda$static$14);
      f.put(Ay.o, Oj::lambda$static$15);
      f.put(Ay.X, Oj::lambda$static$16);
      f.put(Ay.C, Oj::lambda$static$17);
      f.put(Ay.W, Oj::lambda$static$18);
      f.put(Ay.h, Oj::lambda$static$19);
      f.put(Ay.j, Oj::lambda$static$20);
      f.put(Ay.m, Oj::lambda$static$21);
      f.put(Ay.P, Oj::lambda$static$22);
      f.put(Ay.K, Oj::lambda$static$23);
      f.put(Ay.R, Oj::lambda$static$24);
      f.put(Ay.p, Oj::lambda$static$25);
   }
}
