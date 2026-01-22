/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*    */ import java.util.Objects;
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
/*    */ final class TextDecorationAndStateImpl
/*    */   implements TextDecorationAndState
/*    */ {
/*    */   private final TextDecoration decoration;
/*    */   private final TextDecoration.State state;
/*    */   
/*    */   TextDecorationAndStateImpl(TextDecoration decoration, TextDecoration.State state) {
/* 39 */     this.decoration = decoration;
/* 40 */     this.state = Objects.<TextDecoration.State>requireNonNull(state, "state");
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public TextDecoration decoration() {
/* 45 */     return this.decoration;
/*    */   }
/*    */ 
/*    */   
/*    */   public TextDecoration.State state() {
/* 50 */     return this.state;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 55 */     return Internals.toString(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 60 */     if (this == other) return true; 
/* 61 */     if (other == null || getClass() != other.getClass()) return false; 
/* 62 */     TextDecorationAndStateImpl that = (TextDecorationAndStateImpl)other;
/* 63 */     return (this.decoration == that.decoration && this.state == that.state);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 68 */     int result = this.decoration.hashCode();
/* 69 */     result = 31 * result + this.state.hashCode();
/* 70 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\TextDecorationAndStateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */