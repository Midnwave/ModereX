/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public final class ComponentValue<T>
/*    */ {
/*    */   private final ComponentType<T> type;
/*    */   private final T value;
/*    */   
/*    */   public ComponentValue(ComponentType<T> type, T value) {
/* 31 */     this.type = type;
/* 32 */     this.value = value;
/*    */   }
/*    */   
/*    */   public static ComponentValue<?> read(PacketWrapper<?> wrapper) {
/* 36 */     ComponentType<?> type = (ComponentType)wrapper.readMappedEntity(ComponentTypes::getById);
/* 37 */     return read0(wrapper, type);
/*    */   }
/*    */ 
/*    */   
/*    */   private static <T> ComponentValue<T> read0(PacketWrapper<?> wrapper, ComponentType<T> type) {
/* 42 */     return new ComponentValue<>(type, type.read(wrapper));
/*    */   }
/*    */   
/*    */   public static <T> void write(PacketWrapper<?> wrapper, ComponentValue<T> value) {
/* 46 */     wrapper.writeMappedEntity(value.type);
/* 47 */     value.type.write(wrapper, value.value);
/*    */   }
/*    */   
/*    */   public ComponentType<T> getType() {
/* 51 */     return this.type;
/*    */   }
/*    */   
/*    */   public T getValue() {
/* 55 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 60 */     if (this == obj) return true; 
/* 61 */     if (!(obj instanceof ComponentValue)) return false; 
/* 62 */     ComponentValue<?> that = (ComponentValue)obj;
/* 63 */     if (!this.type.equals(that.type)) return false; 
/* 64 */     return this.value.equals(that.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 69 */     return Objects.hash(new Object[] { this.type, this.value });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     return "ComponentValue{type=" + this.type + ", value=" + this.value + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\ComponentValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */