/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateValue;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Map;
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
/*    */ public class ItemBlockStateProperties
/*    */ {
/*    */   private Map<String, String> properties;
/*    */   
/*    */   public ItemBlockStateProperties(Map<String, String> properties) {
/* 33 */     this.properties = properties;
/*    */   }
/*    */   
/*    */   public static ItemBlockStateProperties read(PacketWrapper<?> wrapper) {
/* 37 */     Map<String, String> properties = wrapper.readMap(PacketWrapper::readString, PacketWrapper::readString);
/*    */     
/* 39 */     return new ItemBlockStateProperties(properties);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemBlockStateProperties props) {
/* 43 */     wrapper.writeMap(props.properties, PacketWrapper::writeString, PacketWrapper::writeString);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Object getProperty(StateValue stateValue) {
/* 49 */     String value = getProperty(stateValue.getName());
/* 50 */     if (value != null) {
/* 51 */       return stateValue.getParser().apply(value);
/*    */     }
/* 53 */     return null;
/*    */   }
/*    */   @Nullable
/*    */   public String getProperty(String key) {
/* 57 */     return this.properties.get(key);
/*    */   }
/*    */   
/*    */   public void setProperty(StateValue stateValue, @Nullable Object value) {
/* 61 */     setProperty(stateValue.getName(), (value == null) ? null : value.toString());
/*    */   }
/*    */   
/*    */   public void setProperty(String key, @Nullable String value) {
/* 65 */     if (value == null) {
/* 66 */       this.properties.remove(key);
/*    */     } else {
/* 68 */       this.properties.put(key, value);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void unsetProperty(StateValue stateValue) {
/* 73 */     unsetProperty(stateValue.getName());
/*    */   }
/*    */   
/*    */   public void unsetProperty(String key) {
/* 77 */     setProperty(key, (String)null);
/*    */   }
/*    */   
/*    */   public Map<String, String> getProperties() {
/* 81 */     return this.properties;
/*    */   }
/*    */   
/*    */   public void setProperties(Map<String, String> properties) {
/* 85 */     this.properties = properties;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 90 */     if (this == obj) return true; 
/* 91 */     if (!(obj instanceof ItemBlockStateProperties)) return false; 
/* 92 */     ItemBlockStateProperties that = (ItemBlockStateProperties)obj;
/* 93 */     return this.properties.equals(that.properties);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 98 */     return Objects.hashCode(this.properties);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemBlockStateProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */