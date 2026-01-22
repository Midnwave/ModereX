/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.event;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.nbt.api.BinaryTagHolder;
/*    */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
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
/*    */ public interface DataComponentValue
/*    */   extends Examinable
/*    */ {
/*    */   static Removed removed() {
/* 49 */     return RemovedDataComponentValueImpl.REMOVED;
/*    */   }
/*    */   
/*    */   public static interface Removed extends DataComponentValue {}
/*    */   
/*    */   public static interface TagSerializable extends DataComponentValue {
/*    */     @NotNull
/*    */     BinaryTagHolder asBinaryTag();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\event\DataComponentValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */