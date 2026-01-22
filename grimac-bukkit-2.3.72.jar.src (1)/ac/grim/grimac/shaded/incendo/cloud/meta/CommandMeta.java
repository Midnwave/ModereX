/*    */ package ac.grim.grimac.shaded.incendo.cloud.meta;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKeyContainer;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public abstract class CommandMeta
/*    */   implements CloudKeyContainer
/*    */ {
/*    */   public static CommandMetaBuilder builder() {
/* 46 */     return new CommandMetaBuilder();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.STABLE)
/*    */   public static CommandMeta empty() {
/* 56 */     return builder().build();
/*    */   }
/*    */ 
/*    */   
/*    */   public final String toString() {
/* 61 */     return "";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\meta\CommandMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */