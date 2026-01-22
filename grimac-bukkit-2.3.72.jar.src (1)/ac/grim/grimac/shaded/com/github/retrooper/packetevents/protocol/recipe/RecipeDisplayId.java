/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public final class RecipeDisplayId
/*    */ {
/*    */   private final int id;
/*    */   
/*    */   public RecipeDisplayId(int id) {
/* 28 */     this.id = id;
/*    */   }
/*    */   
/*    */   public static RecipeDisplayId read(PacketWrapper<?> wrapper) {
/* 32 */     int id = wrapper.readVarInt();
/* 33 */     return new RecipeDisplayId(id);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, RecipeDisplayId id) {
/* 37 */     wrapper.writeVarInt(id.id);
/*    */   }
/*    */   
/*    */   public int getId() {
/* 41 */     return this.id;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\RecipeDisplayId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */