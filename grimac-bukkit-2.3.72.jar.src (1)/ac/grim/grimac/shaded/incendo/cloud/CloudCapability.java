/*    */ package ac.grim.grimac.shaded.incendo.cloud;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface CloudCapability
/*    */ {
/*    */   String toString();
/*    */   
/*    */   @API(status = API.Status.STABLE)
/*    */   public enum StandardCapabilities
/*    */     implements CloudCapability
/*    */   {
/* 54 */     ROOT_COMMAND_DELETION;
/*    */ 
/*    */     
/*    */     public String toString() {
/* 58 */       return name();
/*    */     }
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
/*    */   @API(status = API.Status.STABLE)
/*    */   public static final class CloudCapabilityMissingException
/*    */     extends RuntimeException
/*    */   {
/*    */     public CloudCapabilityMissingException(CloudCapability capability) {
/* 78 */       super(String.format("Missing capability '%s'", new Object[] { capability }));
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\CloudCapability.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */