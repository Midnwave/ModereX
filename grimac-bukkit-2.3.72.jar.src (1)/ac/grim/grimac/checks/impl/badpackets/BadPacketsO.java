/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ @CheckData(name = "BadPacketsO")
/*    */ public class BadPacketsO extends Check implements PacketCheck {
/* 17 */   private final LinkedList<Long> keepalives = new LinkedList<>();
/*    */   
/*    */   public BadPacketsO(GrimPlayer player) {
/* 20 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 25 */     if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
/* 26 */       this.keepalives.add(Long.valueOf((new WrapperPlayServerKeepAlive(event)).getId()));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 32 */     if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
/* 33 */       long id = (new WrapperPlayClientKeepAlive(event)).getId();
/*    */       
/* 35 */       for (Iterator<Long> iterator = this.keepalives.iterator(); iterator.hasNext(); ) { long keepalive = ((Long)iterator.next()).longValue();
/* 36 */         if (keepalive == id) {
/*    */           Long data;
/*    */           
/*    */           do {
/* 40 */             data = this.keepalives.poll();
/* 41 */           } while (data != null && data.longValue() != id);
/*    */           
/*    */           return;
/*    */         }  }
/*    */ 
/*    */       
/* 47 */       if (flagAndAlert("id=" + id) && shouldModifyPackets()) {
/* 48 */         event.setCancelled(true);
/* 49 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsO.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */