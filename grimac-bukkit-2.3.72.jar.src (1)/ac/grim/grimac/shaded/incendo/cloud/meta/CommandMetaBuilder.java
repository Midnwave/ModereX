/*    */ package ac.grim.grimac.shaded.incendo.cloud.meta;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.apiguardian.api.API;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public class CommandMetaBuilder
/*    */ {
/* 36 */   private final Map<CloudKey<?>, Object> map = new HashMap<>();
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
/*    */   public CommandMetaBuilder with(CommandMeta commandMeta) {
/* 48 */     this.map.putAll(commandMeta.all());
/* 49 */     return this;
/*    */   }
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
/*    */   public <V> CommandMetaBuilder with(CloudKey<V> key, V value) {
/* 64 */     this.map.put(key, value);
/* 65 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CommandMeta build() {
/* 74 */     return new SimpleCommandMeta(this.map);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\meta\CommandMetaBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */