/*     */ package ac.grim.grimac.shaded.kyori.adventure.resource;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.net.URI;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ForkJoinPool;
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
/*     */ 
/*     */ public interface ResourcePackInfo
/*     */   extends Examinable, ResourcePackInfoLike
/*     */ {
/*     */   @NotNull
/*     */   static ResourcePackInfo resourcePackInfo(@NotNull UUID id, @NotNull URI uri, @NotNull String hash) {
/*  55 */     return new ResourcePackInfoImpl(id, uri, hash);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Builder resourcePackInfo() {
/*  65 */     return new ResourcePackInfoImpl.BuilderImpl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   UUID id();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   URI uri();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   String hash();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default ResourcePackInfo asResourcePackInfo() {
/*  94 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Builder
/*     */     extends AbstractBuilder<ResourcePackInfo>, ResourcePackInfoLike
/*     */   {
/*     */     @NotNull
/*     */     default CompletableFuture<ResourcePackInfo> computeHashAndBuild() {
/* 155 */       return computeHashAndBuild(ForkJoinPool.commonPool());
/*     */     }
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
/*     */     @NotNull
/*     */     default ResourcePackInfo asResourcePackInfo() {
/* 171 */       return build();
/*     */     }
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder id(@NotNull UUID param1UUID);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder uri(@NotNull URI param1URI);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder hash(@NotNull String param1String);
/*     */     
/*     */     @NotNull
/*     */     ResourcePackInfo build();
/*     */     
/*     */     @NotNull
/*     */     CompletableFuture<ResourcePackInfo> computeHashAndBuild(@NotNull Executor param1Executor);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\resource\ResourcePackInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */