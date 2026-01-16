package com.gmail.olexorus.themis;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.List;

public final class iA {
   private static final uX<?>[] J = z();
   private static final long a = kt.a(5489881428707211590L, -918386756570658103L, MethodHandles.lookup().lookupClass()).a(205245812630246L);

   private static uX<?>[] z() {
      long var0 = a ^ 102401007888174L;
      tq.i.B();

      List var2;
      try {
         Field var3 = uX.class.getDeclaredField("J");
         var3.setAccessible(true);
         var2 = (List)var3.get((Object)null);
      } catch (ReflectiveOperationException var6) {
         throw new RuntimeException("Error while accessing registered binary tag types", var6);
      }

      uX[] var7 = new uX[var2.size()];

      for(int var4 = 0; var4 < var7.length; ++var4) {
         uX var5 = (uX)var2.get(var4);
         if (var5.B() != var4) {
            throw new IllegalStateException("Registered nbt tag types are wrong: " + var5.B() + " != " + var4);
         }

         var7[var4] = var5;
      }

      return var7;
   }

   public static MC N(Object var0) {
      long var1 = a ^ 19140178904354L;
      byte var3 = NY.o(var0);
      if (var3 == tq.j.B()) {
         return bP.endBinaryTag();
      } else {
         uX var4 = J[var3];

         try {
            return var4.s(new Vx(var0));
         } catch (IOException var6) {
            throw new RuntimeException("Error while reading adventure nbt tag from buf: " + var0, var6);
         }
      }
   }

   public static void X(Object var0, MC var1) {
      long var2 = a ^ 110586143990133L;
      uX var4 = var1.y();
      NY.Z(var0, var4.B());
      if (var4.B() != tq.j.B()) {
         try {
            var4.l(var1, new bc(var0));
         } catch (IOException var6) {
            throw new RuntimeException("Error while writing adventure nbt tag to buf: " + var1, var6);
         }
      }

   }

   public static Rc o(MC var0) {
      Object var1 = nT.O();

      Rc var2;
      try {
         X(var1, var0);
         var2 = g1.O(var1, zZ.e());
      } finally {
         NY.o(var1);
      }

      return var2;
   }

   public static MC P(Rc var0) {
      Object var1 = nT.O();

      MC var2;
      try {
         g1.A(var1, zZ.e(), var0);
         var2 = N(var1);
      } finally {
         NY.o(var1);
      }

      return var2;
   }

   public static Rc B(String var0) {
      long var1 = a ^ 60193783251151L;

      MC var3;
      try {
         var3 = oN.tagStringIO().asTag(var0);
      } catch (IOException var5) {
         throw new RuntimeException("Error while decoding nbt from string: " + var0, var5);
      }

      return o(var3);
   }

   public static String k(Rc var0) {
      long var1 = a ^ 111880253480912L;
      MC var3 = P(var0);

      try {
         return oN.tagStringIO().asString(var3);
      } catch (IOException var5) {
         throw new RuntimeException("Error while encoding nbt to string: " + var3, var5);
      }
   }
}
