/*     */ package ac.grim.grimac.checks.impl.prediction;
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.checks.debug.AbstractDebugHandler;
/*     */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*     */ import ac.grim.grimac.platform.api.sender.Sender;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.MiniMessage;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import ac.grim.grimac.utils.lists.EvictingQueue;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
/*     */ 
/*     */ public class DebugHandler extends AbstractDebugHandler implements PostPredictionCheck {
/*  20 */   private static final Component GRAY_ARROW = MiniMessage.miniMessage().deserialize("<gray>→0.03→</gray>");
/*  21 */   private static final Component P_PREFIX = MiniMessage.miniMessage().deserialize("<reset>P: </reset>");
/*  22 */   private static final Component A_PREFIX = MiniMessage.miniMessage().deserialize("<reset>A: </reset>");
/*  23 */   private static final Component O_PREFIX = MiniMessage.miniMessage().deserialize("<reset>O: </reset>");
/*     */   
/*  25 */   private Set<GrimPlayer> listeners = new CopyOnWriteArraySet<>(new HashSet<>());
/*     */   
/*     */   private boolean outputToConsole = false;
/*     */   private boolean enabledFlags = false;
/*     */   private boolean lastMovementIsFlag = false;
/*  30 */   private final EvictingQueue<Component> predicted = new EvictingQueue(5);
/*  31 */   private final EvictingQueue<Component> actually = new EvictingQueue(5);
/*  32 */   private final EvictingQueue<Component> offset = new EvictingQueue(5);
/*     */   
/*     */   public DebugHandler(GrimPlayer player) {
/*  35 */     super(player);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/*  40 */     if (!predictionComplete.isChecked())
/*     */       return; 
/*  42 */     double offset = predictionComplete.getOffset();
/*     */     
/*  44 */     if (this.listeners.isEmpty() && !this.outputToConsole)
/*  45 */       return;  if (this.player.predictedVelocity.vector.lengthSquared() == 0.0D && offset == 0.0D)
/*     */       return; 
/*  47 */     String color = pickColor(offset, offset);
/*     */     
/*  49 */     Vector3dm predicted = this.player.predictedVelocity.vector;
/*  50 */     Vector3dm actually = this.player.actualMovement;
/*     */     
/*  52 */     String xColor = pickColor(Math.abs(predicted.getX() - actually.getX()), offset);
/*  53 */     String yColor = pickColor(Math.abs(predicted.getY() - actually.getY()), offset);
/*  54 */     String zColor = pickColor(Math.abs(predicted.getZ() - actually.getZ()), offset);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  62 */     Component p = ((TextComponent)((TextComponent)((TextComponent)((TextComponent)((TextComponent)Component.empty().append(P_PREFIX.color((TextColor)NamedTextColor.NAMES.value(color)))).append(Component.text(predicted.getX()).color((TextColor)NamedTextColor.NAMES.value(xColor)))).append((Component)Component.space())).append(Component.text(predicted.getY()).color((TextColor)NamedTextColor.NAMES.value(yColor)))).append((Component)Component.space())).append(Component.text(predicted.getZ()).color((TextColor)NamedTextColor.NAMES.value(zColor)));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  70 */     Component a = ((TextComponent)((TextComponent)((TextComponent)((TextComponent)((TextComponent)Component.empty().append(A_PREFIX.color((TextColor)NamedTextColor.NAMES.value(color)))).append(Component.text(actually.getX()).color((TextColor)NamedTextColor.NAMES.value(xColor)))).append((Component)Component.space())).append(Component.text(actually.getY()).color((TextColor)NamedTextColor.NAMES.value(yColor)))).append((Component)Component.space())).append(Component.text(actually.getZ()).color((TextColor)NamedTextColor.NAMES.value(zColor)));
/*     */     
/*  72 */     String canSkipTick = ("" + this.player.couldSkipTick + " ").substring(0, 1);
/*  73 */     String actualMovementSkip = "" + ("" + this.player.skippedTickInActualMovement).charAt(0) + " ";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  79 */     Component o = ((TextComponent)((TextComponent)((TextComponent)((TextComponent)Component.empty().append(Component.text(canSkipTick).color((TextColor)NamedTextColor.GRAY))).append(GRAY_ARROW)).append(Component.text(actualMovementSkip).color((TextColor)NamedTextColor.GRAY))).append(O_PREFIX.color((TextColor)NamedTextColor.NAMES.value(color)))).append((Component)Component.text(offset));
/*     */     
/*  81 */     String prefix = (this.player.platformPlayer == null) ? "null" : (this.player.platformPlayer.getName() + " ");
/*  82 */     TextComponent textComponent = Component.text(prefix);
/*     */     
/*  84 */     boolean thisFlag = (!color.equals("gray") && !color.equals("green"));
/*  85 */     if (this.enabledFlags) {
/*  86 */       if (this.lastMovementIsFlag) {
/*  87 */         this.predicted.clear();
/*  88 */         this.actually.clear();
/*  89 */         this.offset.clear();
/*     */       } 
/*  91 */       this.predicted.add(p);
/*  92 */       this.actually.add(a);
/*  93 */       this.offset.add(o);
/*  94 */       this.lastMovementIsFlag = thisFlag;
/*     */     } 
/*     */     
/*  97 */     if (thisFlag) {
/*  98 */       for (int i = 0; i < this.predicted.size(); i++) {
/*  99 */         this.player.user.sendMessage((Component)this.predicted.get(i));
/* 100 */         this.player.user.sendMessage((Component)this.actually.get(i));
/* 101 */         this.player.user.sendMessage((Component)this.offset.get(i));
/*     */       } 
/*     */     }
/*     */     
/* 105 */     for (GrimPlayer listener : this.listeners) {
/* 106 */       TextComponent textComponent1 = (listener == getPlayer()) ? Component.empty() : textComponent;
/* 107 */       listener.sendMessage(textComponent1.append(p));
/* 108 */       listener.sendMessage(textComponent1.append(a));
/* 109 */       listener.sendMessage(textComponent1.append(o));
/*     */     } 
/*     */     
/* 112 */     this.listeners.removeIf(player -> (player.platformPlayer != null && !player.platformPlayer.isOnline()));
/*     */     
/* 114 */     if (this.outputToConsole) {
/* 115 */       Sender consoleSender = GrimAPI.INSTANCE.getPlatformServer().getConsoleSender();
/* 116 */       consoleSender.sendMessage(p);
/* 117 */       consoleSender.sendMessage(a);
/* 118 */       consoleSender.sendMessage(o);
/*     */     } 
/*     */   }
/*     */   
/*     */   private String pickColor(double offset, double totalOffset) {
/* 123 */     if ((this.player.getSetbackTeleportUtil()).blockOffsets) return "gray"; 
/* 124 */     if (offset <= 0.0D || totalOffset <= 0.0D)
/* 125 */       return "gray"; 
/* 126 */     if (offset < 1.0E-4D)
/* 127 */       return "green"; 
/* 128 */     if (offset < 0.01D) {
/* 129 */       return "yellow";
/*     */     }
/* 131 */     return "red";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void toggleListener(GrimPlayer player) {
/* 137 */     if (!this.listeners.remove(player)) this.listeners.add(player);
/*     */   
/*     */   }
/*     */   
/*     */   public boolean toggleConsoleOutput() {
/* 142 */     this.outputToConsole = !this.outputToConsole;
/* 143 */     return this.outputToConsole;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\prediction\DebugHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */