/*     */ package ac.grim.grimac.checks.impl.packetorder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public final class PacketOrderProcessor extends Check implements PacketCheck {
/*     */   private boolean openingInventory;
/*     */   private boolean swapping;
/*     */   private boolean dropping;
/*     */   private boolean interacting;
/*     */   private boolean attacking;
/*     */   private boolean releasing;
/*     */   private boolean digging;
/*     */   private boolean sprinting;
/*     */   private boolean sneaking;
/*     */   
/*     */   public PacketOrderProcessor(GrimPlayer player) {
/*  20 */     super(player);
/*     */   } private boolean placing; private boolean using; private boolean picking; private boolean clickingInInventory; private boolean closingInventory; private boolean quickMoveClicking; private boolean pickUpClicking; private boolean leavingBed; private boolean startingToGlide; private boolean jumpingWithMount; @Generated
/*     */   public boolean isOpeningInventory() {
/*  23 */     return this.openingInventory; } @Generated
/*  24 */   public boolean isSwapping() { return this.swapping; } @Generated
/*  25 */   public boolean isDropping() { return this.dropping; } @Generated
/*  26 */   public boolean isInteracting() { return this.interacting; } @Generated
/*  27 */   public boolean isAttacking() { return this.attacking; } @Generated
/*  28 */   public boolean isReleasing() { return this.releasing; } @Generated
/*  29 */   public boolean isDigging() { return this.digging; } @Generated
/*  30 */   public boolean isSprinting() { return this.sprinting; } @Generated
/*  31 */   public boolean isSneaking() { return this.sneaking; } @Generated
/*  32 */   public boolean isPlacing() { return this.placing; } @Generated
/*  33 */   public boolean isUsing() { return this.using; } @Generated
/*  34 */   public boolean isPicking() { return this.picking; } @Generated
/*  35 */   public boolean isClickingInInventory() { return this.clickingInInventory; } @Generated
/*  36 */   public boolean isClosingInventory() { return this.closingInventory; } @Generated
/*  37 */   public boolean isQuickMoveClicking() { return this.quickMoveClicking; } @Generated
/*  38 */   public boolean isPickUpClicking() { return this.pickUpClicking; } @Generated
/*  39 */   public boolean isLeavingBed() { return this.leavingBed; } @Generated
/*  40 */   public boolean isStartingToGlide() { return this.startingToGlide; } @Generated
/*  41 */   public boolean isJumpingWithMount() { return this.jumpingWithMount; }
/*     */ 
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/*  45 */     PacketTypeCommon packetType = event.getPacketType();
/*     */     
/*  47 */     if (packetType == PacketType.Play.Client.CLIENT_STATUS && (
/*  48 */       new WrapperPlayClientClientStatus(event)).getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT) {
/*  49 */       this.openingInventory = true;
/*     */     }
/*     */ 
/*     */     
/*  53 */     if (packetType == PacketType.Play.Client.INTERACT_ENTITY) {
/*  54 */       if ((new WrapperPlayClientInteractEntity(event)).getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
/*  55 */         this.attacking = true;
/*     */       } else {
/*  57 */         this.interacting = true;
/*     */       } 
/*     */     }
/*     */     
/*  61 */     if (packetType == PacketType.Play.Client.PLAYER_DIGGING) {
/*  62 */       switch ((new WrapperPlayClientPlayerDigging(event)).getAction()) { case QUICK_MOVE:
/*  63 */           this.swapping = true; break;
/*  64 */         case PICKUP: case PICKUP_ALL: this.dropping = true; break;
/*  65 */         case null: this.releasing = true; break;
/*  66 */         case null: case null: case null: this.digging = true;
/*     */           break; }
/*     */     
/*     */     }
/*  70 */     if (packetType == PacketType.Play.Client.ENTITY_ACTION) {
/*  71 */       switch ((new WrapperPlayClientEntityAction(event)).getAction()) { case QUICK_MOVE:
/*     */         case PICKUP:
/*  73 */           if (!this.player.inVehicle())
/*  74 */             this.sprinting = true;  break;
/*     */         case PICKUP_ALL:
/*     */         case null:
/*  77 */           this.sneaking = true; break;
/*  78 */         case null: this.leavingBed = true; break;
/*  79 */         case null: this.startingToGlide = true; break;
/*  80 */         case null: this.openingInventory = true; break;
/*  81 */         case null: case null: this.jumpingWithMount = true;
/*     */           break; }
/*     */     
/*     */     }
/*  85 */     if (packetType == PacketType.Play.Client.USE_ITEM) {
/*  86 */       this.using = true;
/*     */     }
/*     */     
/*  89 */     if (packetType == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
/*  90 */       if ((new WrapperPlayClientPlayerBlockPlacement(event)).getFace() == BlockFace.OTHER) {
/*  91 */         this.using = true;
/*     */       } else {
/*  93 */         this.placing = true;
/*     */       } 
/*     */     }
/*     */     
/*  97 */     if (packetType == PacketType.Play.Client.PICK_ITEM) {
/*  98 */       this.picking = true;
/*     */     }
/*     */     
/* 101 */     if (packetType == PacketType.Play.Client.CLICK_WINDOW) {
/* 102 */       this.clickingInInventory = true;
/*     */       
/* 104 */       switch ((new WrapperPlayClientClickWindow(event)).getWindowClickType()) { case QUICK_MOVE:
/* 105 */           this.quickMoveClicking = true; break;
/* 106 */         case PICKUP: case PICKUP_ALL: this.pickUpClicking = true;
/*     */           break; }
/*     */     
/*     */     } 
/* 110 */     if (packetType == PacketType.Play.Client.CLOSE_WINDOW) {
/* 111 */       this.closingInventory = true;
/*     */     }
/*     */     
/* 114 */     if (this.player.gamemode == GameMode.SPECTATOR || isTickPacket(packetType) || (this.player
/* 115 */       .getClientVersion().isOlderThan(ClientVersion.V_1_21_2) && 
/* 116 */       !this.player.compensatedWorld.isChunkLoaded(GrimMath.floor(this.player.x) >> 4, GrimMath.floor(this.player.z) >> 4))) {
/* 117 */       this.openingInventory = false;
/* 118 */       this.swapping = false;
/* 119 */       this.dropping = false;
/* 120 */       this.attacking = false;
/* 121 */       this.interacting = false;
/* 122 */       this.releasing = false;
/* 123 */       this.digging = false;
/* 124 */       this.placing = false;
/* 125 */       this.using = false;
/* 126 */       this.picking = false;
/* 127 */       this.sprinting = false;
/* 128 */       this.sneaking = false;
/* 129 */       this.clickingInInventory = false;
/* 130 */       this.closingInventory = false;
/* 131 */       this.quickMoveClicking = false;
/* 132 */       this.pickUpClicking = false;
/* 133 */       this.leavingBed = false;
/* 134 */       this.startingToGlide = false;
/* 135 */       this.jumpingWithMount = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public boolean isRightClicking() {
/* 141 */     return (this.placing || this.using || this.interacting);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderProcessor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */