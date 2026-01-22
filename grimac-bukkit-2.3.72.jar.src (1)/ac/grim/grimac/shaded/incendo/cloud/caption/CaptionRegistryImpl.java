/*    */ package ac.grim.grimac.shaded.incendo.cloud.caption;
/*    */ 
/*    */ import java.util.LinkedList;
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
/*    */ @API(status = API.Status.INTERNAL)
/*    */ public final class CaptionRegistryImpl<C>
/*    */   implements CaptionRegistry<C>
/*    */ {
/* 34 */   private final LinkedList<CaptionProvider<C>> providers = new LinkedList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String caption(Caption caption, C sender) {
/* 44 */     for (CaptionProvider<C> provider : this.providers) {
/* 45 */       String result = provider.provide(caption, sender);
/* 46 */       if (result != null) {
/* 47 */         return result;
/*    */       }
/*    */     } 
/* 50 */     throw new IllegalArgumentException(String.format("There is no caption stored with key '%s'", new Object[] { caption }));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaptionRegistry<C> registerProvider(CaptionProvider<C> provider) {
/* 57 */     this.providers.addFirst(provider);
/* 58 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\CaptionRegistryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */