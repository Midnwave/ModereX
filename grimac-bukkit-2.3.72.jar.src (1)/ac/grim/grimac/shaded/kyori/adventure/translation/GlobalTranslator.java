/*    */ package ac.grim.grimac.shaded.kyori.adventure.translation;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.renderer.TranslatableComponentRenderer;
/*    */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
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
/*    */ public interface GlobalTranslator
/*    */   extends Translator, Examinable
/*    */ {
/*    */   @NotNull
/*    */   static GlobalTranslator translator() {
/* 52 */     return GlobalTranslatorImpl.INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   @ScheduledForRemoval(inVersion = "5.0.0")
/*    */   @NotNull
/*    */   static GlobalTranslator get() {
/* 65 */     return GlobalTranslatorImpl.INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static TranslatableComponentRenderer<Locale> renderer() {
/* 75 */     return GlobalTranslatorImpl.INSTANCE.renderer;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static Component render(@NotNull Component component, @NotNull Locale locale) {
/* 87 */     return renderer().render(component, locale);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   Iterable<? extends Translator> sources();
/*    */   
/*    */   boolean addSource(@NotNull Translator paramTranslator);
/*    */   
/*    */   boolean removeSource(@NotNull Translator paramTranslator);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\translation\GlobalTranslator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */