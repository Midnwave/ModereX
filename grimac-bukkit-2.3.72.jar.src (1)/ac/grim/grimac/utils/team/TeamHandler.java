/*    */ package ac.grim.grimac.utils.team;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
/*    */ import ac.grim.grimac.shaded.fastutil.objects.Object2ObjectOpenHashMap;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*    */ import java.util.Map;
/*    */ import java.util.UUID;
/*    */ import lombok.Generated;
/*    */ 
/*    */ 
/*    */ public class TeamHandler
/*    */   extends Check
/*    */   implements PacketCheck
/*    */ {
/* 21 */   private final Map<String, EntityTeam> entityTeams = (Map<String, EntityTeam>)new Object2ObjectOpenHashMap();
/* 22 */   private final Map<String, EntityTeam> entityToTeam = (Map<String, EntityTeam>)new Object2ObjectOpenHashMap();
/*    */   @Nullable
/* 24 */   private EntityTeam playerTeam = null; @Nullable @Generated public EntityTeam getPlayerTeam() { return this.playerTeam; } @Generated public void setPlayerTeam(@Nullable EntityTeam playerTeam) { this.playerTeam = playerTeam; }
/*    */   
/*    */   public TeamHandler(GrimPlayer player) {
/* 27 */     super(player);
/*    */   }
/*    */   
/*    */   public void addEntityToTeam(String entityTeamRepresentation, EntityTeam team) {
/* 31 */     this.entityToTeam.put(entityTeamRepresentation, team);
/*    */   }
/*    */   
/*    */   public void removeEntityFromTeam(String entityTeamRepresentation) {
/* 35 */     this.entityToTeam.remove(entityTeamRepresentation);
/*    */   }
/*    */ 
/*    */   
/*    */   public EntityTeam getEntityTeam(PacketEntity entity) {
/* 40 */     UUID uuid = entity.getUuid();
/* 41 */     return (uuid == null) ? null : this.entityToTeam.get(uuid.toString());
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 46 */     if (event.getPacketType() == PacketType.Play.Server.TEAMS) {
/* 47 */       WrapperPlayServerTeams teams = new WrapperPlayServerTeams(event);
/* 48 */       String teamName = teams.getTeamName();
/* 49 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*    */             EntityTeam newTeam;
/*    */             switch (teams.getTeamMode()) {
/*    */               case CREATE:
/*    */                 newTeam = new EntityTeam(this.player, teamName);
/*    */                 this.entityTeams.put(teamName, newTeam);
/*    */               case REMOVE:
/*    */               
/*    */               default:
/*    */                 break;
/*    */             } 
/*    */             EntityTeam entityTeam = this.entityTeams.get(teamName);
/*    */             if (entityTeam != null)
/*    */               entityTeam.update(teams); 
/*    */           });
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\team\TeamHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */