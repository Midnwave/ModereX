/*    */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface SelectorComponent
/*    */   extends BuildableComponent<SelectorComponent, SelectorComponent.Builder>, ScopedComponent<SelectorComponent>
/*    */ {
/*    */   @NotNull
/*    */   String pattern();
/*    */   
/*    */   @Contract(pure = true)
/*    */   @NotNull
/*    */   SelectorComponent pattern(@NotNull String paramString);
/*    */   
/*    */   @Nullable
/*    */   Component separator();
/*    */   
/*    */   @NotNull
/*    */   SelectorComponent separator(@Nullable ComponentLike paramComponentLike);
/*    */   
/*    */   @NotNull
/*    */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 85 */     return Stream.concat(
/* 86 */         Stream.of(new ExaminableProperty[] {
/* 87 */             ExaminableProperty.of("pattern", pattern()), 
/* 88 */             ExaminableProperty.of("separator", separator())
/*    */           
/* 90 */           }), super.examinableProperties());
/*    */   }
/*    */   
/*    */   public static interface Builder extends ComponentBuilder<SelectorComponent, Builder> {
/*    */     @Contract("_ -> this")
/*    */     @NotNull
/*    */     Builder pattern(@NotNull String param1String);
/*    */     
/*    */     @Contract("_ -> this")
/*    */     @NotNull
/*    */     Builder separator(@Nullable ComponentLike param1ComponentLike);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\SelectorComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */