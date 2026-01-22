/*    */ package ac.grim.grimac.shaded.incendo.cloud.parser;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*    */ import java.util.Objects;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.concurrent.CompletionStage;
/*    */ import org.apiguardian.api.API;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.INTERNAL)
/*    */ public final class MappedArgumentParserImpl<C, I, O>
/*    */   implements MappedArgumentParser<C, I, O>, ArgumentParser.FutureArgumentParser<C, O>
/*    */ {
/*    */   private final ArgumentParser<C, I> base;
/*    */   private final MappedArgumentParser.Mapper<C, I, O> mapper;
/*    */   
/*    */   MappedArgumentParserImpl(ArgumentParser<C, I> base, MappedArgumentParser.Mapper<C, I, O> mapper) {
/* 47 */     this.base = base;
/* 48 */     this.mapper = mapper;
/*    */   }
/*    */ 
/*    */   
/*    */   public ArgumentParser<C, I> baseParser() {
/* 53 */     return this.base;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CompletableFuture<ArgumentParseResult<O>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 61 */     return this.base.parseFuture(commandContext, commandInput)
/* 62 */       .thenCompose(result -> this.mapper.map(commandContext, result));
/*    */   }
/*    */ 
/*    */   
/*    */   public SuggestionProvider<C> suggestionProvider() {
/* 67 */     return this.base.suggestionProvider();
/*    */   }
/*    */ 
/*    */   
/*    */   public <O1> ArgumentParser.FutureArgumentParser<C, O1> flatMap(MappedArgumentParser.Mapper<C, O, O1> mapper) {
/* 72 */     Objects.requireNonNull(mapper, "mapper");
/* 73 */     return new MappedArgumentParserImpl(this.base, (ctx, orig) -> this.mapper.map(ctx, orig).thenCompose(()));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 82 */     return 31 + this.base.hashCode() + 7 * this.mapper
/* 83 */       .hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 88 */     if (!(other instanceof MappedArgumentParserImpl)) {
/* 89 */       return false;
/*    */     }
/*    */     
/* 92 */     MappedArgumentParserImpl<?, ?, ?> that = (MappedArgumentParserImpl<?, ?, ?>)other;
/* 93 */     return (this.base.equals(that.base) && this.mapper
/* 94 */       .equals(that.mapper));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 99 */     return "MappedArgumentParserImpl{base=" + this.base + ',' + "mapper=" + this.mapper + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\MappedArgumentParserImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */