/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Debug.Renderer;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.HSVLike;
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
/*    */ @Renderer(text = "asHexString()")
/*    */ final class TextColorImpl
/*    */   implements TextColor
/*    */ {
/*    */   private final int value;
/*    */   
/*    */   TextColorImpl(int value) {
/* 36 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public int value() {
/* 41 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 46 */     if (this == other) return true; 
/* 47 */     if (!(other instanceof TextColorImpl)) return false; 
/* 48 */     TextColorImpl that = (TextColorImpl)other;
/* 49 */     return (this.value == that.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 54 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     return asHexString();
/*    */   }
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
/*    */   static float distance(@NotNull HSVLike self, @NotNull HSVLike other) {
/* 73 */     float hueDistance = 3.0F * Math.min(Math.abs(self.h() - other.h()), 1.0F - Math.abs(self.h() - other.h()));
/* 74 */     float saturationDiff = self.s() - other.s();
/* 75 */     float valueDiff = self.v() - other.v();
/* 76 */     return hueDistance * hueDistance + saturationDiff * saturationDiff + valueDiff * valueDiff;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\TextColorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */