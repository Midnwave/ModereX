/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
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
/*     */ public final class StandardTags
/*     */ {
/*  48 */   private static final TagResolver ALL = TagResolver.builder()
/*  49 */     .resolvers(new TagResolver[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         HoverTag.RESOLVER, ClickTag.RESOLVER, ColorTagResolver.INSTANCE, KeybindTag.RESOLVER, TranslatableTag.RESOLVER, TranslatableFallbackTag.RESOLVER, InsertionTag.RESOLVER, FontTag.RESOLVER, DecorationTag.RESOLVER, GradientTag.RESOLVER,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         RainbowTag.RESOLVER, ResetTag.RESOLVER, NewlineTag.RESOLVER, TransitionTag.RESOLVER, SelectorTag.RESOLVER, ScoreTag.RESOLVER, NbtTag.RESOLVER, PrideTag.RESOLVER, ShadowColorTag.RESOLVER
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  70 */       }).build();
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
/*     */   public static TagResolver decorations(@NotNull TextDecoration decoration) {
/*  82 */     return Objects.<TagResolver>requireNonNull(DecorationTag.RESOLVERS.get(decoration), "No resolver found for decoration (this should not be possible?)");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver decorations() {
/*  94 */     return DecorationTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver color() {
/* 106 */     return ColorTagResolver.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver hoverEvent() {
/* 116 */     return HoverTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver clickEvent() {
/* 126 */     return ClickTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver keybind() {
/* 136 */     return KeybindTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver translatable() {
/* 148 */     return TranslatableTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver translatableFallback() {
/* 160 */     return TranslatableFallbackTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver insertion() {
/* 170 */     return InsertionTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver font() {
/* 180 */     return FontTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver gradient() {
/* 190 */     return GradientTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver rainbow() {
/* 200 */     return RainbowTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TagResolver transition() {
/* 210 */     return TransitionTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver reset() {
/* 220 */     return ResetTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver newline() {
/* 232 */     return NewlineTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver selector() {
/* 244 */     return SelectorTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver score() {
/* 254 */     return ScoreTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver nbt() {
/* 266 */     return NbtTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver pride() {
/* 276 */     return PrideTag.RESOLVER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagResolver shadowColor() {
/* 288 */     return ShadowColorTag.RESOLVER;
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
/*     */   public static TagResolver defaults() {
/* 301 */     return ALL;
/*     */   }
/*     */   
/*     */   static Set<String> names(String... names) {
/* 305 */     return new HashSet<>(Arrays.asList(names));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\StandardTags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */