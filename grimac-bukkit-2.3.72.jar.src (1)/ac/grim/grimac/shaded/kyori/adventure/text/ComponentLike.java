/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Predicate;
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
/*     */ @FunctionalInterface
/*     */ public interface ComponentLike
/*     */ {
/*     */   @NotNull
/*     */   static List<Component> asComponents(@NotNull List<? extends ComponentLike> likes) {
/*  51 */     return asComponents(likes, null);
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
/*     */   static List<Component> asComponents(@NotNull List<? extends ComponentLike> likes, @Nullable Predicate<? super Component> filter) {
/*  65 */     Objects.requireNonNull(likes, "likes");
/*  66 */     int size = likes.size();
/*  67 */     if (size == 0)
/*     */     {
/*     */       
/*  70 */       return Collections.emptyList();
/*     */     }
/*  72 */     ArrayList<Component> components = null;
/*  73 */     for (int i = 0; i < size; i++) {
/*  74 */       ComponentLike like = likes.get(i);
/*  75 */       if (like == null) {
/*  76 */         throw new NullPointerException("likes[" + i + "]");
/*     */       }
/*  78 */       Component component = like.asComponent();
/*  79 */       if (filter == null || filter.test(component)) {
/*  80 */         if (components == null) {
/*  81 */           components = new ArrayList<>(size);
/*     */         }
/*  83 */         components.add(component);
/*     */       } 
/*     */     } 
/*     */     
/*  87 */     if (components == null) return Collections.emptyList();
/*     */ 
/*     */     
/*  90 */     components.trimToSize();
/*  91 */     return Collections.unmodifiableList(components);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   static Component unbox(@Nullable ComponentLike like) {
/* 102 */     return (like != null) ? like.asComponent() : null;
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   Component asComponent();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\ComponentLike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */