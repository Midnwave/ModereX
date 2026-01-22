/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.Collection;
/*     */ import java.util.Optional;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public interface ParserRegistry<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   default <T> ParserRegistry<C> registerParser(ParserDescriptor<C, T> descriptor) {
/*  69 */     return registerParserSupplier(descriptor.valueType(), parameters -> descriptor.parser());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default ParserRegistry<C> registerNamedParser(String name, ParserDescriptor<C, ?> descriptor) {
/* 101 */     return registerNamedParserSupplier(name, parameters -> descriptor.parser());
/*     */   }
/*     */   
/*     */   <T> ParserRegistry<C> registerParserSupplier(TypeToken<T> paramTypeToken, Function<ParserParameters, ArgumentParser<C, ?>> paramFunction);
/*     */   
/*     */   ParserRegistry<C> registerNamedParserSupplier(String paramString, Function<ParserParameters, ArgumentParser<C, ?>> paramFunction);
/*     */   
/*     */   <A extends Annotation> ParserRegistry<C> registerAnnotationMapper(Class<A> paramClass, AnnotationMapper<A> paramAnnotationMapper);
/*     */   
/*     */   ParserParameters parseAnnotations(TypeToken<?> paramTypeToken, Collection<? extends Annotation> paramCollection);
/*     */   
/*     */   <T> Optional<ArgumentParser<C, T>> createParser(TypeToken<T> paramTypeToken, ParserParameters paramParserParameters);
/*     */   
/*     */   <T> Optional<ArgumentParser<C, T>> createParser(String paramString, ParserParameters paramParserParameters);
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   void registerSuggestionProvider(String paramString, SuggestionProvider<C> paramSuggestionProvider);
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   Optional<SuggestionProvider<C>> getSuggestionProvider(String paramString);
/*     */   
/*     */   @FunctionalInterface
/*     */   @API(status = API.Status.STABLE)
/*     */   public static interface AnnotationMapper<A extends Annotation> {
/*     */     ParserParameters mapAnnotation(A param1A, TypeToken<?> param1TypeToken);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\ParserRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */