package com.gmail.olexorus.themis.api;

import com.gmail.olexorus.themis.Cz;
import com.gmail.olexorus.themis.O4;
import com.gmail.olexorus.themis.kt;
import java.lang.invoke.MethodHandles;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public enum CheckType {
   private final String w;
   private final String j;
   BOAT_MOVEMENT,
   ELYTRA_FLIGHT,
   HORIZONTAL_MOVEMENT,
   ILLEGAL_PACKETS,
   PACKET_SPOOF,
   REACH,
   TICKRATE,
   VERTICAL_MOVEMENT;

   // $FF: synthetic field
   private static final O4 T;
   private static int L;

   private CheckType(String var3, String var4) {
      this.w = var3;
      this.j = var4;
   }

   public final String getCheckName() {
      return this.w;
   }

   public final String getDescription() {
      return this.j;
   }

   public static O4<CheckType> getEntries() {
      return T;
   }

   // $FF: synthetic method
   private static final CheckType[] U(Object[] var0) {
      CheckType[] var1 = new CheckType[]{BOAT_MOVEMENT, ELYTRA_FLIGHT, HORIZONTAL_MOVEMENT, ILLEGAL_PACKETS, PACKET_SPOOF, REACH, TICKRATE, VERTICAL_MOVEMENT};
      return var1;
   }

   static {
      long var10 = kt.a(5562002306685189089L, -3386858739285555479L, MethodHandles.lookup().lookupClass()).a(47339340556813L) ^ 59390810273121L;
      if (w() != 0) {
         T(97);
      }

      Cipher var1;
      Cipher var10000 = var1 = Cipher.getInstance("DES/CBC/PKCS5Padding");
      SecretKeyFactory var10002 = SecretKeyFactory.getInstance("DES");
      byte[] var10003 = new byte[]{(byte)((int)(var10 >>> 56)), 0, 0, 0, 0, 0, 0, 0};

      for(int var2 = 1; var2 < 8; ++var2) {
         var10003[var2] = (byte)((int)(var10 << var2 * 8 >>> 56));
      }

      var10000.init(2, var10002.generateSecret(new DESKeySpec(var10003)), new IvParameterSpec(new byte[8]));
      String[] var8 = new String[24];
      int var6 = 0;
      String var5 = "\u0084\u009b$_fÈ#\u0014\u0016\u00109¹h\u0084¶\u001c\u0010n\u001b÷\u0005Ñg> st\u009dÏìÕýQ\u0018÷\u009b¹\u0012'\u0094¢©\\÷ú$\u0006AÈd\u0090½\f¹&}¸ï\u0010Ç¶¨4u\u009d\tßu\u001a\u008f=´\u000b^7\u0018\u0018ï\u0000\u007fº\u0010\u0085!Yô0ËaTf\u009aëïPÑ¥RéÔ\u0010Ð\rS3ðcÚ\bÇÂ\u0081æ9\u0092ÜB\u0010\u0011îø\u0096\u0019æ3ëà¾\u008a\u0001\u009d¼op\b\u001b.hw>]e\u0015\u0010\u009909d\u0010ÒS]\u0005\u0091ù(fqp\u0019\u0010\u0017ó+ÞÄò\u001a1§\u000bpNè»å\u0019\u0018\u0097\u009cªúz1¹Â§wÛ\u0095 l\u0098\u009dÙµµ®gåÛÀ\u0010æÙòÚÖß§fÂ\u0090äU\u00858qU\u0010+)\u009b\u0082\u008d½}D~\u0083\u0084'Hpèd\u0010)\u008b]ÿ;b;5R=çÐ\u0082yàÆ\b§\u0019\u00871¹\u0018\u0003;\u0010ðÖóÔfö«ßd\u009b¾¼ÁA\r\u0082\u0018Ì.ø\u001eÂ¦ìã\u008d®<^\u007fTÐõ?\u0007\u0083ëJí\t \u0018ü²\tù\u0019ó\u009e\u008d5ë»Ç`E=\u0012H\u0011©ókö-\r\b\"¸\u0016K\u0003q¡\u0007\u0010¡\u0097pÞ\u0091c5+Wù4Ä¹éct\u0010xåÞ\u0016°êÒÝu¥+Ô×\u000e!{\u0010\u0096\u0088\u001a\u0087\u0095;Dþ\u0092C°Ð* ¦¯";
      int var7 = "\u0084\u009b$_fÈ#\u0014\u0016\u00109¹h\u0084¶\u001c\u0010n\u001b÷\u0005Ñg> st\u009dÏìÕýQ\u0018÷\u009b¹\u0012'\u0094¢©\\÷ú$\u0006AÈd\u0090½\f¹&}¸ï\u0010Ç¶¨4u\u009d\tßu\u001a\u008f=´\u000b^7\u0018\u0018ï\u0000\u007fº\u0010\u0085!Yô0ËaTf\u009aëïPÑ¥RéÔ\u0010Ð\rS3ðcÚ\bÇÂ\u0081æ9\u0092ÜB\u0010\u0011îø\u0096\u0019æ3ëà¾\u008a\u0001\u009d¼op\b\u001b.hw>]e\u0015\u0010\u009909d\u0010ÒS]\u0005\u0091ù(fqp\u0019\u0010\u0017ó+ÞÄò\u001a1§\u000bpNè»å\u0019\u0018\u0097\u009cªúz1¹Â§wÛ\u0095 l\u0098\u009dÙµµ®gåÛÀ\u0010æÙòÚÖß§fÂ\u0090äU\u00858qU\u0010+)\u009b\u0082\u008d½}D~\u0083\u0084'Hpèd\u0010)\u008b]ÿ;b;5R=çÐ\u0082yàÆ\b§\u0019\u00871¹\u0018\u0003;\u0010ðÖóÔfö«ßd\u009b¾¼ÁA\r\u0082\u0018Ì.ø\u001eÂ¦ìã\u008d®<^\u007fTÐõ?\u0007\u0083ëJí\t \u0018ü²\tù\u0019ó\u009e\u008d5ë»Ç`E=\u0012H\u0011©ókö-\r\b\"¸\u0016K\u0003q¡\u0007\u0010¡\u0097pÞ\u0091c5+Wù4Ä¹éct\u0010xåÞ\u0016°êÒÝu¥+Ô×\u000e!{\u0010\u0096\u0088\u001a\u0087\u0095;Dþ\u0092C°Ð* ¦¯".length();
      char var4 = 16;
      int var3 = -1;

      label31:
      while(true) {
         ++var3;
         String var12 = var5.substring(var3, var3 + var4);
         byte var10001 = -1;

         while(true) {
            byte[] var9 = var1.doFinal(var12.getBytes("ISO-8859-1"));
            String var16 = a(var9).intern();
            switch(var10001) {
            case 0:
               var8[var6++] = var16;
               if ((var3 += var4) >= var7) {
                  BOAT_MOVEMENT = new CheckType(var8[5], 0, var8[0], var8[9]);
                  ELYTRA_FLIGHT = new CheckType(var8[23], 1, var8[13], var8[1]);
                  HORIZONTAL_MOVEMENT = new CheckType(var8[16], 2, var8[10], var8[14]);
                  ILLEGAL_PACKETS = new CheckType(var8[21], 3, var8[20], var8[6]);
                  PACKET_SPOOF = new CheckType(var8[19], 4, var8[8], var8[11]);
                  REACH = new CheckType(var8[22], 5, var8[18], var8[7]);
                  TICKRATE = new CheckType(var8[3], 6, var8[12], var8[15]);
                  VERTICAL_MOVEMENT = new CheckType(var8[2], 7, var8[4], var8[17]);
                  T = Cz.x((Enum[])i);
                  return;
               }

               var4 = var5.charAt(var3);
               break;
            default:
               var8[var6++] = var16;
               if ((var3 += var4) < var7) {
                  var4 = var5.charAt(var3);
                  continue label31;
               }

               var5 = "ö7Þ\u009eÅ\u0018Í%\u0010ZÏ|`\u000br½n\u000baBî¤]¨|";
               var7 = "ö7Þ\u009eÅ\u0018Í%\u0010ZÏ|`\u000br½n\u000baBî¤]¨|".length();
               var4 = '\b';
               var3 = -1;
            }

            ++var3;
            var12 = var5.substring(var3, var3 + var4);
            var10001 = 0;
         }
      }
   }

   public static void T(int var0) {
      L = var0;
   }

   public static int d() {
      return L;
   }

   public static int w() {
      int var0 = d();

      try {
         return var0 == 0 ? 77 : 0;
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
