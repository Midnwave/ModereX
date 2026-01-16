package com.gmail.olexorus.themis;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class bi {
   public static final tw<bi> X = (new gP()).I();
   public static final bi V = new bi((CW)null, (BI)null, Collections.emptyList());
   private final CW c;
   private final BI A;
   private final List<Ed> x;

   public bi(CW var1, BI var2, List<Ed> var3) {
      this.c = var1;
      this.A = var2;
      this.x = var3;
   }

   public CW m() {
      return this.c;
   }

   public BI A() {
      return this.A;
   }

   public List<Ed> V() {
      return this.x;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof bi)) {
         return false;
      } else {
         bi var2 = (bi)var1;
         if (!Objects.equals(this.c, var2.c)) {
            return false;
         } else {
            return !Objects.equals(this.A, var2.A) ? false : this.x.equals(var2.x);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.c, this.A, this.x});
   }

   static CW b(bi var0) {
      return var0.c;
   }

   static BI s(bi var0) {
      return var0.A;
   }

   static List V(bi var0) {
      return var0.x;
   }
}
