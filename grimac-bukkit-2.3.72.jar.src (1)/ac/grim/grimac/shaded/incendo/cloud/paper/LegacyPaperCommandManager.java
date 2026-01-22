/*     */ package ac.grim.grimac.shaded.incendo.cloud.paper;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CloudCapability;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.BrigadierManagerHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.BrigadierSetting;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.CloudBukkitCapabilities;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.PluginHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.ExecutionCoordinator;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.paper.suggestion.SuggestionListener;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.paper.suggestion.SuggestionListenerFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.Setting;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.state.RegistrationState;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.state.State;
/*     */ import java.util.function.Function;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.event.Listener;
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
/*     */ public class LegacyPaperCommandManager<C>
/*     */   extends BukkitCommandManager<C>
/*     */ {
/*  64 */   private BrigadierManagerHolder<C, ?> brigadierManagerHolder = null;
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
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public LegacyPaperCommandManager(Plugin owningPlugin, ExecutionCoordinator<C> commandExecutionCoordinator, SenderMapper<CommandSender, C> senderMapper) throws BukkitCommandManager.InitializationException {
/*  92 */     super(owningPlugin, commandExecutionCoordinator, senderMapper);
/*     */     
/*  94 */     registerCommandPreProcessor(new PaperCommandPreprocessor<>((PluginHolder)this, 
/*     */           
/*  96 */           senderMapper(), 
/*  97 */           (Function)Function.identity()));
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
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static LegacyPaperCommandManager<CommandSender> createNative(Plugin owningPlugin, ExecutionCoordinator<CommandSender> commandExecutionCoordinator) throws BukkitCommandManager.InitializationException {
/* 116 */     return new LegacyPaperCommandManager<>(owningPlugin, commandExecutionCoordinator, 
/*     */ 
/*     */         
/* 119 */         SenderMapper.identity());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void registerBrigadier() throws BukkitCommandManager.BrigadierInitializationException {
/* 141 */     registerBrigadier(true);
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
/*     */   @Deprecated
/*     */   public synchronized void registerLegacyPaperBrigadier() throws BukkitCommandManager.BrigadierInitializationException {
/* 156 */     registerBrigadier(false);
/*     */   }
/*     */   
/*     */   private void registerBrigadier(boolean allowModern) {
/* 160 */     requireState((State)RegistrationState.BEFORE_REGISTRATION);
/* 161 */     checkBrigadierCompatibility();
/*     */     
/* 163 */     if (this.brigadierManagerHolder != null) {
/* 164 */       throw new IllegalStateException("Brigadier is already registered! Holder: " + this.brigadierManagerHolder);
/*     */     }
/*     */     
/* 167 */     if (!hasCapability((CloudCapability)CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
/* 168 */       super.registerBrigadier();
/* 169 */     } else if (allowModern && CraftBukkitReflection.classExists("io.papermc.paper.command.brigadier.CommandSourceStack")) {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 174 */         ModernPaperBrigadier<C, CommandSender> brig = new ModernPaperBrigadier<>(CommandSender.class, (CommandManager<C>)this, senderMapper(), () -> rec$.lockRegistration());
/*     */ 
/*     */         
/* 177 */         this.brigadierManagerHolder = brig;
/* 178 */         brig.registerPlugin(owningPlugin());
/* 179 */         commandRegistrationHandler(brig);
/* 180 */       } catch (Exception e) {
/* 181 */         throw new BukkitCommandManager.BrigadierInitializationException("Failed to register ModernPaperBrigadier", e);
/*     */       } 
/*     */     } else {
/*     */       try {
/* 185 */         this.brigadierManagerHolder = new LegacyPaperBrigadier<>(this);
/* 186 */         Bukkit.getPluginManager().registerEvents((Listener)this.brigadierManagerHolder, owningPlugin());
/* 187 */         this.brigadierManagerHolder.brigadierManager().settings().set((Setting)BrigadierSetting.FORCE_EXECUTABLE, true);
/* 188 */       } catch (Exception e) {
/* 189 */         throw new BukkitCommandManager.BrigadierInitializationException("Failed to register LegacyPaperBrigadier", e);
/*     */       } 
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
/* 203 */     return (this.brigadierManagerHolder != null || super.hasBrigadierManager());
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
/* 216 */     if (this.brigadierManagerHolder != null) {
/* 217 */       return this.brigadierManagerHolder.brigadierManager();
/*     */     }
/* 219 */     return super.brigadierManager();
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
/*     */   public void registerAsynchronousCompletions() throws IllegalStateException {
/* 236 */     requireState((State)RegistrationState.BEFORE_REGISTRATION);
/* 237 */     if (!hasCapability((CloudCapability)CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
/* 238 */       throw new IllegalStateException("Failed to register asynchronous command completion listener.");
/*     */     }
/*     */     
/* 241 */     SuggestionListenerFactory<C> suggestionListenerFactory = SuggestionListenerFactory.create(this);
/* 242 */     SuggestionListener<C> suggestionListener = suggestionListenerFactory.createListener();
/*     */     
/* 244 */     Bukkit.getServer().getPluginManager().registerEvents((Listener)suggestionListener, 
/*     */         
/* 246 */         owningPlugin());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\LegacyPaperCommandManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */