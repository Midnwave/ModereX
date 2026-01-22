/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Map;
/*    */ import java.util.Optional;
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
/*    */ public interface IComponentMap
/*    */ {
/*    */   static StaticComponentMap decode(NBT nbt, PacketWrapper<?> wrapper, IRegistry<? extends ComponentType<?>> registry) {
/* 37 */     return decode(nbt, wrapper.getServerVersion().toClientVersion(), registry);
/*    */   }
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   static StaticComponentMap decode(NBT nbt, ClientVersion version, IRegistry<? extends ComponentType<?>> registry) {
/* 43 */     NBTCompound compound = (NBTCompound)nbt;
/* 44 */     StaticComponentMap.Builder components = StaticComponentMap.builder();
/* 45 */     for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)compound.getTags().entrySet()) {
/* 46 */       ComponentType<?> type = (ComponentType)registry.getByName(entry.getKey());
/* 47 */       if (type == null) {
/* 48 */         throw new IllegalStateException("Unknown component type named " + (String)entry.getKey() + " encountered");
/*    */       }
/* 50 */       Object value = type.decode(entry.getValue(), version);
/* 51 */       components.set(type, value);
/*    */     } 
/* 53 */     return components.build();
/*    */   }
/*    */   
/*    */   static NBT encode(PacketWrapper<?> wrapper, StaticComponentMap components) {
/* 57 */     return encode(components, wrapper.getServerVersion().toClientVersion());
/*    */   }
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   static NBT encode(StaticComponentMap components, ClientVersion version) {
/* 63 */     NBTCompound compound = new NBTCompound();
/* 64 */     for (Map.Entry<ComponentType<?>, ?> entry : components.getDelegate().entrySet()) {
/* 65 */       String key = ((ComponentType)entry.getKey()).getName().toString();
/* 66 */       NBT value = ((ComponentType)entry.getKey()).encode(entry.getValue(), version);
/* 67 */       compound.setTag(key, value);
/*    */     } 
/* 69 */     return (NBT)compound;
/*    */   }
/*    */   boolean has(ComponentType<?> paramComponentType);
/*    */   default <T> Optional<T> getOptional(ComponentType<T> type) {
/* 73 */     return Optional.ofNullable(get(type));
/*    */   }
/*    */ 
/*    */   
/*    */   @Contract("_, !null -> !null")
/*    */   @Nullable
/*    */   default <T> T getOr(ComponentType<T> type, @Nullable T otherValue) {
/* 80 */     T value = get(type);
/* 81 */     if (value != null) {
/* 82 */       return value;
/*    */     }
/* 84 */     return otherValue;
/*    */   }
/*    */   @Nullable
/*    */   <T> T get(ComponentType<T> paramComponentType);
/*    */   
/*    */   default <T> void set(ComponentValue<T> component) {
/* 90 */     set(component.getType(), component.getValue());
/*    */   }
/*    */   
/*    */   default <T> void set(ComponentType<T> type, @Nullable T value) {
/* 94 */     set(type, Optional.ofNullable(value));
/*    */   }
/*    */   
/*    */   default <T> void unset(ComponentType<T> type) {
/* 98 */     set(type, Optional.empty());
/*    */   }
/*    */   
/*    */   <T> void set(ComponentType<T> paramComponentType, Optional<T> paramOptional);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\IComponentMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */