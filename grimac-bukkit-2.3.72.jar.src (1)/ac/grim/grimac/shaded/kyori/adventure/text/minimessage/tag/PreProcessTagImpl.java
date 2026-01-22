/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ final class PreProcessTagImpl
/*    */   extends AbstractTag
/*    */   implements PreProcess
/*    */ {
/*    */   private final String value;
/*    */   
/*    */   PreProcessTagImpl(String value) {
/* 36 */     this.value = value;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String value() {
/* 41 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 46 */     return Objects.hash(new Object[] { this.value });
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 51 */     if (this == other) return true; 
/* 52 */     if (!(other instanceof PreProcessTagImpl)) return false; 
/* 53 */     PreProcessTagImpl that = (PreProcessTagImpl)other;
/* 54 */     return Objects.equals(this.value, that.value);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 59 */     return Stream.of(ExaminableProperty.of("value", this.value));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\PreProcessTagImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */