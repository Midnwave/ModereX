/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ final class ColorTagResolver
/*     */   implements TagResolver, SerializableResolver.Single
/*     */ {
/*     */   private static final String COLOR_3 = "c";
/*     */   private static final String COLOR_2 = "colour";
/*     */   private static final String COLOR = "color";
/*  51 */   static final TagResolver INSTANCE = new ColorTagResolver(); static {
/*  52 */     STYLE = StyleClaim.claim("color", Style::color, (color, emitter) -> {
/*     */           if (color instanceof NamedTextColor) {
/*     */             emitter.tag((String)NamedTextColor.NAMES.key(color));
/*     */           } else {
/*     */             emitter.tag(color.asHexString());
/*     */           } 
/*     */         });
/*     */   }
/*     */   
/*     */   private static final StyleClaim<TextColor> STYLE;
/*  62 */   private static final Map<String, TextColor> COLOR_ALIASES = new HashMap<>();
/*     */   
/*     */   static {
/*  65 */     COLOR_ALIASES.put("dark_grey", NamedTextColor.DARK_GRAY);
/*  66 */     COLOR_ALIASES.put("grey", NamedTextColor.GRAY);
/*     */   }
/*     */   
/*     */   private static boolean isColorOrAbbreviation(String name) {
/*  70 */     return (name.equals("color") || name.equals("colour") || name.equals("c"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Tag resolve(@NotNull String name, @NotNull ArgumentQueue args, @NotNull Context ctx) throws ParsingException {
/*     */     String colorName;
/*  78 */     if (!has(name)) {
/*  79 */       return null;
/*     */     }
/*     */ 
/*     */     
/*  83 */     if (isColorOrAbbreviation(name)) {
/*  84 */       colorName = args.popOr("Expected to find a color parameter: <name>|#RRGGBB").lowerValue();
/*     */     } else {
/*  86 */       colorName = name;
/*     */     } 
/*     */     
/*  89 */     TextColor color = resolveColor(colorName, ctx);
/*  90 */     return Tag.styling(new StyleBuilderApplicable[] { (StyleBuilderApplicable)color });
/*     */   }
/*     */   @Nullable
/*     */   static TextColor resolveColorOrNull(String colorName) {
/*     */     TextColor color;
/*  95 */     if (COLOR_ALIASES.containsKey(colorName)) {
/*  96 */       color = COLOR_ALIASES.get(colorName);
/*  97 */     } else if (colorName.charAt(0) == '#') {
/*  98 */       color = TextColor.fromHexString(colorName);
/*     */     } else {
/* 100 */       color = (TextColor)NamedTextColor.NAMES.value(colorName);
/*     */     } 
/*     */     
/* 103 */     return color;
/*     */   }
/*     */   @NotNull
/*     */   static TextColor resolveColor(@NotNull String colorName, @NotNull Context ctx) throws ParsingException {
/* 107 */     TextColor color = resolveColorOrNull(colorName);
/* 108 */     if (color == null) {
/* 109 */       throw ctx.newException(String.format("Unable to parse a color from '%s'. Please use named colours or hex (#RRGGBB) colors.", new Object[] { colorName }));
/*     */     }
/* 111 */     return color;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean has(@NotNull String name) {
/* 116 */     return (isColorOrAbbreviation(name) || NamedTextColor.NAMES
/* 117 */       .value(name) != null || COLOR_ALIASES
/* 118 */       .containsKey(name) || 
/* 119 */       TextColor.fromHexString(name) != null);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public StyleClaim<?> claimStyle() {
/* 124 */     return STYLE;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\ColorTagResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */