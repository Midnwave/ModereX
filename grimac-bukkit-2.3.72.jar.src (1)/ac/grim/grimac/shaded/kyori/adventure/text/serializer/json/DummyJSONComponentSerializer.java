/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.json;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*    */ import java.util.function.Consumer;
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
/*    */ final class DummyJSONComponentSerializer
/*    */   implements JSONComponentSerializer
/*    */ {
/* 33 */   static final JSONComponentSerializer INSTANCE = new DummyJSONComponentSerializer();
/*    */ 
/*    */   
/*    */   private static final String UNSUPPORTED_MESSAGE = "No JsonComponentSerializer implementation found\n\nAre you missing an implementation artifact like adventure-text-serializer-gson?\nIs your environment configured in a way that causes ServiceLoader to malfunction?";
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public Component deserialize(@NotNull String input) {
/* 42 */     throw new UnsupportedOperationException("No JsonComponentSerializer implementation found\n\nAre you missing an implementation artifact like adventure-text-serializer-gson?\nIs your environment configured in a way that causes ServiceLoader to malfunction?");
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String serialize(@NotNull Component component) {
/* 47 */     throw new UnsupportedOperationException("No JsonComponentSerializer implementation found\n\nAre you missing an implementation artifact like adventure-text-serializer-gson?\nIs your environment configured in a way that causes ServiceLoader to malfunction?");
/*    */   }
/*    */   
/*    */   static final class BuilderImpl
/*    */     implements JSONComponentSerializer.Builder {
/*    */     @NotNull
/*    */     public JSONComponentSerializer.Builder options(@NotNull OptionState flags) {
/* 54 */       return this;
/*    */     }
/*    */     
/*    */     @NotNull
/*    */     public JSONComponentSerializer.Builder editOptions(@NotNull Consumer<OptionState.Builder> optionEditor) {
/* 59 */       return this;
/*    */     }
/*    */     
/*    */     @Deprecated
/*    */     @NotNull
/*    */     public JSONComponentSerializer.Builder downsampleColors() {
/* 65 */       return this;
/*    */     }
/*    */     
/*    */     @NotNull
/*    */     public JSONComponentSerializer.Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer serializer) {
/* 70 */       return this;
/*    */     }
/*    */     
/*    */     @Deprecated
/*    */     @NotNull
/*    */     public JSONComponentSerializer.Builder emitLegacyHoverEvent() {
/* 76 */       return this;
/*    */     }
/*    */     
/*    */     @NotNull
/*    */     public JSONComponentSerializer build() {
/* 81 */       return DummyJSONComponentSerializer.INSTANCE;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\json\DummyJSONComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */