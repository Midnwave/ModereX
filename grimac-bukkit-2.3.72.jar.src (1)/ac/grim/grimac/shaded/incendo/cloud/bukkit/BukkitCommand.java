/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitHelper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestions;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.util.StringUtils;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.logging.Level;
/*     */ import java.util.stream.Collectors;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.PluginIdentifiableCommand;
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
/*     */ final class BukkitCommand<C>
/*     */   extends Command
/*     */   implements PluginIdentifiableCommand
/*     */ {
/*     */   private final CommandComponent<C> command;
/*     */   private final BukkitCommandManager<C> manager;
/*     */   private final Command<C> cloudCommand;
/*     */   private boolean disabled;
/*     */   
/*     */   BukkitCommand(String label, List<String> aliases, Command<C> cloudCommand, CommandComponent<C> command, BukkitCommandManager<C> manager) {
/*  66 */     super(label, 
/*     */         
/*  68 */         BukkitHelper.description(cloudCommand), "", aliases);
/*     */ 
/*     */ 
/*     */     
/*  72 */     this.command = command;
/*  73 */     this.manager = manager;
/*  74 */     this.cloudCommand = cloudCommand;
/*  75 */     this.disabled = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
/*  84 */     StringBuilder builder = new StringBuilder(this.command.name());
/*  85 */     for (String string : args) {
/*  86 */       builder.append(" ").append(string);
/*     */     }
/*  88 */     Suggestions<C, ?> result = this.manager.suggestionFactory().suggestImmediately(this.manager
/*  89 */         .senderMapper().map(sender), builder
/*  90 */         .toString());
/*     */     
/*  92 */     return (List<String>)result.list().stream()
/*  93 */       .map(Suggestion::suggestion)
/*  94 */       .map(suggestion -> StringUtils.trimBeforeLastSpace(suggestion, result.commandInput()))
/*  95 */       .filter(Objects::nonNull)
/*  96 */       .collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean execute(CommandSender commandSender, String commandLabel, String[] strings) {
/* 106 */     StringBuilder builder = new StringBuilder(this.command.name());
/* 107 */     for (String string : strings) {
/* 108 */       builder.append(" ").append(string);
/*     */     }
/* 110 */     C sender = (C)this.manager.senderMapper().map(commandSender);
/* 111 */     this.manager.commandExecutor().executeCommand(sender, builder.toString());
/* 112 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription() {
/* 117 */     return BukkitHelper.description(this.cloudCommand);
/*     */   }
/*     */ 
/*     */   
/*     */   public Plugin getPlugin() {
/* 122 */     return this.manager.owningPlugin();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUsage() {
/* 127 */     CommandNode<C> node = namedNode();
/* 128 */     if (node == null) {
/* 129 */       getPlugin().getLogger().log(Level.WARNING, "Node does not exist in tree for command " + getLabel() + ".");
/* 130 */       return "";
/*     */     } 
/* 132 */     return this.manager.commandSyntaxFormatter().apply(null, 
/*     */         
/* 134 */         Collections.singletonList(Objects.<CommandComponent>requireNonNull(node.component())), node);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean testPermissionSilent(CommandSender target) {
/* 141 */     CommandNode<C> node = namedNode();
/* 142 */     if (this.disabled || node == null) {
/* 143 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 147 */     Map<Type, Permission> accessMap = (Map<Type, Permission>)node.nodeMeta().getOrDefault(CommandNode.META_KEY_ACCESS, Collections.emptyMap());
/* 148 */     C cloudSender = (C)this.manager.senderMapper().map(target);
/* 149 */     for (Map.Entry<Type, Permission> entry : accessMap.entrySet()) {
/* 150 */       if (GenericTypeReflector.isSuperType(entry.getKey(), cloudSender.getClass()) && 
/* 151 */         this.manager.testPermission(cloudSender, entry.getValue()).allowed()) {
/* 152 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 156 */     return false;
/*     */   }
/*     */   
/*     */   @API(status = API.Status.INTERNAL, since = "1.7.0")
/*     */   void disable() {
/* 161 */     this.disabled = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRegistered() {
/* 168 */     return !this.disabled;
/*     */   }
/*     */   
/*     */   private CommandNode<C> namedNode() {
/* 172 */     return this.manager.commandTree().getNamedNode(this.command.name());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\BukkitCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */