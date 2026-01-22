/*     */ package ac.grim.grimac.shaded.kyori.adventure.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.properties.AdventureProperties;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Optional;
/*     */ import java.util.ServiceConfigurationError;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.Set;
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
/*     */ public final class Services
/*     */ {
/*  42 */   private static final boolean SERVICE_LOAD_FAILURES_ARE_FATAL = Boolean.TRUE.equals(AdventureProperties.SERVICE_LOAD_FAILURES_ARE_FATAL.value());
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
/*     */   public static <P> Optional<P> service(@NotNull Class<P> type) {
/*  56 */     ServiceLoader<P> loader = Services0.loader(type);
/*  57 */     Iterator<P> it = loader.iterator();
/*  58 */     while (it.hasNext()) {
/*     */       P instance;
/*     */       try {
/*  61 */         instance = it.next();
/*  62 */       } catch (Throwable t) {
/*  63 */         if (SERVICE_LOAD_FAILURES_ARE_FATAL) {
/*  64 */           throw new IllegalStateException("Encountered an exception loading service " + type, t);
/*     */         }
/*     */         
/*     */         continue;
/*     */       } 
/*  69 */       if (it.hasNext()) {
/*  70 */         throw new IllegalStateException("Expected to find one service " + type + ", found multiple");
/*     */       }
/*  72 */       return Optional.of(instance);
/*     */     } 
/*  74 */     return Optional.empty();
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
/*     */   @NotNull
/*     */   public static <P> Optional<P> serviceWithFallback(@NotNull Class<P> type) {
/* 100 */     ServiceLoader<P> loader = Services0.loader(type);
/* 101 */     Iterator<P> it = loader.iterator();
/* 102 */     P firstFallback = null;
/*     */     
/* 104 */     while (it.hasNext()) {
/*     */       P instance;
/*     */       
/*     */       try {
/* 108 */         instance = it.next();
/* 109 */       } catch (Throwable t) {
/* 110 */         if (SERVICE_LOAD_FAILURES_ARE_FATAL) {
/* 111 */           throw new IllegalStateException("Encountered an exception loading service " + type, t);
/*     */         }
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 117 */       if (instance instanceof Fallback) {
/* 118 */         if (firstFallback == null)
/* 119 */           firstFallback = instance; 
/*     */         continue;
/*     */       } 
/* 122 */       return Optional.of(instance);
/*     */     } 
/*     */ 
/*     */     
/* 126 */     return Optional.ofNullable(firstFallback);
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
/*     */   public static <P> Set<P> services(Class<? extends P> clazz) {
/* 138 */     ServiceLoader<? extends P> loader = Services0.loader(clazz);
/* 139 */     Set<P> providers = new HashSet<>();
/* 140 */     for (Iterator<? extends P> it = loader.iterator(); it.hasNext(); ) {
/*     */       P instance;
/*     */       try {
/* 143 */         instance = it.next();
/* 144 */       } catch (ServiceConfigurationError ex) {
/* 145 */         if (SERVICE_LOAD_FAILURES_ARE_FATAL) {
/* 146 */           throw new IllegalStateException("Encountered an exception loading a provider for " + clazz + ": ", ex);
/*     */         }
/*     */         
/*     */         continue;
/*     */       } 
/* 151 */       providers.add(instance);
/*     */     } 
/* 153 */     return Collections.unmodifiableSet(providers);
/*     */   }
/*     */   
/*     */   public static interface Fallback {}
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\Services.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */