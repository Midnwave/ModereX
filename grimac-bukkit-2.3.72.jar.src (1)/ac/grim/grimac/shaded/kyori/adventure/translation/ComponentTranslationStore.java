/*    */ package ac.grim.grimac.shaded.kyori.adventure.translation;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
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
/*    */ 
/*    */ final class ComponentTranslationStore
/*    */   extends AbstractTranslationStore<Component>
/*    */ {
/*    */   ComponentTranslationStore(@NotNull Key name) {
/* 37 */     super(name);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
/* 42 */     return null;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
/* 47 */     Component translatedComponent = translationValue(component.key(), locale);
/* 48 */     if (translatedComponent == null) return null; 
/* 49 */     return translatedComponent.append(component.children());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\translation\ComponentTranslationStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */