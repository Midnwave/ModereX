/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointered;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tree.Node;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Services;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
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
/*     */ final class MiniMessageImpl
/*     */   implements MiniMessage
/*     */ {
/*  47 */   private static final Optional<MiniMessage.Provider> SERVICE = Services.service(MiniMessage.Provider.class);
/*  48 */   static final Consumer<MiniMessage.Builder> BUILDER = SERVICE
/*  49 */     .<Consumer<MiniMessage.Builder>>map(MiniMessage.Provider::builder)
/*  50 */     .orElseGet(() -> ());
/*     */ 
/*     */ 
/*     */   
/*     */   static final class Instances
/*     */   {
/*  56 */     static final MiniMessage INSTANCE = MiniMessageImpl.SERVICE
/*  57 */       .map(MiniMessage.Provider::miniMessage)
/*  58 */       .orElseGet(() -> new MiniMessageImpl(TagResolver.standard(), false, true, null, MiniMessageImpl.DEFAULT_NO_OP, MiniMessageImpl.DEFAULT_COMPACTING_METHOD));
/*     */   }
/*     */   
/*  61 */   static final UnaryOperator<String> DEFAULT_NO_OP = UnaryOperator.identity();
/*  62 */   static final UnaryOperator<Component> DEFAULT_COMPACTING_METHOD = Component::compact;
/*     */   private final boolean strict;
/*     */   private final boolean emitVirtuals;
/*     */   @Nullable
/*     */   private final Consumer<String> debugOutput;
/*     */   private final UnaryOperator<Component> postProcessor;
/*     */   private final UnaryOperator<String> preProcessor;
/*     */   final MiniMessageParser parser;
/*     */   
/*     */   MiniMessageImpl(@NotNull TagResolver resolver, boolean strict, boolean emitVirtuals, @Nullable Consumer<String> debugOutput, @NotNull UnaryOperator<String> preProcessor, @NotNull UnaryOperator<Component> postProcessor) {
/*  72 */     this.parser = new MiniMessageParser(resolver);
/*  73 */     this.strict = strict;
/*  74 */     this.emitVirtuals = emitVirtuals;
/*  75 */     this.debugOutput = debugOutput;
/*  76 */     this.preProcessor = preProcessor;
/*  77 */     this.postProcessor = postProcessor;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component deserialize(@NotNull String input) {
/*  82 */     return this.parser.parseFormat(newContext(input, null, null));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component deserialize(@NotNull String input, @NotNull Pointered target) {
/*  87 */     return this.parser.parseFormat(newContext(input, Objects.<Pointered>requireNonNull(target, "target"), null));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component deserialize(@NotNull String input, @NotNull TagResolver tagResolver) {
/*  92 */     return this.parser.parseFormat(newContext(input, null, Objects.<TagResolver>requireNonNull(tagResolver, "tagResolver")));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component deserialize(@NotNull String input, @NotNull Pointered target, @NotNull TagResolver tagResolver) {
/*  97 */     return this.parser.parseFormat(newContext(input, Objects.<Pointered>requireNonNull(target, "target"), Objects.<TagResolver>requireNonNull(tagResolver, "tagResolver")));
/*     */   }
/*     */ 
/*     */   
/*     */   public Node.Root deserializeToTree(@NotNull String input) {
/* 102 */     return (Node.Root)this.parser.parseToTree(newContext(input, null, null));
/*     */   }
/*     */ 
/*     */   
/*     */   public Node.Root deserializeToTree(@NotNull String input, @NotNull Pointered target) {
/* 107 */     return (Node.Root)this.parser.parseToTree(newContext(input, Objects.<Pointered>requireNonNull(target, "target"), null));
/*     */   }
/*     */ 
/*     */   
/*     */   public Node.Root deserializeToTree(@NotNull String input, @NotNull TagResolver tagResolver) {
/* 112 */     return (Node.Root)this.parser.parseToTree(newContext(input, null, Objects.<TagResolver>requireNonNull(tagResolver, "tagResolver")));
/*     */   }
/*     */ 
/*     */   
/*     */   public Node.Root deserializeToTree(@NotNull String input, @NotNull Pointered target, @NotNull TagResolver tagResolver) {
/* 117 */     return (Node.Root)this.parser.parseToTree(newContext(input, Objects.<Pointered>requireNonNull(target, "target"), Objects.<TagResolver>requireNonNull(tagResolver, "tagResolver")));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String serialize(@NotNull Component component) {
/* 122 */     return MiniMessageSerializer.serialize(component, serialResolver(null), this.strict);
/*     */   }
/*     */   
/*     */   private SerializableResolver serialResolver(@Nullable TagResolver extraResolver) {
/* 126 */     if (extraResolver == null) {
/* 127 */       if (this.parser.tagResolver instanceof SerializableResolver) {
/* 128 */         return (SerializableResolver)this.parser.tagResolver;
/*     */       }
/*     */     } else {
/* 131 */       TagResolver combined = TagResolver.resolver(new TagResolver[] { this.parser.tagResolver, extraResolver });
/* 132 */       if (combined instanceof SerializableResolver) {
/* 133 */         return (SerializableResolver)combined;
/*     */       }
/*     */     } 
/*     */     
/* 137 */     return (SerializableResolver)TagResolver.empty();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String escapeTags(@NotNull String input) {
/* 142 */     return this.parser.escapeTokens(newContext(input, null, null));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String escapeTags(@NotNull String input, @NotNull TagResolver tagResolver) {
/* 147 */     return this.parser.escapeTokens(newContext(input, null, tagResolver));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String stripTags(@NotNull String input) {
/* 152 */     return this.parser.stripTokens(newContext(input, null, null));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String stripTags(@NotNull String input, @NotNull TagResolver tagResolver) {
/* 157 */     return this.parser.stripTokens(newContext(input, null, tagResolver));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean strict() {
/* 162 */     return this.strict;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TagResolver tags() {
/* 167 */     return this.parser.tagResolver;
/*     */   }
/*     */   @NotNull
/*     */   private ContextImpl newContext(@NotNull String input, @Nullable Pointered target, @Nullable TagResolver resolver) {
/* 171 */     Objects.requireNonNull(input, "input");
/* 172 */     return new ContextImpl(this.strict, this.emitVirtuals, this.debugOutput, input, this, target, resolver, this.preProcessor, this.postProcessor);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl implements MiniMessage.Builder {
/* 176 */     private TagResolver tagResolver = TagResolver.standard();
/*     */     private boolean strict = false;
/*     */     private boolean emitVirtuals = true;
/* 179 */     private Consumer<String> debug = null;
/* 180 */     private UnaryOperator<Component> postProcessor = MiniMessageImpl.DEFAULT_COMPACTING_METHOD;
/* 181 */     private UnaryOperator<String> preProcessor = MiniMessageImpl.DEFAULT_NO_OP;
/*     */     
/*     */     BuilderImpl() {
/* 184 */       MiniMessageImpl.BUILDER.accept(this);
/*     */     }
/*     */     
/*     */     BuilderImpl(MiniMessageImpl serializer) {
/* 188 */       this();
/* 189 */       this.tagResolver = serializer.parser.tagResolver;
/* 190 */       this.strict = serializer.strict;
/* 191 */       this.debug = serializer.debugOutput;
/* 192 */       this.postProcessor = serializer.postProcessor;
/* 193 */       this.preProcessor = serializer.preProcessor;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public MiniMessage.Builder tags(@NotNull TagResolver tags) {
/* 198 */       this.tagResolver = Objects.<TagResolver>requireNonNull(tags, "tags");
/* 199 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public MiniMessage.Builder editTags(@NotNull Consumer<TagResolver.Builder> adder) {
/* 204 */       Objects.requireNonNull(adder, "adder");
/* 205 */       TagResolver.Builder builder = TagResolver.builder().resolver(this.tagResolver);
/* 206 */       adder.accept(builder);
/* 207 */       this.tagResolver = builder.build();
/* 208 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public MiniMessage.Builder strict(boolean strict) {
/* 213 */       this.strict = strict;
/* 214 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public MiniMessage.Builder emitVirtuals(boolean emitVirtuals) {
/* 219 */       this.emitVirtuals = emitVirtuals;
/* 220 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public MiniMessage.Builder debug(@Nullable Consumer<String> debugOutput) {
/* 225 */       this.debug = debugOutput;
/* 226 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public MiniMessage.Builder postProcessor(@NotNull UnaryOperator<Component> postProcessor) {
/* 231 */       this.postProcessor = Objects.<UnaryOperator<Component>>requireNonNull(postProcessor, "postProcessor");
/* 232 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public MiniMessage.Builder preProcessor(@NotNull UnaryOperator<String> preProcessor) {
/* 237 */       this.preProcessor = Objects.<UnaryOperator<String>>requireNonNull(preProcessor, "preProcessor");
/* 238 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public MiniMessage build() {
/* 243 */       return new MiniMessageImpl(this.tagResolver, this.strict, this.emitVirtuals, this.debug, this.preProcessor, this.postProcessor);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\MiniMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */