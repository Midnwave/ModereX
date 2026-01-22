/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.profession;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*    */ public final class VillagerProfessions
/*    */ {
/* 29 */   private static final VersionedRegistry<VillagerProfession> REGISTRY = new VersionedRegistry("villager_profession");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<VillagerProfession> getRegistry() {
/* 35 */     return REGISTRY;
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   @Internal
/*    */   public static VillagerProfession define(int id, String name) {
/* 41 */     return define(name);
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static VillagerProfession define(String name) {
/* 46 */     return (VillagerProfession)REGISTRY.define(name, StaticVillagerProfession::new);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public static VillagerProfession getById(int id) {
/* 51 */     ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();
/* 52 */     return getById(version.toClientVersion(), id);
/*    */   }
/*    */   
/*    */   public static VillagerProfession getById(ClientVersion version, int id) {
/* 56 */     return (VillagerProfession)REGISTRY.getById(version, id);
/*    */   }
/*    */   
/*    */   public static VillagerProfession getByName(String name) {
/* 60 */     return (VillagerProfession)REGISTRY.getByName(name);
/*    */   }
/*    */   
/* 63 */   public static final VillagerProfession NONE = define("none");
/* 64 */   public static final VillagerProfession ARMORER = define("armorer");
/* 65 */   public static final VillagerProfession BUTCHER = define("butcher");
/* 66 */   public static final VillagerProfession CARTOGRAPHER = define("cartographer");
/* 67 */   public static final VillagerProfession CLERIC = define("cleric");
/* 68 */   public static final VillagerProfession FARMER = define("farmer");
/* 69 */   public static final VillagerProfession FISHERMAN = define("fisherman");
/* 70 */   public static final VillagerProfession FLETCHER = define("fletcher");
/* 71 */   public static final VillagerProfession LEATHERWORKER = define("leatherworker");
/* 72 */   public static final VillagerProfession LIBRARIAN = define("librarian");
/* 73 */   public static final VillagerProfession MASON = define("mason");
/* 74 */   public static final VillagerProfession NITWIT = define("nitwit");
/* 75 */   public static final VillagerProfession SHEPHERD = define("shepherd");
/* 76 */   public static final VillagerProfession TOOLSMITH = define("toolsmith");
/* 77 */   public static final VillagerProfession WEAPONSMITH = define("weaponsmith");
/*    */   
/*    */   static {
/* 80 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\villager\profession\VillagerProfessions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */