/*    */ package ac.grim.grimac.shaded.incendo.cloud.caption;
/*    */ 
/*    */ import java.util.function.Function;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface CaptionProvider<C>
/*    */ {
/*    */   static <C> ImmutableConstantCaptionProvider.Builder<C> constantProvider() {
/* 41 */     return ImmutableConstantCaptionProvider.builder();
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
/*    */   static <C> CaptionProvider<C> constantProvider(Caption caption, String value) {
/* 53 */     return constantProvider().putCaption(caption, value).build();
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
/*    */   
/*    */   static <C> CaptionProvider<C> forCaption(Caption caption, Function<C, String> provider) {
/* 69 */     return (key, recipient) -> key.equals(caption) ? provider.apply(recipient) : null;
/*    */   }
/*    */   
/*    */   String provide(Caption paramCaption, C paramC);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\CaptionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */