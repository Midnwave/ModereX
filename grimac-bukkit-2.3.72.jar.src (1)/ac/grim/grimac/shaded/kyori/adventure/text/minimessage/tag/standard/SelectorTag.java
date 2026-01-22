/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.SelectorComponent;
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
/*    */ final class SelectorTag
/*    */ {
/*    */   private static final String SEL = "sel";
/*    */   private static final String SELECTOR = "selector";
/* 47 */   static final TagResolver RESOLVER = SerializableResolver.claimingComponent(
/* 48 */       StandardTags.names(new String[] { "sel", "selector" }, ), SelectorTag::create, SelectorTag::claim);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Tag create(ArgumentQueue args, Context ctx) throws ParsingException {
/*    */     Component component;
/* 57 */     String key = args.popOr("A selection key is required").value();
/* 58 */     ComponentLike separator = null;
/* 59 */     if (args.hasNext()) {
/* 60 */       component = ctx.deserialize(args.pop().value());
/*    */     }
/*    */     
/* 63 */     return Tag.inserting((Component)Component.selector(key, (ComponentLike)component));
/*    */   }
/*    */   @Nullable
/*    */   static Emitable claim(Component input) {
/* 67 */     if (!(input instanceof SelectorComponent)) return null;
/*    */     
/* 69 */     SelectorComponent st = (SelectorComponent)input;
/* 70 */     return emit -> {
/*    */         emit.tag("sel");
/*    */         emit.argument(st.pattern());
/*    */         if (st.separator() != null)
/*    */           emit.argument(st.separator()); 
/*    */       };
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\SelectorTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */