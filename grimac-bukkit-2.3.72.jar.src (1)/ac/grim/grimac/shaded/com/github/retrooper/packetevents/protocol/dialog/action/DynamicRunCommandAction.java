/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.action;
/*    */ 
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
/*    */ public class DynamicRunCommandAction
/*    */   implements Action
/*    */ {
/*    */   private final DialogTemplate template;
/*    */   
/*    */   public DynamicRunCommandAction(DialogTemplate template) {
/* 31 */     this.template = template;
/*    */   }
/*    */   
/*    */   public static DynamicRunCommandAction decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 35 */     DialogTemplate template = (DialogTemplate)compound.getOrThrow("template", DialogTemplate::decode, wrapper);
/* 36 */     return new DynamicRunCommandAction(template);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, DynamicRunCommandAction action) {
/* 40 */     compound.set("template", action.template, DialogTemplate::encode, wrapper);
/*    */   }
/*    */   
/*    */   public DialogTemplate getTemplate() {
/* 44 */     return this.template;
/*    */   }
/*    */ 
/*    */   
/*    */   public ActionType<?> getType() {
/* 49 */     return ActionTypes.DYNAMIC_RUN_COMMAND;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\action\DynamicRunCommandAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */