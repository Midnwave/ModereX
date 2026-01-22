/*     */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.argument;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import com.mojang.brigadier.context.CommandContext;
/*     */ import com.mojang.brigadier.exceptions.CommandSyntaxException;
/*     */ import com.mojang.brigadier.suggestion.SuggestionProvider;
/*     */ import com.mojang.brigadier.suggestion.SuggestionsBuilder;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Function;
/*     */ import org.apiguardian.api.API;
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
/*     */ @API(status = API.Status.INTERNAL, since = "2.0.0")
/*     */ public final class BrigadierMapping<C, K extends ArgumentParser<C, ?>, S>
/*     */ {
/*     */   private static final SuggestionProvider<?> DELEGATE_TO_CLOUD;
/*     */   private final boolean cloudSuggestions;
/*     */   private final BrigadierMappingBuilder.SuggestionProviderSupplier<K, S> suggestionsOverride;
/*     */   private final Function<K, ? extends ArgumentType<?>> mapper;
/*     */   
/*     */   static {
/*  40 */     DELEGATE_TO_CLOUD = ((c, b) -> b.buildFuture());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> SuggestionProvider<T> delegateSuggestions() {
/*  50 */     return (SuggestionProvider)DELEGATE_TO_CLOUD;
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
/*     */   public static <C, K extends ArgumentParser<C, ?>, S> BrigadierMappingBuilder<K, S> builder() {
/*  62 */     return new BuilderImpl<>();
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
/*     */   BrigadierMapping(boolean cloudSuggestions, BrigadierMappingBuilder.SuggestionProviderSupplier<K, S> suggestionsOverride, Function<K, ? extends ArgumentType<?>> mapper) {
/*  74 */     this.cloudSuggestions = cloudSuggestions;
/*  75 */     this.suggestionsOverride = suggestionsOverride;
/*  76 */     this.mapper = mapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Function<K, ? extends ArgumentType<?>> mapper() {
/*  85 */     return this.mapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BrigadierMapping<C, K, S> withNativeSuggestions(boolean nativeSuggestions) {
/*  96 */     if (nativeSuggestions && this.cloudSuggestions)
/*  97 */       return new BrigadierMapping(false, this.suggestionsOverride, this.mapper); 
/*  98 */     if (!nativeSuggestions && !this.cloudSuggestions) {
/*  99 */       return new BrigadierMapping(true, this.suggestionsOverride, this.mapper);
/*     */     }
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SuggestionProvider<S> makeSuggestionProvider(K commandArgument) {
/* 112 */     if (this.cloudSuggestions) {
/* 113 */       return delegateSuggestions();
/*     */     }
/* 115 */     return (this.suggestionsOverride == null) ? 
/* 116 */       null : 
/* 117 */       (SuggestionProvider)this.suggestionsOverride.provide(commandArgument, 
/*     */         
/* 119 */         delegateSuggestions());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class BuilderImpl<C, K extends ArgumentParser<C, ?>, S>
/*     */     implements BrigadierMappingBuilder<K, S>
/*     */   {
/*     */     private Function<K, ? extends ArgumentType<?>> mapper;
/*     */     
/*     */     private boolean cloudSuggestions = false;
/*     */     
/*     */     private BrigadierMappingBuilder.SuggestionProviderSupplier<K, S> suggestionsOverride;
/*     */ 
/*     */     
/*     */     public BrigadierMappingBuilder<K, S> toConstant(ArgumentType<?> constant) {
/* 135 */       return to(parser -> constant);
/*     */     }
/*     */ 
/*     */     
/*     */     public BrigadierMappingBuilder<K, S> to(Function<K, ? extends ArgumentType<?>> mapper) {
/* 140 */       this.mapper = mapper;
/* 141 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public BrigadierMappingBuilder<K, S> nativeSuggestions() {
/* 146 */       this.cloudSuggestions = false;
/* 147 */       this.suggestionsOverride = null;
/* 148 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public BrigadierMappingBuilder<K, S> cloudSuggestions() {
/* 153 */       this.cloudSuggestions = true;
/* 154 */       this.suggestionsOverride = null;
/* 155 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public BrigadierMappingBuilder<K, S> suggestedByConstant(SuggestionProvider<S> provider) {
/* 160 */       super.suggestedByConstant(provider);
/* 161 */       this.cloudSuggestions = false;
/* 162 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public BrigadierMappingBuilder<K, S> suggestedBy(BrigadierMappingBuilder.SuggestionProviderSupplier<K, S> provider) {
/* 167 */       this.suggestionsOverride = Objects.<BrigadierMappingBuilder.SuggestionProviderSupplier<K, S>>requireNonNull(provider, "provider");
/* 168 */       this.cloudSuggestions = false;
/* 169 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public BrigadierMapping<C, K, S> build() {
/* 174 */       return new BrigadierMapping<>(this.cloudSuggestions, this.suggestionsOverride, this.mapper);
/*     */     }
/*     */     
/*     */     private BuilderImpl() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\argument\BrigadierMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */