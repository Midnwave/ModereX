/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslationArgument;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.Emitable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ final class TranslatableFallbackTag
/*    */ {
/*    */   private static final String TR_OR = "tr_or";
/*    */   private static final String TRANSLATE_OR = "translate_or";
/*    */   private static final String LANG_OR = "lang_or";
/* 52 */   static final TagResolver RESOLVER = SerializableResolver.claimingComponent(
/* 53 */       StandardTags.names(new String[] { "lang_or", "translate_or", "tr_or" }, ), TranslatableFallbackTag::create, TranslatableFallbackTag::claim);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Tag create(ArgumentQueue args, Context ctx) throws ParsingException {
/*    */     List<Component> with;
/* 62 */     String key = args.popOr("A translation key is required").value();
/* 63 */     String fallback = args.popOr("A fallback messages is required").value();
/*    */     
/* 65 */     if (args.hasNext()) {
/* 66 */       with = new ArrayList<>();
/* 67 */       while (args.hasNext()) {
/* 68 */         with.add(ctx.deserialize(args.pop().value()));
/*    */       }
/*    */     } else {
/* 71 */       with = Collections.emptyList();
/*    */     } 
/*    */     
/* 74 */     return Tag.inserting((Component)Component.translatable(key, fallback, with, new ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable[0]));
/*    */   }
/*    */   @Nullable
/*    */   static Emitable claim(Component input) {
/* 78 */     if (!(input instanceof TranslatableComponent) || ((TranslatableComponent)input).fallback() == null) return null;
/*    */     
/* 80 */     TranslatableComponent tr = (TranslatableComponent)input;
/* 81 */     return emit -> {
/*    */         emit.tag("lang_or");
/*    */         emit.argument(tr.key());
/*    */         emit.argument(tr.fallback());
/*    */         for (TranslationArgument with : tr.arguments())
/*    */           emit.argument(with.asComponent()); 
/*    */       };
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\TranslatableFallbackTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */