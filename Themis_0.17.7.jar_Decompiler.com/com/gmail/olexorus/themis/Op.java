package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum op {
   PLUGIN_MESSAGE,
   DISCONNECT,
   CONFIGURATION_END,
   KEEP_ALIVE,
   PING,
   REGISTRY_DATA,
   RESOURCE_PACK_SEND,
   UPDATE_ENABLED_FEATURES,
   UPDATE_TAGS;

   // $FF: synthetic method
   private static op[] d() {
      return new op[]{PLUGIN_MESSAGE, DISCONNECT, CONFIGURATION_END, KEEP_ALIVE, PING, REGISTRY_DATA, RESOURCE_PACK_SEND, UPDATE_ENABLED_FEATURES, UPDATE_TAGS};
   }

   static {
      long var0 = kt.a(-6192170774380960251L, -4807956290998012214L, MethodHandles.lookup().lookupClass()).a(78362585063576L) ^ 128142607184395L;
      PLUGIN_MESSAGE = new op("PLUGIN_MESSAGE", 0);
      DISCONNECT = new op("DISCONNECT", 1);
      CONFIGURATION_END = new op("CONFIGURATION_END", 2);
      KEEP_ALIVE = new op("KEEP_ALIVE", 3);
      PING = new op("PING", 4);
      REGISTRY_DATA = new op("REGISTRY_DATA", 5);
      RESOURCE_PACK_SEND = new op("RESOURCE_PACK_SEND", 6);
      UPDATE_ENABLED_FEATURES = new op("UPDATE_ENABLED_FEATURES", 7);
      UPDATE_TAGS = new op("UPDATE_TAGS", 8);
   }
}
