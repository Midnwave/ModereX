/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.Stream;
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
/*     */ final class DecorationTag
/*     */ {
/*     */   private static final String B = "b";
/*     */   private static final String I = "i";
/*     */   private static final String EM = "em";
/*     */   private static final String OBF = "obf";
/*     */   private static final String ST = "st";
/*     */   private static final String U = "u";
/*     */   public static final String REVERT = "!";
/*     */   static final Map<TextDecoration, TagResolver> RESOLVERS;
/*     */   
/*     */   static Map.Entry<TextDecoration, Stream<TagResolver>> resolvers(TextDecoration decoration, @Nullable String shortName, @NotNull String... secondaryAliases) {
/*  67 */     String canonicalName = (String)TextDecoration.NAMES.key(decoration);
/*  68 */     Set<String> names = new HashSet<>();
/*  69 */     names.add(canonicalName);
/*  70 */     if (shortName != null) names.add(shortName); 
/*  71 */     Collections.addAll(names, secondaryAliases);
/*     */     
/*  73 */     return new AbstractMap.SimpleImmutableEntry<>(decoration, Stream.concat(
/*  74 */           Stream.of(SerializableResolver.claimingStyle(names, (args, ctx) -> create(decoration, args, ctx), 
/*     */ 
/*     */               
/*  77 */               claim(decoration, (state, emitter) -> emit(canonicalName, (shortName == null) ? canonicalName : shortName, state, emitter)))), names
/*     */           
/*  79 */           .stream().map(name -> TagResolver.resolver("!" + name, createNegated(decoration)))));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  90 */     RESOLVERS = (Map<TextDecoration, TagResolver>)Stream.<Map.Entry>of(new Map.Entry[] { resolvers(TextDecoration.OBFUSCATED, "obf", new String[0]), resolvers(TextDecoration.BOLD, "b", new String[0]), resolvers(TextDecoration.STRIKETHROUGH, "st", new String[0]), resolvers(TextDecoration.UNDERLINED, "u", new String[0]), resolvers(TextDecoration.ITALIC, "em", new String[] { "i" }) }).collect(Collectors.toMap(Map.Entry::getKey, ent -> (TagResolver)((Stream)ent.getValue()).collect(TagResolver.toTagResolver()), (l, r) -> TagResolver.builder().resolver(l).resolver(r).build(), java.util.LinkedHashMap::new));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  97 */   static final TagResolver RESOLVER = TagResolver.resolver(RESOLVERS.values());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Tag create(TextDecoration toApply, ArgumentQueue args, Context ctx) {
/* 103 */     boolean flag = (!args.hasNext() || !args.pop().isFalse());
/*     */     
/* 105 */     return Tag.styling(new StyleBuilderApplicable[] { (StyleBuilderApplicable)toApply.withState(flag) });
/*     */   }
/*     */   
/*     */   static Tag createNegated(TextDecoration toApply) {
/* 109 */     return Tag.styling(new StyleBuilderApplicable[] { (StyleBuilderApplicable)toApply.withState(false) });
/*     */   }
/*     */   @NotNull
/*     */   static StyleClaim<TextDecoration.State> claim(@NotNull TextDecoration decoration, @NotNull BiConsumer<TextDecoration.State, TokenEmitter> emitable) {
/* 113 */     Objects.requireNonNull(decoration, "decoration");
/* 114 */     return StyleClaim.claim("decoration_" + (String)TextDecoration.NAMES
/* 115 */         .key(decoration), style -> style.decoration(decoration), state -> (state != TextDecoration.State.NOT_SET), emitable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void emit(@NotNull String longName, @NotNull String shortName, TextDecoration.State state, @NotNull TokenEmitter emitter) {
/* 123 */     if (state == TextDecoration.State.FALSE) {
/* 124 */       emitter.tag("!" + longName);
/*     */     } else {
/* 126 */       emitter.tag(longName);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\DecorationTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */