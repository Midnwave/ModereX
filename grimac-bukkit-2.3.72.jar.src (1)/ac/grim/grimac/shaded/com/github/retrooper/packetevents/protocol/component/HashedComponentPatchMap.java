/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.function.IntFunction;
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
/*    */ public final class HashedComponentPatchMap
/*    */ {
/*    */   private final Map<ComponentType<?>, Integer> addedComponents;
/*    */   private final Set<ComponentType<?>> removedComponents;
/*    */   
/*    */   public HashedComponentPatchMap(Map<ComponentType<?>, Integer> addedComponents, Set<ComponentType<?>> removedComponents) {
/* 33 */     this.addedComponents = addedComponents;
/* 34 */     this.removedComponents = removedComponents;
/*    */   }
/*    */   
/*    */   public static HashedComponentPatchMap read(PacketWrapper<?> wrapper) {
/* 38 */     Map<ComponentType<?>, Integer> addedComponents = wrapper.readMap(ew -> (ComponentType)ew.readMappedEntity((IRegistry)ComponentTypes.getRegistry()), PacketWrapper::readInt);
/*    */     
/* 40 */     Set<ComponentType<?>> removedComponents = (Set<ComponentType<?>>)wrapper.readCollection(java.util.HashSet::new, ew -> (ComponentType)ew.readMappedEntity((IRegistry)ComponentTypes.getRegistry()));
/*    */     
/* 42 */     return new HashedComponentPatchMap(addedComponents, removedComponents);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, HashedComponentPatchMap map) {
/* 46 */     wrapper.writeMap(map.addedComponents, PacketWrapper::writeMappedEntity, PacketWrapper::writeInt);
/* 47 */     wrapper.writeCollection(map.removedComponents, PacketWrapper::writeMappedEntity);
/*    */   }
/*    */   
/*    */   public Map<ComponentType<?>, Integer> getAddedComponents() {
/* 51 */     return this.addedComponents;
/*    */   }
/*    */   
/*    */   public Set<ComponentType<?>> getRemovedComponents() {
/* 55 */     return this.removedComponents;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\HashedComponentPatchMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */