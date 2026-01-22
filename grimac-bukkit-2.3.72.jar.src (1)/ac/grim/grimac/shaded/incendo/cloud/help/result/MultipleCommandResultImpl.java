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
/*     */ 
/*     */ 
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "MultipleCommandResult", generator = "Immutables")
/*     */ @Immutable
/*     */ final class MultipleCommandResultImpl<C>
/*     */   implements MultipleCommandResult<C>
/*     */ {
/*     */   private final HelpQuery<C> query;
/*     */   private final String longestPath;
/*     */   private final List<String> childSuggestions;
/*     */   
/*     */   private MultipleCommandResultImpl(HelpQuery<C> query, String longestPath, Iterable<String> childSuggestions) {
/*  63 */     this.query = Objects.<HelpQuery<C>>requireNonNull(query, "query");
/*  64 */     this.longestPath = Objects.<String>requireNonNull(longestPath, "longestPath");
/*  65 */     this.childSuggestions = createUnmodifiableList(false, createSafeList(childSuggestions, true, false));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MultipleCommandResultImpl(MultipleCommandResultImpl<C> original, HelpQuery<C> query, String longestPath, List<String> childSuggestions) {
/*  73 */     this.query = query;
/*  74 */     this.longestPath = longestPath;
/*  75 */     this.childSuggestions = childSuggestions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HelpQuery<C> query() {
/*  83 */     return this.query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String longestPath() {
/*  91 */     return this.longestPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> childSuggestions() {
/*  99 */     return this.childSuggestions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final MultipleCommandResultImpl<C> withQuery(HelpQuery<C> value) {
/* 109 */     if (this.query == value) return this; 
/* 110 */     HelpQuery<C> newValue = Objects.<HelpQuery<C>>requireNonNull(value, "query");
/* 111 */     return new MultipleCommandResultImpl(this, newValue, this.longestPath, this.childSuggestions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final MultipleCommandResultImpl<C> withLongestPath(String value) {
/* 121 */     String newValue = Objects.<String>requireNonNull(value, "longestPath");
/* 122 */     if (this.longestPath.equals(newValue)) return this; 
/* 123 */     return new MultipleCommandResultImpl(this, this.query, newValue, this.childSuggestions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final MultipleCommandResultImpl<C> withChildSuggestions(String... elements) {
/* 132 */     List<String> newValue = createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
/* 133 */     return new MultipleCommandResultImpl(this, this.query, this.longestPath, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final MultipleCommandResultImpl<C> withChildSuggestions(Iterable<String> elements) {
/* 143 */     if (this.childSuggestions == elements) return this; 
/* 144 */     List<String> newValue = createUnmodifiableList(false, createSafeList(elements, true, false));
/* 145 */     return new MultipleCommandResultImpl(this, this.query, this.longestPath, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 154 */     if (this == another) return true; 
/* 155 */     return (another instanceof MultipleCommandResultImpl && 
/* 156 */       equalTo(0, (MultipleCommandResultImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, MultipleCommandResultImpl<?> another) {
/* 160 */     return (this.query.equals(another.query) && this.longestPath
/* 161 */       .equals(another.longestPath) && this.childSuggestions
/* 162 */       .equals(another.childSuggestions));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 171 */     int h = 5381;
/* 172 */     h += (h << 5) + this.query.hashCode();
/* 173 */     h += (h << 5) + this.longestPath.hashCode();
/* 174 */     h += (h << 5) + this.childSuggestions.hashCode();
/* 175 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 184 */     return "MultipleCommandResult{query=" + this.query + ", longestPath=" + this.longestPath + ", childSuggestions=" + this.childSuggestions + "}";
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
/*     */   public static <C> MultipleCommandResultImpl<C> of(HelpQuery<C> query, String longestPath, List<String> childSuggestions) {
/* 200 */     return of(query, longestPath, childSuggestions);
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
/*     */   public static <C> MultipleCommandResultImpl<C> of(HelpQuery<C> query, String longestPath, Iterable<String> childSuggestions) {
/* 212 */     return new MultipleCommandResultImpl<>(query, longestPath, childSuggestions);
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
/*     */   public static <C> MultipleCommandResultImpl<C> copyOf(MultipleCommandResult<C> instance) {
/* 224 */     if (instance instanceof MultipleCommandResultImpl) {
/* 225 */       return (MultipleCommandResultImpl<C>)instance;
/*     */     }
/* 227 */     return of(instance.query(), instance.longestPath(), instance.childSuggestions());
/*     */   }
/*     */   
/*     */   private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
/*     */     ArrayList<T> list;
/* 232 */     if (iterable instanceof Collection) {
/* 233 */       int size = ((Collection)iterable).size();
/* 234 */       if (size == 0) return Collections.emptyList(); 
/* 235 */       list = new ArrayList<>(size);
/*     */     } else {
/* 237 */       list = new ArrayList<>();
/*     */     } 
/* 239 */     for (T element : iterable) {
/* 240 */       if (skipNulls && element == null)
/* 241 */         continue;  if (checkNulls) Objects.requireNonNull(element, "element"); 
/* 242 */       list.add(element);
/*     */     } 
/* 244 */     return list;
/*     */   }
/*     */   
/*     */   private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
/* 248 */     switch (list.size()) { case 0:
/* 249 */         return Collections.emptyList();
/* 250 */       case 1: return Collections.singletonList(list.get(0)); }
/*     */     
/* 252 */     if (clone) {
/* 253 */       return Collections.unmodifiableList(new ArrayList<>(list));
/*     */     }
/* 255 */     if (list instanceof ArrayList) {
/* 256 */       ((ArrayList)list).trimToSize();
/*     */     }
/* 258 */     return Collections.unmodifiableList(list);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\result\MultipleCommandResultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */