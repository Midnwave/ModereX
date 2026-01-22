/*     */ package ac.grim.grimac.events.packets;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerInitializeWorldBorder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWorldBorder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWorldBorderCenter;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayWorldBorderLerpSize;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public class PacketWorldBorder extends Check implements PacketCheck {
/*     */   private double centerX;
/*     */   private double centerZ;
/*     */   
/*     */   @Generated
/*  18 */   public double getCenterX() { return this.centerX; } private double oldDiameter; private double newDiameter; private double absoluteMaxSize; @Generated
/*     */   public double getCenterZ() {
/*  20 */     return this.centerZ;
/*     */   }
/*     */   @Generated
/*     */   public double getAbsoluteMaxSize() {
/*  24 */     return this.absoluteMaxSize;
/*     */   }
/*  26 */   private long startTime = 1L;
/*  27 */   private long endTime = 1L;
/*     */   
/*     */   public PacketWorldBorder(GrimPlayer playerData) {
/*  30 */     super(playerData);
/*     */   }
/*     */   
/*     */   public double getCurrentDiameter() {
/*  34 */     double d0 = (System.currentTimeMillis() - this.startTime) / (this.endTime - this.startTime);
/*  35 */     return (d0 < 1.0D) ? GrimMath.lerp(d0, this.oldDiameter, this.newDiameter) : this.newDiameter;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/*  40 */     if (event.getPacketType() == PacketType.Play.Server.WORLD_BORDER) {
/*  41 */       WrapperPlayServerWorldBorder packet = new WrapperPlayServerWorldBorder(event);
/*     */       
/*  43 */       this.player.sendTransaction();
/*     */       
/*  45 */       if (packet.getAction() == WrapperPlayServerWorldBorder.WorldBorderAction.SET_SIZE) {
/*  46 */         double size = packet.getRadius();
/*  47 */         this.player.addRealTimeTaskNow(() -> setSize(size));
/*  48 */       } else if (packet.getAction() == WrapperPlayServerWorldBorder.WorldBorderAction.LERP_SIZE) {
/*  49 */         double oldDiameter = packet.getOldRadius();
/*  50 */         double newDiameter = packet.getNewRadius();
/*  51 */         long speed = packet.getSpeed();
/*  52 */         this.player.addRealTimeTaskNow(() -> setLerp(oldDiameter, newDiameter, speed));
/*  53 */       } else if (packet.getAction() == WrapperPlayServerWorldBorder.WorldBorderAction.SET_CENTER) {
/*  54 */         double centerX = packet.getCenterX();
/*  55 */         double centerZ = packet.getCenterZ();
/*  56 */         this.player.addRealTimeTaskNow(() -> setCenter(centerX, centerZ));
/*  57 */       } else if (packet.getAction() == WrapperPlayServerWorldBorder.WorldBorderAction.INITIALIZE) {
/*  58 */         double centerX = packet.getCenterX();
/*  59 */         double centerZ = packet.getCenterZ();
/*  60 */         double oldDiameter = packet.getOldRadius();
/*  61 */         double newDiameter = packet.getNewRadius();
/*  62 */         long speed = packet.getSpeed();
/*  63 */         int portalTeleportBoundary = packet.getPortalTeleportBoundary();
/*  64 */         this.player.addRealTimeTaskNow(() -> {
/*     */               setCenter(centerX, centerZ);
/*     */               
/*     */               setLerp(oldDiameter, newDiameter, speed);
/*     */               this.absoluteMaxSize = portalTeleportBoundary;
/*     */             });
/*     */       } 
/*     */     } 
/*  72 */     if (event.getPacketType() == PacketType.Play.Server.INITIALIZE_WORLD_BORDER) {
/*  73 */       this.player.sendTransaction();
/*  74 */       WrapperPlayServerInitializeWorldBorder packet = new WrapperPlayServerInitializeWorldBorder(event);
/*  75 */       double centerX = packet.getX();
/*  76 */       double centerZ = packet.getZ();
/*  77 */       double oldDiameter = packet.getOldDiameter();
/*  78 */       double newDiameter = packet.getNewDiameter();
/*  79 */       long speed = packet.getSpeed();
/*  80 */       int portalTeleportBoundary = packet.getPortalTeleportBoundary();
/*  81 */       this.player.addRealTimeTaskNow(() -> {
/*     */             setCenter(centerX, centerZ);
/*     */             
/*     */             setLerp(oldDiameter, newDiameter, speed);
/*     */             this.absoluteMaxSize = portalTeleportBoundary;
/*     */           });
/*     */     } 
/*  88 */     if (event.getPacketType() == PacketType.Play.Server.WORLD_BORDER_CENTER) {
/*  89 */       this.player.sendTransaction();
/*  90 */       WrapperPlayServerWorldBorderCenter packet = new WrapperPlayServerWorldBorderCenter(event);
/*  91 */       double centerX = packet.getX();
/*  92 */       double centerZ = packet.getZ();
/*  93 */       this.player.addRealTimeTaskNow(() -> setCenter(centerX, centerZ));
/*     */     } 
/*     */     
/*  96 */     if (event.getPacketType() == PacketType.Play.Server.WORLD_BORDER_SIZE) {
/*  97 */       this.player.sendTransaction();
/*  98 */       double size = (new WrapperPlayServerWorldBorderSize(event)).getDiameter();
/*  99 */       this.player.addRealTimeTaskNow(() -> setSize(size));
/*     */     } 
/*     */     
/* 102 */     if (event.getPacketType() == PacketType.Play.Server.WORLD_BORDER_LERP_SIZE) {
/* 103 */       this.player.sendTransaction();
/* 104 */       WrapperPlayWorldBorderLerpSize packet = new WrapperPlayWorldBorderLerpSize(event);
/* 105 */       double oldDiameter = packet.getOldDiameter();
/* 106 */       double newDiameter = packet.getNewDiameter();
/* 107 */       long speed = packet.getSpeed();
/* 108 */       this.player.addRealTimeTaskNow(() -> setLerp(oldDiameter, newDiameter, speed));
/*     */     } 
/*     */   }
/*     */   
/*     */   @Contract(mutates = "this")
/*     */   private void setCenter(double x, double z) {
/* 114 */     this.centerX = x;
/* 115 */     this.centerZ = z;
/*     */   }
/*     */   
/*     */   @Contract(mutates = "this")
/*     */   private void setSize(double size) {
/* 120 */     this.oldDiameter = size;
/* 121 */     this.newDiameter = size;
/*     */   }
/*     */   
/*     */   @Contract(mutates = "this")
/*     */   private void setLerp(double oldDiameter, double newDiameter, long length) {
/* 126 */     this.oldDiameter = oldDiameter;
/* 127 */     this.newDiameter = newDiameter;
/* 128 */     this.startTime = System.currentTimeMillis();
/* 129 */     this.endTime = this.startTime + length;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketWorldBorder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */