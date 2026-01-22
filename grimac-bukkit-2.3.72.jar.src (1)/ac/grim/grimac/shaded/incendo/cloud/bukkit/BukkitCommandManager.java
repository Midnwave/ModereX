/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CloudCapability;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapperHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.BrigadierManagerHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.Caption;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.ExecutionCoordinator;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandRegistrationHandler;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.state.RegistrationState;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.state.State;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Pair;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Triplet;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
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
/*     */ public abstract class BukkitCommandManager<C>
/*     */   extends CommandManager<C>
/*     */   implements BrigadierManagerHolder<C, Object>, SenderMapperHolder<CommandSender, C>, PluginHolder
/*     */ {
/*     */   private final Plugin owningPlugin;
/*     */   private final SenderMapper<CommandSender, C> senderMapper;
/*     */   private boolean splitAliases = false;
/*     */   
/*     */   @API(status = API.Status.INTERNAL, since = "2.0.0")
/*     */   protected BukkitCommandManager(Plugin owningPlugin, ExecutionCoordinator<C> commandExecutionCoordinator, SenderMapper<CommandSender, C> senderMapper) throws InitializationException {
/*  83 */     super(commandExecutionCoordinator, new BukkitPluginRegistrationHandler());
/*     */     try {
/*  85 */       ((BukkitPluginRegistrationHandler<C>)commandRegistrationHandler()).initialize(this);
/*  86 */     } catch (ReflectiveOperationException exception) {
/*  87 */       throw new InitializationException("Failed to initialize command registration handler", exception);
/*     */     } 
/*  89 */     this.owningPlugin = owningPlugin;
/*  90 */     this.senderMapper = senderMapper;
/*     */ 
/*     */     
/*  93 */     CloudBukkitCapabilities.CAPABLE.forEach(x$0 -> rec$.registerCapability(x$0));
/*  94 */     registerCapability((CloudCapability)CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
/*     */ 
/*     */     
/*  97 */     registerCommandPreProcessor(new BukkitCommandPreprocessor<>(this));
/*     */     
/*  99 */     BukkitParsers.register(this);
/*     */ 
/*     */     
/* 102 */     this.owningPlugin.getServer().getPluginManager().registerEvents(new CloudBukkitListener<>(this), this.owningPlugin);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 107 */     registerDefaultExceptionHandlers();
/* 108 */     captionRegistry().registerProvider((CaptionProvider)new BukkitDefaultCaptionsProvider());
/*     */   }
/*     */ 
/*     */   
/*     */   public final Plugin owningPlugin() {
/* 113 */     return this.owningPlugin;
/*     */   }
/*     */ 
/*     */   
/*     */   public final SenderMapper<CommandSender, C> senderMapper() {
/* 118 */     return this.senderMapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean hasPermission(C sender, String permission) {
/* 123 */     if (permission.isEmpty()) {
/* 124 */       return true;
/*     */     }
/* 126 */     return ((CommandSender)this.senderMapper.reverse(sender)).hasPermission(permission);
/*     */   }
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   protected final boolean splitAliases() {
/* 131 */     return this.splitAliases;
/*     */   }
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   protected final void splitAliases(boolean value) {
/* 136 */     requireState((State)RegistrationState.BEFORE_REGISTRATION);
/* 137 */     this.splitAliases = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void checkBrigadierCompatibility() throws BrigadierInitializationException {
/* 147 */     if (!hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
/* 148 */       throw new BrigadierInitializationException("Missing capability " + CloudBukkitCapabilities.class
/* 149 */           .getSimpleName() + "." + CloudBukkitCapabilities.BRIGADIER + " (Minecraft version too old? Brigadier was added in 1.13). See the Javadocs for more details");
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void registerBrigadier() throws BrigadierInitializationException {
/* 167 */     requireState((State)RegistrationState.BEFORE_REGISTRATION);
/* 168 */     checkBrigadierCompatibility();
/* 169 */     if (!hasCapability(CloudBukkitCapabilities.COMMODORE_BRIGADIER)) {
/* 170 */       throw new BrigadierInitializationException("Missing capability " + CloudBukkitCapabilities.class
/* 171 */           .getSimpleName() + "." + CloudBukkitCapabilities.COMMODORE_BRIGADIER + " (Minecraft version too new). See the Javadocs for more details");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 176 */     CommandRegistrationHandler<C> handler = commandRegistrationHandler();
/* 177 */     if (handler instanceof CloudCommodoreManager) {
/* 178 */       throw new IllegalStateException("Brigadier is already registered! Holder: " + handler);
/*     */     }
/*     */     try {
/* 181 */       CloudCommodoreManager<C> cloudCommodoreManager = new CloudCommodoreManager<>(this);
/* 182 */       cloudCommodoreManager.initialize(this);
/* 183 */       commandRegistrationHandler(cloudCommodoreManager);
/* 184 */       splitAliases(true);
/* 185 */     } catch (Exception e) {
/* 186 */       throw new BrigadierInitializationException("Unexpected exception initializing " + CloudCommodoreManager.class
/* 187 */           .getSimpleName(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public boolean hasBrigadierManager() {
/* 200 */     return commandRegistrationHandler() instanceof CloudCommodoreManager;
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
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public CloudBrigadierManager<C, ?> brigadierManager() {
/* 213 */     if (commandRegistrationHandler() instanceof CloudCommodoreManager) {
/* 214 */       return ((CloudCommodoreManager<C>)commandRegistrationHandler()).brigadierManager();
/*     */     }
/* 216 */     throw new BrigadierManagerHolder.BrigadierManagerNotPresent("The CloudBrigadierManager is either not supported in the current environment, or it is not enabled.");
/*     */   }
/*     */ 
/*     */   
/*     */   private void registerDefaultExceptionHandlers() {
/* 221 */     registerDefaultExceptionHandlers(triplet -> ((CommandSender)senderMapper().reverse(((CommandContext)triplet.first()).sender())).sendMessage(ChatColor.RED + ((CommandContext)triplet.first()).formatCaption((Caption)triplet.second(), (List)triplet.third())), pair -> owningPlugin().getLogger().log(Level.SEVERE, (String)pair.first(), (Throwable)pair.second()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void lockIfBrigadierCapable() {
/* 229 */     if (hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
/* 230 */       lockRegistration();
/*     */     }
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
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static final class InitializationException
/*     */     extends IllegalStateException
/*     */   {
/*     */     @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */     public InitializationException(String message, Throwable cause) {
/* 251 */       super(message, cause);
/*     */     }
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
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static final class BrigadierInitializationException
/*     */     extends IllegalStateException
/*     */   {
/*     */     @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */     public BrigadierInitializationException(String reason) {
/* 270 */       super(reason);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */     public BrigadierInitializationException(String reason, Throwable cause) {
/* 281 */       super(reason, cause);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\BukkitCommandManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */