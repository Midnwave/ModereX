package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class rP<R extends c7<?, ? extends bJ>> {
   protected final Map<Class<?>, zo<?, R>> n = new HashMap();
   protected final wi C;
   private static final long b = kt.a(-7342107597103897427L, 1015586689578320932L, MethodHandles.lookup().lookupClass()).a(178199260765989L);

   rP(wi var1) {
      this.C = var1;
      this.W(bJ.class, rP::lambda$new$0);
      this.J(Short.class, this::lambda$new$1);
      this.J(Short.TYPE, this::lambda$new$2);
      this.J(Integer.class, this::lambda$new$3);
      this.J(Integer.TYPE, this::lambda$new$4);
      this.J(Long.class, this::lambda$new$5);
      this.J(Long.TYPE, this::lambda$new$6);
      this.J(Float.class, this::lambda$new$7);
      this.J(Float.TYPE, this::lambda$new$8);
      this.J(Double.class, this::lambda$new$9);
      this.J(Double.TYPE, this::lambda$new$10);
      this.J(Number.class, this::lambda$new$11);
      this.J(BigDecimal.class, this::lambda$new$12);
      this.J(BigInteger.class, this::lambda$new$13);
      this.J(Boolean.class, rP::lambda$new$14);
      this.J(Boolean.TYPE, rP::lambda$new$15);
      this.J(Character.TYPE, rP::lambda$new$16);
      this.J(String.class, rP::lambda$new$17);
      this.J(String[].class, rP::lambda$new$18);
      this.J(Enum.class, rP::lambda$new$19);
      this.o(Eh.class, rP::lambda$new$20);
   }

   private Number N(String var1, R var2, Number var3, Number var4) {
      long var5 = b ^ 25175117298231L;
      Number var7 = Oa.S(var1, var2.r("suffixes"));
      this.G(var2, var7, var3, var4);
      return var7;
   }

   private void L(R var1, Number var2) {
      this.G(var1, var2, (Number)null, (Number)null);
   }

   private void G(R var1, Number var2, Number var3, Number var4) {
      long var5 = b ^ 113084790079805L;
      Double var7 = var1.q("min", var3);
      Double var8 = var1.q("max", var4);
      if (var8 != null && var2.doubleValue() > var8.doubleValue()) {
         throw new Vu(N6.PLEASE_SPECIFY_AT_MOST, new String[]{"{max}", String.valueOf(var8)});
      } else if (var7 != null && var2.doubleValue() < var7.doubleValue()) {
         throw new Vu(N6.PLEASE_SPECIFY_AT_LEAST, new String[]{"{min}", String.valueOf(var7)});
      }
   }

   public <T> void c(Class<T> var1, b9<T, R> var2) {
      this.n.put(var1, var2);
   }

   public <T> void W(Class<T> var1, a1<T, R> var2) {
      this.n.put(var1, var2);
   }

   public <T> void o(Class<T> var1, tu<T, R> var2) {
      this.n.put(var1, var2);
   }

   public <T> void J(Class<T> var1, zo<T, R> var2) {
      this.n.put(var1, var2);
   }

   public zo<?, R> S(Class<?> var1) {
      long var2 = b ^ 60521710037548L;
      Class var4 = var1;

      while(var1 != Object.class) {
         zo var5 = (zo)this.n.get(var1);
         if (var5 != null) {
            return var5;
         }

         if ((var1 = var1.getSuperclass()) == null) {
            break;
         }
      }

      this.C.L(vq.ERROR, "Could not find context resolver", new IllegalStateException("No context resolver defined for " + var4.getName()));
      return null;
   }

   private static Eh lambda$new$20(wi var0, c7 var1) {
      long var2 = b ^ 101537243658110L;
      String var4 = var1.S();
      String var5 = var1.C();
      Integer var6 = 1;
      List var7 = null;
      if (var5 != null && Oa.D(var5)) {
         var1.d();
         var6 = Oa.H(var5);
         if (var6 == null) {
            throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var5});
         }

         if (!var1.e().isEmpty()) {
            var7 = var1.e();
         }
      } else if (var4 != null && Oa.D(var4)) {
         var1.D();
         var6 = Oa.H(var4);
         if (var6 == null) {
            throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
         }

         if (!var1.e().isEmpty()) {
            var7 = var1.e();
         }
      } else if (!var1.e().isEmpty()) {
         var7 = var1.e();
      }

      Eh var8 = var0.i();
      var8.s(var6);
      Integer var9 = var1.i("perpage", (Integer)null);
      if (var9 != null) {
         var8.F(var9);
      }

      if (var7 != null) {
         String var10 = String.join(" ", var7);
         if (var8.h(var10)) {
            return var8;
         }
      }

      var8.p(var7);
      return var8;
   }

   private static Enum lambda$new$19(c7 var0) {
      long var1 = b ^ 47294024060414L;
      String var3 = var0.D();
      Class var4 = var0.s().getType();
      Enum var5 = Oa.A(var4, var3);
      if (var5 == null) {
         List var6 = Oa.I(var4);
         throw new Vu(N6.PLEASE_SPECIFY_ONE_OF, new String[]{"{valid}", Oa.d(var6, ", ")});
      } else {
         return var5;
      }
   }

   private static String[] lambda$new$18(c7 var0) {
      long var1 = b ^ 84350570085808L;
      List var4 = var0.e();
      String var3;
      if (var0.G() && !var0.k(Jw.class)) {
         var3 = Oa.R(var4);
      } else {
         var3 = var0.D();
      }

      String var5 = var0.t(Ri.class, 8);
      if (var5 != null) {
         if (var3.isEmpty()) {
            throw new Vu();
         } else {
            return nQ.h(var5).split(var3);
         }
      } else {
         if (!var0.G()) {
            Oa.y(new IllegalStateException("Weird Command signature... String[] should be last or @Split"));
         }

         String[] var6 = (String[])var4.toArray(new String[0]);
         var4.clear();
         return var6;
      }
   }

   private static String lambda$new$17(c7 var0) {
      long var1 = b ^ 95283072606195L;
      if (var0.k(le.class)) {
         return var0.D();
      } else {
         String var3 = var0.G() && !var0.k(Jw.class) ? Oa.R(var0.e()) : var0.D();
         Integer var4 = var0.i("minlen", (Integer)null);
         Integer var5 = var0.i("maxlen", (Integer)null);
         if (var4 != null && var3.length() < var4) {
            throw new Vu(N6.MUST_BE_MIN_LENGTH, new String[]{"{min}", String.valueOf(var4)});
         } else if (var5 != null && var3.length() > var5) {
            throw new Vu(N6.MUST_BE_MAX_LENGTH, new String[]{"{max}", String.valueOf(var5)});
         } else {
            return var3;
         }
      }
   }

   private static Character lambda$new$16(c7 var0) {
      long var1 = b ^ 125664504705409L;
      String var3 = var0.D();
      if (var3.length() > 1) {
         throw new Vu(N6.MUST_BE_MAX_LENGTH, new String[]{"{max}", String.valueOf(1)});
      } else {
         return var3.charAt(0);
      }
   }

   private static Boolean lambda$new$15(c7 var0) {
      return Oa.C(var0.D());
   }

   private static Boolean lambda$new$14(c7 var0) {
      return Oa.C(var0.D());
   }

   private BigInteger lambda$new$13(c7 var1) {
      long var2 = b ^ 1722805253136L;
      String var4 = var1.D();

      try {
         BigDecimal var5 = Oa.z(var4, var1.r("suffixes"));
         this.L(var1, var5);
         return var5.toBigIntegerExact();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private BigDecimal lambda$new$12(c7 var1) {
      long var2 = b ^ 116342412224325L;
      String var4 = var1.D();

      try {
         BigDecimal var5 = Oa.z(var4, var1.r("suffixes"));
         this.L(var1, var5);
         return var5;
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Number lambda$new$11(c7 var1) {
      long var2 = b ^ 98790204975026L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, -1.7976931348623157E308D, Double.MAX_VALUE);
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Double lambda$new$10(c7 var1) {
      long var2 = b ^ 68861827633016L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, -1.7976931348623157E308D, Double.MAX_VALUE).doubleValue();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Double lambda$new$9(c7 var1) {
      long var2 = b ^ 8376647324942L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, -1.7976931348623157E308D, Double.MAX_VALUE).doubleValue();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Float lambda$new$8(c7 var1) {
      long var2 = b ^ 41005091827618L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, -3.4028235E38F, Float.MAX_VALUE).floatValue();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Float lambda$new$7(c7 var1) {
      long var2 = b ^ 5382623016986L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, -3.4028235E38F, Float.MAX_VALUE).floatValue();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Long lambda$new$6(c7 var1) {
      long var2 = b ^ 115920304353927L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, Long.MIN_VALUE, Long.MAX_VALUE).longValue();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Long lambda$new$5(c7 var1) {
      long var2 = b ^ 49983453958548L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, Long.MIN_VALUE, Long.MAX_VALUE).longValue();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Integer lambda$new$4(c7 var1) {
      long var2 = b ^ 1639588452824L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, Integer.MIN_VALUE, Integer.MAX_VALUE).intValue();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Integer lambda$new$3(c7 var1) {
      long var2 = b ^ 62226591634772L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, Integer.MIN_VALUE, Integer.MAX_VALUE).intValue();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Short lambda$new$2(c7 var1) {
      long var2 = b ^ 25806295244783L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, -32768, (short)32767).shortValue();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private Short lambda$new$1(c7 var1) {
      long var2 = b ^ 63016529538388L;
      String var4 = var1.D();

      try {
         return this.N(var4, var1, -32768, (short)32767).shortValue();
      } catch (NumberFormatException var6) {
         throw new Vu(N6.MUST_BE_A_NUMBER, new String[]{"{num}", var4});
      }
   }

   private static bJ lambda$new$0(c7 var0) {
      return var0.q();
   }
}
