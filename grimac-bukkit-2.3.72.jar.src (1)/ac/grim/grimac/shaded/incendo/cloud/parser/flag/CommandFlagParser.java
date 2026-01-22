/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.flag;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.Caption;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.StandardCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.ParserException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.CompletionStage;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ public final class CommandFlagParser<C>
/*     */   implements ArgumentParser.FutureArgumentParser<C, Object>, SuggestionProvider<C>
/*     */ {
/*  62 */   public static final Object FLAG_PARSE_RESULT_OBJECT = new Object();
/*     */ 
/*     */ 
/*     */   
/*  66 */   public static final CloudKey<String> FLAG_META_KEY = CloudKey.of("__last_flag__", TypeToken.get(String.class));
/*     */ 
/*     */ 
/*     */   
/*  70 */   public static final CloudKey<Integer> FLAG_CURSOR_KEY = CloudKey.of("__flag_cursor__", TypeToken.get(Integer.class));
/*     */ 
/*     */ 
/*     */   
/*  74 */   public static final CloudKey<Set<CommandFlag<?>>> PARSED_FLAGS = CloudKey.of("__parsed_flags__", new TypeToken<Set<CommandFlag<?>>>() {
/*     */       
/*     */       });
/*  77 */   private static final Pattern FLAG_PRIMARY_PATTERN = Pattern.compile(" --(?<name>([A-Za-z]+))");
/*  78 */   private static final Pattern FLAG_ALIAS_PATTERN = Pattern.compile(" -(?<name>([A-Za-z]+))");
/*     */ 
/*     */ 
/*     */   
/*     */   private final Collection<CommandFlag<?>> flags;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandFlagParser(Collection<CommandFlag<?>> flags) {
/*  88 */     this.flags = flags;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public Collection<CommandFlag<?>> flags() {
/*  98 */     return Collections.unmodifiableCollection(this.flags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<ArgumentParseResult<Object>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 106 */     return (new FlagParser()).parse(commandContext, commandInput);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public CompletableFuture<Optional<String>> parseCurrentFlag(CommandContext<C> commandContext, CommandInput commandInput, Executor completionExecutor) {
/* 128 */     if (commandInput.isEmpty()) {
/* 129 */       return CompletableFuture.completedFuture(Optional.empty());
/*     */     }
/*     */     
/* 132 */     String lastInputValue = commandInput.lastRemainingToken();
/*     */ 
/*     */     
/* 135 */     FlagParser parser = new FlagParser();
/* 136 */     CompletableFuture<ArgumentParseResult<Object>> result = parser.parse(commandContext, commandInput);
/*     */     
/* 138 */     return result.thenApplyAsync(parseResult -> { if (commandContext.contains(FLAG_CURSOR_KEY)) { commandInput.cursor(((Integer)commandContext.get(FLAG_CURSOR_KEY)).intValue()); } else if (parser.lastParsedFlag() == null && commandInput.isEmpty()) { int count = lastInputValue.length(); commandInput.moveCursor(-count); }  return Optional.ofNullable(parser.lastParsedFlag()); }completionExecutor);
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
/*     */   public CompletableFuture<Iterable<Suggestion>> suggestionsFuture(CommandContext<C> commandContext, CommandInput input) {
/* 156 */     String lastArg = Objects.<String>requireNonNull((String)commandContext.getOrDefault(FLAG_META_KEY, ""));
/* 157 */     if (!lastArg.startsWith("-")) {
/* 158 */       String str1, readInput = input.readInput();
/*     */       
/* 160 */       List<CommandFlag<?>> usedFlags = new LinkedList<>();
/*     */       
/* 162 */       Matcher primaryMatcher = FLAG_PRIMARY_PATTERN.matcher(readInput);
/* 163 */       while (primaryMatcher.find()) {
/* 164 */         String name = primaryMatcher.group("name");
/* 165 */         for (CommandFlag<?> flag : this.flags) {
/* 166 */           if (flag.name().equalsIgnoreCase(name)) {
/* 167 */             usedFlags.add(flag);
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 173 */       Matcher aliasMatcher = FLAG_ALIAS_PATTERN.matcher(readInput);
/* 174 */       while (aliasMatcher.find()) {
/* 175 */         String name = aliasMatcher.group("name");
/* 176 */         for (CommandFlag<?> flag : this.flags) {
/* 177 */           for (String alias : flag.aliases()) {
/*     */             
/* 179 */             if (name.contains(alias)) {
/* 180 */               usedFlags.add(flag);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 186 */       String nextToken = input.peekString();
/*     */       
/* 188 */       if (nextToken.length() > 1) {
/* 189 */         str1 = nextToken.substring(1);
/*     */       } else {
/* 191 */         str1 = "";
/*     */       } 
/*     */       
/* 194 */       List<Suggestion> suggestions = new LinkedList<>();
/*     */       
/* 196 */       for (CommandFlag<?> flag : this.flags) {
/* 197 */         if (usedFlags.contains(flag) && flag.mode() != CommandFlag.FlagMode.REPEATABLE) {
/*     */           continue;
/*     */         }
/* 200 */         if (!commandContext.hasPermission(flag.permission())) {
/*     */           continue;
/*     */         }
/*     */         
/* 204 */         suggestions.add(Suggestion.suggestion(String.format("--%s", new Object[] { flag.name() })));
/*     */       } 
/*     */       
/* 207 */       boolean suggestCombined = (nextToken.length() > 1 && nextToken.startsWith("-") && !nextToken.startsWith("--"));
/* 208 */       for (CommandFlag<?> flag : this.flags) {
/* 209 */         if (usedFlags.contains(flag) && flag.mode() != CommandFlag.FlagMode.REPEATABLE) {
/*     */           continue;
/*     */         }
/* 212 */         if (!commandContext.hasPermission(flag.permission())) {
/*     */           continue;
/*     */         }
/*     */         
/* 216 */         for (String alias : flag.aliases()) {
/* 217 */           if (alias.equalsIgnoreCase(str1)) {
/*     */             continue;
/*     */           }
/* 220 */           if (suggestCombined && flag.commandComponent() == null) {
/* 221 */             suggestions.add(Suggestion.suggestion(String.format("%s%s", new Object[] { input.peekString(), alias }))); continue;
/*     */           } 
/* 223 */           suggestions.add(Suggestion.suggestion(String.format("-%s", new Object[] { alias })));
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 228 */       if (suggestCombined) {
/* 229 */         suggestions.add(Suggestion.suggestion(input.peekString()));
/*     */       }
/* 231 */       return CompletableFuture.completedFuture(suggestions);
/*     */     } 
/* 233 */     CommandFlag<?> currentFlag = null;
/* 234 */     if (lastArg.startsWith("--")) {
/* 235 */       String flagName = lastArg.substring(2);
/* 236 */       for (CommandFlag<?> flag : this.flags) {
/* 237 */         if (flagName.equalsIgnoreCase(flag.name())) {
/* 238 */           currentFlag = flag;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } else {
/* 243 */       String flagName = lastArg.substring(1);
/*     */       
/* 245 */       for (CommandFlag<?> flag : this.flags) {
/* 246 */         for (String alias : flag.aliases()) {
/* 247 */           if (alias.equalsIgnoreCase(flagName)) {
/* 248 */             currentFlag = flag;
/*     */             // Byte code: goto -> 855
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 254 */     if (currentFlag != null && commandContext
/* 255 */       .hasPermission(currentFlag.permission()) && currentFlag
/* 256 */       .commandComponent() != null) {
/* 257 */       SuggestionProvider suggestionProvider = currentFlag.commandComponent().suggestionProvider();
/* 258 */       return suggestionProvider.suggestionsFuture(commandContext, input);
/*     */     } 
/*     */     
/* 261 */     commandContext.store(FLAG_META_KEY, "");
/* 262 */     return suggestionsFuture(commandContext, input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public enum FailureReason
/*     */   {
/* 272 */     UNKNOWN_FLAG((String)StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_UNKNOWN_FLAG),
/* 273 */     DUPLICATE_FLAG((String)StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_DUPLICATE_FLAG),
/* 274 */     NO_FLAG_STARTED((String)StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_NO_FLAG_STARTED),
/* 275 */     MISSING_ARGUMENT((String)StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_MISSING_ARGUMENT),
/* 276 */     NO_PERMISSION((String)StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_NO_PERMISSION);
/*     */     
/*     */     private final Caption caption;
/*     */     
/*     */     FailureReason(Caption caption) {
/* 281 */       this.caption = caption;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Caption caption() {
/* 290 */       return this.caption;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final class FlagParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final String input;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final CommandFlagParser.FailureReason failureReason;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public FlagParseException(String input, CommandFlagParser.FailureReason failureReason, CommandContext<?> context) {
/* 316 */       super(CommandFlagParser.class, context, failureReason
/*     */ 
/*     */           
/* 319 */           .caption(), new CaptionVariable[] {
/* 320 */             CaptionVariable.of("input", input), 
/* 321 */             CaptionVariable.of("flag", input)
/*     */           });
/* 323 */       this.input = input;
/* 324 */       this.failureReason = failureReason;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 333 */       return this.input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     public CommandFlagParser.FailureReason failureReason() {
/* 343 */       return this.failureReason;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final class FlagParser
/*     */   {
/*     */     private String lastParsedFlag;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private FlagParser() {}
/*     */ 
/*     */ 
/*     */     
/*     */     private CompletableFuture<ArgumentParseResult<Object>> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 362 */       CompletableFuture<ArgumentParseResult<Object>> result = CompletableFuture.completedFuture(null);
/* 363 */       Set<CommandFlag<?>> parsedFlags = (Set<CommandFlag<?>>)commandContext.computeIfAbsent(CommandFlagParser.PARSED_FLAGS, k -> new HashSet());
/*     */       
/* 365 */       int remainingTokens = commandInput.remainingTokens();
/* 366 */       for (int i = 0; i <= remainingTokens; i++) {
/* 367 */         result = result.thenCompose(parseResult -> {
/*     */               commandInput.skipWhitespace();
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               if (parseResult != null || commandInput.isEmpty()) {
/*     */                 return CompletableFuture.completedFuture(parseResult);
/*     */               }
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               String string = commandInput.peekString();
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               if (!string.startsWith("-")) {
/*     */                 return CompletableFuture.completedFuture(ArgumentParseResult.success(CommandFlagParser.FLAG_PARSE_RESULT_OBJECT));
/*     */               }
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               this.lastParsedFlag = null;
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               if (string.startsWith("--")) {
/*     */                 commandInput.moveCursor(2);
/*     */               } else {
/*     */                 commandInput.moveCursor(1);
/*     */               } 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               String flagName = commandInput.readStringSkipWhitespace();
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               CommandFlag<?> flag = null;
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               if (string.startsWith("--")) {
/*     */                 for (CommandFlag<?> flagCandidate : (Iterable<CommandFlag<?>>)CommandFlagParser.this.flags) {
/*     */                   if (flagName.equalsIgnoreCase(flagCandidate.name())) {
/*     */                     flag = flagCandidate;
/*     */ 
/*     */ 
/*     */                     
/*     */                     break;
/*     */                   } 
/*     */                 } 
/*     */               } else if (flagName.length() == 1) {
/*     */                 for (CommandFlag<?> flagCandidate : (Iterable<CommandFlag<?>>)CommandFlagParser.this.flags) {
/*     */                   for (String alias : flagCandidate.aliases()) {
/*     */                     if (alias.equalsIgnoreCase(flagName)) {
/*     */                       flag = flagCandidate;
/*     */ 
/*     */ 
/*     */                       
/*     */                       // Byte code: goto -> 502
/*     */                     } 
/*     */                   } 
/*     */                 } 
/*     */               } else {
/*     */                 boolean flagFound = false;
/*     */ 
/*     */ 
/*     */                 
/*     */                 for (int j = 0; j < flagName.length(); j++) {
/*     */                   String parsedFlag = Character.toString(flagName.charAt(j)).toLowerCase(Locale.ENGLISH);
/*     */ 
/*     */ 
/*     */                   
/*     */                   for (CommandFlag<?> candidateFlag : (Iterable<CommandFlag<?>>)CommandFlagParser.this.flags) {
/*     */                     if (candidateFlag.commandComponent() != null) {
/*     */                       continue;
/*     */                     }
/*     */ 
/*     */ 
/*     */                     
/*     */                     if (!candidateFlag.aliases().contains(parsedFlag)) {
/*     */                       continue;
/*     */                     }
/*     */ 
/*     */ 
/*     */                     
/*     */                     if (parsedFlags.contains(candidateFlag) && candidateFlag.mode() != CommandFlag.FlagMode.REPEATABLE) {
/*     */                       return fail((Throwable)new CommandFlagParser.FlagParseException(string, CommandFlagParser.FailureReason.DUPLICATE_FLAG, commandContext));
/*     */                     }
/*     */ 
/*     */ 
/*     */                     
/*     */                     if (!commandContext.hasPermission(candidateFlag.permission())) {
/*     */                       return fail((Throwable)new CommandFlagParser.FlagParseException(string, CommandFlagParser.FailureReason.NO_PERMISSION, commandContext));
/*     */                     }
/*     */ 
/*     */ 
/*     */                     
/*     */                     commandContext.flags().addPresenceFlag(candidateFlag);
/*     */ 
/*     */ 
/*     */                     
/*     */                     parsedFlags.add(candidateFlag);
/*     */ 
/*     */ 
/*     */                     
/*     */                     flagFound = true;
/*     */                   } 
/*     */                 } 
/*     */ 
/*     */ 
/*     */                 
/*     */                 return !flagFound ? fail((Throwable)new CommandFlagParser.FlagParseException(string, CommandFlagParser.FailureReason.NO_FLAG_STARTED, commandContext)) : CompletableFuture.completedFuture(null);
/*     */               } 
/*     */ 
/*     */ 
/*     */               
/*     */               if (flag == null) {
/*     */                 return fail((Throwable)new CommandFlagParser.FlagParseException(string, CommandFlagParser.FailureReason.UNKNOWN_FLAG, commandContext));
/*     */               }
/*     */ 
/*     */ 
/*     */               
/*     */               if (parsedFlags.contains(flag) && flag.mode() != CommandFlag.FlagMode.REPEATABLE) {
/*     */                 return fail((Throwable)new CommandFlagParser.FlagParseException(string, CommandFlagParser.FailureReason.DUPLICATE_FLAG, commandContext));
/*     */               }
/*     */ 
/*     */ 
/*     */               
/*     */               if (!commandContext.hasPermission(flag.permission())) {
/*     */                 return fail((Throwable)new CommandFlagParser.FlagParseException(string, CommandFlagParser.FailureReason.NO_PERMISSION, commandContext));
/*     */               }
/*     */ 
/*     */ 
/*     */               
/*     */               if (flag.commandComponent() == null) {
/*     */                 commandContext.remove(CommandFlagParser.FLAG_CURSOR_KEY);
/*     */ 
/*     */ 
/*     */                 
/*     */                 commandContext.flags().addPresenceFlag(flag);
/*     */ 
/*     */ 
/*     */                 
/*     */                 parsedFlags.add(flag);
/*     */ 
/*     */ 
/*     */                 
/*     */                 return CompletableFuture.completedFuture(null);
/*     */               } 
/*     */ 
/*     */ 
/*     */               
/*     */               if (commandInput.hasRemainingInput() && commandInput.peek() == ' ') {
/*     */                 this.lastParsedFlag = string;
/*     */               }
/*     */ 
/*     */ 
/*     */               
/*     */               if (commandInput.isEmpty(true)) {
/*     */                 return fail((Throwable)new CommandFlagParser.FlagParseException(flag.name(), CommandFlagParser.FailureReason.MISSING_ARGUMENT, commandContext));
/*     */               }
/*     */ 
/*     */ 
/*     */               
/*     */               this.lastParsedFlag = string;
/*     */ 
/*     */ 
/*     */               
/*     */               CommandFlag<?> parsingFlag = flag;
/*     */ 
/*     */ 
/*     */               
/*     */               CommandInput commandInputCopy = commandInput.copy();
/*     */ 
/*     */ 
/*     */               
/*     */               return flag.commandComponent().parser().parseFuture(commandContext, commandInput).thenApply(());
/*     */             });
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 558 */       return result.thenApply(r -> (r == null) ? ArgumentParseResult.success(CommandFlagParser.FLAG_PARSE_RESULT_OBJECT) : r);
/*     */     }
/*     */     
/*     */     private String lastParsedFlag() {
/* 562 */       return this.lastParsedFlag;
/*     */     }
/*     */     
/*     */     private CompletableFuture<ArgumentParseResult<Object>> fail(Throwable exception) {
/* 566 */       return ArgumentParseResult.failureFuture(exception);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\flag\CommandFlagParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */