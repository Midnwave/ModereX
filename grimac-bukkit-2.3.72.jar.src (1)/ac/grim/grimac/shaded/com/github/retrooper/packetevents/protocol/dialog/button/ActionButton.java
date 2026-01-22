/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.button;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.action.Action;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
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
/*    */ 
/*    */ 
/*    */ @NullMarked
/*    */ public class ActionButton
/*    */ {
/*    */   private final CommonButtonData button;
/*    */   private final Action action;
/*    */   
/*    */   public ActionButton(CommonButtonData button, Action action) {
/* 35 */     this.button = button;
/* 36 */     this.action = action;
/*    */   }
/*    */   
/*    */   public static ActionButton decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 40 */     NBTCompound compound = (NBTCompound)nbt;
/* 41 */     CommonButtonData button = CommonButtonData.decode(compound, wrapper);
/* 42 */     Action action = (Action)compound.getOrNull("action", Action::decode, wrapper);
/* 43 */     return new ActionButton(button, action);
/*    */   }
/*    */   
/*    */   public static NBT encode(PacketWrapper<?> wrapper, ActionButton button) {
/* 47 */     NBTCompound compound = new NBTCompound();
/* 48 */     CommonButtonData.encode(compound, wrapper, button.button);
/* 49 */     if (button.action != null) {
/* 50 */       compound.set("action", button.action, Action::encode, wrapper);
/*    */     }
/* 52 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   public CommonButtonData getButton() {
/* 56 */     return this.button;
/*    */   }
/*    */   
/*    */   public Action getAction() {
/* 60 */     return this.action;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\button\ActionButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */