/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.event;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.function.UnaryOperator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface HoverEventSource<V>
/*    */ {
/*    */   @Nullable
/*    */   static <V> HoverEvent<V> unbox(@Nullable HoverEventSource<V> source) {
/* 46 */     return (source != null) ? source.asHoverEvent() : null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   default HoverEvent<V> asHoverEvent() {
/* 56 */     return asHoverEvent(UnaryOperator.identity());
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   HoverEvent<V> asHoverEvent(@NotNull UnaryOperator<V> paramUnaryOperator);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\event\HoverEventSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */