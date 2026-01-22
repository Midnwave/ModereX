/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
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
/*    */ final class ShadowColorImpl
/*    */   implements ShadowColor, Examinable
/*    */ {
/*    */   static final int NONE_VALUE = 0;
/* 35 */   static final ShadowColorImpl NONE = new ShadowColorImpl(0);
/*    */   
/*    */   private final int value;
/*    */   
/*    */   ShadowColorImpl(int value) {
/* 40 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public int value() {
/* 45 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 50 */     if (!(other instanceof ShadowColorImpl)) return false; 
/* 51 */     ShadowColorImpl that = (ShadowColorImpl)other;
/* 52 */     return (this.value == that.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 57 */     return Integer.hashCode(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 62 */     return Internals.toString(this);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 67 */     return Stream.of(
/* 68 */         ExaminableProperty.of("value", this.value));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\ShadowColorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */