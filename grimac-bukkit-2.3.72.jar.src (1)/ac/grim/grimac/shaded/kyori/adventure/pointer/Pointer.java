/*    */ package ac.grim.grimac.shaded.kyori.adventure.pointer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*    */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*    */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*    */ import java.util.stream.Stream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface Pointer<V>
/*    */   extends Examinable
/*    */ {
/*    */   @NotNull
/*    */   static <V> Pointer<V> pointer(@NotNull Class<V> type, @NotNull Key key) {
/* 49 */     return new PointerImpl<>(type, key);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   Class<V> type();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   Key key();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 70 */     return Stream.of(new ExaminableProperty[] {
/* 71 */           ExaminableProperty.of("type", type()), 
/* 72 */           ExaminableProperty.of("key", key())
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\pointer\Pointer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */