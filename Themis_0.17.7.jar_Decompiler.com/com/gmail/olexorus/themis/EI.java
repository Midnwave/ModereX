package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ei<C extends om> {
   private final wi T;
   private Map<String, u1> z;
   private Map<Class, String> U;
   private static final long b = kt.a(4635916448562087247L, -4344794771496817083L, MethodHandles.lookup().lookupClass()).a(165128426980716L);

   public Ei(wi var1) {
      long var2 = b ^ 47183694115324L;
      super();
      this.z = new HashMap();
      this.U = new HashMap();
      this.T = var1;
      this.F("empty", Collections.emptyList());
      this.F("nothing", Collections.emptyList());
      this.F("timeunits", Arrays.asList("minutes", "hours", "days", "weeks", "months", "years"));
      this.v("range", Ei::lambda$new$0);
   }

   public u1 n(String var1, u1<C> var2) {
      return (u1)this.z.put(s(var1), var2);
   }

   public u1 v(String var1, Al<C> var2) {
      return (u1)this.z.put(s(var1), var2);
   }

   public u1 F(String var1, Collection<String> var2) {
      return this.v(var1, Ei::lambda$registerStaticCompletion$1);
   }

   public void Z(String var1, Class... var2) {
      long var3 = b ^ 64738567391321L;
      var1 = s(var1);
      u1 var5 = (u1)this.z.get(var1);
      if (var5 == null) {
         throw new IllegalStateException("Completion not registered for " + var1);
      } else {
         Class[] var6 = var2;
         int var7 = var2.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Class var9 = var6[var8];
            this.U.put(var9, var1);
         }

      }
   }

   private static String s(String var0) {
      return (var0.startsWith("@") ? "" : "@") + var0.toLowerCase(Locale.ENGLISH);
   }

   List<String> o(Bd var1, bJ var2, String[] var3, boolean var4) {
      long var5 = b ^ 118801787929408L;
      String[] var7 = nQ.J.split(var1.N);
      int var8 = var3.length - 1;
      String var9 = var8 < var7.length ? var7[var8] : null;
      if (var9 == null || var9.isEmpty() || "*".equals(var9)) {
         var9 = this.p(var1, var3);
      }

      if (var9 == null && var7.length > 0) {
         String var10 = var7[var7.length - 1];
         if (var10.startsWith("repeat@")) {
            var9 = var10;
         } else if (var8 >= var7.length && var1.z[var1.z.length - 1].B) {
            var9 = var10;
         }
      }

      return var9 == null ? Collections.emptyList() : this.n(var1, var2, var9, var3, var4);
   }

   String p(Bd var1, String[] var2) {
      long var3 = b ^ 92756970185002L;
      int var5 = 0;
      AM[] var6 = var1.z;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         AM var9 = var6[var8];
         if (var9.X()) {
            ++var5;
            if (var5 == var2.length) {
               for(Class var10 = var9.g(); var10 != null; var10 = var10.getSuperclass()) {
                  String var11 = (String)this.U.get(var10);
                  if (var11 != null) {
                     return var11;
                  }
               }

               if (var9.g().isEnum()) {
                  bO var12 = wi.N();
                  var12.j = Oa.I(var9.g());
                  return "@__defaultenum__";
               }
               break;
            }
         }
      }

      return null;
   }

   List<String> n(Bd var1, bJ var2, String var3, String[] var4, boolean var5) {
      long var6 = b ^ 221732194221L;
      if ("@__defaultenum__".equals(var3)) {
         bO var23 = wi.N();
         return var23.j;
      } else {
         boolean var8 = var3.startsWith("repeat@");
         if (var8) {
            var3 = var3.substring(6);
         }

         var3 = this.T.L().B(var3);
         ArrayList var9 = new ArrayList();
         String var10 = var4.length > 0 ? var4[var4.length - 1] : "";
         String[] var11 = nQ.e.split(var3);
         int var12 = var11.length;
         int var13 = 0;

         while(true) {
            if (var13 >= var12) {
               return var9;
            }

            String var14 = var11[var13];
            String[] var15 = nQ.V.split(var14, 2);
            u1 var16 = (u1)this.z.get(var15[0].toLowerCase(Locale.ENGLISH));
            if (var16 != null) {
               if (var5 && !(var16 instanceof Al)) {
                  Oa.y(new TC());
                  return null;
               }

               String var17 = var15.length == 1 ? null : var15[1];
               om var18 = this.T.x(var1, var2, var10, var17, var4);

               try {
                  Collection var19 = var16.v(var18);
                  if (!var8 && var19 != null && var1.z[var1.z.length - 1].B && var4.length > nQ.J.split(var1.N).length) {
                     String var20 = String.join(" ", var4);
                     var19 = (Collection)var19.stream().map(Ei::lambda$getCompletionValues$2).collect(Collectors.toList());
                  }

                  if (var19 == null) {
                     break;
                  }

                  var9.addAll(var19);
               } catch (wN var21) {
                  break;
               } catch (Exception var22) {
                  var1.W(var2, Arrays.asList(var4), var22);
                  break;
               }
            } else {
               var9.add(var14);
            }

            ++var13;
         }

         return Collections.emptyList();
      }
   }

   private static String lambda$getCompletionValues$2(String[] var0, String var1, String var2) {
      if (var2 != null && var2.split(" ").length >= var0.length && Ra.R(var2, var1)) {
         String[] var3 = var2.split(" ");
         return String.join(" ", (CharSequence[])Arrays.copyOfRange(var3, var0.length - 1, var3.length));
      } else {
         return var2;
      }
   }

   private static Collection lambda$registerStaticCompletion$1(Collection var0, om var1) {
      return var0;
   }

   private static Collection lambda$new$0(om var0) {
      String var1 = var0.Y();
      if (var1 == null) {
         return Collections.emptyList();
      } else {
         String[] var2 = nQ.u.split(var1);
         int var3;
         int var4;
         if (var2.length != 2) {
            var3 = 0;
            var4 = Oa.L(var2[0], 0);
         } else {
            var3 = Oa.L(var2[0], 0);
            var4 = Oa.L(var2[1], 0);
         }

         return (Collection)IntStream.rangeClosed(var3, var4).mapToObj(Integer::toString).collect(Collectors.toList());
      }
   }
}
