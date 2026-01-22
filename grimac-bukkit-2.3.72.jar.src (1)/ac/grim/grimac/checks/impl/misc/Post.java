/*     */ package ac.grim.grimac.checks.impl.misc;
/*     */ 
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.CheckData;
/*     */ import ac.grim.grimac.checks.type.PacketCheck;
/*     */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import ac.grim.grimac.utils.lists.EvictingQueue;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @CheckData(name = "Post")
/*     */ public class Post
/*     */   extends Check
/*     */   implements PacketCheck, PostPredictionCheck
/*     */ {
/*  36 */   private final ArrayDeque<PacketTypeCommon> post = new ArrayDeque<>();
/*     */ 
/*     */   
/*  39 */   private final List<String> flags = (List<String>)new EvictingQueue(10);
/*     */   private boolean sentFlying = false;
/*  41 */   private int isExemptFromSwingingCheck = Integer.MIN_VALUE;
/*     */   
/*     */   public Post(GrimPlayer playerData) {
/*  44 */     super(playerData);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/*  49 */     if (!this.flags.isEmpty()) {
/*     */ 
/*     */ 
/*     */       
/*  53 */       if (this.player.isTickingReliablyFor(3)) {
/*  54 */         for (String flag : this.flags) {
/*  55 */           flagAndAlert(flag);
/*     */         }
/*     */       }
/*     */       
/*  59 */       this.flags.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/*  65 */     if (event.getPacketType() == PacketType.Play.Server.ENTITY_ANIMATION) {
/*  66 */       WrapperPlayServerEntityAnimation animation = new WrapperPlayServerEntityAnimation(event);
/*  67 */       if (animation.getEntityId() == this.player.entityID && (
/*  68 */         animation.getType() == WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM || animation
/*  69 */         .getType() == WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_OFF_HAND)) {
/*  70 */         this.isExemptFromSwingingCheck = this.player.lastTransactionSent.get();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/*  78 */     if (isTickPacket(event.getPacketType())) {
/*  79 */       this.post.clear();
/*  80 */       this.sentFlying = true;
/*     */     } else {
/*     */       
/*  83 */       PacketTypeCommon packetType = event.getPacketType();
/*  84 */       if (isTransaction(packetType) && this.player.packetStateData.lastTransactionPacketWasValid) {
/*  85 */         if (this.sentFlying && !this.post.isEmpty()) {
/*  86 */           this.flags.add(((PacketTypeCommon)this.post.getFirst()).toString().toLowerCase(Locale.ROOT).replace("_", " ") + " v" + ((PacketTypeCommon)this.post.getFirst()).toString().toLowerCase(Locale.ROOT).replace("_", " "));
/*     */         }
/*  88 */         this.post.clear();
/*  89 */         this.sentFlying = false;
/*  90 */       } else if (PacketType.Play.Client.PLAYER_ABILITIES.equals(packetType) || (PacketType.Play.Client.HELD_ITEM_CHANGE
/*  91 */         .equals(packetType) && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8)) || PacketType.Play.Client.INTERACT_ENTITY
/*  92 */         .equals(packetType) || PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT.equals(packetType) || PacketType.Play.Client.USE_ITEM
/*  93 */         .equals(packetType) || PacketType.Play.Client.PLAYER_DIGGING.equals(packetType)) {
/*  94 */         if (this.sentFlying) this.post.add(event.getPacketType()); 
/*  95 */       } else if (PacketType.Play.Client.CLICK_WINDOW.equals(packetType) && this.player.getClientVersion().isOlderThan(ClientVersion.V_1_13)) {
/*     */         
/*  97 */         if (this.sentFlying) this.post.add(event.getPacketType()); 
/*  98 */       } else if (PacketType.Play.Client.ANIMATION.equals(packetType) && (this.player
/*  99 */         .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) || 
/* 100 */         PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8)) && this.player
/* 101 */         .getClientVersion().isOlderThan(ClientVersion.V_1_13) && this.isExemptFromSwingingCheck < this.player.lastTransactionReceived
/* 102 */         .get()) {
/* 103 */         if (this.sentFlying) this.post.add(event.getPacketType()); 
/* 104 */       } else if (PacketType.Play.Client.ENTITY_ACTION.equals(packetType) && (this.player
/* 105 */         .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) || (new WrapperPlayClientEntityAction(event)).getAction() != WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA)) {
/*     */         
/* 107 */         if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19_3) && this.player.inVehicle()) {
/*     */           return;
/*     */         }
/* 110 */         if (this.sentFlying) this.post.add(event.getPacketType()); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\misc\Post.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */