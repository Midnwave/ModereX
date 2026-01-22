/*     */ package ac.grim.grimac.shaded.incendo.cloud.paper;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.BrigadierManagerHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierCommand;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.permission.BrigadierPermissionChecker;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.PluginHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitBackwardsBrigadierSenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitHelper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandRegistrationHandler;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*     */ import com.mojang.brigadier.Command;
/*     */ import com.mojang.brigadier.CommandDispatcher;
/*     */ import com.mojang.brigadier.tree.CommandNode;
/*     */ import com.mojang.brigadier.tree.LiteralCommandNode;
/*     */ import com.mojang.brigadier.tree.RootCommandNode;
/*     */ import io.papermc.paper.command.brigadier.CommandRegistrationFlag;
/*     */ import io.papermc.paper.command.brigadier.CommandSourceStack;
/*     */ import io.papermc.paper.command.brigadier.Commands;
/*     */ import io.papermc.paper.plugin.bootstrap.BootstrapContext;
/*     */ import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
/*     */ import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
/*     */ import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
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
/*     */ final class ModernPaperBrigadier<C, B>
/*     */   implements CommandRegistrationHandler<C>, BrigadierManagerHolder<C, CommandSourceStack>
/*     */ {
/*     */   private final CommandManager<C> manager;
/*     */   private final Runnable lockRegistration;
/*     */   private final PluginMetaHolder metaHolder;
/*     */   private final CloudBrigadierManager<C, CommandSourceStack> brigadierManager;
/*  73 */   private final Map<String, Set<String>> aliases = new ConcurrentHashMap<>();
/*  74 */   private final Set<Command<C>> registeredCommands = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile Commands commands;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ModernPaperBrigadier(Class<B> baseType, CommandManager<C> manager, SenderMapper<B, C> senderMapper, Runnable lockRegistration) {
/*  85 */     this.manager = manager;
/*  86 */     this.lockRegistration = lockRegistration;
/*     */     
/*  88 */     if (manager instanceof PluginMetaHolder) {
/*  89 */       this.metaHolder = (PluginMetaHolder)manager;
/*  90 */     } else if (manager instanceof PluginHolder) {
/*  91 */       this.metaHolder = PluginMetaHolder.fromPluginHolder((PluginHolder)manager);
/*     */     } else {
/*  93 */       throw new IllegalArgumentException(manager.toString());
/*     */     } 
/*     */     
/*  96 */     this
/*     */       
/*  98 */       .brigadierManager = new CloudBrigadierManager(this.manager, SenderMapper.create(source -> baseType.equals(CommandSender.class) ? senderMapper.map(source.getSender()) : senderMapper.map(source), sender -> baseType.equals(CommandSender.class) ? (CommandSourceStack)(new BukkitBackwardsBrigadierSenderMapper(senderMapper)).apply(sender) : (CommandSourceStack)senderMapper.reverse(sender)));
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
/* 117 */     BukkitBrigadierMapper<C> mapper = new BukkitBrigadierMapper(Logger.getLogger(this.metaHolder.owningPluginMeta().getName()), this.brigadierManager);
/* 118 */     mapper.registerBuiltInMappings();
/* 119 */     PaperBrigadierMappings.register(mapper);
/*     */   }
/*     */   
/*     */   void registerPlugin(Plugin plugin) {
/* 123 */     plugin.getLifecycleManager().registerEventHandler((LifecycleEventType)LifecycleEvents.COMMANDS, this::register);
/*     */   }
/*     */   
/*     */   void registerBootstrap(BootstrapContext context) {
/* 127 */     context.getLifecycleManager().registerEventHandler((LifecycleEventType)LifecycleEvents.COMMANDS, this::register);
/*     */   }
/*     */   
/*     */   private void register(ReloadableRegistrarEvent<Commands> event) {
/* 131 */     this.lockRegistration.run();
/*     */     
/* 133 */     Commands commands = (Commands)event.registrar();
/* 134 */     this.commands = commands;
/*     */     
/* 136 */     this.aliases.clear();
/* 137 */     for (CommandNode<C> rootNode : (Iterable<CommandNode<C>>)this.manager.commandTree().rootNodes()) {
/* 138 */       registerCommand(commands, rootNode);
/*     */     }
/*     */   }
/*     */   
/*     */   private void registerCommand(Commands commands, CommandNode<C> rootNode) {
/* 143 */     Set<String> registered = commands.registerWithFlags(this.metaHolder
/* 144 */         .owningPluginMeta(), 
/* 145 */         createRootNode(rootNode, rootNode.component().name()), 
/* 146 */         findBukkitDescription(rootNode), new ArrayList(rootNode
/* 147 */           .component().alternativeAliases()), new HashSet(
/* 148 */           Collections.singletonList(CommandRegistrationFlag.FLATTEN_ALIASES)));
/*     */     
/* 150 */     this.aliases.put(rootNode.component().name(), registered);
/*     */   }
/*     */   
/*     */   private LiteralCommandNode<CommandSourceStack> createRootNode(CommandNode<C> rootNode, String label) {
/* 154 */     BrigadierPermissionChecker<C> permissionChecker = (sender, permission) -> (this.manager.commandTree().getNamedNode(rootNode.component().name()) == null) ? false : this.manager.testPermission(sender, permission).allowed();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 162 */     return this.brigadierManager.literalBrigadierNodeFactory().createNode(label, rootNode, (Command)new CloudBrigadierCommand(this.manager, this.brigadierManager, command -> BukkitHelper.stripNamespace(this.metaHolder.owningPluginMeta().getName(), command)), permissionChecker);
/*     */   }
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
/*     */   private String findBukkitDescription(CommandNode<C> node) {
/* 175 */     if (node.command() != null) {
/* 176 */       return BukkitHelper.description(node.command());
/*     */     }
/* 178 */     for (CommandNode<C> child : (Iterable<CommandNode<C>>)node.children()) {
/* 179 */       String result = findBukkitDescription(child);
/* 180 */       if (result != null) {
/* 181 */         return result;
/*     */       }
/*     */     } 
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasBrigadierManager() {
/* 189 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public CloudBrigadierManager<C, CommandSourceStack> brigadierManager() {
/* 194 */     return this.brigadierManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean registerCommand(Command<C> command) {
/* 199 */     if (!this.registeredCommands.add(command)) {
/* 200 */       return true;
/*     */     }
/* 202 */     Commands commands = this.commands;
/* 203 */     if (commands == null) {
/* 204 */       return true;
/*     */     }
/*     */     
/* 207 */     if (this.aliases.containsKey(command.rootComponent().name())) {
/*     */       
/* 209 */       CommandDispatcher<CommandSourceStack> dispatcher = unsafeGet(commands, Commands::getDispatcher);
/* 210 */       Set<String> set = this.aliases.get(command.rootComponent().name());
/* 211 */       LiteralCommandNode<CommandSourceStack> newRoot = createRootNode(this.manager
/* 212 */           .commandTree().getNamedNode(command.rootComponent().name()), command
/* 213 */           .rootComponent().name());
/*     */       
/* 215 */       for (String label : set) {
/*     */         
/* 217 */         CommandNode<CommandSourceStack> node = dispatcher.getRoot().getChild(label);
/* 218 */         for (CommandNode<CommandSourceStack> newChild : (Iterable<CommandNode<CommandSourceStack>>)newRoot.getChildren()) {
/* 219 */           node.addChild(newChild);
/*     */         }
/*     */       } 
/*     */     } else {
/* 223 */       unsafeOperation(commands, cmds -> registerCommand(cmds, this.manager.commandTree().getNamedNode(command.rootComponent().name())));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 229 */     resendCommands();
/*     */     
/* 231 */     Set<String> registered = this.aliases.get(command.rootComponent().name());
/*     */     
/* 233 */     boolean ret = (registered != null && !registered.isEmpty());
/* 234 */     if (!ret) {
/* 235 */       this.registeredCommands.remove(command);
/*     */     }
/* 237 */     return ret;
/*     */   }
/*     */   
/* 240 */   private static Method commandnodeRemoveMethod = null;
/*     */   
/*     */   private void unregisterRoot(Commands commands, String label) {
/* 243 */     Set<String> removed = this.aliases.remove(label);
/* 244 */     if (removed == null || removed.isEmpty()) {
/*     */       return;
/*     */     }
/* 247 */     this.registeredCommands.removeIf(command -> command.rootComponent().name().equals(label));
/*     */     
/*     */     try {
/* 250 */       if (commandnodeRemoveMethod == null) {
/* 251 */         commandnodeRemoveMethod = CommandNode.class.getMethod("removeCommand", new Class[] { String.class });
/*     */ 
/*     */         
/* 254 */         commandnodeRemoveMethod.setAccessible(true);
/*     */       } 
/* 256 */     } catch (ReflectiveOperationException e) {
/* 257 */       throw new RuntimeException("Failed to find removeCommand method", e);
/*     */     } 
/*     */     
/* 260 */     unsafeOperation(commands, cmds -> {
/*     */           CommandDispatcher<CommandSourceStack> dispatcher = cmds.getDispatcher();
/*     */           RootCommandNode<CommandSourceStack> root = dispatcher.getRoot();
/*     */           for (String removedLabel : removed) {
/*     */             try {
/*     */               commandnodeRemoveMethod.invoke(root, new Object[] { removedLabel });
/* 266 */             } catch (ReflectiveOperationException e) {
/*     */               throw new RuntimeException("Failed to delete node " + removedLabel, e);
/*     */             } 
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public void unregisterRootCommand(CommandComponent<C> rootCommand) {
/* 275 */     Commands commands = this.commands;
/* 276 */     if (commands == null) {
/*     */       return;
/*     */     }
/*     */     
/* 280 */     unregisterRoot(commands, rootCommand.name());
/*     */     
/* 282 */     resendCommands();
/*     */   }
/*     */   
/*     */   private void resendCommands() {
/* 286 */     for (Player player : this.metaHolder.owningPlugin().getServer().getOnlinePlayers()) {
/* 287 */       player.updateCommands();
/*     */     }
/*     */   }
/*     */   
/* 291 */   private static Field commandsInvalidField = null;
/*     */   
/*     */   private static void unsafeOperation(Commands commands, Consumer<Commands> task) {
/* 294 */     unsafeGet(commands, cmds -> {
/*     */           task.accept(cmds);
/*     */           return null;
/*     */         });
/*     */   }
/*     */   
/*     */   private static <T> T unsafeGet(Commands commands, Function<Commands, T> task) {
/*     */     try {
/* 302 */       if (commandsInvalidField == null) {
/* 303 */         commandsInvalidField = commands.getClass().getDeclaredField("invalid");
/* 304 */         commandsInvalidField.setAccessible(true);
/*     */       } 
/* 306 */       boolean prev = commandsInvalidField.getBoolean(commands);
/*     */       try {
/* 308 */         commandsInvalidField.setBoolean(commands, false);
/* 309 */         return task.apply(commands);
/*     */       } finally {
/* 311 */         commandsInvalidField.setBoolean(commands, prev);
/*     */       } 
/* 313 */     } catch (ReflectiveOperationException e) {
/* 314 */       throw new RuntimeException("Failed to perform unsafe command operation", e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\ModernPaperBrigadier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */