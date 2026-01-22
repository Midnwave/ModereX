/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ @NonExtendable
/*    */ public abstract class ParsingException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 4502774670340827070L;
/*    */   public static final int LOCATION_UNKNOWN = -1;
/*    */   
/*    */   protected ParsingException(@Nullable String message) {
/* 48 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected ParsingException(@Nullable String message, @Nullable Throwable cause) {
/* 59 */     super(message, cause);
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
/*    */   protected ParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 72 */     super(message, cause, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public abstract String originalText();
/*    */   
/*    */   @Nullable
/*    */   public abstract String detailMessage();
/*    */   
/*    */   public abstract int startIndex();
/*    */   
/*    */   public abstract int endIndex();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\ParsingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */