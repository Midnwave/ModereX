/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.configuration.client;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.common.client.WrapperCommonClientCustomClickAction;
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
/*    */ public class WrapperConfigClientCustomClickAction
/*    */   extends WrapperCommonClientCustomClickAction<WrapperConfigClientCustomClickAction>
/*    */ {
/*    */   public WrapperConfigClientCustomClickAction(PacketReceiveEvent event) {
/* 31 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperConfigClientCustomClickAction(ResourceLocation id, NBT payload) {
/* 35 */     super((PacketTypeCommon)PacketType.Configuration.Client.CUSTOM_CLICK_ACTION, id, payload);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\configuration\client\WrapperConfigClientCustomClickAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */