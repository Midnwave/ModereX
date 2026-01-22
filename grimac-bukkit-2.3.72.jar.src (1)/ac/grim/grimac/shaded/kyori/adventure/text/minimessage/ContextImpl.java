/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointered;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.ParsingExceptionImpl;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.Token;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.TagPart;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ class ContextImpl
/*     */   implements Context
/*     */ {
/*  49 */   private static final Token[] EMPTY_TOKEN_ARRAY = new Token[0];
/*     */   
/*     */   private final boolean strict;
/*     */   
/*     */   private final boolean emitVirtuals;
/*     */   
/*     */   private final Consumer<String> debugOutput;
/*     */   
/*     */   private String message;
/*     */   
/*     */   private final MiniMessage miniMessage;
/*     */   
/*     */   @Nullable
/*     */   private final Pointered target;
/*     */   
/*     */   private final TagResolver tagResolver;
/*     */   
/*     */   private final UnaryOperator<String> preProcessor;
/*     */   
/*     */   private final UnaryOperator<Component> postProcessor;
/*     */ 
/*     */   
/*     */   ContextImpl(boolean strict, boolean emitVirtuals, Consumer<String> debugOutput, String message, MiniMessage miniMessage, @Nullable Pointered target, @Nullable TagResolver extraTags, @Nullable UnaryOperator<String> preProcessor, @Nullable UnaryOperator<Component> postProcessor) {
/*  72 */     this.strict = strict;
/*  73 */     this.emitVirtuals = emitVirtuals;
/*  74 */     this.debugOutput = debugOutput;
/*  75 */     this.message = message;
/*  76 */     this.miniMessage = miniMessage;
/*  77 */     this.target = target;
/*  78 */     this.tagResolver = (extraTags == null) ? TagResolver.empty() : extraTags;
/*  79 */     this.preProcessor = (preProcessor == null) ? UnaryOperator.<String>identity() : preProcessor;
/*  80 */     this.postProcessor = (postProcessor == null) ? UnaryOperator.<Component>identity() : postProcessor;
/*     */   }
/*     */   
/*     */   public boolean strict() {
/*  84 */     return this.strict;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean emitVirtuals() {
/*  89 */     return this.emitVirtuals;
/*     */   }
/*     */   
/*     */   public Consumer<String> debugOutput() {
/*  93 */     return this.debugOutput;
/*     */   }
/*     */   @NotNull
/*     */   public String message() {
/*  97 */     return this.message;
/*     */   }
/*     */   
/*     */   void message(@NotNull String message) {
/* 101 */     this.message = message;
/*     */   }
/*     */   @NotNull
/*     */   public TagResolver extraTags() {
/* 105 */     return this.tagResolver;
/*     */   }
/*     */   
/*     */   public UnaryOperator<Component> postProcessor() {
/* 109 */     return this.postProcessor;
/*     */   }
/*     */   
/*     */   public UnaryOperator<String> preProcessor() {
/* 113 */     return this.preProcessor;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Pointered target() {
/* 118 */     return this.target;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Pointered targetOrThrow() {
/* 123 */     if (this.target == null) {
/* 124 */       throw newException("A target is required for this deserialization attempt");
/*     */     }
/* 126 */     return this.target;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public <T extends Pointered> T targetAsType(@NotNull Class<T> targetClass) {
/* 132 */     if (((Class)Objects.<Class<?>>requireNonNull(targetClass, "targetClass")).isInstance(this.target)) {
/* 133 */       return targetClass.cast(this.target);
/*     */     }
/* 135 */     throw newException("A target with type " + targetClass.getSimpleName() + " is required for this deserialization attempt");
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Component deserialize(@NotNull String message) {
/* 141 */     return deserializeWithOptionalTarget(Objects.<String>requireNonNull(message, "message"), this.tagResolver);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component deserialize(@NotNull String message, @NotNull TagResolver resolver) {
/* 146 */     Objects.requireNonNull(message, "message");
/* 147 */     TagResolver combinedResolver = TagResolver.builder().resolver(this.tagResolver).resolver(resolver).build();
/* 148 */     return deserializeWithOptionalTarget(message, combinedResolver);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component deserialize(@NotNull String message, @NotNull TagResolver... resolvers) {
/* 153 */     Objects.requireNonNull(message, "message");
/* 154 */     TagResolver combinedResolver = TagResolver.builder().resolver(this.tagResolver).resolvers(resolvers).build();
/* 155 */     return deserializeWithOptionalTarget(message, combinedResolver);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ParsingException newException(@NotNull String message) {
/* 160 */     return (ParsingException)new ParsingExceptionImpl(message, this.message, null, false, EMPTY_TOKEN_ARRAY);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ParsingException newException(@NotNull String message, @NotNull ArgumentQueue tags) {
/* 165 */     return (ParsingException)new ParsingExceptionImpl(message, this.message, null, false, tagsToTokens(((ArgumentQueueImpl)tags).args));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ParsingException newException(@NotNull String message, @Nullable Throwable cause, @NotNull ArgumentQueue tags) {
/* 170 */     return (ParsingException)new ParsingExceptionImpl(message, this.message, cause, false, tagsToTokens(((ArgumentQueueImpl)tags).args));
/*     */   }
/*     */   @NotNull
/*     */   private Component deserializeWithOptionalTarget(@NotNull String message, @NotNull TagResolver tagResolver) {
/* 174 */     if (this.target != null) {
/* 175 */       return this.miniMessage.deserialize(message, this.target, tagResolver);
/*     */     }
/* 177 */     return this.miniMessage.deserialize(message, tagResolver);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Token[] tagsToTokens(List<? extends Tag.Argument> tags) {
/* 182 */     Token[] tokens = new Token[tags.size()];
/* 183 */     for (int i = 0, length = tokens.length; i < length; i++) {
/* 184 */       tokens[i] = ((TagPart)tags.get(i)).token();
/*     */     }
/* 186 */     return tokens;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\ContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */