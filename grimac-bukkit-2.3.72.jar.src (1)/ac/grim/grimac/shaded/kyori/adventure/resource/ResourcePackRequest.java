/*     */ package ac.grim.grimac.shaded.kyori.adventure.resource;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ 
/*     */ public interface ResourcePackRequest
/*     */   extends Examinable, ResourcePackRequestLike
/*     */ {
/*     */   @NotNull
/*     */   static ResourcePackRequest addingRequest(@NotNull ResourcePackInfoLike first, @NotNull ResourcePackInfoLike... others) {
/*  53 */     return (ResourcePackRequest)resourcePackRequest().packs(first, others).replace(false).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Builder resourcePackRequest() {
/*  63 */     return new ResourcePackRequestImpl.BuilderImpl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Builder resourcePackRequest(@NotNull ResourcePackRequest existing) {
/*  74 */     return new ResourcePackRequestImpl.BuilderImpl(Objects.<ResourcePackRequest>requireNonNull(existing, "existing"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   List<ResourcePackInfo> packs();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   ResourcePackRequest packs(@NotNull Iterable<? extends ResourcePackInfoLike> paramIterable);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   ResourcePackCallback callback();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   ResourcePackRequest callback(@NotNull ResourcePackCallback paramResourcePackCallback);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean replace();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   ResourcePackRequest replace(boolean paramBoolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean required();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   Component prompt();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default ResourcePackRequest asResourcePackRequest() {
/* 155 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Builder
/*     */     extends AbstractBuilder<ResourcePackRequest>, ResourcePackRequestLike
/*     */   {
/*     */     @Contract("_, _ -> this")
/*     */     @NotNull
/*     */     Builder packs(@NotNull ResourcePackInfoLike param1ResourcePackInfoLike, @NotNull ResourcePackInfoLike... param1VarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder packs(@NotNull Iterable<? extends ResourcePackInfoLike> param1Iterable);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder callback(@NotNull ResourcePackCallback param1ResourcePackCallback);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder replace(boolean param1Boolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder required(boolean param1Boolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder prompt(@Nullable Component param1Component);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     default ResourcePackRequest asResourcePackRequest() {
/* 232 */       return (ResourcePackRequest)build();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\resource\ResourcePackRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */