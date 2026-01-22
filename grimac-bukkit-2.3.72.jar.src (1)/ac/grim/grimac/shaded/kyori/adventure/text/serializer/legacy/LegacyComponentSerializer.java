/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.legacy;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.flattener.ComponentFlattener;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.ComponentSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.PlatformAPI;
/*     */ import java.util.List;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.regex.Pattern;
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
/*     */ 
/*     */ public interface LegacyComponentSerializer
/*     */   extends ComponentSerializer<Component, TextComponent, String>, Buildable<LegacyComponentSerializer, LegacyComponentSerializer.Builder>
/*     */ {
/*     */   public static final char SECTION_CHAR = '§';
/*     */   public static final char AMPERSAND_CHAR = '&';
/*     */   public static final char HEX_CHAR = '#';
/*     */   
/*     */   @NotNull
/*     */   static LegacyComponentSerializer legacySection() {
/*  64 */     return LegacyComponentSerializerImpl.Instances.SECTION;
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
/*     */   
/*     */   @NotNull
/*     */   static LegacyComponentSerializer legacyAmpersand() {
/*  78 */     return LegacyComponentSerializerImpl.Instances.AMPERSAND;
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
/*     */   static LegacyComponentSerializer legacy(char legacyCharacter) {
/*  91 */     if (legacyCharacter == '§')
/*  92 */       return legacySection(); 
/*  93 */     if (legacyCharacter == '&') {
/*  94 */       return legacyAmpersand();
/*     */     }
/*  96 */     return builder().character(legacyCharacter).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   static LegacyFormat parseChar(char character) {
/* 107 */     return LegacyComponentSerializerImpl.legacyFormat(character);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Builder builder() {
/* 117 */     return new LegacyComponentSerializerImpl.BuilderImpl();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   TextComponent deserialize(@NotNull String paramString);
/*     */   
/*     */   @NotNull
/*     */   String serialize(@NotNull Component paramComponent);
/*     */   
/*     */   public static interface Builder extends AbstractBuilder<LegacyComponentSerializer>, Buildable.Builder<LegacyComponentSerializer> {
/*     */     @NotNull
/*     */     Builder character(char param1Char);
/*     */     
/*     */     @NotNull
/*     */     Builder hexCharacter(char param1Char);
/*     */     
/*     */     @NotNull
/*     */     Builder extractUrls();
/*     */     
/*     */     @NotNull
/*     */     Builder extractUrls(@NotNull Pattern param1Pattern);
/*     */     
/*     */     @NotNull
/*     */     Builder extractUrls(@Nullable Style param1Style);
/*     */     
/*     */     @NotNull
/*     */     Builder extractUrls(@NotNull Pattern param1Pattern, @Nullable Style param1Style);
/*     */     
/*     */     @NotNull
/*     */     Builder hexColors();
/*     */     
/*     */     @NotNull
/*     */     Builder useUnusualXRepeatedCharacterHexFormat();
/*     */     
/*     */     @NotNull
/*     */     Builder flattener(@NotNull ComponentFlattener param1ComponentFlattener);
/*     */     
/*     */     @NotNull
/*     */     Builder formats(@NotNull List<CharacterAndFormat> param1List);
/*     */     
/*     */     @NotNull
/*     */     LegacyComponentSerializer build();
/*     */   }
/*     */   
/*     */   @PlatformAPI
/*     */   @Internal
/*     */   public static interface Provider {
/*     */     @PlatformAPI
/*     */     @Internal
/*     */     @NotNull
/*     */     LegacyComponentSerializer legacyAmpersand();
/*     */     
/*     */     @PlatformAPI
/*     */     @Internal
/*     */     @NotNull
/*     */     LegacyComponentSerializer legacySection();
/*     */     
/*     */     @PlatformAPI
/*     */     @Internal
/*     */     @NotNull
/*     */     Consumer<LegacyComponentSerializer.Builder> legacy();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\legacy\LegacyComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */