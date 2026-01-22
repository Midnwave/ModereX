/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.QuotingOverride;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
/*    */ final class ClickTag
/*    */ {
/*    */   private static final String CLICK = "click";
/*    */   static final TagResolver RESOLVER;
/*    */   
/*    */   static {
/* 46 */     RESOLVER = SerializableResolver.claimingStyle("click", ClickTag::create, 
/*    */ 
/*    */         
/* 49 */         StyleClaim.claim("click", Style::clickEvent, (event, emitter) -> emitter.tag("click").argument((String)ClickEvent.Action.NAMES.key(event.action())).argument(event.value(), QuotingOverride.QUOTED)));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Tag create(ArgumentQueue args, Context ctx) throws ParsingException {
/* 60 */     String actionName = args.popOr(() -> "A click tag requires an action of one of " + ClickEvent.Action.NAMES.keys()).lowerValue();
/* 61 */     ClickEvent.Action action = (ClickEvent.Action)ClickEvent.Action.NAMES.value(actionName);
/* 62 */     if (action == null) {
/* 63 */       throw ctx.newException("Unknown click event action '" + actionName + "'", args);
/*    */     }
/*    */     
/* 66 */     String value = args.popOr("Click event actions require a value").value();
/* 67 */     return Tag.styling(new StyleBuilderApplicable[] { (StyleBuilderApplicable)ClickEvent.clickEvent(action, value) });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\ClickTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */