/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.BiFunction;
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Value.Immutable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ @Immutable
/*     */ public interface ParserDescriptor<C, T>
/*     */ {
/*     */   ArgumentParser<C, T> parser();
/*     */   
/*     */   TypeToken<T> valueType();
/*     */   
/*     */   default <O> ParserDescriptor<C, O> flatMap(TypeToken<O> mappedType, MappedArgumentParser.Mapper<C, T, O> mapper) {
/*  66 */     return parserDescriptor(parser().flatMap(mapper), mappedType);
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
/*     */   default <O> ParserDescriptor<C, O> flatMap(Class<O> mappedType, MappedArgumentParser.Mapper<C, T, O> mapper) {
/*  81 */     return parserDescriptor(parser().flatMap(mapper), mappedType);
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
/*     */   default <O> ParserDescriptor<C, O> flatMapSuccess(TypeToken<O> mappedType, BiFunction<CommandContext<C>, T, CompletableFuture<ArgumentParseResult<O>>> mapper) {
/*  96 */     return parserDescriptor(parser().flatMapSuccess(mapper), mappedType);
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
/*     */   default <O> ParserDescriptor<C, O> flatMapSuccess(Class<O> mappedType, BiFunction<CommandContext<C>, T, CompletableFuture<ArgumentParseResult<O>>> mapper) {
/* 111 */     return parserDescriptor(parser().flatMapSuccess(mapper), mappedType);
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
/*     */   default <O> ParserDescriptor<C, O> mapSuccess(TypeToken<O> mappedType, BiFunction<CommandContext<C>, T, CompletableFuture<O>> mapper) {
/* 126 */     return parserDescriptor(parser().mapSuccess(mapper), mappedType);
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
/*     */   default <O> ParserDescriptor<C, O> mapSuccess(Class<O> mappedType, BiFunction<CommandContext<C>, T, CompletableFuture<O>> mapper) {
/* 141 */     return parserDescriptor(parser().mapSuccess(mapper), mappedType);
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
/*     */   static <C, T> ParserDescriptor<C, T> of(ArgumentParser<C, T> parser, TypeToken<T> valueType) {
/* 157 */     return ParserDescriptorImpl.of(parser, valueType);
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
/*     */   static <C, T> ParserDescriptor<C, T> of(ArgumentParser<C, T> parser, Class<T> valueType) {
/* 173 */     return ParserDescriptorImpl.of(parser, TypeToken.get(valueType));
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
/*     */   static <C, T> ParserDescriptor<C, T> parserDescriptor(ArgumentParser<C, T> parser, TypeToken<T> valueType) {
/* 189 */     return of(parser, valueType);
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
/*     */   static <C, T> ParserDescriptor<C, T> parserDescriptor(ArgumentParser<C, T> parser, Class<T> valueType) {
/* 205 */     return of(parser, TypeToken.get(valueType));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\ParserDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */