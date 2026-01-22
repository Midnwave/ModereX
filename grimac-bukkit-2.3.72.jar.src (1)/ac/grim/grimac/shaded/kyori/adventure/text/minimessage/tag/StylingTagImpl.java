/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*    */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*    */ import java.util.Arrays;
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
/*    */ final class StylingTagImpl
/*    */   extends AbstractTag
/*    */   implements Inserting
/*    */ {
/*    */   private final StyleBuilderApplicable[] styles;
/*    */   
/*    */   StylingTagImpl(StyleBuilderApplicable[] styles) {
/* 39 */     this.styles = styles;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Component value() {
/* 44 */     return (Component)Component.text("", Style.style(this.styles));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 49 */     return 31 + Arrays.hashCode((Object[])this.styles);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 54 */     if (this == other) return true; 
/* 55 */     if (!(other instanceof StylingTagImpl)) return false; 
/* 56 */     StylingTagImpl that = (StylingTagImpl)other;
/* 57 */     return Arrays.equals((Object[])this.styles, (Object[])that.styles);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 62 */     return Stream.of(ExaminableProperty.of("styles", this.styles));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\StylingTagImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */