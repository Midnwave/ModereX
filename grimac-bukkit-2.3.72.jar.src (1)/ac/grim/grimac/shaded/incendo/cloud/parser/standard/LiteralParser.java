/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
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
/*     */ public final class LiteralParser<C>
/*     */   implements ArgumentParser<C, String>, BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, String> literal(String name, String... aliases) {
/*  59 */     return ParserDescriptor.of(new LiteralParser(name, aliases), String.class);
/*     */   }
/*     */   
/*  62 */   private final Set<String> allAcceptedAliases = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
/*  63 */   private final Set<String> alternativeAliases = new HashSet<>();
/*     */   
/*     */   private final String name;
/*     */   
/*     */   private LiteralParser(String name, String... aliases) {
/*  68 */     validateNames(name, aliases);
/*  69 */     this.name = name;
/*  70 */     this.allAcceptedAliases.add(this.name);
/*  71 */     this.allAcceptedAliases.addAll(Arrays.asList(aliases));
/*  72 */     this.alternativeAliases.addAll(Arrays.asList(aliases));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<String> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*  80 */     String string = commandInput.peekString();
/*  81 */     if (this.allAcceptedAliases.contains(string)) {
/*  82 */       commandInput.readString();
/*  83 */       return ArgumentParseResult.success(this.name);
/*     */     } 
/*  85 */     return ArgumentParseResult.failure(new IllegalArgumentException(string));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/*  91 */     return Collections.singletonList(this.name);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public Collection<String> aliases() {
/* 104 */     return Collections.unmodifiableCollection(this.allAcceptedAliases);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public Collection<String> alternativeAliases() {
/* 117 */     return Collections.unmodifiableCollection(this.alternativeAliases);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void insertAlias(String alias) {
/* 126 */     validateNames("valid", new String[] { alias });
/* 127 */     this.allAcceptedAliases.add(alias);
/* 128 */     this.alternativeAliases.add(alias);
/*     */   }
/*     */   
/*     */   private static void validateNames(String name, String[] aliases) {
/* 132 */     List<String> errors = null;
/* 133 */     errors = validateName(name, false, errors);
/* 134 */     for (String alias : aliases) {
/* 135 */       errors = validateName(alias, true, errors);
/*     */     }
/* 137 */     if (errors != null && !errors.isEmpty()) {
/* 138 */       throw new IllegalArgumentException(String.join("\n", errors));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<String> validateName(String name, boolean alias, List<String> errors) {
/* 148 */     int found = name.codePoints().filter(Character::isWhitespace).findFirst().orElse(-2147483648);
/* 149 */     if (found != Integer.MIN_VALUE) {
/* 150 */       if (errors == null) {
/* 151 */         errors = new ArrayList<>();
/*     */       }
/* 153 */       errors.add(String.format("%s '%s' is invalid: contains whitespace", new Object[] { alias ? "Alias" : "Name", name }));
/*     */     } 
/* 155 */     return errors;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\LiteralParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */