/*    */ package ac.grim.grimac.shaded.incendo.cloud.caption;
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
/*    */ public abstract class DelegatingCaptionProvider<C>
/*    */   implements CaptionProvider<C>
/*    */ {
/*    */   public abstract CaptionProvider<C> delegate();
/*    */   
/*    */   public final String provide(Caption caption, C recipient) {
/* 45 */     return delegate().provide(caption, recipient);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\DelegatingCaptionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */