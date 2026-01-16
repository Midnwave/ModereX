package com.gmail.olexorus.themis;

import java.io.ByteArrayInputStream;
import java.util.BitSet;

public interface Ju {
   default g3[] i(E7 var1, BitSet var2, BitSet var3, boolean var4, boolean var5, boolean var6, int var7, byte[] var8, Br var9) {
      AR var10 = var1.c((Gs)null, (vL)null);
      lm var11 = lm.z(nT.l(var8));

      g3[] var12;
      try {
         var12 = this.X(var10, var2, var3, var4, var5, var6, var7, var8.length, var11);
      } finally {
         NY.o(var11.g);
      }

      return var12;
   }

   default g3[] z(AR var1, BitSet var2, BitSet var3, boolean var4, boolean var5, boolean var6, int var7, byte[] var8, Br var9) {
      E7 var10 = E7.D(var1, (Gs)null, (vL)null);
      return this.i(var10, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   default g3[] X(AR var1, BitSet var2, BitSet var3, boolean var4, boolean var5, boolean var6, int var7, int var8, lm<?> var9) {
      byte[] var10 = var9.G(var8);
      Br var11 = new Br(new ByteArrayInputStream(var10));
      return this.z(var1, var2, var3, var4, var5, var6, var7, var10, var11);
   }
}
