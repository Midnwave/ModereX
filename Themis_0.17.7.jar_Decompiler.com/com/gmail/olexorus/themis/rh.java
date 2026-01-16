package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum Rh {
   KEEP_BLOCKS,
   DESTROY_BLOCKS,
   DECAY_DESTROYED_BLOCKS,
   TRIGGER_BLOCKS;

   private static final Rh[] q;

   private static Rh[] t() {
      return new Rh[]{KEEP_BLOCKS, DESTROY_BLOCKS, DECAY_DESTROYED_BLOCKS, TRIGGER_BLOCKS};
   }

   static {
      long var0 = kt.a(6644996674181717319L, 8253707680202443524L, MethodHandles.lookup().lookupClass()).a(155825329445791L) ^ 77233638291515L;
      KEEP_BLOCKS = new Rh("KEEP_BLOCKS", 0);
      DESTROY_BLOCKS = new Rh("DESTROY_BLOCKS", 1);
      DECAY_DESTROYED_BLOCKS = new Rh("DECAY_DESTROYED_BLOCKS", 2);
      TRIGGER_BLOCKS = new Rh("TRIGGER_BLOCKS", 3);
      q = t();
   }
}
