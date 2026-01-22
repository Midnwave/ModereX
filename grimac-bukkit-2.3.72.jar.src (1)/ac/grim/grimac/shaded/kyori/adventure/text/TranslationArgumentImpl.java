/*    */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*    */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*    */ import java.util.Objects;
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
/*    */ final class TranslationArgumentImpl
/*    */   implements TranslationArgument
/*    */ {
/* 34 */   private static final Component TRUE = Component.text("true");
/* 35 */   private static final Component FALSE = Component.text("false");
/*    */   
/*    */   private final Object value;
/*    */   
/*    */   TranslationArgumentImpl(Object value) {
/* 40 */     this.value = value;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Object value() {
/* 45 */     return this.value;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Component asComponent() {
/* 50 */     if (this.value instanceof Component)
/* 51 */       return (Component)this.value; 
/* 52 */     if (this.value instanceof Boolean) {
/* 53 */       return ((Boolean)this.value).booleanValue() ? TRUE : FALSE;
/*    */     }
/* 55 */     return Component.text(String.valueOf(this.value));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 61 */     if (this == other) return true; 
/* 62 */     if (other == null || getClass() != other.getClass()) return false; 
/* 63 */     TranslationArgumentImpl that = (TranslationArgumentImpl)other;
/* 64 */     return Objects.equals(this.value, that.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 69 */     return Objects.hash(new Object[] { this.value });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     return Internals.toString(this);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 79 */     return Stream.of(
/* 80 */         ExaminableProperty.of("value", this.value));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\TranslationArgumentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */