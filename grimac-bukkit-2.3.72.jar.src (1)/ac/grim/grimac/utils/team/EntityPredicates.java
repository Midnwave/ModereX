/*    */ package ac.grim.grimac.utils.team;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
/*    */ 
/*    */ public final class EntityPredicates {
/*    */   @Generated
/*    */   private EntityPredicates() {
/*  7 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean canBePushedBy(EntityTeam entityTeam, EntityTeam playersTeam) {
/* 32 */     WrapperPlayServerTeams.CollisionRule entityCollisionRule = (entityTeam == null) ? WrapperPlayServerTeams.CollisionRule.ALWAYS : entityTeam.getCollisionRule();
/* 33 */     if (entityCollisionRule == WrapperPlayServerTeams.CollisionRule.NEVER) return false;
/*    */     
/* 35 */     WrapperPlayServerTeams.CollisionRule playerCollisionRule = (playersTeam == null) ? WrapperPlayServerTeams.CollisionRule.ALWAYS : playersTeam.getCollisionRule();
/* 36 */     if (playerCollisionRule == WrapperPlayServerTeams.CollisionRule.NEVER) return false;
/*    */     
/* 38 */     boolean isSameTeam = (entityTeam != null && entityTeam.equals(playersTeam));
/* 39 */     return ((!isSameTeam || (entityCollisionRule != WrapperPlayServerTeams.CollisionRule.PUSH_OWN_TEAM && playerCollisionRule != WrapperPlayServerTeams.CollisionRule.PUSH_OWN_TEAM)) && ((entityCollisionRule != WrapperPlayServerTeams.CollisionRule.PUSH_OTHER_TEAMS && playerCollisionRule != WrapperPlayServerTeams.CollisionRule.PUSH_OTHER_TEAMS) || isSameTeam));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\team\EntityPredicates.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */