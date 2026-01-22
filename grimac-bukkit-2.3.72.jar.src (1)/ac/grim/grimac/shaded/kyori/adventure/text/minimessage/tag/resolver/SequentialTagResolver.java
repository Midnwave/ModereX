/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.ClaimConsumer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import java.util.Arrays;
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
/*     */ final class SequentialTagResolver
/*     */   implements TagResolver, SerializableResolver
/*     */ {
/*     */   final TagResolver[] resolvers;
/*     */   
/*     */   SequentialTagResolver(@NotNull TagResolver[] resolvers) {
/*  40 */     this.resolvers = resolvers;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
/*  45 */     ParsingException thrown = null;
/*  46 */     for (TagResolver resolver : this.resolvers) {
/*     */       try {
/*  48 */         Tag placeholder = resolver.resolve(name, arguments, ctx);
/*     */         
/*  50 */         if (placeholder != null) return placeholder; 
/*  51 */       } catch (ParsingException ex) {
/*  52 */         arguments.reset();
/*  53 */         if (thrown == null) {
/*  54 */           thrown = ex;
/*     */         } else {
/*  56 */           thrown.addSuppressed((Throwable)ex);
/*     */         } 
/*  58 */       } catch (Exception ex) {
/*  59 */         arguments.reset();
/*  60 */         ParsingException err = ctx.newException("Exception thrown while parsing <" + name + ">", ex, arguments);
/*  61 */         if (thrown == null) {
/*  62 */           thrown = err;
/*     */         } else {
/*  64 */           thrown.addSuppressed((Throwable)err);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  69 */     if (thrown != null) {
/*  70 */       throw thrown;
/*     */     }
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean has(@NotNull String name) {
/*  77 */     for (TagResolver resolver : this.resolvers) {
/*  78 */       if (resolver.has(name)) {
/*  79 */         return true;
/*     */       }
/*     */     } 
/*  82 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handle(@NotNull Component serializable, @NotNull ClaimConsumer consumer) {
/*  87 */     for (TagResolver resolver : this.resolvers) {
/*  88 */       if (resolver instanceof SerializableResolver) {
/*  89 */         ((SerializableResolver)resolver).handle(serializable, consumer);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/*  96 */     if (other == this) {
/*  97 */       return true;
/*     */     }
/*  99 */     if (!(other instanceof SequentialTagResolver)) {
/* 100 */       return false;
/*     */     }
/* 102 */     SequentialTagResolver that = (SequentialTagResolver)other;
/* 103 */     return Arrays.equals((Object[])this.resolvers, (Object[])that.resolvers);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 108 */     return Arrays.hashCode((Object[])this.resolvers);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\resolver\SequentialTagResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */