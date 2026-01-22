/*     */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.node;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.SuggestionsType;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import com.mojang.brigadier.suggestion.SuggestionProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Generated;
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
/*     */ @API(status = API.Status.STABLE, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "ArgumentMapping", generator = "Immutables")
/*     */ final class ImmutableArgumentMapping<S>
/*     */   implements ArgumentMapping<S>
/*     */ {
/*     */   private final ArgumentType<?> argumentType;
/*     */   private final SuggestionsType suggestionsType;
/*     */   private final SuggestionProvider<S> suggestionProvider;
/*     */   
/*     */   private ImmutableArgumentMapping(ArgumentType<?> argumentType, SuggestionsType suggestionsType, SuggestionProvider<S> suggestionProvider) {
/*  58 */     this.argumentType = Objects.<ArgumentType>requireNonNull(argumentType, "argumentType");
/*  59 */     this.suggestionsType = Objects.<SuggestionsType>requireNonNull(suggestionsType, "suggestionsType");
/*  60 */     this.suggestionProvider = suggestionProvider;
/*     */   }
/*     */   
/*     */   private ImmutableArgumentMapping(Builder<S> builder) {
/*  64 */     this.argumentType = builder.argumentType;
/*  65 */     this.suggestionProvider = builder.suggestionProvider;
/*  66 */     this
/*     */       
/*  68 */       .suggestionsType = (builder.suggestionsType != null) ? builder.suggestionsType : Objects.<SuggestionsType>requireNonNull(super.suggestionsType(), "suggestionsType");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ImmutableArgumentMapping(ImmutableArgumentMapping<S> original, ArgumentType<?> argumentType, SuggestionsType suggestionsType, SuggestionProvider<S> suggestionProvider) {
/*  76 */     this.argumentType = argumentType;
/*  77 */     this.suggestionsType = suggestionsType;
/*  78 */     this.suggestionProvider = suggestionProvider;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentType<?> argumentType() {
/*  86 */     return this.argumentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SuggestionsType suggestionsType() {
/*  94 */     return this.suggestionsType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SuggestionProvider<S> suggestionProvider() {
/* 102 */     return this.suggestionProvider;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ImmutableArgumentMapping<S> withArgumentType(ArgumentType<?> value) {
/* 112 */     if (this.argumentType == value) return this; 
/* 113 */     ArgumentType<?> newValue = Objects.<ArgumentType>requireNonNull(value, "argumentType");
/* 114 */     return new ImmutableArgumentMapping(this, newValue, this.suggestionsType, this.suggestionProvider);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ImmutableArgumentMapping<S> withSuggestionsType(SuggestionsType value) {
/* 124 */     SuggestionsType newValue = Objects.<SuggestionsType>requireNonNull(value, "suggestionsType");
/* 125 */     if (this.suggestionsType == newValue) return this; 
/* 126 */     return new ImmutableArgumentMapping(this, this.argumentType, newValue, this.suggestionProvider);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ImmutableArgumentMapping<S> withSuggestionProvider(SuggestionProvider<S> value) {
/* 136 */     if (this.suggestionProvider == value) return this; 
/* 137 */     return new ImmutableArgumentMapping(this, this.argumentType, this.suggestionsType, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object another) {
/* 146 */     if (this == another) return true; 
/* 147 */     return (another instanceof ImmutableArgumentMapping && 
/* 148 */       equalsByValue((ImmutableArgumentMapping)another));
/*     */   }
/*     */   
/*     */   private boolean equalsByValue(ImmutableArgumentMapping<?> another) {
/* 152 */     return (this.argumentType.equals(another.argumentType) && this.suggestionsType
/* 153 */       .equals(another.suggestionsType) && 
/* 154 */       Objects.equals(this.suggestionProvider, another.suggestionProvider));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 163 */     int h = 5381;
/* 164 */     h += (h << 5) + this.argumentType.hashCode();
/* 165 */     h += (h << 5) + this.suggestionsType.hashCode();
/* 166 */     h += (h << 5) + Objects.hashCode(this.suggestionProvider);
/* 167 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 176 */     return "ArgumentMapping{argumentType=" + this.argumentType + ", suggestionsType=" + this.suggestionsType + ", suggestionProvider=" + this.suggestionProvider + "}";
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <S> ImmutableArgumentMapping<S> of(ArgumentType<?> argumentType, SuggestionsType suggestionsType, SuggestionProvider<S> suggestionProvider) {
/* 192 */     return new ImmutableArgumentMapping<>(argumentType, suggestionsType, suggestionProvider);
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
/*     */   public static <S> ImmutableArgumentMapping<S> copyOf(ArgumentMapping<S> instance) {
/* 204 */     if (instance instanceof ImmutableArgumentMapping) {
/* 205 */       return (ImmutableArgumentMapping<S>)instance;
/*     */     }
/* 207 */     return builder()
/* 208 */       .from(instance)
/* 209 */       .build();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <S> Builder<S> builder() {
/* 225 */     return new Builder<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Generated(from = "ArgumentMapping", generator = "Immutables")
/*     */   static final class Builder<S>
/*     */   {
/*     */     private static final long INIT_BIT_ARGUMENT_TYPE = 1L;
/*     */ 
/*     */ 
/*     */     
/* 238 */     private long initBits = 1L;
/*     */ 
/*     */ 
/*     */     
/*     */     private ArgumentType<?> argumentType;
/*     */ 
/*     */ 
/*     */     
/*     */     private SuggestionsType suggestionsType;
/*     */ 
/*     */ 
/*     */     
/*     */     private SuggestionProvider<S> suggestionProvider;
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder<S> from(ArgumentMapping<S> instance) {
/* 255 */       Objects.requireNonNull(instance, "instance");
/* 256 */       argumentType(instance.argumentType());
/* 257 */       suggestionsType(instance.suggestionsType());
/* 258 */       SuggestionProvider<S> suggestionProviderValue = instance.suggestionProvider();
/* 259 */       if (suggestionProviderValue != null) {
/* 260 */         suggestionProvider(suggestionProviderValue);
/*     */       }
/* 262 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder<S> argumentType(ArgumentType<?> argumentType) {
/* 271 */       this.argumentType = Objects.<ArgumentType>requireNonNull(argumentType, "argumentType");
/* 272 */       this.initBits &= 0xFFFFFFFFFFFFFFFEL;
/* 273 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder<S> suggestionsType(SuggestionsType suggestionsType) {
/* 283 */       this.suggestionsType = Objects.<SuggestionsType>requireNonNull(suggestionsType, "suggestionsType");
/* 284 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder<S> suggestionProvider(SuggestionProvider<S> suggestionProvider) {
/* 293 */       this.suggestionProvider = suggestionProvider;
/* 294 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ImmutableArgumentMapping<S> build() {
/* 303 */       if (this.initBits != 0L) {
/* 304 */         throw new IllegalStateException(formatRequiredAttributesMessage());
/*     */       }
/* 306 */       return new ImmutableArgumentMapping<>(this);
/*     */     }
/*     */     
/*     */     private String formatRequiredAttributesMessage() {
/* 310 */       List<String> attributes = new ArrayList<>();
/* 311 */       if ((this.initBits & 0x1L) != 0L) attributes.add("argumentType"); 
/* 312 */       return "Cannot build ArgumentMapping, some of required attributes are not set " + attributes;
/*     */     }
/*     */     
/*     */     private Builder() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\node\ImmutableArgumentMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */