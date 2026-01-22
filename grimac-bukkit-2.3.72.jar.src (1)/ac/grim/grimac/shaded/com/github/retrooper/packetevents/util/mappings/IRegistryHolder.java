/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ @Internal
/*    */ public interface IRegistryHolder
/*    */ {
/*    */   default <T extends ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity> IRegistry<T> getRegistryOr(IRegistry<T> fallbackRegistry) {
/* 32 */     return getRegistryOr(fallbackRegistry, PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
/*    */   }
/*    */ 
/*    */   
/*    */   default <T extends ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity> IRegistry<T> getRegistryOr(IRegistry<T> fallbackRegistry, ClientVersion version) {
/* 37 */     IRegistry<?> replacedRegistry = getRegistry(fallbackRegistry.getRegistryKey(), version);
/* 38 */     return (replacedRegistry != null) ? (IRegistry)replacedRegistry : fallbackRegistry;
/*    */   }
/*    */   @Nullable
/*    */   default IRegistry<?> getRegistry(ResourceLocation registryKey) {
/* 42 */     return getRegistry(registryKey, PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   IRegistry<?> getRegistry(ResourceLocation paramResourceLocation, ClientVersion paramClientVersion);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\mappings\IRegistryHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */