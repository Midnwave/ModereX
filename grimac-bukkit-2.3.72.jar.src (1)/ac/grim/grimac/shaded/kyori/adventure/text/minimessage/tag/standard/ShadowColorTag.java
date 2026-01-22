/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.ShadowColor;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleGetter;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.RGBLike;
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
/*    */ final class ShadowColorTag
/*    */ {
/*    */   private static final String SHADOW_COLOR = "shadow";
/*    */   private static final String SHADOW_NONE = "!shadow";
/*    */   private static final float DEFAULT_ALPHA = 0.25F;
/* 46 */   static final TagResolver RESOLVER = TagResolver.resolver(new TagResolver[] {
/* 47 */         SerializableResolver.claimingStyle("shadow", ShadowColorTag::create, 
/*    */ 
/*    */           
/* 50 */           StyleClaim.claim("shadow", StyleGetter::shadowColor, ShadowColorTag::emit)), 
/*    */         
/* 52 */         (TagResolver)TagResolver.resolver("!shadow", Tag.styling(new StyleBuilderApplicable[] { (StyleBuilderApplicable)ShadowColor.none() })) });
/*    */   
/*    */   static Tag create(@NotNull ArgumentQueue args, @NotNull Context ctx) throws ParsingException {
/*    */     ShadowColor color;
/* 56 */     String colorString = args.popOr("Expected to find a color parameter: #RRGGBBAA").lowerValue();
/*    */     
/* 58 */     if (colorString.startsWith("#") && colorString.length() == 9) {
/* 59 */       color = ShadowColor.fromHexString(colorString);
/* 60 */       if (color == null) {
/* 61 */         throw ctx.newException(String.format("Unable to parse a shadow color from '%s'. Please use #RRGGBBAA formatting.", new Object[] { colorString }));
/*    */       }
/*    */     } else {
/* 64 */       TextColor text = ColorTagResolver.resolveColor(colorString, ctx);
/* 65 */       float alpha = args.hasNext() ? (float)args.pop().asDouble().<Throwable>orElseThrow(() -> ctx.newException("Number was expected to be a double")) : 0.25F;
/* 66 */       color = ShadowColor.shadowColor((RGBLike)text, (int)(alpha * 255.0F));
/*    */     } 
/*    */     
/* 69 */     return Tag.styling(new StyleBuilderApplicable[] { (StyleBuilderApplicable)color });
/*    */   }
/*    */   
/*    */   static void emit(@NotNull ShadowColor color, @NotNull TokenEmitter emitter) {
/* 73 */     if (ShadowColor.none().equals(color)) {
/* 74 */       emitter.tag("!shadow");
/*    */       
/*    */       return;
/*    */     } 
/* 78 */     emitter.tag("shadow");
/*    */     
/* 80 */     NamedTextColor possibleMatch = NamedTextColor.namedColor(TextColor.color((RGBLike)color).value());
/* 81 */     if (possibleMatch != null) {
/* 82 */       emitter.argument((String)NamedTextColor.NAMES.key(possibleMatch)).argument(Float.toString(color.alpha() / 255.0F));
/*    */     } else {
/* 84 */       emitter.argument(color.asHexString());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\ShadowColorTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */