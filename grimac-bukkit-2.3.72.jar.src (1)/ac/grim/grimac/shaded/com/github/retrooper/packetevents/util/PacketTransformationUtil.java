/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.Equipment;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.LightData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateLight;
/*    */ import java.util.Collections;
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
/*    */ public class PacketTransformationUtil
/*    */ {
/*    */   public static PacketWrapper<?>[] transform(PacketWrapper<?> wrapper) {
/*    */     WrapperPlayServerSetPlayerInventory wrapperPlayServerSetPlayerInventory;
/* 37 */     if (wrapper instanceof WrapperPlayServerDestroyEntities) {
/* 38 */       WrapperPlayServerDestroyEntities destroyEntities = (WrapperPlayServerDestroyEntities)wrapper;
/* 39 */       int len = (destroyEntities.getEntityIds()).length;
/* 40 */       if (wrapper.getServerVersion() == ServerVersion.V_1_17 && len > 1) {
/*    */         
/* 42 */         PacketWrapper[] arrayOfPacketWrapper = new PacketWrapper[len];
/* 43 */         for (int i = 0; i < len; i++) {
/* 44 */           int entityId = destroyEntities.getEntityIds()[i];
/* 45 */           arrayOfPacketWrapper[i] = (PacketWrapper)new WrapperPlayServerDestroyEntities(entityId);
/*    */         } 
/* 47 */         return (PacketWrapper<?>[])arrayOfPacketWrapper;
/*    */       } 
/* 49 */     } else if (wrapper instanceof WrapperPlayServerEntityEquipment) {
/* 50 */       WrapperPlayServerEntityEquipment entityEquipment = (WrapperPlayServerEntityEquipment)wrapper;
/* 51 */       int len = entityEquipment.getEquipment().size();
/* 52 */       if (entityEquipment.getServerVersion().isOlderThan(ServerVersion.V_1_16) && len > 1) {
/*    */         
/* 54 */         PacketWrapper[] arrayOfPacketWrapper = new PacketWrapper[len];
/* 55 */         for (int i = 0; i < len; i++) {
/* 56 */           Equipment equipment = entityEquipment.getEquipment().get(i);
/* 57 */           arrayOfPacketWrapper[i] = (PacketWrapper)new WrapperPlayServerEntityEquipment(entityEquipment.getEntityId(), Collections.singletonList(equipment));
/*    */         } 
/* 59 */         return (PacketWrapper<?>[])arrayOfPacketWrapper;
/*    */       } 
/* 61 */     } else if (wrapper instanceof WrapperPlayServerChunkData) {
/* 62 */       WrapperPlayServerChunkData chunkData = (WrapperPlayServerChunkData)wrapper;
/* 63 */       LightData lightData = chunkData.getLightData();
/*    */       
/* 65 */       if (chunkData.getServerVersion().isOlderThan(ServerVersion.V_1_18) && lightData != null) {
/*    */         
/* 67 */         PacketWrapper[] arrayOfPacketWrapper = new PacketWrapper[2];
/* 68 */         arrayOfPacketWrapper[0] = (PacketWrapper)new WrapperPlayServerUpdateLight(chunkData
/* 69 */             .getColumn().getX(), chunkData
/* 70 */             .getColumn().getZ(), lightData);
/*    */ 
/*    */ 
/*    */         
/* 74 */         arrayOfPacketWrapper[1] = (PacketWrapper)chunkData;
/*    */         
/* 76 */         return (PacketWrapper<?>[])arrayOfPacketWrapper;
/*    */       } 
/* 78 */     } else if (wrapper instanceof WrapperPlayServerSetSlot) {
/*    */       WrapperPlayServerSetCursorItem wrapperPlayServerSetCursorItem;
/* 80 */       WrapperPlayServerSetSlot setSlot = (WrapperPlayServerSetSlot)wrapper;
/* 81 */       if (setSlot.getSlot() == -1) {
/* 82 */         if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/* 83 */           wrapperPlayServerSetCursorItem = new WrapperPlayServerSetCursorItem(setSlot.getItem());
/*    */         }
/* 85 */       } else if (setSlot.getWindowId() == -2 && 
/* 86 */         wrapperPlayServerSetCursorItem.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/* 87 */         wrapperPlayServerSetPlayerInventory = new WrapperPlayServerSetPlayerInventory(setSlot.getSlot(), setSlot.getItem());
/*    */       } 
/*    */     } 
/*    */     
/* 91 */     return (PacketWrapper<?>[])new PacketWrapper[] { (PacketWrapper)wrapperPlayServerSetPlayerInventory };
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\PacketTransformationUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */