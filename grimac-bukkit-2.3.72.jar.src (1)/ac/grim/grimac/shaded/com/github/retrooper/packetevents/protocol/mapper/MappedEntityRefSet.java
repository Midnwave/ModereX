/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistryHolder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ @NullMarked
/*    */ public interface MappedEntityRefSet<T extends MappedEntity>
/*    */ {
/*    */   default MappedEntitySet<T> resolve(PacketWrapper<?> wrapper, IRegistry<T> registry) {
/* 31 */     ClientVersion version = wrapper.getServerVersion().toClientVersion();
/* 32 */     IRegistry<T> replacedRegistry = wrapper.getRegistryHolder().getRegistryOr(registry, version);
/* 33 */     return resolve(version, replacedRegistry);
/*    */   }
/*    */   
/*    */   default MappedEntitySet<T> resolve(ClientVersion version, IRegistryHolder registryHolder, IRegistry<T> registry) {
/* 37 */     IRegistry<T> replacedRegistry = registryHolder.getRegistryOr(registry, version);
/* 38 */     return resolve(version, replacedRegistry);
/*    */   }
/*    */   
/*    */   MappedEntitySet<T> resolve(ClientVersion paramClientVersion, IRegistry<T> paramIRegistry);
/*    */   
/*    */   boolean isEmpty();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\mapper\MappedEntityRefSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */