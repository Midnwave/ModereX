/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
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
/*    */ @Internal
/*    */ public final class GlobalRegistryHolder
/*    */   implements IRegistryHolder
/*    */ {
/* 31 */   public static final IRegistryHolder INSTANCE = new GlobalRegistryHolder();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Object getGlobalRegistryCacheKey(@Nullable User user, ClientVersion version) {
/* 37 */     return version;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public IRegistry<?> getRegistry(ResourceLocation registryKey, ClientVersion version) {
/* 42 */     SynchronizedRegistriesHandler.RegistryEntry<?> registryEntry = SynchronizedRegistriesHandler.getRegistryEntry(registryKey);
/* 43 */     if (registryEntry == null) {
/* 44 */       return null;
/*    */     }
/* 46 */     Object cacheKey = getGlobalRegistryCacheKey(null, version);
/* 47 */     return registryEntry.getSyncedRegistry(cacheKey);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\mappings\GlobalRegistryHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */