/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.key.InvalidKeyException;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class FontTag
/*    */ {
/*    */   static final String FONT = "font";
/* 47 */   static final TagResolver RESOLVER = SerializableResolver.claimingStyle("font", FontTag::create, 
/*    */ 
/*    */       
/* 50 */       StyleClaim.claim("font", Style::font, FontTag::emit));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Tag create(ArgumentQueue args, Context ctx) throws ParsingException {
/*    */     Key font;
/* 58 */     String valueOrNamespace = args.popOr("A font tag must have either arguments of either <value> or <namespace:value>").value();
/*    */     try {
/* 60 */       if (!args.hasNext()) {
/* 61 */         font = Key.key(valueOrNamespace);
/*    */       } else {
/* 63 */         String fontKey = args.pop().value();
/* 64 */         font = Key.key(valueOrNamespace, fontKey);
/*    */       } 
/* 66 */     } catch (InvalidKeyException ex) {
/* 67 */       throw ctx.newException(ex.getMessage(), args);
/*    */     } 
/*    */     
/* 70 */     return Tag.styling(builder -> builder.font(font));
/*    */   }
/*    */   
/*    */   static void emit(Key font, TokenEmitter emitter) {
/* 74 */     emitter.tag("font");
/* 75 */     if (font.namespace().equals("minecraft")) {
/* 76 */       emitter.argument(font.value());
/*    */     } else {
/* 78 */       emitter.arguments(new String[] { font.namespace(), font.value() });
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\FontTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */