package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum l1 {
   ASCENDING_EAST,
   ASCENDING_NORTH,
   ASCENDING_SOUTH,
   ASCENDING_WEST,
   EAST_WEST,
   INNER_LEFT,
   INNER_RIGHT,
   NORTH_EAST,
   NORTH_SOUTH,
   NORTH_WEST,
   OUTER_LEFT,
   OUTER_RIGHT,
   SOUTH_EAST,
   SOUTH_WEST,
   STRAIGHT;

   // $FF: synthetic method
   private static l1[] n() {
      return new l1[]{ASCENDING_EAST, ASCENDING_NORTH, ASCENDING_SOUTH, ASCENDING_WEST, EAST_WEST, INNER_LEFT, INNER_RIGHT, NORTH_EAST, NORTH_SOUTH, NORTH_WEST, OUTER_LEFT, OUTER_RIGHT, SOUTH_EAST, SOUTH_WEST, STRAIGHT};
   }

   static {
      long var0 = kt.a(-3345540925157953983L, 2568185181268247149L, MethodHandles.lookup().lookupClass()).a(162450665482042L) ^ 125301830064905L;
      ASCENDING_EAST = new l1("ASCENDING_EAST", 0);
      ASCENDING_NORTH = new l1("ASCENDING_NORTH", 1);
      ASCENDING_SOUTH = new l1("ASCENDING_SOUTH", 2);
      ASCENDING_WEST = new l1("ASCENDING_WEST", 3);
      EAST_WEST = new l1("EAST_WEST", 4);
      INNER_LEFT = new l1("INNER_LEFT", 5);
      INNER_RIGHT = new l1("INNER_RIGHT", 6);
      NORTH_EAST = new l1("NORTH_EAST", 7);
      NORTH_SOUTH = new l1("NORTH_SOUTH", 8);
      NORTH_WEST = new l1("NORTH_WEST", 9);
      OUTER_LEFT = new l1("OUTER_LEFT", 10);
      OUTER_RIGHT = new l1("OUTER_RIGHT", 11);
      SOUTH_EAST = new l1("SOUTH_EAST", 12);
      SOUTH_WEST = new l1("SOUTH_WEST", 13);
      STRAIGHT = new l1("STRAIGHT", 14);
   }
}
