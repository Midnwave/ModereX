/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointered;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tree.Node;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.ComponentSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.PlatformAPI;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.UnaryOperator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface MiniMessage
/*     */   extends ComponentSerializer<Component, Component, String>
/*     */ {
/*     */   @NotNull
/*     */   static MiniMessage miniMessage() {
/*  55 */     return MiniMessageImpl.Instances.INSTANCE;
/*     */   }
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
/*     */   @NotNull
/*     */   String escapeTags(@NotNull String paramString);
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
/*     */   @NotNull
/*     */   String escapeTags(@NotNull String paramString, @NotNull TagResolver paramTagResolver);
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
/*     */   @NotNull
/*     */   String escapeTags(@NotNull String input, @NotNull TagResolver... tagResolvers) {
/*  94 */     return escapeTags(input, TagResolver.resolver(tagResolvers));
/*     */   }
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
/*     */   @NotNull
/*     */   String stripTags(@NotNull String paramString);
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
/*     */   @NotNull
/*     */   String stripTags(@NotNull String paramString, @NotNull TagResolver paramTagResolver);
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
/*     */   @NotNull
/*     */   String stripTags(@NotNull String input, @NotNull TagResolver... tagResolvers) {
/* 133 */     return stripTags(input, TagResolver.resolver(tagResolvers));
/*     */   }
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
/*     */   @NotNull
/*     */   Component deserialize(@NotNull String paramString, @NotNull Pointered paramPointered);
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
/*     */   @NotNull
/*     */   Component deserialize(@NotNull String paramString, @NotNull TagResolver paramTagResolver);
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
/*     */   @NotNull
/*     */   Component deserialize(@NotNull String paramString, @NotNull Pointered paramPointered, @NotNull TagResolver paramTagResolver);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Component deserialize(@NotNull String input, @NotNull TagResolver... tagResolvers) {
/* 182 */     return deserialize(input, TagResolver.resolver(tagResolvers));
/*     */   }
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
/*     */   @NotNull
/*     */   Component deserialize(@NotNull String input, @NotNull Pointered target, @NotNull TagResolver... tagResolvers) {
/* 197 */     return deserialize(input, target, TagResolver.resolver(tagResolvers));
/*     */   }
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
/*     */   Node.Root deserializeToTree(@NotNull String paramString);
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
/*     */   Node.Root deserializeToTree(@NotNull String paramString, @NotNull Pointered paramPointered);
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
/*     */   Node.Root deserializeToTree(@NotNull String paramString, @NotNull TagResolver paramTagResolver);
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
/*     */   Node.Root deserializeToTree(@NotNull String paramString, @NotNull Pointered paramPointered, @NotNull TagResolver paramTagResolver);
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
/*     */   Node.Root deserializeToTree(@NotNull String input, @NotNull TagResolver... tagResolvers) {
/* 260 */     return deserializeToTree(input, TagResolver.resolver(tagResolvers));
/*     */   }
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
/*     */   Node.Root deserializeToTree(@NotNull String input, @NotNull Pointered target, @NotNull TagResolver... tagResolvers) {
/* 276 */     return deserializeToTree(input, target, TagResolver.resolver(tagResolvers));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean strict();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   TagResolver tags();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Builder builder() {
/* 303 */     return new MiniMessageImpl.BuilderImpl();
/*     */   }
/*     */   
/*     */   @PlatformAPI
/*     */   @Internal
/*     */   public static interface Provider {
/*     */     @PlatformAPI
/*     */     @Internal
/*     */     @NotNull
/*     */     MiniMessage miniMessage();
/*     */     
/*     */     @PlatformAPI
/*     */     @Internal
/*     */     @NotNull
/*     */     Consumer<MiniMessage.Builder> builder();
/*     */   }
/*     */   
/*     */   public static interface Builder extends AbstractBuilder<MiniMessage> {
/*     */     @NotNull
/*     */     Builder tags(@NotNull TagResolver param1TagResolver);
/*     */     
/*     */     @NotNull
/*     */     Builder editTags(@NotNull Consumer<TagResolver.Builder> param1Consumer);
/*     */     
/*     */     @NotNull
/*     */     Builder strict(boolean param1Boolean);
/*     */     
/*     */     @NotNull
/*     */     Builder emitVirtuals(boolean param1Boolean);
/*     */     
/*     */     @NotNull
/*     */     Builder debug(@Nullable Consumer<String> param1Consumer);
/*     */     
/*     */     @NotNull
/*     */     Builder postProcessor(@NotNull UnaryOperator<Component> param1UnaryOperator);
/*     */     
/*     */     @NotNull
/*     */     Builder preProcessor(@NotNull UnaryOperator<String> param1UnaryOperator);
/*     */     
/*     */     @NotNull
/*     */     MiniMessage build();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\MiniMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */