/*    */ package ac.grim.grimac.utils.team;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.UserProfile;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
/*    */ import ac.grim.grimac.shaded.fastutil.objects.ObjectIterator;
/*    */ import java.util.HashSet;
/*    */ import java.util.Objects;
/*    */ import java.util.Set;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class EntityTeam
/*    */ {
/*    */   public final String name;
/* 15 */   public final Set<String> entries = new HashSet<>(); private final GrimPlayer player; @Generated
/*    */   public WrapperPlayServerTeams.CollisionRule getCollisionRule() {
/* 17 */     return this.collisionRule;
/*    */   }
/*    */   private WrapperPlayServerTeams.CollisionRule collisionRule;
/*    */   public EntityTeam(GrimPlayer player, String name) {
/* 21 */     this.player = player;
/* 22 */     this.name = name;
/*    */   }
/*    */   
/*    */   public void update(WrapperPlayServerTeams teams) {
/* 26 */     teams.getTeamInfo().ifPresent(info -> this.collisionRule = info.getCollisionRule());
/*    */     
/* 28 */     TeamHandler teamHandler = (TeamHandler)this.player.checkManager.getPacketCheck(TeamHandler.class);
/* 29 */     WrapperPlayServerTeams.TeamMode mode = teams.getTeamMode();
/* 30 */     if (mode == WrapperPlayServerTeams.TeamMode.ADD_ENTITIES || mode == WrapperPlayServerTeams.TeamMode.CREATE) {
/*    */       
/* 32 */       for (String teamPlayer : teams.getPlayers()) {
/* 33 */         if (teamPlayer.equals(this.player.user.getName())) {
/* 34 */           teamHandler.setPlayerTeam(this);
/*    */           
/*    */           continue;
/*    */         } 
/* 38 */         for (ObjectIterator<UserProfile> objectIterator = this.player.compensatedEntities.profiles.values().iterator(); objectIterator.hasNext(); ) { UserProfile profile = objectIterator.next();
/* 39 */           if (profile.getName() != null && profile.getName().equals(teamPlayer)) {
/* 40 */             teamHandler.addEntityToTeam(profile.getUUID().toString(), this);
/*    */           } }
/*    */ 
/*    */ 
/*    */         
/* 45 */         teamHandler.addEntityToTeam(teamPlayer, this);
/*    */       } 
/* 47 */     } else if (mode == WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES) {
/*    */       
/* 49 */       for (String teamPlayer : teams.getPlayers()) {
/* 50 */         if (teamPlayer.equals(this.player.user.getName())) {
/*    */           
/* 52 */           teamHandler.setPlayerTeam(null);
/*    */           
/*    */           continue;
/*    */         } 
/* 56 */         for (ObjectIterator<UserProfile> objectIterator = this.player.compensatedEntities.profiles.values().iterator(); objectIterator.hasNext(); ) { UserProfile profile = objectIterator.next();
/* 57 */           if (profile.getName() != null && profile.getName().equals(teamPlayer)) {
/* 58 */             String uuid = profile.getUUID().toString();
/* 59 */             this.entries.remove(uuid);
/* 60 */             teamHandler.removeEntityFromTeam(uuid);
/*    */           }  }
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 66 */         teamHandler.removeEntityFromTeam(teamPlayer);
/* 67 */         this.entries.remove(teamPlayer);
/*    */       } 
/* 69 */     } else if (mode == WrapperPlayServerTeams.TeamMode.REMOVE) {
/*    */       
/* 71 */       EntityTeam playersTeam = teamHandler.getPlayerTeam();
/*    */       
/* 73 */       if (playersTeam != null && playersTeam.name.equals(this.name)) {
/* 74 */         teamHandler.setPlayerTeam(null);
/*    */       }
/*    */ 
/*    */       
/* 78 */       for (String entry : this.entries) {
/* 79 */         teamHandler.removeEntityFromTeam(entry);
/*    */       }
/* 81 */       this.entries.clear();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 87 */     if (this != o) { if (o instanceof EntityTeam) { EntityTeam t = (EntityTeam)o; if (Objects.equals(this.name, t.name)); }  return false; }
/*    */   
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 92 */     return Objects.hash(new Object[] { this.name });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\team\EntityTeam.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */