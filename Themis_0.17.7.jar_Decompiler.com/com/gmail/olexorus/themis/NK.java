package com.gmail.olexorus.themis;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Nk {
   final SetMultimap<String, Bd> g = HashMultimap.create();
   final Set<Nk> j = new HashSet();
   final Map<Class<?>, String> R = new HashMap();
   private Method y;
   private String P;
   private String Z;
   private String[] r;
   wi<?, ?, ?, ?, ?, ?> C = null;
   Nk a;
   Map<String, ti> e = new HashMap();
   String I;
   String m;
   String l;
   String N;
   boolean h;
   private cj A = null;
   private final ThreadLocal<bO> t = new ThreadLocal();
   private String V;
   private final Set<String> v = new HashSet();
   private static final long b = kt.a(8756394022349685218L, -2230903510008136753L, MethodHandles.lookup().lookupClass()).a(159858062210936L);

   void G(wi var1) {
      this.p(var1, this.m);
   }

   private void p(wi var1, String var2) {
      var1.H(this);
      this.C = var1;
      TA var3 = var1.I();
      Class var4 = this.getClass();
      String[] var5 = var3.n(var4, AB.class, 11);
      if (var2 == null && var5 != null) {
         var2 = var5[0];
      }

      this.m = var2 != null ? var2 : var4.getSimpleName().toLowerCase(Locale.ENGLISH);
      this.l = var3.L(var4, Ou.class, 1);
      this.I = var3.L(var4, BK.class, 9);
      this.V = this.W(var4);
      this.N = var3.L(var4, rK.class, 9);
      this.P();
      this.I();
      this.K(var2);
      if (var5 != null) {
         HashSet var6 = new HashSet();
         Collections.addAll(var6, var5);
         var6.remove(var2);
         Iterator var7 = var6.iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();
            this.K(var8);
            this.k(var8, this);
         }
      }

      if (var2 != null) {
         this.k(var2, this);
      }

   }

   private void K(String var1) {
      long var2 = b ^ 119020818444788L;
      Class[] var4 = this.getClass().getDeclaredClasses();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Class var7 = var4[var6];
         if (Nk.class.isAssignableFrom(var7)) {
            try {
               Nk var8 = null;
               Constructor[] var9 = var7.getDeclaredConstructors();
               ArrayList var10 = new ArrayList();
               Constructor[] var11 = var9;
               int var12 = var9.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  Constructor var14 = var11[var13];
                  var14.setAccessible(true);
                  Parameter[] var15 = var14.getParameters();
                  if (var15.length == 1 && var15[0].getType().isAssignableFrom(this.getClass())) {
                     var8 = (Nk)var14.newInstance(this);
                  } else {
                     var10.add("Found unusable constructor: " + var14.getName() + "(" + (String)Stream.of(var15).map(Nk::lambda$registerSubclasses$0).collect(Collectors.joining("<c2>,</c2> ")) + ")");
                  }
               }

               if (var8 != null) {
                  var8.a = this;
                  this.j.add(var8);
                  var8.p(this.C, var1);
                  this.g.putAll(var8.g);
                  this.e.putAll(var8.e);
               } else {
                  this.C.U(vq.ERROR, "Could not find a subcommand ctor for " + var7.getName());
                  Iterator var17 = var10.iterator();

                  while(var17.hasNext()) {
                     String var18 = (String)var17.next();
                     this.C.U(vq.INFO, var18);
                  }
               }
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException var16) {
               this.C.L(vq.ERROR, "Error registering subclass", var16);
            }
         }
      }

   }

   private void I() {
      long var1 = b ^ 67033206936963L;
      TA var3 = this.C.I();
      boolean var4 = false;
      boolean var5 = this.V == null || this.V.isEmpty();
      LinkedHashSet var6 = new LinkedHashSet();
      Collections.addAll(var6, this.getClass().getDeclaredMethods());
      Collections.addAll(var6, this.getClass().getMethods());
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         Method var8 = (Method)var7.next();
         var8.setAccessible(true);
         String var9 = null;
         String var10 = this.d(var8);
         String var11 = var3.L(var8, ui.class, 0);
         String var12 = var3.L(var8, AB.class, 0);
         if (var3.T(var8, Cq.class)) {
            if (!var5) {
               var10 = this.V;
            } else {
               this.D(var8, "__default");
            }
         }

         if (var10 != null) {
            var9 = var10;
         } else if (var12 != null) {
            var9 = var12;
         } else if (var11 != null) {
            var9 = var11;
            this.h = true;
         }

         boolean var13 = var3.T(var8, Gt.class);
         boolean var14 = var3.T(var8, Rn.class) || var3.T(var8, W1.class) || var3.T(var8, Mk.class);
         if (var14 || !var4 && var11 != null) {
            if (!var4) {
               if (var14) {
                  this.g.get("__catchunknown").clear();
                  var4 = true;
               }

               this.D(var8, "__catchunknown");
            } else {
               Oa.y(new IllegalStateException("Multiple @CatchUnknown/@HelpCommand commands, duplicate on " + var8.getDeclaringClass().getName() + "#" + var8.getName()));
            }
         } else if (var13) {
            if (this.y == null) {
               this.y = var8;
            } else {
               Oa.y(new IllegalStateException("Multiple @PreCommand commands, duplicate on " + var8.getDeclaringClass().getName() + "#" + var8.getName()));
            }
         }

         if (Objects.equals(var8.getDeclaringClass(), this.getClass()) && var9 != null) {
            this.D(var8, var9);
         }
      }

   }

   private void P() {
      this.v.clear();
      if (this.l != null && !this.l.isEmpty()) {
         this.v.addAll(Arrays.asList(nQ.D.split(this.l)));
      }

      if (this.a != null) {
         this.v.addAll(this.a.M());
      }

      this.g.values().forEach(Bd::p);
      this.j.forEach(Nk::P);
   }

   private String d(Method var1) {
      String var2 = this.C.I().L(var1, rW.class, 0);
      if (var2 == null) {
         return null;
      } else {
         Class var3 = var1.getDeclaringClass();
         String var4 = this.W(var3);
         return var4 != null && !var4.isEmpty() ? var4 + " " + var2 : var2;
      }
   }

   private String W(Class<?> var1) {
      ArrayList var2;
      for(var2 = new ArrayList(); var1 != null; var1 = var1.getEnclosingClass()) {
         String var3 = this.C.I().L(var1, rW.class, 0);
         if (var3 != null) {
            var2.add(var3);
         }
      }

      Collections.reverse(var2);
      return Oa.d(var2, " ");
   }

   private void k(String var1, Nk var2) {
      String var3 = var1.toLowerCase(Locale.ENGLISH);
      ti var4 = this.C.r(var3);
      var4.u(var2);
      this.e.put(var3, var4);
   }

   private void D(Method var1, String var2) {
      long var3 = b ^ 25321289031403L;
      var2 = this.C.L().B(var2.toLowerCase(Locale.ENGLISH));
      String[] var5 = nQ.J.split(var2);
      Set var6 = l(var5);

      String[] var8;
      for(int var7 = 0; var7 < var5.length; ++var7) {
         var8 = nQ.e.split(var5[var7]);
         if (var8.length == 0 || var8[0].isEmpty()) {
            throw new IllegalArgumentException("Invalid @Subcommand configuration for " + var1.getName() + " - parts can not start with | or be empty");
         }

         var5[var7] = var8[0];
      }

      String var15 = Ra.G(var5, " ");
      var8 = this.C.I().n(var1, AB.class, 3);
      String var9 = var8 != null ? var8[0] : this.m + " ";
      Bd var10 = this.C.J(this, var9, var1, var15);
      Iterator var11 = var6.iterator();

      while(var11.hasNext()) {
         String var12 = (String)var11.next();
         this.g.put(var12, var10);
      }

      var10.F(var6);
      if (var8 != null) {
         String[] var16 = var8;
         int var17 = var8.length;

         for(int var13 = 0; var13 < var17; ++var13) {
            String var14 = var16[var13];
            this.k(var14, new NN(this, var10, var5));
         }
      }

   }

   private static Set<String> l(String[] var0) {
      int var1 = 0;
      HashSet var2 = null;

      while(true) {
         HashSet var3 = new HashSet();
         if (var1 < var0.length) {
            String[] var4 = nQ.e.split(var0[var1]);
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String var7 = var4[var6];
               if (var2 != null) {
                  var3.addAll((Collection)var2.stream().map(Nk::lambda$getSubCommandPossibilityList$1).collect(Collectors.toList()));
               } else {
                  var3.add(var7);
               }
            }
         }

         if (var1 + 1 >= var0.length) {
            return var3;
         }

         var2 = var3;
         ++var1;
      }
   }

   void R(bJ var1, b7 var2) {
      try {
         bO var3 = this.G(var1, var2.R, var2.g, false);
         this.Z = var2.c;
         this.S(var3, var1, var2.g, var2.A);
      } finally {
         this.p();
      }

   }

   private void p() {
      ((Stack)wi.u.get()).pop();
      this.t.set((Object)null);
      this.Z = null;
      this.P = null;
      this.r = new String[0];
   }

   private bO G(bJ var1, String var2, String[] var3, boolean var4) {
      Stack var5 = (Stack)wi.u.get();
      bO var6 = this.C.I(this, var1, var2, var3, var4);
      var5.push(var6);
      this.t.set(var6);
      this.Z = null;
      this.P = var2;
      this.r = var3;
      return var6;
   }

   private void S(bO var1, bJ var2, String[] var3, Bd var4) {
      if (var4.l(var2)) {
         var1.y(var4);
         if (this.Y(var1, var4, var2, var3)) {
            return;
         }

         List var5 = Arrays.asList(var3);
         var4.V(var2, var5, var1);
      } else {
         var2.W(zX.F, N6.PERMISSION_DENIED);
      }

   }

   public boolean f(bJ var1, Bd<?> var2) {
      return true;
   }

   List<String> l(bJ var1, ti var2, String[] var3, boolean var4) {
      if (var3.length == 0) {
         var3 = new String[]{""};
      }

      String var5 = var2.P();

      List var14;
      try {
         zJ var6 = this.C.q();
         this.G(var1, var5, var3, var4);
         JD var7 = var6.k(var2, var5, var3, true);
         ArrayList var8 = new ArrayList();
         if (var7 != null) {
            Iterator var9 = var7.F.iterator();

            while(var9.hasNext()) {
               Bd var10 = (Bd)var9.next();
               if (var10.d == this) {
                  var8.addAll(this.P(var1, var10, var7.U, var5, var4));
               }
            }
         }

         var14 = q(var3[var3.length - 1], var8);
      } finally {
         this.p();
      }

      return var14;
   }

   List<String> t(bJ var1, String[] var2) {
      HashSet var3 = new HashSet();
      int var4 = Math.max(0, var2.length - 1);
      String var5 = Ra.G(var2, " ").toLowerCase(Locale.ENGLISH);
      Iterator var6 = this.g.entries().iterator();

      while(var6.hasNext()) {
         Entry var7 = (Entry)var6.next();
         String var8 = (String)var7.getKey();
         if (var8.startsWith(var5) && !T(var8)) {
            Bd var9 = (Bd)var7.getValue();
            if (var9.l(var1) && !var9.n) {
               String[] var10 = nQ.J.split(var9.y);
               var3.add(var10[var4]);
            }
         }
      }

      return new ArrayList(var3);
   }

   static boolean T(String var0) {
      long var1 = b ^ 43259465857186L;
      return "__catchunknown".equals(var0) || "__default".equals(var0);
   }

   private List<String> P(bJ var1, Bd var2, String[] var3, String var4, boolean var5) {
      if (var2.l(var1) && var3.length != 0 && var2.z.length != 0) {
         if (!var2.z[var2.z.length - 1].B && var3.length > var2.w) {
            return Collections.emptyList();
         } else {
            List var6 = this.C.S().o(var2, var1, var3, var5);
            return q(var3[var3.length - 1], var6);
         }
      } else {
         return Collections.emptyList();
      }
   }

   private static List<String> q(String var0, List<String> var1) {
      return (List)var1.stream().distinct().filter(Nk::lambda$filterTabComplete$2).collect(Collectors.toList());
   }

   private boolean Y(bO var1, Bd var2, bJ var3, String[] var4) {
      long var5 = b ^ 98200939219749L;
      Method var7 = this.y;
      if (var7 != null) {
         try {
            Class[] var8 = var7.getParameterTypes();
            Object[] var9 = new Object[var7.getParameterCount()];

            for(int var10 = 0; var10 < var9.length; ++var10) {
               Class var11 = var8[var10];
               Object var12 = var3.Q();
               if (this.C.A(var11) && var11.isAssignableFrom(var12.getClass())) {
                  var9[var10] = var12;
               } else if (bJ.class.isAssignableFrom(var11)) {
                  var9[var10] = var3;
               } else if (Bd.class.isAssignableFrom(var11)) {
                  var9[var10] = var2;
               } else if (String[].class.isAssignableFrom(var11)) {
                  var9[var10] = var4;
               } else {
                  var9[var10] = null;
               }
            }

            return (Boolean)var7.invoke(this, var9);
         } catch (InvocationTargetException | IllegalAccessException var13) {
            this.C.L(vq.ERROR, "Exception encountered while command pre-processing", var13);
         }
      }

      return false;
   }

   public void P(bJ var1, String[] var2) {
      var1.W(zX.F, N6.UNKNOWN_COMMAND);
   }

   public void W(bJ var1, Bd<?> var2) {
      long var3 = b ^ 21585169652472L;
      var1.W(zX.y, N6.INVALID_SYNTAX, "{command}", this.C.o(var1) + var2.W, "{syntax}", var2.N(var1));
   }

   public boolean L(bJ var1) {
      return this.C.N(var1, this.M());
   }

   public Set<String> M() {
      return this.v;
   }

   public String V() {
      return this.m;
   }

   public cj n() {
      return this.A;
   }

   public Bd p() {
      long var1 = b ^ 96022273794565L;
      return (Bd)Oa.U(this.g.get("__default"));
   }

   public List<Bd> G() {
      ArrayList var1 = new ArrayList();
      var1.addAll(this.g.values());
      return var1;
   }

   private static boolean lambda$filterTabComplete$2(String var0, String var1) {
      return var1 != null && (var0.isEmpty() || Ra.R(var1, var0));
   }

   private static String lambda$getSubCommandPossibilityList$1(String var0, String var1) {
      return var1 + " " + var0;
   }

   private static String lambda$registerSubclasses$0(Parameter var0) {
      return var0.getType().getSimpleName() + " " + var0.getName();
   }
}
