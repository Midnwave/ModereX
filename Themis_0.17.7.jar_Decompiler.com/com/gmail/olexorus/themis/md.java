package com.gmail.olexorus.themis;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Iterator;

public class mD extends Rc implements Iterator<Rc>, Iterable<Rc>, r8, Closeable {
   private final DataInputStream S;
   private final Gq N;
   private final Runnable E;
   private final Ay<?> U;
   private Rc F;
   public int C;
   private static final long a = kt.a(186662929842537087L, -8348869252380070237L, MethodHandles.lookup().lookupClass()).a(163004568831950L);

   private mD(DataInputStream var1, Gq var2, Runnable var3) {
      this.S = var1;
      this.N = var2;
      this.E = var3;
      var2.e(37);

      try {
         this.U = gq.O.O(var2, var1);
         this.C = var1.readInt();
         var2.e(this.C * 4);
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }

      this.n();
   }

   public Ay<?> b() {
      return Ay.P;
   }

   public boolean equals(Object var1) {
      return this == var1;
   }

   public int hashCode() {
      return 1;
   }

   public Rc X() {
      throw new UnsupportedOperationException();
   }

   public boolean hasNext() {
      return this.C > 0;
   }

   public Rc C() {
      long var1 = a ^ 105964316326738L;
      Oj.z(this.F);
      if (!this.hasNext()) {
         throw new IllegalStateException("No more elements in list");
      } else {
         try {
            --this.C;
            if (this.U == Ay.K) {
               this.F = new mC(this.S, this.N, this::n, (S)null);
            } else if (this.U == Ay.P) {
               this.F = new mD(this.S, this.N, this::n);
            } else {
               this.F = gq.O.R(this.N, this.S, this.U);
               this.n();
            }

            return this.F;
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   private void n() {
      if (!this.hasNext()) {
         this.E.run();
      }

   }

   public Iterator<Rc> iterator() {
      return this;
   }

   public void b() {
      if (this.F instanceof r8) {
         ((r8)this.F).b();
      }

      if (this.hasNext()) {
         try {
            CK var1 = (CK)Oj.Q().get(this.U);

            for(int var2 = 0; var2 < this.C; ++var2) {
               var1.X(this.N, this.S);
            }
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }

         this.C = 0;
         this.n();
      }
   }

   public void p() {
      Oj.z(this.F);
      if (this.hasNext()) {
         try {
            ((CK)Oj.Q().get(this.U)).X(this.N, this.S);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }

         --this.C;
         this.n();
      }
   }

   public m_<Rc> B() {
      try {
         if (this.F instanceof r8) {
            ((r8)this.F).b();
         }

         if (!this.hasNext()) {
            return new m_(this.U, 0);
         } else {
            m_ var1 = new m_(this.U, this.C);

            for(int var2 = 0; var2 < this.C; ++var2) {
               var1.Z(gq.O.R(this.N, this.S, this.U));
            }

            this.C = 0;
            this.n();
            return var1;
         }
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public byte[] O() {
      try {
         if (this.F instanceof r8) {
            ((r8)this.F).b();
         }

         if (!this.hasNext()) {
            return new byte[]{9};
         } else {
            byte[] var1 = null;

            for(int var2 = 0; var2 < this.C; ++var2) {
               byte[] var3 = ((JG)Oj.W().get(this.U)).w(this.N, this.S);
               if (var1 == null) {
                  var1 = new byte[6 + this.C * var3.length];
                  var1[0] = 9;
                  var1[1] = ((Integer)gq.O.g.get(this.U)).byteValue();
                  var1[2] = (byte)(this.C >>> 24);
                  var1[3] = (byte)(this.C >>> 16);
                  var1[4] = (byte)(this.C >>> 8);
                  var1[5] = (byte)this.C;
               }

               System.arraycopy(var3, 0, var1, 5 + var2 * var3.length, var3.length);
            }

            this.C = 0;
            this.n();
            return var1;
         }
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   public void close() {
      this.S.close();
   }

   mD(DataInputStream var1, Gq var2, Runnable var3, S var4) {
      this(var1, var2, var3);
   }
}
