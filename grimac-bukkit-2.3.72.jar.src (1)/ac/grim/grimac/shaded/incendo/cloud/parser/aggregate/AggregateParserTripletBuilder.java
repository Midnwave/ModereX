/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.aggregate;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.TypedCommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Triplet;
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
/*     */ public final class AggregateParserTripletBuilder<C, U, V, Z, O>
/*     */ {
/*     */   private final Mapper<C, U, V, Z, O> mapper;
/*     */   private final TypeToken<O> outType;
/*     */   private final TypedCommandComponent<C, U> first;
/*     */   private final TypedCommandComponent<C, V> second;
/*     */   private final TypedCommandComponent<C, Z> third;
/*     */   
/*     */   public static <C, U, V, Z> Mapper<C, U, V, Z, Triplet<U, V, Z>> defaultMapper() {
/*  49 */     return (ctx, u, v, z) -> ArgumentParseResult.successFuture(Triplet.of(u, v, z));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public AggregateParserTripletBuilder(TypedCommandComponent<C, U> first, TypedCommandComponent<C, V> second, TypedCommandComponent<C, Z> third, Mapper<C, U, V, Z, O> mapper, TypeToken<O> outType) {
/*  74 */     this.mapper = mapper;
/*  75 */     this.outType = outType;
/*  76 */     this.first = first;
/*  77 */     this.second = second;
/*  78 */     this.third = third;
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
/*     */   public <O1> AggregateParserTripletBuilder<C, U, V, Z, O1> withMapper(TypeToken<O1> outType, Mapper<C, U, V, Z, O1> mapper) {
/*  93 */     return new AggregateParserTripletBuilder(this.first, this.second, this.third, mapper, outType);
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
/*     */   public <O1> AggregateParserTripletBuilder<C, U, V, Z, O1> withDirectMapper(TypeToken<O1> outType, Mapper.DirectSuccessMapper<C, U, V, Z, O1> mapper) {
/* 108 */     return withMapper(outType, mapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AggregateParser<C, O> build() {
/* 117 */     return (new AggregateParserBuilder<>((List)Arrays.asList((Object[])new TypedCommandComponent[] { this.first, this.second, this.third }))).<O>withMapper(this.outType, (commandContext, aggregateContext) -> {
/*     */           U firstResult = (U)aggregateContext.get(this.first.name());
/*     */           
/*     */           V secondResult = (V)aggregateContext.get(this.second.name());
/*     */           
/*     */           Z thirdResult = (Z)aggregateContext.get(this.third.name());
/*     */           
/*     */           return this.mapper.map(commandContext, firstResult, secondResult, thirdResult);
/* 125 */         }).build();
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
/*     */   public static <C, U, V, Z, O> Mapper<C, U, V, Z, O> directMapper(Mapper.DirectSuccessMapper<C, U, V, Z, O> mapper) {
/* 141 */     return Objects.<Mapper<C, U, V, Z, O>>requireNonNull(mapper, "mapper");
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
/*     */   public static interface Mapper<C, U, V, Z, O>
/*     */   {
/*     */     CompletableFuture<ArgumentParseResult<O>> map(CommandContext<C> param1CommandContext, U param1U, V param1V, Z param1Z);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static interface DirectSuccessMapper<C, U, V, Z, O>
/*     */       extends Mapper<C, U, V, Z, O>
/*     */     {
/*     */       O mapSuccess(CommandContext<C> param2CommandContext, U param2U, V param2V, Z param2Z);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       default CompletableFuture<ArgumentParseResult<O>> map(CommandContext<C> commandContext, U firstResult, V secondResult, Z thirdResult)
/*     */       {
/* 187 */         return ArgumentParseResult.successFuture(mapSuccess(commandContext, firstResult, secondResult, thirdResult)); } } } public static interface DirectSuccessMapper<C, U, V, Z, O> extends Mapper<C, U, V, Z, O> { default CompletableFuture<ArgumentParseResult<O>> map(CommandContext<C> commandContext, U firstResult, V secondResult, Z thirdResult) { return ArgumentParseResult.successFuture(mapSuccess(commandContext, firstResult, secondResult, thirdResult)); }
/*     */ 
/*     */     
/*     */     O mapSuccess(CommandContext<C> param1CommandContext, U param1U, V param1V, Z param1Z); }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\aggregate\AggregateParserTripletBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */