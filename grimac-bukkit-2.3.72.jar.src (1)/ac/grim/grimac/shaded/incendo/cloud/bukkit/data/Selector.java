/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.data;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
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
/*    */ @API(status = API.Status.STABLE, since = "2.0.0")
/*    */ public interface Selector<V>
/*    */ {
/*    */   String inputString();
/*    */   
/*    */   Collection<V> values();
/*    */   
/*    */   @API(status = API.Status.STABLE, since = "2.0.0")
/*    */   public static interface Single<V>
/*    */     extends Selector<V>
/*    */   {
/*    */     default Collection<V> values() {
/* 65 */       return Collections.singletonList(single());
/*    */     }
/*    */     
/*    */     V single();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\data\Selector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */