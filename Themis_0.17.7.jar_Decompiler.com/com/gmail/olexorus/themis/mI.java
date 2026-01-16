package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mi {
   private final wi t;
   private final Map<String, Entry<Pattern, String>> l;
   private static final long a = kt.a(-8518607044925467315L, -91853825327913330L, MethodHandles.lookup().lookupClass()).a(153045313069948L);

   Mi(wi var1) {
      long var2 = a ^ 63617135731917L;
      super();
      this.l = new LinkedHashMap();
      this.t = var1;
      this.h("truthy", "true|false|yes|no|1|0|on|off|t|f");
   }

   private String h(String var1, String var2) {
      long var3 = a ^ 122952909151340L;
      var1 = nQ.T.matcher(var1.toLowerCase(Locale.ENGLISH)).replaceAll("");
      Pattern var5 = Pattern.compile("%\\{" + Pattern.quote(var1) + "}|%" + Pattern.quote(var1) + "\\b", 2);
      SimpleImmutableEntry var6 = new SimpleImmutableEntry(var5, var2);
      Entry var7 = (Entry)this.l.put(var1, var6);
      return var7 != null ? (String)var7.getValue() : null;
   }

   public String B(String var1) {
      long var2 = a ^ 54976030203858L;
      if (var1 == null) {
         return null;
      } else {
         Entry var5;
         for(Iterator var4 = this.l.values().iterator(); var4.hasNext(); var1 = ((Pattern)var5.getKey()).matcher(var1).replaceAll((String)var5.getValue())) {
            var5 = (Entry)var4.next();
         }

         Matcher var6 = nQ.t.matcher(var1);

         while(var6.find()) {
            this.t.U(vq.ERROR, "Found unregistered replacement: " + var6.group());
         }

         return var1;
      }
   }
}
