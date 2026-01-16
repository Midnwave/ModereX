package com.gmail.olexorus.themis;

import java.util.List;
import org.bukkit.command.CommandSender;

class tS extends tN {
   final List b;
   final VB t;

   tS(VB var1, wr var2, CommandSender var3, List var4) {
      super(var2, var3);
      this.t = var1;
      this.b = var4;
   }

   public void f(String var1) {
      this.b.add(var1);
   }
}
