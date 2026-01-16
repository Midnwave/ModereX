package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum uy {
   HANDSHAKING,
   STATUS,
   LOGIN,
   PLAY,
   CONFIGURATION;

   // $FF: synthetic method
   private static uy[] M() {
      return new uy[]{HANDSHAKING, STATUS, LOGIN, PLAY, CONFIGURATION};
   }

   static {
      long var0 = kt.a(462527539236500102L, -4706726010088680884L, MethodHandles.lookup().lookupClass()).a(221855418206891L) ^ 36103220813701L;
      HANDSHAKING = new uy("HANDSHAKING", 0);
      STATUS = new uy("STATUS", 1);
      LOGIN = new uy("LOGIN", 2);
      PLAY = new uy("PLAY", 3);
      CONFIGURATION = new uy("CONFIGURATION", 4);
   }
}
