/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.internal;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCommandMeta;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.PluginHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.description.CommandDescription;
/*     */ import java.util.Locale;
/*     */ import java.util.Optional;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.INTERNAL)
/*     */ public final class BukkitHelper
/*     */ {
/*     */   public static String description(Command<?> command) {
/*  51 */     Optional<String> bukkitDescription = command.commandMeta().optional(BukkitCommandMeta.BUKKIT_DESCRIPTION);
/*  52 */     if (bukkitDescription.isPresent()) {
/*  53 */       return bukkitDescription.get();
/*     */     }
/*     */     
/*  56 */     CommandDescription description = command.commandDescription();
/*  57 */     if (!description.isEmpty()) {
/*  58 */       return description.description().textDescription();
/*     */     }
/*     */     
/*  61 */     return command.rootComponent().description().textDescription();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String namespacedLabel(PluginHolder manager, String label) {
/*  72 */     return namespacedLabel(manager.owningPlugin().getName(), label);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String namespacedLabel(String pluginName, String label) {
/*  83 */     return (pluginName + ':' + label).toLowerCase(Locale.ROOT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String stripNamespace(PluginHolder manager, String command) {
/*  94 */     return stripNamespace(manager.owningPlugin().getName(), command);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String stripNamespace(String pluginName, String command) {
/* 105 */     String[] split = command.split(" ");
/* 106 */     if (!split[0].contains(":")) {
/* 107 */       return command;
/*     */     }
/* 109 */     String token = split[0];
/* 110 */     String[] splitToken = token.split(":");
/* 111 */     if (namespacedLabel(pluginName, splitToken[1]).equals(token)) {
/* 112 */       split[0] = splitToken[1];
/* 113 */       return String.join(" ", (CharSequence[])split);
/*     */     } 
/* 115 */     return command;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Executor mainThreadExecutor(PluginHolder pluginHolder) {
/* 125 */     Plugin plugin = pluginHolder.owningPlugin();
/* 126 */     Server server = plugin.getServer();
/* 127 */     return task -> {
/*     */         if (server.isPrimaryThread()) {
/*     */           task.run();
/*     */           return;
/*     */         } 
/*     */         server.getScheduler().runTask(plugin, task);
/*     */       };
/*     */   }
/*     */   
/*     */   public static void ensurePluginEnabledOrEnabling(Plugin plugin) {
/* 137 */     Plugin fromManager = Bukkit.getServer().getPluginManager().getPlugin(plugin.getName());
/* 138 */     if (!plugin.equals(fromManager) || !plugin.isEnabled())
/* 139 */       throw new IllegalStateException("The plugin '" + plugin + "' is not (yet?) valid per the PluginManager. Try calling this method from onEnable rather than in the plugin constructor or onLoad."); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\internal\BukkitHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */