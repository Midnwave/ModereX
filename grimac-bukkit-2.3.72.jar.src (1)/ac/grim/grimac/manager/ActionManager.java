/*    */ package ac.grim.grimac.manager;
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class ActionManager extends Check implements PacketCheck {
/*    */   private boolean attacking = false;
/*    */   
/*    */   @Generated
/*    */   public boolean isAttacking() {
/* 13 */     return this.attacking;
/* 14 */   } private long lastAttack = 0L; @Generated public long getLastAttack() { return this.lastAttack; }
/*    */   
/*    */   public ActionManager(GrimPlayer player) {
/* 17 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 22 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
/* 23 */       WrapperPlayClientInteractEntity action = new WrapperPlayClientInteractEntity(event);
/* 24 */       if (action.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
/* 25 */         this.player.totalFlyingPacketsSent = 0;
/* 26 */         this.attacking = true;
/* 27 */         this.lastAttack = System.currentTimeMillis();
/*    */       } 
/* 29 */     } else if (isTickPacketIncludingNonMovement(event.getPacketType())) {
/* 30 */       this.player.totalFlyingPacketsSent++;
/* 31 */       this.attacking = false;
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean hasAttackedSince(long time) {
/* 36 */     return (System.currentTimeMillis() - this.lastAttack < time);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\ActionManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */