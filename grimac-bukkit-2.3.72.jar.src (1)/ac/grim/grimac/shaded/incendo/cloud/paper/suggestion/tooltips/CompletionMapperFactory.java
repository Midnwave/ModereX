/*    */ package ac.grim.grimac.shaded.incendo.cloud.paper.suggestion.tooltips;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.audience.Audience;
/*    */ import org.apiguardian.api.API;
/*    */ import org.bukkit.entity.Player;
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
/*    */ @API(status = API.Status.INTERNAL, since = "2.0.0")
/*    */ public interface CompletionMapperFactory
/*    */ {
/*    */   static CompletionMapperFactory detectingRelocation() {
/* 41 */     return new CompletionMapperFactoryImpl();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   CompletionMapper createMapper();
/*    */ 
/*    */ 
/*    */   
/*    */   public static final class CompletionMapperFactoryImpl
/*    */     implements CompletionMapperFactory
/*    */   {
/*    */     private CompletionMapperFactoryImpl() {}
/*    */ 
/*    */ 
/*    */     
/*    */     public CompletionMapper createMapper() {
/* 59 */       if (Audience.class.isAssignableFrom(Player.class)) {
/* 60 */         return new NativeCompletionMapper();
/*    */       }
/* 62 */       return new ReflectiveCompletionMapper();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\suggestion\tooltips\CompletionMapperFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */