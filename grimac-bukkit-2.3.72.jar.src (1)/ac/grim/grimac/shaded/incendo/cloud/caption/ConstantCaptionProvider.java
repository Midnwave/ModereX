/*    */ package ac.grim.grimac.shaded.incendo.cloud.caption;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.apiguardian.api.API;
/*    */ import org.immutables.value.Value.Immutable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ @Immutable
/*    */ public abstract class ConstantCaptionProvider<C>
/*    */   implements CaptionProvider<C>
/*    */ {
/*    */   public abstract Map<Caption, String> captions();
/*    */   
/*    */   public final String provide(Caption caption, C recipient) {
/* 47 */     return captions().get(caption);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\ConstantCaptionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */