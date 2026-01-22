/*     */ package ac.grim.grimac.shaded.incendo.cloud.help.result;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.help.HelpQuery;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.ParametersAreNonnullByDefault;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Generated;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "IndexCommandResult", generator = "Immutables")
/*     */ @Immutable
/*     */ final class IndexCommandResultImpl<C>
/*     */   implements IndexCommandResult<C>
/*     */ {
/*     */   private final HelpQuery<C> query;
/*     */   private final List<CommandEntry<C>> entries;
/*     */   
/*     */   private IndexCommandResultImpl(HelpQuery<C> query, Iterable<? extends CommandEntry<C>> entries) {
/*  60 */     this.query = Objects.<HelpQuery<C>>requireNonNull(query, "query");
/*  61 */     this.entries = createUnmodifiableList(false, createSafeList(entries, true, false));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private IndexCommandResultImpl(IndexCommandResultImpl<C> original, HelpQuery<C> query, List<CommandEntry<C>> entries) {
/*  68 */     this.query = query;
/*  69 */     this.entries = entries;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HelpQuery<C> query() {
/*  77 */     return this.query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<CommandEntry<C>> entries() {
/*  85 */     return this.entries;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final IndexCommandResultImpl<C> withQuery(HelpQuery<C> value) {
/*  95 */     if (this.query == value) return this; 
/*  96 */     HelpQuery<C> newValue = Objects.<HelpQuery<C>>requireNonNull(value, "query");
/*  97 */     return new IndexCommandResultImpl(this, newValue, this.entries);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public final IndexCommandResultImpl<C> withEntries(CommandEntry<C>... elements) {
/* 107 */     List<CommandEntry<C>> newValue = createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
/* 108 */     return new IndexCommandResultImpl(this, this.query, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final IndexCommandResultImpl<C> withEntries(Iterable<? extends CommandEntry<C>> elements) {
/* 118 */     if (this.entries == elements) return this; 
/* 119 */     List<CommandEntry<C>> newValue = createUnmodifiableList(false, createSafeList(elements, true, false));
/* 120 */     return new IndexCommandResultImpl(this, this.query, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 129 */     if (this == another) return true; 
/* 130 */     return (another instanceof IndexCommandResultImpl && 
/* 131 */       equalTo(0, (IndexCommandResultImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, IndexCommandResultImpl<?> another) {
/* 135 */     return (this.query.equals(another.query) && this.entries
/* 136 */       .equals(another.entries));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 145 */     int h = 5381;
/* 146 */     h += (h << 5) + this.query.hashCode();
/* 147 */     h += (h << 5) + this.entries.hashCode();
/* 148 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 157 */     return "IndexCommandResult{query=" + this.query + ", entries=" + this.entries + "}";
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
/*     */   public static <C> IndexCommandResultImpl<C> of(HelpQuery<C> query, List<CommandEntry<C>> entries) {
/* 171 */     return of(query, entries);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <C> IndexCommandResultImpl<C> of(HelpQuery<C> query, Iterable<? extends CommandEntry<C>> entries) {
/* 182 */     return new IndexCommandResultImpl<>(query, entries);
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
/*     */   public static <C> IndexCommandResultImpl<C> copyOf(IndexCommandResult<C> instance) {
/* 194 */     if (instance instanceof IndexCommandResultImpl) {
/* 195 */       return (IndexCommandResultImpl<C>)instance;
/*     */     }
/* 197 */     return of(instance.query(), instance.entries());
/*     */   }
/*     */   
/*     */   private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
/*     */     ArrayList<T> list;
/* 202 */     if (iterable instanceof Collection) {
/* 203 */       int size = ((Collection)iterable).size();
/* 204 */       if (size == 0) return Collections.emptyList(); 
/* 205 */       list = new ArrayList<>(size);
/*     */     } else {
/* 207 */       list = new ArrayList<>();
/*     */     } 
/* 209 */     for (T element : iterable) {
/* 210 */       if (skipNulls && element == null)
/* 211 */         continue;  if (checkNulls) Objects.requireNonNull(element, "element"); 
/* 212 */       list.add(element);
/*     */     } 
/* 214 */     return list;
/*     */   }
/*     */   
/*     */   private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
/* 218 */     switch (list.size()) { case 0:
/* 219 */         return Collections.emptyList();
/* 220 */       case 1: return Collections.singletonList(list.get(0)); }
/*     */     
/* 222 */     if (clone) {
/* 223 */       return Collections.unmodifiableList(new ArrayList<>(list));
/*     */     }
/* 225 */     if (list instanceof ArrayList) {
/* 226 */       ((ArrayList)list).trimToSize();
/*     */     }
/* 228 */     return Collections.unmodifiableList(list);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\result\IndexCommandResultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */