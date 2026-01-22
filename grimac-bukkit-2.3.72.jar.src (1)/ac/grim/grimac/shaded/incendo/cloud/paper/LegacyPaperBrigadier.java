/*     */ package ac.grim.grimac.shaded.incendo.cloud.paper;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandTree;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.BrigadierManagerHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierCommand;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.node.LiteralBrigadierNodeFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.permission.BrigadierPermissionChecker;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.PluginHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitBackwardsBrigadierSenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitHelper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*     */ import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
/*     */ import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
/*     */ import com.mojang.brigadier.Command;
/*     */ import java.util.function.Function;
/*     */ import java.util.regex.Pattern;
/*     */ import org.bukkit.command.PluginIdentifiableCommand;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
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
/*     */ class LegacyPaperBrigadier<C>
/*     */   implements Listener, BrigadierManagerHolder<C, BukkitBrigadierCommandSource>
/*     */ {
/*     */   private final CloudBrigadierManager<C, BukkitBrigadierCommandSource> brigadierManager;
/*     */   private final LegacyPaperCommandManager<C> paperCommandManager;
/*     */   
/*     */   LegacyPaperBrigadier(LegacyPaperCommandManager<C> paperCommandManager) {
/*  51 */     this.paperCommandManager = paperCommandManager;
/*  52 */     this
/*     */       
/*  54 */       .brigadierManager = new CloudBrigadierManager((CommandManager)this.paperCommandManager, SenderMapper.create(sender -> this.paperCommandManager.senderMapper().map(sender.getBukkitSender()), (Function)new BukkitBackwardsBrigadierSenderMapper(this.paperCommandManager
/*     */             
/*  56 */             .senderMapper())));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  61 */     BukkitBrigadierMapper<C> mapper = new BukkitBrigadierMapper(this.paperCommandManager.owningPlugin().getLogger(), this.brigadierManager);
/*  62 */     mapper.registerBuiltInMappings();
/*  63 */     PaperBrigadierMappings.register(mapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean hasBrigadierManager() {
/*  68 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public final CloudBrigadierManager<C, BukkitBrigadierCommandSource> brigadierManager() {
/*  73 */     return this.brigadierManager;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onCommandRegister(CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
/*     */     String label;
/*  81 */     if (!(event.getCommand() instanceof PluginIdentifiableCommand)) {
/*     */       return;
/*     */     }
/*  84 */     if (!((PluginIdentifiableCommand)event.getCommand()).getPlugin().equals(this.paperCommandManager.owningPlugin())) {
/*     */       return;
/*     */     }
/*     */     
/*  88 */     CommandTree<C> commandTree = this.paperCommandManager.commandTree();
/*     */ 
/*     */     
/*  91 */     if (event.getCommandLabel().contains(":")) {
/*  92 */       label = event.getCommandLabel().split(Pattern.quote(":"))[1];
/*     */     } else {
/*  94 */       label = event.getCommandLabel();
/*     */     } 
/*     */     
/*  97 */     CommandNode<C> node = commandTree.getNamedNode(label);
/*  98 */     if (node == null) {
/*     */       return;
/*     */     }
/*     */     
/* 102 */     BrigadierPermissionChecker<C> permissionChecker = (sender, permission) -> (commandTree.getNamedNode(label) == null) ? false : this.paperCommandManager.testPermission(sender, permission).allowed();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 111 */     LiteralBrigadierNodeFactory<C, BukkitBrigadierCommandSource> literalFactory = this.brigadierManager.literalBrigadierNodeFactory();
/* 112 */     event.setLiteral(literalFactory.createNode(event
/* 113 */           .getLiteral().getLiteral(), node, (Command)new CloudBrigadierCommand((CommandManager)this.paperCommandManager, this.brigadierManager, command -> BukkitHelper.stripNamespace((PluginHolder)this.paperCommandManager, command)), permissionChecker));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\LegacyPaperBrigadier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */