package com.gmail.olexorus.themis;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Themis extends JavaPlugin implements Listener {
   public static final k g;
   private nU J;
   private boolean V;
   public static final String TO_SPIGOTMC_RESOURCE_STAFF = "Please note that I received permission to follow the premium obfuscation rules and use Zelix. At the time of writing this, that's confirmed on https://www.spigotmc.org/threads/approved-obfuscators.420746/. As far as I am aware, I'm following all the obfuscation rules. If there are any problems, please contact me instead of deleting Themis!";
   private static Themis x;
   private static boolean B;
   private static boolean G;
   private static int[] u;
   private static final long a = kt.a(-6615365719026147890L, 3403746804801088719L, MethodHandles.lookup().lookupClass()).a(103586770225369L);
   private static final String[] b;
   private static final String[] c;
   private static final Map d = new HashMap(13);

   public final double s(Object[] param1) {
      // $FF: Couldn't be decompiled
   }

   public void onLoad() {
      long var1 = a ^ 125360170899983L;
      long var3 = var1 ^ 118237481317585L;
      k var10000 = g;
      x = this;
      System.setProperty(true.l<invokedynamic>(5893, 2787926027146506998L ^ var1), true.l<invokedynamic>(32474, 3121038093207558952L ^ var1));

      try {
         N1.M(new Object[]{var3});
      } catch (IllegalStateException var6) {
         this.V = true;
         throw var6;
      }

      oS.n(Gk.T((Plugin)this));
      oS.J().R();
   }

   public void onEnable() {
      // $FF: Couldn't be decompiled
   }

   public void onDisable() {
      // $FF: Couldn't be decompiled
   }

   private static final void F() {
      long var0 = a ^ 41191648867151L;
      long var2 = var0 ^ 72710790592491L;
      Jh.z(new Object[]{var2});
   }

   public static final Themis k(Object[] var0) {
      return x;
   }

   public static final boolean N(Object[] var0) {
      return B;
   }

   public static final boolean h(Object[] var0) {
      return G;
   }

   static {
      Q((int[])null);
      long var0 = a ^ 5651216557847L;
      Cipher var2;
      Cipher var10000 = var2 = Cipher.getInstance("DES/CBC/PKCS5Padding");
      SecretKeyFactory var10002 = SecretKeyFactory.getInstance("DES");
      byte[] var10003 = new byte[]{(byte)((int)(var0 >>> 56)), 0, 0, 0, 0, 0, 0, 0};

      for(int var3 = 1; var3 < 8; ++var3) {
         var10003[var3] = (byte)((int)(var0 << var3 * 8 >>> 56));
      }

      var10000.init(2, var10002.generateSecret(new DESKeySpec(var10003)), new IvParameterSpec(new byte[8]));
      String[] var9 = new String[17];
      int var7 = 0;
      String var6 = "4ëª\u001c\u0094'\u009fÝês9Ye\fC\u0087;á\u0018½*}¯Ø¥Ë±\u0085\rl\u001cnµ\u0096\u008a<ð>\u008fn\u0010\u008eÇ¾$7\u0098Ùv°ØÔQeÛ@_\u0010©<\u0000~£Ô¯íú|Êß*ìê©\u0010Õ\n/5¿\u008f±Ê\u009dÃ\u0098[¥~E\u0098\u0010zå«¸:\u001cý\u0083l\u0093\u008a2\u0014¹]\u0097\u0018Ù\u001a\u008d²\u008eBa\u0098\u0087\u0080®\u0081¢Xâø\trï\u0019ØçÁ{PP~]\u0092q?§H½\"ÙW8óg 7\u001büa\u0019!YÎìOáâ7\u0099HW\n\u0011\u008aqZZîÕDÁyä\u009fG[½%v#Þª\u001e\u009b÷\u0093\u001cVÅò½\u0080\u0014ôõÀ\u0001\u008e´R¾u8p¬±µ\u000eª\u0010Ií8ÇÀª8\u0086.\u0084\u008bJ\u008f6Ôó\u0010\u0089\u0005ý?ë\u009dÁ^èµ\u0011®nýkuh\u008fÝW\u0081ã\u0003â]§óLW\u009ab\rïW<YÞ\u0017Ã|w\u001a\u009eí\u008d\u0018\u000bþò\u0089é\u000b\\\u0011s%¬\u0085yÆ\u001f\tV»\u0081\u0007\u0089rÐ\u009d&B\u0084-AgR\u009bw¶{\u0015P`ê\u0094\u000e!pë¼m@Î o\u0095+ÖÉÄ!ªæèv\u0004{`MY\u0093ÐzD³\"Z1u\u009f`t?1R·\u0002_nåWÜ\u0018\t9\u008aY á\u0080êÆ\u0088T\u0002tûG\u0006Å·\u0015%f\u0096A\u0013\u0091ÿ.¶8rÈ\u0094o°aVÉ\u0095B²ñ\u0086\u001eÞb\n@6B(üvT&\u0007':\r\u001e\u001c£RPý\f\u001a`|`¶´¶\u0094\u001bÔ:¶V7\u008b\u0086\u0081ÑF\u0010â\u000fo\u0006\u0007+fH©\u0093í.\u0093h\n\u0094\u0018\u00ad¥\u008bÿ§Ìsè+s\u0000\u0007×\u0010<¾\u0003AwÞqõ\u009a\u0083\u0010\u001fSÀ@\u0088úï\u0091¡\u008a)þÀ\u009b\f\u008eXc\u0016êý§ý\u009aï×\u0088@Á\u0015\u0090èxE£(®.)Óm\u008e@0 '\u0097ÛÂá\u0088T\u0005\u0017!û5¥i¼Tí\u0090¥\u0094\n^\u0080\u0014rÝtü\u009b\u0015=\u0010\u009fØ\bli WI\u009cÌov®ûR\u0087\f>,J¥\u0016:(\u000eRÍ\\";
      int var8 = "4ëª\u001c\u0094'\u009fÝês9Ye\fC\u0087;á\u0018½*}¯Ø¥Ë±\u0085\rl\u001cnµ\u0096\u008a<ð>\u008fn\u0010\u008eÇ¾$7\u0098Ùv°ØÔQeÛ@_\u0010©<\u0000~£Ô¯íú|Êß*ìê©\u0010Õ\n/5¿\u008f±Ê\u009dÃ\u0098[¥~E\u0098\u0010zå«¸:\u001cý\u0083l\u0093\u008a2\u0014¹]\u0097\u0018Ù\u001a\u008d²\u008eBa\u0098\u0087\u0080®\u0081¢Xâø\trï\u0019ØçÁ{PP~]\u0092q?§H½\"ÙW8óg 7\u001büa\u0019!YÎìOáâ7\u0099HW\n\u0011\u008aqZZîÕDÁyä\u009fG[½%v#Þª\u001e\u009b÷\u0093\u001cVÅò½\u0080\u0014ôõÀ\u0001\u008e´R¾u8p¬±µ\u000eª\u0010Ií8ÇÀª8\u0086.\u0084\u008bJ\u008f6Ôó\u0010\u0089\u0005ý?ë\u009dÁ^èµ\u0011®nýkuh\u008fÝW\u0081ã\u0003â]§óLW\u009ab\rïW<YÞ\u0017Ã|w\u001a\u009eí\u008d\u0018\u000bþò\u0089é\u000b\\\u0011s%¬\u0085yÆ\u001f\tV»\u0081\u0007\u0089rÐ\u009d&B\u0084-AgR\u009bw¶{\u0015P`ê\u0094\u000e!pë¼m@Î o\u0095+ÖÉÄ!ªæèv\u0004{`MY\u0093ÐzD³\"Z1u\u009f`t?1R·\u0002_nåWÜ\u0018\t9\u008aY á\u0080êÆ\u0088T\u0002tûG\u0006Å·\u0015%f\u0096A\u0013\u0091ÿ.¶8rÈ\u0094o°aVÉ\u0095B²ñ\u0086\u001eÞb\n@6B(üvT&\u0007':\r\u001e\u001c£RPý\f\u001a`|`¶´¶\u0094\u001bÔ:¶V7\u008b\u0086\u0081ÑF\u0010â\u000fo\u0006\u0007+fH©\u0093í.\u0093h\n\u0094\u0018\u00ad¥\u008bÿ§Ìsè+s\u0000\u0007×\u0010<¾\u0003AwÞqõ\u009a\u0083\u0010\u001fSÀ@\u0088úï\u0091¡\u008a)þÀ\u009b\f\u008eXc\u0016êý§ý\u009aï×\u0088@Á\u0015\u0090èxE£(®.)Óm\u008e@0 '\u0097ÛÂá\u0088T\u0005\u0017!û5¥i¼Tí\u0090¥\u0094\n^\u0080\u0014rÝtü\u009b\u0015=\u0010\u009fØ\bli WI\u009cÌov®ûR\u0087\f>,J¥\u0016:(\u000eRÍ\\".length();
      char var5 = '(';
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
                  c = new String[17];
                  g = new k((MH)null);
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

               var6 = "e\u001dK3¦l#ª\u0086×k\u009b\u007fZ\u0006\u0000\u0010\u008a:ÑðR\u0081\u0015Â*©½r¯Å\u008a9";
               var8 = "e\u001dK3¦l#ª\u0086×k\u009b\u007fZ\u0006\u0000\u0010\u008a:ÑðR\u0081\u0015Â*©½r¯Å\u008a9".length();
               var5 = 16;
               var4 = -1;
            }

            ++var4;
            var11 = var6.substring(var4, var4 + var5);
            var10001 = 0;
         }
      }
   }

   public static void Q(int[] var0) {
      u = var0;
   }

   public static int[] t() {
      return u;
   }

   private static IllegalStateException a(IllegalStateException var0) {
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
      int var5 = var0 ^ (int)(var1 & 32767L) ^ 880;
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
            throw new RuntimeException("com/gmail/olexorus/themis/Themis", var10);
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
         throw new RuntimeException("com/gmail/olexorus/themis/Themis" + " : " + var1 + " : " + var2.toString(), var5);
      }
   }
}
