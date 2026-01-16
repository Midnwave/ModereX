package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

class VJ {
   private final RT H;
   private static final long a = kt.a(7787927842645186191L, 5788082723918296713L, MethodHandles.lookup().lookupClass()).a(8361804316470L);

   public VJ(RT var1) {
      this.H = var1;
   }

   public void P(String var1, Consumer<Boolean> var2) {
      this.z(var1, VJ::lambda$useBoolean$0);
   }

   public <R> R D(String var1, Function<Boolean, R> var2) {
      return this.y(var1, VJ::lambda$readBoolean$1);
   }

   public void z(String var1, Consumer<Number> var2) {
      this.p(var1, VJ::lambda$useNumber$2);
   }

   public <R> R y(String var1, Function<Number, R> var2) {
      return this.g(var1, VJ::lambda$readNumber$3);
   }

   public void g(String var1, Consumer<String> var2) {
      this.p(var1, VJ::lambda$useUTF$4);
   }

   public <R> R V(String var1, Function<String, R> var2) {
      return this.g(var1, VJ::lambda$readUTF$5);
   }

   public <R> R E(String var1, Function<byte[], R> var2) {
      return this.g(var1, VJ::lambda$readByteArray$7);
   }

   public <R> R w(String var1, Function<int[], R> var2) {
      return this.g(var1, VJ::lambda$readIntArray$9);
   }

   public <R> R A(String var1, Function<long[], R> var2) {
      return this.g(var1, VJ::lambda$readLongArray$11);
   }

   public <R> R s(String var1, Function<RT, R> var2) {
      return this.g(var1, VJ::lambda$readCompound$13);
   }

   public <R> R h(String var1, Function<List<?>, R> var2) {
      return this.g(var1, VJ::lambda$readList$15);
   }

   public <R> R B(String var1, Function<Rc, R> var2) {
      return this.g(var1, var2);
   }

   public VJ N(String var1) {
      return (VJ)this.g(var1, VJ::lambda$child$16);
   }

   public Ay<?> O(String var1) {
      return (Ay)this.g(var1, Rc::b);
   }

   private void p(String var1, Consumer<Rc> var2) {
      Rc var3 = this.H.M(var1);
      if (var3 != null) {
         var2.accept(var3);
      }

   }

   private <R> R g(String var1, Function<Rc, R> var2) {
      Rc var3 = this.H.M(var1);
      return var3 == null ? null : var2.apply(var3);
   }

   private static VJ lambda$child$16(Rc var0) {
      return new VJ((RT)Rw.m(var0, Ay.K));
   }

   private static Object lambda$readList$15(Function var0, Rc var1) {
      return var0.apply(((m_)Rw.m(var1, Ay.P)).N());
   }

   private static void lambda$useList$14(Consumer var0, Rc var1) {
      var0.accept(((m_)Rw.m(var1, Ay.P)).N());
   }

   private static Object lambda$readCompound$13(Function var0, Rc var1) {
      return var0.apply((RT)Rw.m(var1, Ay.K));
   }

   private static void lambda$useCompound$12(Consumer var0, Rc var1) {
      var0.accept((RT)Rw.m(var1, Ay.K));
   }

   private static Object lambda$readLongArray$11(Function var0, Rc var1) {
      return var0.apply(((mt)Rw.m(var1, Ay.p)).i());
   }

   private static void lambda$useLongArray$10(Consumer var0, Rc var1) {
      var0.accept(((mt)Rw.m(var1, Ay.p)).i());
   }

   private static Object lambda$readIntArray$9(Function var0, Rc var1) {
      return var0.apply(((mW)Rw.m(var1, Ay.R)).F());
   }

   private static void lambda$useIntArray$8(Consumer var0, Rc var1) {
      var0.accept(((mW)Rw.m(var1, Ay.R)).F());
   }

   private static Object lambda$readByteArray$7(Function var0, Rc var1) {
      return var0.apply(((Rq)Rw.m(var1, Ay.j)).T());
   }

   private static void lambda$useByteArray$6(Consumer var0, Rc var1) {
      var0.accept(((Rq)Rw.m(var1, Ay.j)).T());
   }

   private static Object lambda$readUTF$5(Function var0, Rc var1) {
      return var0.apply(((mZ)Rw.m(var1, Ay.m)).b());
   }

   private static void lambda$useUTF$4(Consumer var0, Rc var1) {
      var0.accept(((mZ)Rw.m(var1, Ay.m)).b());
   }

   private static Object lambda$readNumber$3(Function var0, Rc var1) {
      long var2 = a ^ 18941565370433L;
      if (var1 instanceof mh) {
         return var0.apply(((mh)var1).r());
      } else {
         throw new IllegalArgumentException("Expected number but got " + var1.b());
      }
   }

   private static void lambda$useNumber$2(Consumer var0, Rc var1) {
      long var2 = a ^ 114327430939946L;
      if (var1 instanceof mh) {
         var0.accept(((mh)var1).r());
      } else {
         throw new IllegalArgumentException("Expected number but got " + var1.b());
      }
   }

   private static Object lambda$readBoolean$1(Function var0, Number var1) {
      return var0.apply(var1.byteValue() != 0);
   }

   private static void lambda$useBoolean$0(Consumer var0, Number var1) {
      var0.accept(var1.byteValue() != 0);
   }
}
