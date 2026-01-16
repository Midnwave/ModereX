package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.Vector;

public final class mu {
   private static final Class<?> n;
   private static final Class<?> l;
   private static final Class<?> m;
   private static final Class<?> k;
   private static final Class<?> u;
   private static final Class<?> h;
   private static final Constructor<? extends Object> v;
   private static final Method o;
   private static final Method C;
   private static final Method H;
   private static final Method K;
   private static final Method X;
   private static final Method I;
   private static final Method Z;
   private static int J;

   public static final Object s(Object[] var0) {
      int var2 = (Integer)var0[0];
      int var3 = (Integer)var0[1];
      int var1 = (Integer)var0[2];
      Constructor var10000 = v;
      Object[] var4 = new Object[]{var2, var3, var1};
      return var10000.newInstance(var4);
   }

   public static final Object O(Object[] var0) {
      World var1 = (World)var0[0];
      return o.invoke(var1);
   }

   public static final Object y(Object[] var0) {
      Object var2 = (Object)var0[0];
      Object var1 = (Object)var0[1];
      Method var10000 = C;
      Object[] var3 = new Object[]{var1};
      return var10000.invoke(var2, var3);
   }

   public static final float H(Object[] var0) {
      Object var1 = (Object)var0[0];
      Object var3 = (Object)var0[1];
      Object var2 = (Object)var0[2];
      Method var10000 = H;
      Object[] var4 = new Object[]{var3, var2};
      return (Float)var10000.invoke(var1, var4);
   }

   public static final Object L(Object[] var0) {
      Object var3 = (Object)var0[0];
      Object var1 = (Object)var0[1];
      Object var2 = (Object)var0[2];
      Method var10000 = K;
      Object[] var4 = new Object[]{var1, var2};
      return var10000.invoke(var3, var4);
   }

   public static final Vector n(Object[] var0) {
      Object var1 = (Object)var0[0];
      return new Vector((Double)X.invoke(var1), (Double)I.invoke(var1), (Double)Z.invoke(var1));
   }

   static {
      long var10 = kt.a(5645060825549050889L, -4641098223977924222L, MethodHandles.lookup().lookupClass()).a(136630990025473L) ^ 8340981154963L;
      if (T() == 0) {
         k(45);
      }

      Cipher var1;
      Cipher var10000 = var1 = Cipher.getInstance("DES/CBC/PKCS5Padding");
      SecretKeyFactory var10002 = SecretKeyFactory.getInstance("DES");
      byte[] var10003 = new byte[]{(byte)((int)(var10 >>> 56)), 0, 0, 0, 0, 0, 0, 0};

      for(int var2 = 1; var2 < 8; ++var2) {
         var10003[var2] = (byte)((int)(var10 << var2 * 8 >>> 56));
      }

      var10000.init(2, var10002.generateSecret(new DESKeySpec(var10003)), new IvParameterSpec(new byte[8]));
      String[] var8 = new String[13];
      int var6 = 0;
      String var5 = "H\u007fSªk\u0089'Â\b\u008dNÿ;ßòÍ9(µ\u0011\u0017Î\u009bv`º\u0003\\E\u008d¿\u0088{ôÜM\u001dÑÃ\u008fÂ\u0015¸A?ü?\u0012¬÷L\u0082\r\u008disxç\u0010GòÈK\u001b¸\fhx\u009bÅu\u000bqút µ\u0011\u0017Î\u009bv`ºHäÌ·\u0097¤iÞ\u0010Õ~îFñ5Õ®\u0002\u00062\u0006\u008fre µ\u0011\u0017Î\u009bv`ºHäÌ·\u0097¤iÞ±ÜÝgÇc;:\u001cªC±ñÅhï(µ\u0011\u0017Î\u009bv`ºHäÌ·\u0097¤iÞ\u0010Õ~îFñ5Õ\u0006\u0095;,\u0093\r³kéé öÕ\u0085\u0094q\b\u0084Wã\u008e\u0084\u008bhu\b¯*q\u0012çS×5\u0010æ(åá\u0099·äJ\u001bäÎü\u0097 ®g\u0010\rY@\u001cwÇp\u000b\u0086rÙ\u0085ºô¼Õ";
      int var7 = "H\u007fSªk\u0089'Â\b\u008dNÿ;ßòÍ9(µ\u0011\u0017Î\u009bv`º\u0003\\E\u008d¿\u0088{ôÜM\u001dÑÃ\u008fÂ\u0015¸A?ü?\u0012¬÷L\u0082\r\u008disxç\u0010GòÈK\u001b¸\fhx\u009bÅu\u000bqút µ\u0011\u0017Î\u009bv`ºHäÌ·\u0097¤iÞ\u0010Õ~îFñ5Õ®\u0002\u00062\u0006\u008fre µ\u0011\u0017Î\u009bv`ºHäÌ·\u0097¤iÞ±ÜÝgÇc;:\u001cªC±ñÅhï(µ\u0011\u0017Î\u009bv`ºHäÌ·\u0097¤iÞ\u0010Õ~îFñ5Õ\u0006\u0095;,\u0093\r³kéé öÕ\u0085\u0094q\b\u0084Wã\u008e\u0084\u008bhu\b¯*q\u0012çS×5\u0010æ(åá\u0099·äJ\u001bäÎü\u0097 ®g\u0010\rY@\u001cwÇp\u000b\u0086rÙ\u0085ºô¼Õ".length();
      char var4 = '\b';
      int var3 = -1;

      label99:
      while(true) {
         ++var3;
         String var19 = var5.substring(var3, var3 + var4);
         byte var10001 = -1;

         while(true) {
            byte[] var9 = var1.doFinal(var19.getBytes("ISO-8859-1"));
            String var24 = a(var9).intern();
            switch(var10001) {
            case 0:
               var8[var6++] = var24;
               if ((var3 += var4) >= var7) {
                  String[] var0 = var8;
                  n = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + var8[9]);
                  l = Class.forName(var8[2]);
                  m = Class.forName(var8[12]);
                  k = Class.forName(var8[6]);
                  u = Class.forName(var8[5]);
                  h = Class.forName(var8[4]);
                  Class var21 = l;
                  Class[] var13 = new Class[3];

                  String var22;
                  label86: {
                     try {
                        var13[0] = Integer.TYPE;
                        var13[1] = Integer.TYPE;
                        var13[2] = Integer.TYPE;
                        v = var21.getConstructor(var13);
                        o = n.getDeclaredMethod(var0[11]);
                        var21 = h;
                        if (Themis.g.f(new Object[0])) {
                           var22 = var0[1];
                           break label86;
                        }
                     } catch (RuntimeException var18) {
                        throw a(var18);
                     }

                     var22 = var8[3];
                  }

                  Class[] var12 = new Class[1];

                  label78: {
                     try {
                        var12[0] = l;
                        C = var21.getDeclaredMethod(var22, var12);
                        var21 = m;
                        if (Themis.g.f(new Object[0])) {
                           var22 = "a";
                           break label78;
                        }
                     } catch (RuntimeException var17) {
                        throw a(var17);
                     }

                     var22 = var8[10];
                  }

                  var12 = new Class[]{k, l};
                  H = var21.getDeclaredMethod(var22, var12);
                  var21 = m;
                  var22 = "c";
                  var12 = new Class[2];

                  label70: {
                     try {
                        var12[0] = k;
                        var12[1] = l;
                        K = var21.getDeclaredMethod(var22, var12);
                        var21 = u;
                        if (Themis.g.f(new Object[0])) {
                           var22 = "a";
                           break label70;
                        }
                     } catch (RuntimeException var16) {
                        throw a(var16);
                     }

                     var22 = var8[7];
                  }

                  label63: {
                     try {
                        X = var21.getDeclaredMethod(var22);
                        var21 = u;
                        if (Themis.g.f(new Object[0])) {
                           var22 = "b";
                           break label63;
                        }
                     } catch (RuntimeException var15) {
                        throw a(var15);
                     }

                     var22 = var8[0];
                  }

                  label56: {
                     try {
                        I = var21.getDeclaredMethod(var22);
                        var21 = u;
                        if (Themis.g.f(new Object[0])) {
                           var22 = "c";
                           break label56;
                        }
                     } catch (RuntimeException var14) {
                        throw a(var14);
                     }

                     var22 = var8[8];
                  }

                  Z = var21.getDeclaredMethod(var22);
                  return;
               }

               var4 = var5.charAt(var3);
               break;
            default:
               var8[var6++] = var24;
               if ((var3 += var4) < var7) {
                  var4 = var5.charAt(var3);
                  continue label99;
               }

               var5 = "Vè,\"Bùá\u008c\u009d¾4ñ´»û40µ\u0011\u0017Î\u009bv`ºHäÌ·\u0097¤iÞ\u0010Õ~îFñ5Õ@½\u0016DR0\u0088*æ¹Â¦ÃYÔìv$/Ç1ÚÎÎ";
               var7 = "Vè,\"Bùá\u008c\u009d¾4ñ´»û40µ\u0011\u0017Î\u009bv`ºHäÌ·\u0097¤iÞ\u0010Õ~îFñ5Õ@½\u0016DR0\u0088*æ¹Â¦ÃYÔìv$/Ç1ÚÎÎ".length();
               var4 = 16;
               var3 = -1;
            }

            ++var3;
            var19 = var5.substring(var3, var3 + var4);
            var10001 = 0;
         }
      }
   }

   public static void k(int var0) {
      J = var0;
   }

   public static int w() {
      return J;
   }

   public static int T() {
      int var0 = w();

      try {
         return var0 == 0 ? 42 : 0;
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
}
