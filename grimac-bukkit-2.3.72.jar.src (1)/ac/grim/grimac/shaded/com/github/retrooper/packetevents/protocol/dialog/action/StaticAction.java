/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.action;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent.ClickEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent.ClickEventAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent.ClickEventActions;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ @NullMarked
/*    */ public class StaticAction
/*    */   implements Action
/*    */ {
/*    */   private final ActionType<?> actionType;
/*    */   private final ClickEvent clickEvent;
/*    */   
/*    */   public StaticAction(ClickEvent clickEvent) {
/* 35 */     if (!clickEvent.getAction().isAllowFromServer()) {
/* 36 */       throw new IllegalArgumentException("Can't create action for unreadable click event with action " + clickEvent
/* 37 */           .getAction());
/*    */     }
/* 39 */     this.actionType = (ActionType)ActionTypes.getRegistry().getByNameOrThrow(clickEvent.getAction().getName());
/* 40 */     this.clickEvent = clickEvent;
/*    */   }
/*    */   
/*    */   public static StaticAction decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 44 */     String actionName = compound.getStringTagValueOrThrow("type");
/* 45 */     ClickEventAction<?> action = (ClickEventAction)ClickEventActions.getRegistry().getByNameOrThrow(actionName);
/* 46 */     ClickEvent clickEvent = action.decode(compound, wrapper);
/* 47 */     return new StaticAction(clickEvent);
/*    */   }
/*    */ 
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, StaticAction action) {
/* 52 */     action.clickEvent.getAction().encode(compound, wrapper, action.clickEvent);
/*    */   }
/*    */ 
/*    */   
/*    */   public ActionType<?> getType() {
/* 57 */     return this.actionType;
/*    */   }
/*    */   
/*    */   public ClickEvent getClickEvent() {
/* 61 */     return this.clickEvent;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\action\StaticAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */