/*     */ package ac.grim.grimac.shaded.kyori.adventure.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
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
/*     */ public final class MonkeyBars
/*     */ {
/*     */   @SafeVarargs
/*     */   @NotNull
/*     */   public static <E extends Enum<E>> Set<E> enumSet(Class<E> type, E... constants) {
/*  58 */     Set<E> set = EnumSet.noneOf(type);
/*  59 */     Collections.addAll(set, constants);
/*  60 */     return Collections.unmodifiableSet(set);
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
/*     */   public static <T> List<T> addOne(@NotNull List<T> oldList, T newElement) {
/*  75 */     if (oldList.isEmpty()) return Collections.singletonList(newElement); 
/*  76 */     List<T> newList = new ArrayList<>(oldList.size() + 1);
/*  77 */     newList.addAll(oldList);
/*  78 */     newList.add(newElement);
/*  79 */     return Collections.unmodifiableList(newList);
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
/*     */   @SafeVarargs
/*     */   @NotNull
/*     */   public static <I, O> List<O> nonEmptyArrayToList(@NotNull Function<I, O> mapper, @NotNull I first, @NotNull I... others) {
/*  97 */     List<O> ret = new ArrayList<>(others.length + 1);
/*  98 */     ret.add(mapper.apply(first));
/*  99 */     for (I other : others) {
/* 100 */       ret.add(Objects.requireNonNull(mapper.apply(Objects.requireNonNull(other, "source[?]")), "mapper(source[?])"));
/*     */     }
/* 102 */     return Collections.unmodifiableList(ret);
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
/*     */   @NotNull
/*     */   public static <I, O> List<O> toUnmodifiableList(@NotNull Function<I, O> mapper, @NotNull Iterable<? extends I> source) {
/* 118 */     ArrayList<O> ret = (source instanceof Collection) ? new ArrayList<>(((Collection)source).size()) : new ArrayList<>();
/* 119 */     for (I el : source) {
/* 120 */       ret.add(Objects.requireNonNull(mapper.apply(Objects.requireNonNull(el, "source[?]")), "mapper(source[?])"));
/*     */     }
/* 122 */     return Collections.unmodifiableList(ret);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\MonkeyBars.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */