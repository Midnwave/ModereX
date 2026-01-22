/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class StringArrayParser<C>
/*     */   implements ArgumentParser<C, String[]>
/*     */ {
/*  46 */   private static final Pattern FLAG_PATTERN = Pattern.compile("(-[A-Za-z_\\-0-9])|(--[A-Za-z_\\-0-9]*)");
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean flagYielding;
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, String[]> stringArrayParser() {
/*  56 */     return ParserDescriptor.of(new StringArrayParser(), String[].class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, String[]> flagYieldingStringArrayParser() {
/*  67 */     return ParserDescriptor.of(new StringArrayParser(true), String[].class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> CommandComponent.Builder<C, String[]> characterComponent() {
/*  78 */     return CommandComponent.builder().parser(stringArrayParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringArrayParser() {
/*  87 */     this.flagYielding = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public StringArrayParser(boolean flagYielding) {
/*  97 */     this.flagYielding = flagYielding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<String[]> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 105 */     int size = commandInput.remainingTokens();
/*     */     
/* 107 */     if (this.flagYielding) {
/* 108 */       List<String> list = new LinkedList<>();
/*     */       
/* 110 */       for (int j = 0; j < size; j++) {
/* 111 */         String string = commandInput.peekString();
/* 112 */         if (string.isEmpty() || FLAG_PATTERN.matcher(string).matches()) {
/*     */           break;
/*     */         }
/* 115 */         list.add(commandInput.readString());
/*     */       } 
/*     */       
/* 118 */       return ArgumentParseResult.success(list.<String>toArray(new String[0]));
/*     */     } 
/* 120 */     String[] result = new String[size];
/* 121 */     for (int i = 0; i < result.length; i++) {
/* 122 */       result[i] = commandInput.readString();
/*     */     }
/* 124 */     return ArgumentParseResult.success(result);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\StringArrayParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */