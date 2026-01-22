/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.Emitable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
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
/*    */ final class NewlineTag
/*    */ {
/*    */   private static final String BR = "br";
/*    */   private static final String NEWLINE = "newline";
/* 45 */   static final TagResolver RESOLVER = SerializableResolver.claimingComponent(
/* 46 */       StandardTags.names(new String[] { "newline", "br" }, ), NewlineTag::create, NewlineTag::claimComponent);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Tag create(ArgumentQueue args, Context ctx) throws ParsingException {
/* 55 */     return Tag.selfClosingInserting((Component)Component.newline());
/*    */   }
/*    */   @Nullable
/*    */   static Emitable claimComponent(Component input) {
/* 59 */     if (Component.newline().equals(input)) {
/* 60 */       return emit -> emit.selfClosingTag("br");
/*    */     }
/* 62 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\NewlineTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */