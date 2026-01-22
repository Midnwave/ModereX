/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.aggregate;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public class AggregateParserBuilder<C>
/*     */ {
/*     */   private final List<CommandComponent<C>> components;
/*     */   
/*     */   AggregateParserBuilder(List<? extends CommandComponent<C>> components) {
/*  43 */     this.components = Collections.unmodifiableList(components);
/*     */   }
/*     */   
/*     */   AggregateParserBuilder() {
/*  47 */     this.components = Collections.emptyList();
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
/*     */   public final <O> MappedAggregateParserBuilder<C, O> withMapper(TypeToken<O> valueType, AggregateResultMapper<C, O> mapper) {
/*  62 */     return new MappedAggregateParserBuilder<>(components(), valueType, mapper);
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
/*     */   public final <O> MappedAggregateParserBuilder<C, O> withMapper(Class<O> valueType, AggregateResultMapper<C, O> mapper) {
/*  77 */     return new MappedAggregateParserBuilder<>(components(), TypeToken.get(valueType), mapper);
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
/*     */   
/*     */   public final <O> MappedAggregateParserBuilder<C, O> withDirectMapper(Class<O> valueType, AggregateResultMapper.DirectSuccessMapper<C, O> mapper) {
/*  94 */     return new MappedAggregateParserBuilder<>(components(), TypeToken.get(valueType), mapper);
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
/*     */   
/*     */   public final <O> MappedAggregateParserBuilder<C, O> withDirectMapper(TypeToken<O> valueType, AggregateResultMapper.DirectSuccessMapper<C, O> mapper) {
/* 111 */     return new MappedAggregateParserBuilder<>(components(), valueType, mapper);
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
/*     */   public AggregateParserBuilder<C> withComponent(CommandComponent<C> component) {
/* 123 */     List<CommandComponent<C>> components = new ArrayList<>(components());
/* 124 */     components.add(component);
/* 125 */     return new AggregateParserBuilder(components);
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
/*     */   public <T> AggregateParserBuilder<C> withComponent(String name, ParserDescriptor<C, T> parserDescriptor) {
/* 140 */     return withComponent((CommandComponent<C>)CommandComponent.builder().name(name).parser(parserDescriptor).build());
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
/*     */   
/*     */   public <T> AggregateParserBuilder<C> withComponent(String name, ParserDescriptor<C, T> parserDescriptor, SuggestionProvider<C> suggestionProvider) {
/* 157 */     return withComponent(
/* 158 */         (CommandComponent<C>)CommandComponent.builder()
/* 159 */         .name(name)
/* 160 */         .parser(parserDescriptor)
/* 161 */         .suggestionProvider(suggestionProvider)
/* 162 */         .build());
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
/*     */   public <T> AggregateParserBuilder<C> withComponent(CloudKey<T> name, ParserDescriptor<C, T> parserDescriptor) {
/* 178 */     return withComponent((CommandComponent<C>)CommandComponent.builder().key(name).parser(parserDescriptor).build());
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
/*     */   
/*     */   public <T> AggregateParserBuilder<C> withComponent(CloudKey<T> name, ParserDescriptor<C, T> parserDescriptor, SuggestionProvider<C> suggestionProvider) {
/* 195 */     return withComponent(
/* 196 */         (CommandComponent<C>)CommandComponent.builder()
/* 197 */         .key(name)
/* 198 */         .parser(parserDescriptor)
/* 199 */         .suggestionProvider(suggestionProvider)
/* 200 */         .build());
/*     */   }
/*     */ 
/*     */   
/*     */   final List<CommandComponent<C>> components() {
/* 205 */     return this.components;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class MappedAggregateParserBuilder<C, O>
/*     */     extends AggregateParserBuilder<C>
/*     */   {
/*     */     private final AggregateResultMapper<C, O> mapper;
/*     */     
/*     */     private final TypeToken<O> valueType;
/*     */ 
/*     */     
/*     */     MappedAggregateParserBuilder(List<CommandComponent<C>> components, TypeToken<O> valueType, AggregateResultMapper<C, O> mapper) {
/* 219 */       super(components);
/* 220 */       this.valueType = valueType;
/* 221 */       this.mapper = mapper;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public MappedAggregateParserBuilder<C, O> withComponent(CommandComponent<C> component) {
/* 228 */       List<CommandComponent<C>> components = new ArrayList<>(components());
/* 229 */       components.add(component);
/* 230 */       return new MappedAggregateParserBuilder(components, this.valueType, this.mapper);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> MappedAggregateParserBuilder<C, O> withComponent(String name, ParserDescriptor<C, T> parserDescriptor) {
/* 238 */       return withComponent((CommandComponent<C>)CommandComponent.builder().name(name).parser(parserDescriptor).build());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> MappedAggregateParserBuilder<C, O> withComponent(String name, ParserDescriptor<C, T> parserDescriptor, SuggestionProvider<C> suggestionProvider) {
/* 247 */       return withComponent(
/* 248 */           (CommandComponent<C>)CommandComponent.builder()
/* 249 */           .name(name)
/* 250 */           .parser(parserDescriptor)
/* 251 */           .suggestionProvider(suggestionProvider)
/* 252 */           .build());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> MappedAggregateParserBuilder<C, O> withComponent(CloudKey<T> name, ParserDescriptor<C, T> parserDescriptor) {
/* 261 */       return withComponent((CommandComponent<C>)CommandComponent.builder().key(name).parser(parserDescriptor).build());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> MappedAggregateParserBuilder<C, O> withComponent(CloudKey<T> name, ParserDescriptor<C, T> parserDescriptor, SuggestionProvider<C> suggestionProvider) {
/* 270 */       return withComponent(
/* 271 */           (CommandComponent<C>)CommandComponent.builder()
/* 272 */           .key(name)
/* 273 */           .parser(parserDescriptor)
/* 274 */           .suggestionProvider(suggestionProvider)
/* 275 */           .build());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public AggregateParser<C, O> build() {
/* 285 */       return new AggregateParserImpl<>(components(), this.valueType, this.mapper);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\aggregate\AggregateParserBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */