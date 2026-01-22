/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCaptionKeys;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.ParserException;
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
/*    */ @API(status = API.Status.STABLE, since = "2.0.0")
/*    */ public final class SelectorUnsupportedException
/*    */   extends ParserException
/*    */ {
/*    */   public SelectorUnsupportedException(CommandContext<?> context, Class<?> parser) {
/* 53 */     super(parser, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_SELECTOR_UNSUPPORTED, new ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable[0]);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\selector\SelectorUnsupportedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */