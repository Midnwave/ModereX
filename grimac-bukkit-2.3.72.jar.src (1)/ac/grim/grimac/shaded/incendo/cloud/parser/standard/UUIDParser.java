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
/*     */ import java.util.UUID;
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
/*     */ public final class UUIDParser<C>
/*     */   implements ArgumentParser<C, UUID>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, UUID> uuidParser() {
/*  51 */     return ParserDescriptor.of(new UUIDParser(), UUID.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> CommandComponent.Builder<C, UUID> uuidComponent() {
/*  62 */     return CommandComponent.builder().parser(uuidParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<UUID> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*  70 */     String input = commandInput.readString();
/*     */     
/*     */     try {
/*  73 */       UUID uuid = UUID.fromString(input);
/*  74 */       return ArgumentParseResult.success(uuid);
/*  75 */     } catch (IllegalArgumentException e) {
/*  76 */       return ArgumentParseResult.failure((Throwable)new UUIDParseException(input, commandContext));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final class UUIDParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final String input;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public UUIDParseException(String input, CommandContext<?> context) {
/*  96 */       super(UUIDParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 100 */             CaptionVariable.of("input", input)
/*     */           });
/* 102 */       this.input = input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 111 */       return this.input;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 116 */       if (this == o) {
/* 117 */         return true;
/*     */       }
/* 119 */       if (o == null || getClass() != o.getClass()) {
/* 120 */         return false;
/*     */       }
/* 122 */       UUIDParseException that = (UUIDParseException)o;
/* 123 */       return this.input.equals(that.input);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 128 */       return Objects.hash(new Object[] { this.input });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\UUIDParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */