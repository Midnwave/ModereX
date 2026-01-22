/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data;
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
/*    */ public class EntityData<T>
/*    */ {
/*    */   private int index;
/*    */   private EntityDataType<T> type;
/*    */   private T value;
/*    */   
/*    */   public EntityData(int index, EntityDataType<T> type, T value) {
/* 28 */     this.index = index;
/* 29 */     this.type = type;
/* 30 */     this.value = value;
/*    */   }
/*    */   
/*    */   public int getIndex() {
/* 34 */     return this.index;
/*    */   }
/*    */   
/*    */   public void setIndex(int index) {
/* 38 */     this.index = index;
/*    */   }
/*    */   
/*    */   public EntityDataType<T> getType() {
/* 42 */     return this.type;
/*    */   }
/*    */   
/*    */   public void setType(EntityDataType<T> type) {
/* 46 */     this.type = type;
/*    */   }
/*    */   
/*    */   public T getValue() {
/* 50 */     return this.value;
/*    */   }
/*    */   
/*    */   public void setValue(T value) {
/* 54 */     this.value = value;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\data\EntityData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */