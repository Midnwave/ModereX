/*    */ package ac.grim.grimac.shaded.incendo.cloud.paper;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.paper.parser.KeyedWorldParser;
/*    */ import org.apiguardian.api.API;
/*    */ import org.bukkit.World;
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
/*    */ @API(status = API.Status.INTERNAL)
/*    */ final class PaperBrigadierMappings
/*    */ {
/*    */   static <C> void register(BukkitBrigadierMapper<C> mapper) {
/* 47 */     Class<?> keyed = CraftBukkitReflection.findClass("org.bukkit.Keyed");
/* 48 */     if (keyed != null && keyed.isAssignableFrom(World.class))
/* 49 */       mapper.mapSimpleNMS(new TypeToken<KeyedWorldParser<C>>() {  }, "resource_location", true); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\PaperBrigadierMappings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */