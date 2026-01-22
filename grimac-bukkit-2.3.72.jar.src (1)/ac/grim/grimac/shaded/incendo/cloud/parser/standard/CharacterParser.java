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
/*     */ import java.util.Objects;
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
/*     */ public final class CharacterParser<C>
/*     */   implements ArgumentParser<C, Character>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, Character> characterParser() {
/*  50 */     return ParserDescriptor.of(new CharacterParser(), Character.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> CommandComponent.Builder<C, Character> characterComponent() {
/*  61 */     return CommandComponent.builder().parser(characterParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Character> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*  69 */     if (commandInput.peekString().length() != 1) {
/*  70 */       return ArgumentParseResult.failure((Throwable)new CharParseException(commandInput.peekString(), commandContext));
/*     */     }
/*     */     
/*  73 */     return ArgumentParseResult.success(Character.valueOf(commandInput.read()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final class CharParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final String input;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CharParseException(String input, CommandContext<?> context) {
/*  92 */       super(CharacterParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_CHAR, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/*  96 */             CaptionVariable.of("input", input)
/*     */           });
/*  98 */       this.input = input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 107 */       return this.input;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 112 */       if (this == o) {
/* 113 */         return true;
/*     */       }
/* 115 */       if (o == null || getClass() != o.getClass()) {
/* 116 */         return false;
/*     */       }
/* 118 */       CharParseException that = (CharParseException)o;
/* 119 */       return this.input.equals(that.input);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 124 */       return Objects.hash(new Object[] { this.input });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\CharacterParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */