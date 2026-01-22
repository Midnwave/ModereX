/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
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
/*    */ final class InsertingImpl
/*    */   extends AbstractTag
/*    */   implements Inserting
/*    */ {
/*    */   private final boolean allowsChildren;
/*    */   private final Component value;
/*    */   
/*    */   InsertingImpl(boolean allowsChildren, Component value) {
/* 38 */     this.allowsChildren = allowsChildren;
/* 39 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean allowsChildren() {
/* 44 */     return this.allowsChildren;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Component value() {
/* 49 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 54 */     return Objects.hash(new Object[] { Boolean.valueOf(this.allowsChildren), this.value });
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 59 */     if (this == other) return true; 
/* 60 */     if (!(other instanceof InsertingImpl)) return false; 
/* 61 */     InsertingImpl that = (InsertingImpl)other;
/* 62 */     return (this.allowsChildren == that.allowsChildren && Objects.equals(this.value, that.value));
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 67 */     return Stream.of(new ExaminableProperty[] {
/* 68 */           ExaminableProperty.of("allowsChildren", this.allowsChildren), 
/* 69 */           ExaminableProperty.of("value", this.value)
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\InsertingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */