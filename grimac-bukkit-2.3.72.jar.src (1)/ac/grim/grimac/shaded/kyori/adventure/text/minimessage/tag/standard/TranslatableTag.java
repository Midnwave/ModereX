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
/*    */ final class TranslatableTag
/*    */ {
/*    */   private static final String TR = "tr";
/*    */   private static final String TRANSLATE = "translate";
/*    */   private static final String LANG = "lang";
/* 51 */   static final TagResolver RESOLVER = SerializableResolver.claimingComponent(
/* 52 */       StandardTags.names(new String[] { "lang", "translate", "tr" }, ), TranslatableTag::create, TranslatableTag::claim);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Tag create(ArgumentQueue args, Context ctx) throws ParsingException {
/*    */     List<Component> with;
/* 61 */     String key = args.popOr("A translation key is required").value();
/*    */     
/* 63 */     if (args.hasNext()) {
/* 64 */       with = new ArrayList<>();
/* 65 */       while (args.hasNext()) {
/* 66 */         with.add(ctx.deserialize(args.pop().value()));
/*    */       }
/*    */     } else {
/* 69 */       with = Collections.emptyList();
/*    */     } 
/*    */     
/* 72 */     return Tag.inserting((Component)Component.translatable(key, with));
/*    */   }
/*    */   @Nullable
/*    */   static Emitable claim(Component input) {
/* 76 */     if (!(input instanceof TranslatableComponent) || ((TranslatableComponent)input).fallback() != null) return null;
/*    */     
/* 78 */     TranslatableComponent tr = (TranslatableComponent)input;
/* 79 */     return emit -> {
/*    */         emit.tag("lang");
/*    */         emit.argument(tr.key());
/*    */         for (TranslationArgument with : tr.arguments())
/*    */           emit.argument(with.asComponent()); 
/*    */       };
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\TranslatableTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */