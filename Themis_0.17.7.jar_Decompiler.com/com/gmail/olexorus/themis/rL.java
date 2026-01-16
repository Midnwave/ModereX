package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum RL {
   CREATE_OR_UPDATE_ITEM,
   REMOVE_ITEM;

   public static final RL[] VALUES;
   private static final RL[] W;

   private static RL[] C() {
      return new RL[]{CREATE_OR_UPDATE_ITEM, REMOVE_ITEM};
   }

   static {
      long var0 = kt.a(1213467469257437377L, -3689955483682979893L, MethodHandles.lookup().lookupClass()).a(61444926467155L) ^ 38645274302900L;
      CREATE_OR_UPDATE_ITEM = new RL("CREATE_OR_UPDATE_ITEM", 0);
      REMOVE_ITEM = new RL("REMOVE_ITEM", 1);
      W = C();
      VALUES = values();
   }
}
