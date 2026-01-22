/*     */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.TooltipSuggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import com.mojang.brigadier.StringReader;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import com.mojang.brigadier.context.CommandContext;
/*     */ import com.mojang.brigadier.context.StringRange;
/*     */ import com.mojang.brigadier.exceptions.CommandSyntaxException;
/*     */ import com.mojang.brigadier.suggestion.Suggestion;
/*     */ import com.mojang.brigadier.suggestion.Suggestions;
/*     */ import com.mojang.brigadier.suggestion.SuggestionsBuilder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Supplier;
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
/*     */ public class WrappedBrigadierParser<C, T>
/*     */   implements ArgumentParser<C, T>, SuggestionProvider<C>
/*     */ {
/*     */   public static final String COMMAND_CONTEXT_BRIGADIER_NATIVE_SENDER = "_cloud_brigadier_native_sender";
/*     */   private final Supplier<ArgumentType<T>> nativeType;
/*     */   private final ParseFunction<T> parse;
/*     */   
/*     */   public WrappedBrigadierParser(ArgumentType<T> argumentType) {
/*  70 */     this(() -> argumentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WrappedBrigadierParser(Supplier<ArgumentType<T>> argumentTypeSupplier) {
/*  80 */     this(argumentTypeSupplier, null);
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
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public WrappedBrigadierParser(Supplier<ArgumentType<T>> argumentTypeSupplier, ParseFunction<T> parse) {
/*  95 */     Objects.requireNonNull(argumentTypeSupplier, "brigadierType");
/*  96 */     this.nativeType = argumentTypeSupplier;
/*  97 */     this.parse = parse;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ArgumentType<T> nativeArgumentType() {
/* 107 */     return this.nativeType.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ArgumentParseResult<T> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 116 */     StringReader reader = CloudStringReader.of(commandInput);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 122 */       T result = (this.parse != null) ? this.parse.apply(this.nativeType.get(), reader) : (T)((ArgumentType)this.nativeType.get()).parse(reader);
/* 123 */       return ArgumentParseResult.success(result);
/* 124 */     } catch (CommandSyntaxException ex) {
/* 125 */       return ArgumentParseResult.failure((Throwable)ex);
/*     */     } 
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
/*     */   public final CompletableFuture<Iterable<Suggestion>> suggestionsFuture(CommandContext<C> commandContext, CommandInput input) {
/* 146 */     CommandContext<Object> reverseMappedContext = new CommandContext(commandContext.getOrDefault("_cloud_brigadier_native_sender", commandContext.sender()), input.input(), Collections.emptyMap(), null, null, Collections.emptyList(), StringRange.at(input.cursor()), null, null, false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     return ((ArgumentType)this.nativeType.get()).listSuggestions(reverseMappedContext, new SuggestionsBuilder(input
/*     */           
/* 154 */           .input(), input.cursor()))
/* 155 */       .thenApply(suggestions -> {
/*     */           List<Suggestion> cloud = new ArrayList<>();
/*     */           for (Suggestion suggestion : suggestions.getList()) {
/*     */             String beforeSuggestion = input.input().substring(input.cursor(), suggestion.getRange().getStart());
/*     */             String afterSuggestion = input.input().substring(suggestion.getRange().getEnd());
/*     */             if (beforeSuggestion.isEmpty() && afterSuggestion.isEmpty()) {
/*     */               cloud.add(TooltipSuggestion.suggestion(suggestion.getText(), suggestion.getTooltip()));
/*     */               continue;
/*     */             } 
/*     */             cloud.add(TooltipSuggestion.suggestion(beforeSuggestion + suggestion.getText() + afterSuggestion, suggestion.getTooltip()));
/*     */           } 
/*     */           return cloud;
/*     */         });
/*     */   }
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "1.8.0")
/*     */   @FunctionalInterface
/*     */   public static interface ParseFunction<T> {
/*     */     T apply(ArgumentType<T> param1ArgumentType, StringReader param1StringReader) throws CommandSyntaxException;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\parser\WrappedBrigadierParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */