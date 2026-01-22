/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.type;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import java.util.Collection;
/*    */ import java.util.function.Function;
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
/*    */ public final class VillagerTypes
/*    */ {
/* 31 */   private static final VersionedRegistry<VillagerType> REGISTRY = new VersionedRegistry("villager_type");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<VillagerType> getRegistry() {
/* 37 */     return REGISTRY;
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   @Internal
/*    */   public static VillagerType define(int id, String name) {
/* 43 */     return define(name);
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static VillagerType define(String name) {
/* 48 */     return (VillagerType)REGISTRY.define(name, StaticVillagerType::new);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public static VillagerType getById(int id) {
/* 53 */     ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();
/* 54 */     return getById(version.toClientVersion(), id);
/*    */   }
/*    */   
/*    */   public static VillagerType getById(ClientVersion version, int id) {
/* 58 */     return (VillagerType)REGISTRY.getById(version, id);
/*    */   }
/*    */   
/*    */   public static VillagerType getByName(String name) {
/* 62 */     return (VillagerType)REGISTRY.getByName(name);
/*    */   }
/*    */   
/* 65 */   public static final VillagerType DESERT = define("desert");
/* 66 */   public static final VillagerType JUNGLE = define("jungle");
/* 67 */   public static final VillagerType PLAINS = define("plains");
/* 68 */   public static final VillagerType SAVANNA = define("savanna");
/* 69 */   public static final VillagerType SNOW = define("snow");
/* 70 */   public static final VillagerType SWAMP = define("swamp");
/* 71 */   public static final VillagerType TAIGA = define("taiga");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Collection<VillagerType> values() {
/* 79 */     return REGISTRY.getEntries();
/*    */   }
/*    */   
/*    */   static {
/* 83 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\villager\type\VillagerTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */