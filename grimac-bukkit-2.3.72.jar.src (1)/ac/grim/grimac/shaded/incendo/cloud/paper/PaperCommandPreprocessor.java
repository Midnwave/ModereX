/*     */ package ac.grim.grimac.shaded.incendo.cloud.paper;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCommandContextKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.PluginHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitHelper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessor;
/*     */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.function.Function;
/*     */ import org.bukkit.command.BlockCommandSender;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Entity;
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
/*     */ final class PaperCommandPreprocessor<B, C>
/*     */   implements CommandPreprocessor<C>
/*     */ {
/*  47 */   private static final boolean FOLIA = CraftBukkitReflection.classExists("io.papermc.paper.threadedregions.RegionizedServer");
/*     */ 
/*     */   
/*     */   private final PluginHolder pluginHolder;
/*     */   
/*     */   private final SenderMapper<B, C> mapper;
/*     */   
/*     */   private final Function<B, CommandSender> senderExtractor;
/*     */ 
/*     */   
/*     */   PaperCommandPreprocessor(PluginHolder pluginHolder, SenderMapper<B, C> mapper, Function<B, CommandSender> senderExtractor) {
/*  58 */     this.pluginHolder = pluginHolder;
/*  59 */     this.mapper = mapper;
/*  60 */     this.senderExtractor = senderExtractor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void accept(CommandPreprocessingContext<C> ctx) {
/*  67 */     if (FOLIA) {
/*  68 */       ctx.commandContext().store(BukkitCommandContextKeys.SENDER_SCHEDULER_EXECUTOR, 
/*     */           
/*  70 */           foliaExecutorFor((C)ctx.commandContext().sender()));
/*     */     }
/*  72 */     else if (!(this.pluginHolder instanceof ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCommandManager)) {
/*  73 */       ctx.commandContext().store(BukkitCommandContextKeys.SENDER_SCHEDULER_EXECUTOR, 
/*     */           
/*  75 */           BukkitHelper.mainThreadExecutor(this.pluginHolder));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Executor foliaExecutorFor(C sender) {
/*  81 */     CommandSender commandSender = this.senderExtractor.apply((B)this.mapper.reverse(sender));
/*  82 */     Plugin plugin = this.pluginHolder.owningPlugin();
/*  83 */     if (commandSender instanceof Entity) {
/*  84 */       return task -> ((Entity)commandSender).getScheduler().run(plugin, (), null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  91 */     if (commandSender instanceof BlockCommandSender) {
/*  92 */       BlockCommandSender blockSender = (BlockCommandSender)commandSender;
/*  93 */       return task -> blockSender.getServer().getRegionScheduler().run(plugin, blockSender.getBlock().getLocation(), ());
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     return task -> plugin.getServer().getGlobalRegionScheduler().run(plugin, ());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\PaperCommandPreprocessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */