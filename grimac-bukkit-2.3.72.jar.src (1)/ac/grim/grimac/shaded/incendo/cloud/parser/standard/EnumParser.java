/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.StandardCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.ParserException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import java.util.stream.Collectors;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class EnumParser<C, E extends Enum<E>>
/*     */   implements ArgumentParser<C, E>, BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   private final Class<E> enumClass;
/*     */   private final EnumSet<E> acceptedValues;
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C, E extends Enum<E>> ParserDescriptor<C, E> enumParser(Class<E> enumClass) {
/*  59 */     return ParserDescriptor.of(new EnumParser<>(enumClass), enumClass);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C, E extends Enum<E>> CommandComponent.Builder<C, E> enumComponent(Class<E> enumClass) {
/*  74 */     return CommandComponent.builder().parser(enumParser(enumClass));
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
/*     */   public EnumParser(Class<E> enumClass) {
/*  86 */     this.enumClass = enumClass;
/*  87 */     this.acceptedValues = EnumSet.allOf(enumClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<E> enumClass() {
/*  96 */     return this.enumClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<E> acceptedValues() {
/* 105 */     return Collections.unmodifiableSet(this.acceptedValues);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<E> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 113 */     String input = commandInput.readString();
/*     */     
/* 115 */     for (Enum enum_ : this.acceptedValues) {
/* 116 */       if (enum_.name().equalsIgnoreCase(input)) {
/* 117 */         return ArgumentParseResult.success(enum_);
/*     */       }
/*     */     } 
/*     */     
/* 121 */     return ArgumentParseResult.failure((Throwable)new EnumParseException(input, this.enumClass, commandContext));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 127 */     return (Iterable<String>)EnumSet.<E>allOf(this.enumClass).stream().map(e -> e.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final class EnumParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final String input;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final Class<? extends Enum<?>> enumClass;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public EnumParseException(String input, Class<? extends Enum<?>> enumClass, CommandContext<?> context) {
/* 149 */       super(EnumParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 153 */             CaptionVariable.of("input", input), 
/* 154 */             CaptionVariable.of("acceptableValues", join((Class)enumClass))
/*     */           });
/* 156 */       this.input = input;
/* 157 */       this.enumClass = enumClass;
/*     */     }
/*     */ 
/*     */     
/*     */     private static String join(Class<? extends Enum> clazz) {
/* 162 */       EnumSet<?> enumSet = EnumSet.allOf(clazz);
/* 163 */       return enumSet.stream()
/* 164 */         .map(e -> e.toString().toLowerCase(Locale.ROOT))
/* 165 */         .collect(Collectors.joining(", "));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 174 */       return this.input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Class<? extends Enum<?>> enumClass() {
/* 183 */       return this.enumClass;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 188 */       if (this == o) {
/* 189 */         return true;
/*     */       }
/* 191 */       if (o == null || getClass() != o.getClass()) {
/* 192 */         return false;
/*     */       }
/* 194 */       EnumParseException that = (EnumParseException)o;
/* 195 */       return (this.input.equals(that.input) && this.enumClass.equals(that.enumClass));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 200 */       return Objects.hash(new Object[] { this.input, this.enumClass });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\EnumParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */