/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONComponentSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONOptions;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.PlatformAPI;
/*     */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.GsonBuilder;
/*     */ import com.google.gson.JsonElement;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.UnaryOperator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface GsonComponentSerializer
/*     */   extends JSONComponentSerializer, Buildable<GsonComponentSerializer, GsonComponentSerializer.Builder>
/*     */ {
/*     */   @NotNull
/*     */   static GsonComponentSerializer gson() {
/*  61 */     return GsonComponentSerializerImpl.Instances.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static GsonComponentSerializer colorDownsamplingGson() {
/*  74 */     return GsonComponentSerializerImpl.Instances.LEGACY_INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Builder builder() {
/*  84 */     return new GsonComponentSerializerImpl.BuilderImpl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Gson serializer();
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   UnaryOperator<GsonBuilder> populator();
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Component deserializeFromTree(@NotNull JsonElement paramJsonElement);
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   JsonElement serializeToTree(@NotNull Component paramComponent);
/*     */ 
/*     */ 
/*     */   
/*     */   @PlatformAPI
/*     */   @Internal
/*     */   public static interface Provider
/*     */   {
/*     */     @PlatformAPI
/*     */     @Internal
/*     */     @NotNull
/*     */     GsonComponentSerializer gson();
/*     */ 
/*     */ 
/*     */     
/*     */     @PlatformAPI
/*     */     @Internal
/*     */     @NotNull
/*     */     GsonComponentSerializer gsonLegacy();
/*     */ 
/*     */ 
/*     */     
/*     */     @PlatformAPI
/*     */     @Internal
/*     */     @NotNull
/*     */     Consumer<GsonComponentSerializer.Builder> builder();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Builder
/*     */     extends AbstractBuilder<GsonComponentSerializer>, Buildable.Builder<GsonComponentSerializer>, JSONComponentSerializer.Builder
/*     */   {
/*     */     @NotNull
/*     */     default Builder downsampleColors() {
/* 141 */       return editOptions(features -> features.value(JSONOptions.EMIT_RGB, Boolean.valueOf(false)));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @NotNull
/*     */     default Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer serializer) {
/* 156 */       return legacyHoverEventSerializer(serializer);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @NotNull
/*     */     default Builder emitLegacyHoverEvent() {
/* 170 */       return editOptions(b -> b.value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.ALL));
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     Builder options(@NotNull OptionState param1OptionState);
/*     */     
/*     */     @NotNull
/*     */     Builder editOptions(@NotNull Consumer<OptionState.Builder> param1Consumer);
/*     */     
/*     */     @NotNull
/*     */     Builder legacyHoverEventSerializer(LegacyHoverEventSerializer param1LegacyHoverEventSerializer);
/*     */     
/*     */     @NotNull
/*     */     GsonComponentSerializer build();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\GsonComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */