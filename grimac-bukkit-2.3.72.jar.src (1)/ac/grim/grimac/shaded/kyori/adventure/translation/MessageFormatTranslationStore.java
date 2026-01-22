/*    */ package ac.grim.grimac.shaded.kyori.adventure.translation;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*    */ import java.text.MessageFormat;
/*    */ import java.util.Locale;
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
/*    */ final class MessageFormatTranslationStore
/*    */   extends AbstractTranslationStore.StringBased<MessageFormat>
/*    */   implements TranslationRegistry
/*    */ {
/*    */   MessageFormatTranslationStore(Key name) {
/* 35 */     super(name);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   protected MessageFormat parse(@NotNull String string, @NotNull Locale locale) {
/* 40 */     return new MessageFormat(string, locale);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
/* 45 */     return translationValue(key, locale);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\translation\MessageFormatTranslationStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */