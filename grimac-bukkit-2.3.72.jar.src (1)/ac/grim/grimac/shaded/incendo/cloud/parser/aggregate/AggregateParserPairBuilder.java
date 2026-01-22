/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.aggregate;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.TypedCommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Pair;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class AggregateParserPairBuilder<C, U, V, O>
/*     */ {
/*     */   private final Mapper<C, U, V, O> mapper;
/*     */   private final TypeToken<O> outType;
/*     */   private final TypedCommandComponent<C, U> first;
/*     */   private final TypedCommandComponent<C, V> second;
/*     */   
/*     */   public static <C, U, V> Mapper<C, U, V, Pair<U, V>> defaultMapper() {
/*  48 */     return (ctx, u, v) -> ArgumentParseResult.successFuture(Pair.of(u, v));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AggregateParserPairBuilder(TypedCommandComponent<C, U> first, TypedCommandComponent<C, V> second, Mapper<C, U, V, O> mapper, TypeToken<O> outType) {
/*  70 */     this.mapper = mapper;
/*  71 */     this.outType = outType;
/*  72 */     this.first = first;
/*  73 */     this.second = second;
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
/*     */   public <O1> AggregateParserPairBuilder<C, U, V, O1> withMapper(TypeToken<O1> outType, Mapper<C, U, V, O1> mapper) {
/*  88 */     return new AggregateParserPairBuilder(this.first, this.second, mapper, outType);
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
/*     */   public <O1> AggregateParserPairBuilder<C, U, V, O1> withDirectMapper(TypeToken<O1> outType, Mapper.DirectSuccessMapper<C, U, V, O1> mapper) {
/* 103 */     return withMapper(outType, mapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AggregateParser<C, O> build() {
/* 112 */     return (new AggregateParserBuilder<>((List)Arrays.asList((Object[])new TypedCommandComponent[] { this.first, this.second }))).<O>withMapper(this.outType, (commandContext, aggregateContext) -> {
/*     */           U firstResult = (U)aggregateContext.get(this.first.name());
/*     */ 
/*     */           
/*     */           V secondResult = (V)aggregateContext.get(this.second.name());
/*     */           
/*     */           return this.mapper.map(commandContext, firstResult, secondResult);
/* 119 */         }).build();
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
/*     */   public static <C, U, V, O> Mapper<C, U, V, O> directMapper(Mapper.DirectSuccessMapper<C, U, V, O> mapper) {
/* 134 */     return Objects.<Mapper<C, U, V, O>>requireNonNull(mapper, "mapper");
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
/*     */   public static interface Mapper<C, U, V, O>
/*     */   {
/*     */     CompletableFuture<ArgumentParseResult<O>> map(CommandContext<C> param1CommandContext, U param1U, V param1V);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static interface DirectSuccessMapper<C, U, V, O>
/*     */       extends Mapper<C, U, V, O>
/*     */     {
/*     */       O mapSuccess(CommandContext<C> param2CommandContext, U param2U, V param2V);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       default CompletableFuture<ArgumentParseResult<O>> map(CommandContext<C> commandContext, U firstResult, V secondResult)
/*     */       {
/* 175 */         return ArgumentParseResult.successFuture(mapSuccess(commandContext, firstResult, secondResult)); } } } public static interface DirectSuccessMapper<C, U, V, O> extends Mapper<C, U, V, O> { default CompletableFuture<ArgumentParseResult<O>> map(CommandContext<C> commandContext, U firstResult, V secondResult) { return ArgumentParseResult.successFuture(mapSuccess(commandContext, firstResult, secondResult)); }
/*     */ 
/*     */     
/*     */     O mapSuccess(CommandContext<C> param1CommandContext, U param1U, V param1V); }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\aggregate\AggregateParserPairBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */