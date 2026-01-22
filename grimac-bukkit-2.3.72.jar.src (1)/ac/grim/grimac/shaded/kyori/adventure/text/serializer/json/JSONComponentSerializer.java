/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.json;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.ComponentSerializer;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.PlatformAPI;
/*    */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*    */ import java.util.function.Consumer;
/*    */ import java.util.function.Supplier;
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
/*    */ public interface JSONComponentSerializer
/*    */   extends ComponentSerializer<Component, Component, String>
/*    */ {
/*    */   @NotNull
/*    */   static JSONComponentSerializer json() {
/* 52 */     return JSONComponentSerializerAccessor.Instances.INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Builder builder() {
/* 62 */     return JSONComponentSerializerAccessor.Instances.BUILDER_SUPPLIER.get();
/*    */   }
/*    */   
/*    */   public static interface Builder {
/*    */     @NotNull
/*    */     Builder options(@NotNull OptionState param1OptionState);
/*    */     
/*    */     @NotNull
/*    */     Builder editOptions(@NotNull Consumer<OptionState.Builder> param1Consumer);
/*    */     
/*    */     @Deprecated
/*    */     @NotNull
/*    */     Builder downsampleColors();
/*    */     
/*    */     @NotNull
/*    */     Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer param1LegacyHoverEventSerializer);
/*    */     
/*    */     @Deprecated
/*    */     @NotNull
/*    */     Builder emitLegacyHoverEvent();
/*    */     
/*    */     @NotNull
/*    */     JSONComponentSerializer build();
/*    */   }
/*    */   
/*    */   @PlatformAPI
/*    */   @Internal
/*    */   public static interface Provider {
/*    */     @PlatformAPI
/*    */     @Internal
/*    */     @NotNull
/*    */     JSONComponentSerializer instance();
/*    */     
/*    */     @PlatformAPI
/*    */     @Internal
/*    */     @NotNull
/*    */     Supplier<JSONComponentSerializer.Builder> builder();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\json\JSONComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */