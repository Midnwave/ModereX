package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

final class TQ implements NJ<RJ> {
   static final TQ e = new TQ();

   private TQ() {
   }

   public X B(X var1, RJ var2) {
      if (!var2.G) {
         return var1;
      } else {
         boolean var3 = var2.y;
         var2.y = true;
         List var4 = var1.C();
         int var5 = var4.size();
         WR var6 = var1.o();
         ArrayList var7 = null;
         Object var8 = var1;
         int var11;
         if (var1 instanceof Aa) {
            String var9 = ((Aa)var1).H();
            Matcher var10 = var2.N.matcher(var9);
            var11 = 0;

            while(var10.find()) {
               RM var12 = var2.R.k(var10, ++var2.U, var2.L);
               if (var12 != RM.CONTINUE) {
                  if (var12 == RM.STOP) {
                     var2.G = false;
                     break;
                  }

                  lv var13;
                  if (var10.start() == 0) {
                     if (var10.end() == var9.length()) {
                        var13 = (lv)var2.F.apply(var10, (nh)X.p().P(var10.group()).r(var1.o()));
                        var8 = var13 == null ? X.f() : var13.T();
                        if (((X)var8).o().U() != null) {
                           var6 = var6.I((AT)null);
                        }

                        var8 = ((X)var8).Z(((X)var8).o().N(var1.o(), ba.IF_ABSENT_ON_TARGET));
                        if (var7 == null) {
                           var7 = new ArrayList(var5 + ((X)var8).C().size());
                           var7.addAll(((X)var8).C());
                        }
                     } else {
                        var8 = X.f("", var1.o());
                        var13 = (lv)var2.F.apply(var10, X.p().P(var10.group()));
                        if (var13 != null) {
                           if (var7 == null) {
                              var7 = new ArrayList(var5 + 1);
                           }

                           var7.add(var13.T());
                        }
                     }
                  } else {
                     if (var7 == null) {
                        var7 = new ArrayList(var5 + 2);
                     }

                     if (var2.y) {
                        var8 = ((Aa)var1).q(var9.substring(0, var10.start()));
                     } else if (var11 < var10.start()) {
                        var7.add(X.N(var9.substring(var11, var10.start())));
                     }

                     var13 = (lv)var2.F.apply(var10, X.p().P(var10.group()));
                     if (var13 != null) {
                        var7.add(var13.T());
                     }
                  }

                  ++var2.L;
                  var2.y = false;
                  var11 = var10.end();
               }
            }

            if (var11 < var9.length() && var11 > 0) {
               if (var7 == null) {
                  var7 = new ArrayList(var5);
               }

               var7.add(X.N(var9.substring(var11)));
            }
         } else if (var1 instanceof GY) {
            List var15 = ((GY)var1).n();
            ArrayList var18 = null;
            var11 = 0;

            for(int var21 = var15.size(); var11 < var21; ++var11) {
               VE var24 = (VE)var15.get(var11);
               VE var14 = var24.P() instanceof X ? VE.g(this.B((X)var24.P(), var2)) : var24;
               if (var14 != var24 && var18 == null) {
                  var18 = new ArrayList(var21);
                  if (var11 > 0) {
                     var18.addAll(var15.subList(0, var11));
                  }
               }

               if (var18 != null) {
                  var18.add(var14);
               }
            }

            if (var18 != null) {
               var8 = ((GY)var1).P(var18);
            }
         }

         if (var2.G) {
            if (var2.B) {
               mk var16 = var6.U();
               if (var16 != null) {
                  mk var19 = var16.t(this, var2);
                  if (var16 != var19) {
                     var8 = ((X)var8).K(TQ::lambda$render$0);
                  }
               }
            }

            boolean var17 = true;

            for(int var20 = 0; var20 < var5; ++var20) {
               X var22 = (X)var4.get(var20);
               X var23 = this.B(var22, var2);
               if (var23 != var22) {
                  if (var7 == null) {
                     var7 = new ArrayList(var5);
                  }

                  if (var17) {
                     var7.addAll(var4.subList(0, var20));
                  }

                  var17 = false;
               }

               if (var7 != null) {
                  var7.add(var23);
                  var17 = false;
               }
            }
         } else if (var7 != null) {
            var7.addAll(var4);
         }

         var2.y = var3;
         return (X)(var7 != null ? ((X)var8).T(var7) : var8);
      }
   }

   private static void lambda$render$0(mk var0, Nr var1) {
      var1.I(var0);
   }
}
