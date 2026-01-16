package com.gmail.olexorus.themis;

import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

class JP extends SimpleCommandMap {
   private wr D;
   CommandMap V;

   JP(wr var1, CommandMap var2) {
      super(Bukkit.getServer());
      this.D = var1;
      this.V = var2;
   }

   public void registerAll(String var1, List<Command> var2) {
      this.V.registerAll(var1, var2);
   }

   public boolean register(String var1, String var2, Command var3) {
      return this.i(var3) ? super.register(var1, var2, var3) : this.V.register(var1, var2, var3);
   }

   boolean B(String var1) {
      String[] var2 = nQ.J.split(var1);
      return var2.length != 0 && this.i((Command)this.knownCommands.get(var2[0].toLowerCase(Locale.ENGLISH)));
   }

   boolean i(Command var1) {
      return var1 instanceof ti && ((ti)var1).Y() == this.D;
   }

   public boolean register(String var1, Command var2) {
      return this.i(var2) ? super.register(var1, var2) : this.V.register(var1, var2);
   }

   public boolean dispatch(CommandSender var1, String var2) {
      return this.B(var2) ? super.dispatch(var1, var2) : this.V.dispatch(var1, var2);
   }

   public void clearCommands() {
      super.clearCommands();
      this.V.clearCommands();
   }

   public Command getCommand(String var1) {
      return this.B(var1) ? super.getCommand(var1) : this.V.getCommand(var1);
   }

   public List<String> tabComplete(CommandSender var1, String var2) {
      return this.B(var2) ? super.tabComplete(var1, var2) : this.V.tabComplete(var1, var2);
   }
}
