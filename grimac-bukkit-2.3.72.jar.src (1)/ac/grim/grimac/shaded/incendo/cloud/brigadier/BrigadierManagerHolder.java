/*    */ package ac.grim.grimac.shaded.incendo.cloud.brigadier;
/*    */ 
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
/*    */ public interface BrigadierManagerHolder<C, S>
/*    */ {
/*    */   @API(status = API.Status.STABLE, since = "2.0.0")
/*    */   boolean hasBrigadierManager();
/*    */   
/*    */   @API(status = API.Status.STABLE, since = "2.0.0")
/*    */   CloudBrigadierManager<C, ? extends S> brigadierManager();
/*    */   
/*    */   @API(status = API.Status.STABLE, since = "2.0.0")
/*    */   public static final class BrigadierManagerNotPresent
/*    */     extends RuntimeException
/*    */   {
/*    */     public BrigadierManagerNotPresent(String message) {
/* 79 */       super(message);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\BrigadierManagerHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */