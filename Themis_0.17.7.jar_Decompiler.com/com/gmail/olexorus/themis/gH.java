package com.gmail.olexorus.themis;

import java.util.List;

public interface GH<C extends d<C, B>, B extends GH<C, B>> extends vr<C>, oP<C>, VH, lv, R8<B> {
   B U(X var1);

   B y(Iterable<? extends lv> var1);

   List<X> X();

   B r(WR var1);

   B D(BX var1);

   default B X(ux var1) {
      return this.Q(var1, NW.TRUE);
   }

   default B d(ux... var1) {
      return (GH)R8.super.D(var1);
   }

   default B C(ux var1, boolean var2) {
      return this.Q(var1, NW.O(var2));
   }

   B Q(ux var1, NW var2);

   B k(Eb var1);

   C m();

   default X T() {
      return this.m();
   }
}
