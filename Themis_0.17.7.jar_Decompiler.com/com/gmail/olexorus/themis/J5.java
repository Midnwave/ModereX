package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum J5 {
   BOTTOM,
   DOUBLE,
   LEFT,
   NORMAL,
   RIGHT,
   SINGLE,
   STICKY,
   TOP;

   // $FF: synthetic method
   private static J5[] b() {
      return new J5[]{BOTTOM, DOUBLE, LEFT, NORMAL, RIGHT, SINGLE, STICKY, TOP};
   }

   static {
      long var0 = kt.a(7402712537549582465L, 1632124770495322894L, MethodHandles.lookup().lookupClass()).a(71275125744091L) ^ 84773592873618L;
      BOTTOM = new J5("BOTTOM", 0);
      DOUBLE = new J5("DOUBLE", 1);
      LEFT = new J5("LEFT", 2);
      NORMAL = new J5("NORMAL", 3);
      RIGHT = new J5("RIGHT", 4);
      SINGLE = new J5("SINGLE", 5);
      STICKY = new J5("STICKY", 6);
      TOP = new J5("TOP", 7);
   }
}
