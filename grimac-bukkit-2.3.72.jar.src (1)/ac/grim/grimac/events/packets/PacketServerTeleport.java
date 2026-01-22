/*     */ package ac.grim.grimac.events.packets;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.teleport.RelativeFlag;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerRotation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerVehicleMove;
/*     */ import ac.grim.grimac.utils.data.Pair;
/*     */ import ac.grim.grimac.utils.data.RotationData;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Location;
/*     */ import java.util.Objects;
/*     */ 
/*     */ public class PacketServerTeleport extends PacketListenerAbstract {
/*  24 */   private static final boolean STUPID_TELEPORT_SYSTEM = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2);
/*     */   
/*     */   public PacketServerTeleport() {
/*  27 */     super(PacketListenerPriority.LOW);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/*  32 */     if (event.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
/*  33 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  34 */       if (player == null)
/*     */         return; 
/*  36 */       WrapperPlayServerPlayerPositionAndLook teleport = new WrapperPlayServerPlayerPositionAndLook(event);
/*     */       
/*  38 */       Vector3d pos = new Vector3d(teleport.getX(), teleport.getY(), teleport.getZ());
/*     */ 
/*     */       
/*  41 */       if (player.getSetbackTeleportUtil().getRequiredSetBack() == null) {
/*     */         
/*  43 */         player.x = teleport.getX();
/*  44 */         player.y = teleport.getY();
/*  45 */         player.z = teleport.getZ();
/*  46 */         player.xRot = teleport.getYaw();
/*  47 */         player.yRot = teleport.getPitch();
/*     */         
/*  49 */         player.lastX = teleport.getX();
/*  50 */         player.lastY = teleport.getY();
/*  51 */         player.lastZ = teleport.getZ();
/*  52 */         player.lastXRot = teleport.getYaw();
/*  53 */         player.lastYRot = teleport.getPitch();
/*     */         
/*  55 */         player.pollData();
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  64 */       if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8) || player.inVehicle()) {
/*  65 */         boolean relativeX = teleport.isRelativeFlag(RelativeFlag.X);
/*  66 */         boolean relativeY = teleport.isRelativeFlag(RelativeFlag.Y);
/*  67 */         boolean relativeZ = teleport.isRelativeFlag(RelativeFlag.Z);
/*     */         
/*  69 */         if (relativeX) {
/*  70 */           pos = pos.add(new Vector3d(player.x, 0.0D, 0.0D));
/*  71 */           teleport.setRelative(RelativeFlag.X, false);
/*     */         } 
/*     */         
/*  74 */         if (relativeY) {
/*  75 */           pos = pos.add(new Vector3d(0.0D, player.y, 0.0D));
/*  76 */           teleport.setRelative(RelativeFlag.Y, false);
/*     */         } 
/*     */         
/*  79 */         if (relativeZ) {
/*  80 */           pos = pos.add(new Vector3d(0.0D, 0.0D, player.z));
/*  81 */           teleport.setRelative(RelativeFlag.Z, false);
/*     */         } 
/*     */         
/*  84 */         if (relativeX || relativeY || relativeZ) {
/*  85 */           teleport.setX(pos.getX());
/*  86 */           teleport.setY(pos.getY());
/*  87 */           teleport.setZ(pos.getZ());
/*     */           
/*  89 */           event.markForReEncode(true);
/*     */         } 
/*     */       } 
/*     */       
/*  93 */       if (STUPID_TELEPORT_SYSTEM && player.inVehicle()) {
/*  94 */         boolean relativeDeltaX = teleport.isRelativeFlag(RelativeFlag.DELTA_X);
/*  95 */         boolean relativeDeltaY = teleport.isRelativeFlag(RelativeFlag.DELTA_Y);
/*  96 */         boolean relativeDeltaZ = teleport.isRelativeFlag(RelativeFlag.DELTA_Z);
/*     */         
/*  98 */         if (relativeDeltaX) {
/*  99 */           teleport.setRelative(RelativeFlag.DELTA_X, false);
/*     */         }
/*     */         
/* 102 */         if (relativeDeltaY) {
/* 103 */           teleport.setRelative(RelativeFlag.DELTA_Y, false);
/*     */         }
/*     */         
/* 106 */         if (relativeDeltaZ) {
/* 107 */           teleport.setRelative(RelativeFlag.DELTA_Z, false);
/*     */         }
/*     */         
/* 110 */         if (relativeDeltaX || relativeDeltaY || relativeDeltaZ) {
/* 111 */           teleport.setDeltaMovement(Vector3d.zero());
/* 112 */           event.markForReEncode(true);
/*     */         } 
/*     */       } 
/*     */       
/* 116 */       player.sendTransaction();
/* 117 */       int lastTransactionSent = player.lastTransactionSent.get();
/* 118 */       Objects.requireNonNull(player); event.getTasksAfterSend().add(player::sendTransaction);
/*     */       
/* 120 */       if (teleport.isDismountVehicle())
/*     */       {
/* 122 */         event.getTasksAfterSend().add(() -> player.compensatedEntities.self.eject());
/*     */       }
/*     */ 
/*     */       
/* 126 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_8)) {
/* 127 */         pos = pos.withY(pos.getY() - 1.62D);
/*     */       }
/* 129 */       Location target = new Location(null, pos.getX(), pos.getY(), pos.getZ());
/* 130 */       player.getSetbackTeleportUtil().addSentTeleport(target, teleport.getDeltaMovement(), lastTransactionSent, teleport.getRelativeFlags(), true, teleport.getTeleportId());
/*     */     } 
/*     */     
/* 133 */     if (event.getPacketType() == PacketType.Play.Server.PLAYER_ROTATION) {
/* 134 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 135 */       if (player == null)
/*     */         return; 
/* 137 */       WrapperPlayServerPlayerRotation packet = new WrapperPlayServerPlayerRotation(event);
/*     */ 
/*     */       
/* 140 */       if (!Float.isFinite(packet.getPitch())) {
/* 141 */         packet.setPitch(0.0F);
/* 142 */         event.markForReEncode(true);
/*     */       } 
/* 144 */       if (!Float.isFinite(packet.getYaw())) {
/* 145 */         packet.setYaw(0.0F);
/* 146 */         event.markForReEncode(true);
/*     */       } 
/*     */       
/* 149 */       player.sendTransaction();
/* 150 */       player.pendingRotations.add(new RotationData(packet.getYaw(), GrimMath.clamp(packet.getPitch() % 360.0F, -90.0F, 90.0F), player.getLastTransactionSent()));
/* 151 */       Objects.requireNonNull(player); event.getTasksAfterSend().add(player::sendTransaction);
/*     */     } 
/*     */     
/* 154 */     if (event.getPacketType() == PacketType.Play.Server.VEHICLE_MOVE) {
/* 155 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 156 */       if (player == null)
/*     */         return; 
/* 158 */       player.sendTransaction();
/* 159 */       Objects.requireNonNull(player); event.getTasksAfterSend().add(player::sendTransaction);
/* 160 */       player.vehicleData.vehicleTeleports.add(new Pair(
/* 161 */             Integer.valueOf(player.lastTransactionSent.get()), (new WrapperPlayServerVehicleMove(event))
/* 162 */             .getPosition()));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketServerTeleport.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */