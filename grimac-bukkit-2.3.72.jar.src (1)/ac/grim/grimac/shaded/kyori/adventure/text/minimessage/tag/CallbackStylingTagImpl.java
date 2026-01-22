/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*    */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*    */ import java.util.Objects;
/*    */ import java.util.function.Consumer;
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
/*    */ final class CallbackStylingTagImpl
/*    */   extends AbstractTag
/*    */   implements Inserting
/*    */ {
/*    */   private final Consumer<Style.Builder> styles;
/*    */   
/*    */   CallbackStylingTagImpl(Consumer<Style.Builder> styles) {
/* 38 */     this.styles = styles;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Component value() {
/* 43 */     return (Component)Component.text("", Style.style(this.styles));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 48 */     return Objects.hash(new Object[] { this.styles });
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 53 */     if (this == other) return true; 
/* 54 */     if (!(other instanceof CallbackStylingTagImpl)) return false; 
/* 55 */     CallbackStylingTagImpl that = (CallbackStylingTagImpl)other;
/* 56 */     return Objects.equals(this.styles, that.styles);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 61 */     return Stream.of(ExaminableProperty.of("styles", this.styles));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\CallbackStylingTagImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */