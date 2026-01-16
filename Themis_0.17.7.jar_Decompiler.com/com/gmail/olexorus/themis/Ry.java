package com.gmail.olexorus.themis;

// $FF: synthetic class
class RY {
   static final int[] D = new int[ax.values().length];

   static {
      try {
         D[ax.PARTIALLY_FILTERED.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         D[ax.PASS_THROUGH.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      try {
         D[ax.FULLY_FILTERED.ordinal()] = 3;
      } catch (NoSuchFieldError var1) {
      }

   }
}
