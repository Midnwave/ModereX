/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitHelper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandRegistrationHandler;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.ManagerSetting;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.Setting;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandMap;
/*     */ import org.bukkit.command.PluginIdentifiableCommand;
/*     */ import org.bukkit.command.SimpleCommandMap;
/*     */ import org.bukkit.entity.Player;
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
/*     */ public class BukkitPluginRegistrationHandler<C>
/*     */   implements CommandRegistrationHandler<C>
/*     */ {
/*  53 */   private final Map<CommandComponent<C>, RegisteredCommandData<C>> registeredCommands = new HashMap<>();
/*  54 */   private final Set<String> recognizedAliases = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
/*     */   
/*     */   private Map<String, Command> bukkitCommands;
/*     */   
/*     */   private BukkitCommandManager<C> bukkitCommandManager;
/*     */   
/*     */   private CommandMap commandMap;
/*     */ 
/*     */   
/*     */   final void initialize(BukkitCommandManager<C> bukkitCommandManager) throws ReflectiveOperationException {
/*  64 */     Method getCommandMap = Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap", new Class[0]);
/*  65 */     getCommandMap.setAccessible(true);
/*  66 */     this.commandMap = (CommandMap)getCommandMap.invoke(Bukkit.getServer(), new Object[0]);
/*  67 */     Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
/*  68 */     knownCommands.setAccessible(true);
/*     */     
/*  70 */     Map<String, Command> bukkitCommands = (Map<String, Command>)knownCommands.get(this.commandMap);
/*  71 */     this.bukkitCommands = bukkitCommands;
/*  72 */     this.bukkitCommandManager = bukkitCommandManager;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean registerCommand(Command<C> command) {
/*  78 */     CommandComponent<C> component = command.rootComponent();
/*  79 */     if (!(this.bukkitCommandManager.commandRegistrationHandler() instanceof CloudCommodoreManager) && this.registeredCommands
/*  80 */       .containsKey(component)) {
/*  81 */       return false;
/*     */     }
/*  83 */     String label = component.name();
/*  84 */     String namespacedLabel = BukkitHelper.namespacedLabel(this.bukkitCommandManager, label);
/*     */     
/*  86 */     List<String> aliases = new ArrayList<>(component.alternativeAliases());
/*     */     
/*  88 */     BukkitCommand<C> bukkitCommand = new BukkitCommand<>(label, aliases, command, component, this.bukkitCommandManager);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     if (this.bukkitCommandManager.settings().get((Setting)ManagerSetting.OVERRIDE_EXISTING_COMMANDS)) {
/*  97 */       this.bukkitCommands.remove(label);
/*  98 */       Objects.requireNonNull(this.bukkitCommands); aliases.forEach(this.bukkitCommands::remove);
/*     */     } 
/*     */     
/* 101 */     Set<String> newAliases = new HashSet<>();
/*     */     
/* 103 */     for (String alias : aliases) {
/* 104 */       String namespacedAlias = BukkitHelper.namespacedLabel(this.bukkitCommandManager, alias);
/* 105 */       newAliases.add(namespacedAlias);
/* 106 */       if (!bukkitCommandOrAliasExists(alias)) {
/* 107 */         newAliases.add(alias);
/*     */       }
/*     */     } 
/*     */     
/* 111 */     if (!bukkitCommandExists(label)) {
/* 112 */       newAliases.add(label);
/*     */     }
/* 114 */     newAliases.add(namespacedLabel);
/*     */     
/* 116 */     this.commandMap.register(label, this.bukkitCommandManager
/*     */         
/* 118 */         .owningPlugin().getName().toLowerCase(Locale.ROOT), bukkitCommand);
/*     */ 
/*     */ 
/*     */     
/* 122 */     this.recognizedAliases.addAll(newAliases);
/* 123 */     if (this.bukkitCommandManager.splitAliases()) {
/* 124 */       newAliases.forEach(alias -> registerExternal(alias, command, bukkitCommand));
/*     */     }
/*     */     
/* 127 */     this.registeredCommands.put(component, new RegisteredCommandData<>(bukkitCommand, newAliases));
/* 128 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void unregisterRootCommand(CommandComponent<C> component) {
/* 135 */     RegisteredCommandData<C> registeredCommand = this.registeredCommands.get(component);
/* 136 */     if (registeredCommand == null) {
/*     */       return;
/*     */     }
/* 139 */     registeredCommand.bukkit.disable();
/*     */     
/* 141 */     Set<String> registeredAliases = registeredCommand.recognizedAliases;
/*     */     
/* 143 */     for (String alias : registeredAliases) {
/* 144 */       this.bukkitCommands.remove(alias);
/*     */     }
/*     */     
/* 147 */     this.recognizedAliases.removeAll(registeredAliases);
/* 148 */     if (this.bukkitCommandManager.splitAliases()) {
/* 149 */       registeredAliases.forEach(this::unregisterExternal);
/*     */     }
/*     */     
/* 152 */     this.registeredCommands.remove(component);
/*     */     
/* 154 */     if (this.bukkitCommandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER))
/*     */     {
/* 156 */       Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRecognized(String alias) {
/* 167 */     return this.recognizedAliases.contains(alias);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void registerExternal(String label, Command<?> command, BukkitCommand<C> bukkitCommand) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "1.7.0")
/*     */   protected void unregisterExternal(String label) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean bukkitCommandExists(String commandLabel) {
/* 188 */     Command existingCommand = this.bukkitCommands.get(commandLabel);
/* 189 */     if (existingCommand == null) {
/* 190 */       return false;
/*     */     }
/* 192 */     if (existingCommand instanceof PluginIdentifiableCommand) {
/* 193 */       return (existingCommand.getLabel().equals(commandLabel) && 
/*     */         
/* 195 */         !((PluginIdentifiableCommand)existingCommand).getPlugin().getName().equalsIgnoreCase(this.bukkitCommandManager.owningPlugin().getName()));
/*     */     }
/* 197 */     return existingCommand.getLabel().equals(commandLabel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean bukkitCommandOrAliasExists(String commandLabel) {
/* 207 */     Command command = this.bukkitCommands.get(commandLabel);
/* 208 */     if (command instanceof PluginIdentifiableCommand) {
/* 209 */       return 
/* 210 */         !((PluginIdentifiableCommand)command).getPlugin().getName().equalsIgnoreCase(this.bukkitCommandManager.owningPlugin().getName());
/*     */     }
/* 212 */     return (command != null);
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class RegisteredCommandData<C>
/*     */   {
/*     */     private final BukkitCommand<C> bukkit;
/*     */     
/*     */     private final Set<String> recognizedAliases;
/*     */     
/*     */     private RegisteredCommandData(BukkitCommand<C> bukkit, Set<String> recognizedAliases) {
/* 223 */       this.bukkit = bukkit;
/* 224 */       Set<String> treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
/* 225 */       treeSet.addAll(recognizedAliases);
/* 226 */       this.recognizedAliases = Collections.unmodifiableSet(treeSet);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\BukkitPluginRegistrationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */