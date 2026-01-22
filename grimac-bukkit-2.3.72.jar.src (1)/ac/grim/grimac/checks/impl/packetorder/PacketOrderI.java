/*     */ package ac.grim.grimac.checks.impl.packetorder;
/*     */ import ac.grim.grimac.api.config.ConfigManager;
/*     */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import ac.grim.grimac.utils.nmsutil.BlockBreakSpeed;
/*     */ import java.util.ArrayDeque;
/*     */ 
/*     */ @CheckData(name = "PacketOrderI", experimental = true)
/*     */ public class PacketOrderI extends Check implements PostPredictionCheck {
/*     */   private boolean exemptPlacingWhileDigging;
/*     */   private boolean setback;
/*     */   
/*     */   public PacketOrderI(GrimPlayer player) {
/*  22 */     super(player);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  29 */     this.flags = new ArrayDeque<>();
/*     */   }
/*     */   private boolean digging; private final ArrayDeque<String> flags;
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/*  33 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
/*  34 */       if ((new WrapperPlayClientInteractEntity(event)).getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
/*  35 */         if (this.player.packetOrderProcessor.isRightClicking() || this.player.packetOrderProcessor.isPicking() || this.player.packetOrderProcessor.isReleasing() || this.player.packetOrderProcessor.isDigging()) {
/*     */ 
/*     */ 
/*     */           
/*  39 */           String verbose = "type=attack, rightClicking=" + this.player.packetOrderProcessor.isRightClicking() + ", picking=" + this.player.packetOrderProcessor.isPicking() + ", releasing=" + this.player.packetOrderProcessor.isReleasing() + ", digging=" + this.player.packetOrderProcessor.isDigging();
/*  40 */           if (!this.player.canSkipTicks()) {
/*  41 */             if (flagAndAlert(verbose) && shouldModifyPackets()) {
/*  42 */               event.setCancelled(true);
/*  43 */               this.player.onPacketCancel();
/*     */             } 
/*     */           } else {
/*  46 */             this.flags.add(verbose);
/*     */           } 
/*     */         } 
/*  49 */       } else if (this.player.packetOrderProcessor.isReleasing() || this.player.packetOrderProcessor.isDigging()) {
/*  50 */         String verbose = "type=interact, releasing=" + this.player.packetOrderProcessor.isReleasing() + ", digging=" + this.player.packetOrderProcessor.isDigging();
/*  51 */         if (!this.player.canSkipTicks()) {
/*  52 */           if (flagAndAlert(verbose) && shouldModifyPackets()) {
/*  53 */             event.setCancelled(true);
/*  54 */             this.player.onPacketCancel();
/*     */           } 
/*     */         } else {
/*  57 */           this.flags.add(verbose);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*  62 */     if ((event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT || event.getPacketType() == PacketType.Play.Client.USE_ITEM) && (
/*  63 */       this.player.packetOrderProcessor.isReleasing() || this.digging)) {
/*  64 */       String verbose = "type=place/use, releasing=" + this.player.packetOrderProcessor.isReleasing() + ", digging=" + this.digging;
/*  65 */       if (!this.player.canSkipTicks()) {
/*  66 */         if (flagAndAlert(verbose) && shouldModifyPackets()) {
/*  67 */           event.setCancelled(true);
/*  68 */           this.player.onPacketCancel();
/*     */         } 
/*     */       } else {
/*  71 */         this.flags.add(verbose);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  76 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
/*  77 */       double damage; WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);
/*     */       
/*  79 */       switch (packet.getAction()) {
/*     */         case RELEASE_USE_ITEM:
/*  81 */           if (this.player.packetOrderProcessor.isAttacking() || this.player.packetOrderProcessor.isRightClicking() || this.player.packetOrderProcessor.isPicking() || this.player.packetOrderProcessor.isDigging()) {
/*     */ 
/*     */ 
/*     */             
/*  85 */             String verbose = "type=release, attacking=" + this.player.packetOrderProcessor.isAttacking() + ", rightClicking=" + this.player.packetOrderProcessor.isRightClicking() + ", picking=" + this.player.packetOrderProcessor.isPicking() + ", digging=" + this.player.packetOrderProcessor.isDigging();
/*  86 */             if (!this.player.canSkipTicks()) {
/*  87 */               if (flagAndAlert(verbose))
/*  88 */                 this.setback = true; 
/*     */               break;
/*     */             } 
/*  91 */             this.flags.add(verbose);
/*  92 */             this.setback = true;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case START_DIGGING:
/*  97 */           damage = BlockBreakSpeed.getBlockDamage(this.player, this.player.compensatedWorld.getBlock(packet.getBlockPosition()));
/*  98 */           if (damage >= 1.0D || (damage <= 0.0D && this.player.gamemode == GameMode.CREATIVE))
/*     */             return; 
/*     */         case CANCELLED_DIGGING:
/*     */         case FINISHED_DIGGING:
/* 102 */           if (this.exemptPlacingWhileDigging || this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10)) {
/*     */             return;
/*     */           }
/* 105 */           this.digging = true;
/*     */           break;
/*     */       } 
/*     */     } 
/* 109 */     if (this.player.gamemode == GameMode.SPECTATOR || isTickPacket(event.getPacketType())) {
/* 110 */       this.digging = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 116 */     if (!this.player.canSkipTicks()) {
/* 117 */       if (this.setback) {
/* 118 */         setbackIfAboveSetbackVL();
/* 119 */         this.setback = false;
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 124 */     if (this.player.isTickingReliablyFor(3)) {
/* 125 */       for (String verbose : this.flags) {
/* 126 */         if (flagAndAlert(verbose) && this.setback) {
/* 127 */           setbackIfAboveSetbackVL();
/* 128 */           this.setback = false;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 133 */     this.flags.clear();
/* 134 */     this.setback = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onReload(ConfigManager config) {
/* 139 */     this.exemptPlacingWhileDigging = config.getBooleanElse(getConfigName() + ".exempt-placing-while-digging", false);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderI.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */