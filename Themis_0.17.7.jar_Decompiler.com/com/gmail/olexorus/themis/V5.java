package com.gmail.olexorus.themis;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

public class v5 extends Command implements ti, PluginIdentifiableCommand {
   private final wr r;
   private final String h;
   private Nk F;
   private SetMultimap<String, Bd> k = HashMultimap.create();
   private List<Nk> Z = new ArrayList();
   boolean v = false;
   private static final long a = kt.a(-6689879579917324669L, 6721102797412011687L, MethodHandles.lookup().lookupClass()).a(58296047088980L);

   protected v5(wr var1, String var2) {
      super(var2);
      this.r = var1;
      this.h = var2;
   }

   public String getDescription() {
      Bd var1 = this.E();
      String var2 = null;
      if (var1 != null && !var1.I().isEmpty()) {
         var2 = var1.I();
      } else if (var1 != null && var1.d.I != null) {
         var2 = var1.d.I;
      } else if (this.F.I != null) {
         var2 = this.F.I;
      }

      return var2 != null ? this.r.O().W(var2) : super.getDescription();
   }

   public String P() {
      return this.h;
   }

   public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
      if (var2.contains(":")) {
         var2 = nQ.Z.split(var2, 2)[1];
      }

      return this.J(this.r.w(var1), var2, var3);
   }

   public boolean execute(CommandSender var1, String var2, String[] var3) {
      if (var2.contains(":")) {
         var2 = nQ.Z.split(var2, 2)[1];
      }

      this.w(this.r.w(var1), var2, var3);
      return true;
   }

   public boolean testPermissionSilent(CommandSender var1) {
      return this.X(this.r.w(var1));
   }

   public void u(Nk var1) {
      long var2 = a ^ 30009967624089L;
      if (this.F == null || !var1.g.get("__default").isEmpty()) {
         this.F = var1;
      }

      this.j(this.Z, this.k, var1);
      this.setPermission(this.i());
   }

   public wi Y() {
      return this.r;
   }

   public SetMultimap<String, Bd> M() {
      return this.k;
   }

   public List<Nk> R() {
      return this.Z;
   }

   public Nk y() {
      return this.F;
   }

   public Plugin getPlugin() {
      return this.r.W();
   }
}
