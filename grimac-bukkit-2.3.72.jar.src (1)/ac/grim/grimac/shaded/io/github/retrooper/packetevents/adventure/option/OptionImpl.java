/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option;
/*    */ 
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option.value.ValueType;
/*    */ import java.util.Objects;
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
/*    */ final class OptionImpl<V>
/*    */   implements Option<V>
/*    */ {
/*    */   private final String id;
/*    */   private final ValueType<V> type;
/*    */   private final V defaultValue;
/*    */   
/*    */   OptionImpl(String id, ValueType<V> type, V defaultValue) {
/* 37 */     this.id = id;
/* 38 */     this.type = type;
/* 39 */     this.defaultValue = defaultValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public String id() {
/* 44 */     return this.id;
/*    */   }
/*    */ 
/*    */   
/*    */   public ValueType<V> valueType() {
/* 49 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public V defaultValue() {
/* 54 */     return this.defaultValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 59 */     if (this == other) return true; 
/* 60 */     if (other == null || getClass() != other.getClass()) return false; 
/* 61 */     OptionImpl<?> that = (OptionImpl)other;
/* 62 */     return (Objects.equals(this.id, that.id) && 
/* 63 */       Objects.equals(this.type, that.type));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 68 */     return Objects.hash(new Object[] { this.id, this.type });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return getClass().getSimpleName() + "{id=" + this.id + ",type=" + this.type + ",defaultValue=" + this.defaultValue + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\option\OptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */