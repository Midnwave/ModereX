package com.gmail.olexorus.themis;

// $FF: synthetic class
class Vk {
   static final int[] j = new int[MB.values().length];

   static {
      try {
         j[MB.END_COMBAT.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         j[MB.ENTITY_DEAD.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      try {
         j[MB.ENTER_COMBAT.ordinal()] = 3;
      } catch (NoSuchFieldError var1) {
      }

   }
}
