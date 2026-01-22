/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.HashMap;
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
/*    */ public class DebugStickState
/*    */ {
/*    */   private Map<StateType, String> properties;
/*    */   
/*    */   public DebugStickState(Map<StateType, String> properties) {
/* 38 */     this.properties = properties;
/*    */   }
/*    */   
/*    */   public static DebugStickState read(PacketWrapper<?> wrapper) {
/* 42 */     NBTCompound compound = wrapper.readNBT();
/* 43 */     Map<StateType, String> properties = new HashMap<>(compound.size());
/* 44 */     for (Map.Entry<String, NBT> tag : (Iterable<Map.Entry<String, NBT>>)compound.getTags().entrySet()) {
/* 45 */       StateType stateType = StateTypes.getByName(tag.getKey());
/* 46 */       String property = ((NBTString)tag.getValue()).getValue();
/* 47 */       properties.put(stateType, property);
/*    */     } 
/* 49 */     return new DebugStickState(properties);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, DebugStickState state) {
/* 53 */     NBTCompound compound = new NBTCompound();
/* 54 */     for (Map.Entry<StateType, String> property : state.properties.entrySet()) {
/* 55 */       compound.setTag(((StateType)property.getKey()).getName(), (NBT)new NBTString(property
/* 56 */             .getValue()));
/*    */     }
/* 58 */     wrapper.writeNBT(compound);
/*    */   }
/*    */   @Nullable
/*    */   public String getProperty(StateType stateType) {
/* 62 */     return this.properties.get(stateType);
/*    */   }
/*    */   
/*    */   public void setProperty(StateType stateType, @Nullable String property) {
/* 66 */     if (property != null) {
/* 67 */       this.properties.put(stateType, property);
/*    */     } else {
/* 69 */       this.properties.remove(stateType);
/*    */     } 
/*    */   }
/*    */   
/*    */   public Map<StateType, String> getProperties() {
/* 74 */     return this.properties;
/*    */   }
/*    */   
/*    */   public void setProperties(Map<StateType, String> properties) {
/* 78 */     this.properties = properties;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 83 */     if (this == o) return true; 
/* 84 */     if (!(o instanceof DebugStickState)) return false; 
/* 85 */     DebugStickState that = (DebugStickState)o;
/* 86 */     return this.properties.equals(that.properties);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 91 */     return Objects.hashCode(this.properties);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\DebugStickState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */