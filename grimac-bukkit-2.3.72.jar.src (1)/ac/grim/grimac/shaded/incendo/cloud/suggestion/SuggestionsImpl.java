/*     */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
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
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "Suggestions", generator = "Immutables")
/*     */ @Immutable
/*     */ final class SuggestionsImpl<C, S extends Suggestion>
/*     */   implements Suggestions<C, S>
/*     */ {
/*     */   private final CommandContext<C> commandContext;
/*     */   private final List<S> list;
/*     */   private final CommandInput commandInput;
/*     */   
/*     */   private SuggestionsImpl(CommandContext<C> commandContext, Iterable<? extends S> list, CommandInput commandInput) {
/*  63 */     this.commandContext = Objects.<CommandContext<C>>requireNonNull(commandContext, "commandContext");
/*  64 */     this.list = createUnmodifiableList(false, createSafeList(list, true, false));
/*  65 */     this.commandInput = Objects.<CommandInput>requireNonNull(commandInput, "commandInput");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SuggestionsImpl(SuggestionsImpl<C, S> original, CommandContext<C> commandContext, List<S> list, CommandInput commandInput) {
/*  73 */     this.commandContext = commandContext;
/*  74 */     this.list = list;
/*  75 */     this.commandInput = commandInput;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandContext<C> commandContext() {
/*  83 */     return this.commandContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<S> list() {
/*  91 */     return this.list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandInput commandInput() {
/*  99 */     return this.commandInput;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SuggestionsImpl<C, S> withCommandContext(CommandContext<C> value) {
/* 109 */     if (this.commandContext == value) return this; 
/* 110 */     CommandContext<C> newValue = Objects.<CommandContext<C>>requireNonNull(value, "commandContext");
/* 111 */     return new SuggestionsImpl(this, newValue, this.list, this.commandInput);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public final SuggestionsImpl<C, S> withList(S... elements) {
/* 121 */     List<S> newValue = createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
/* 122 */     return new SuggestionsImpl(this, this.commandContext, newValue, this.commandInput);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SuggestionsImpl<C, S> withList(Iterable<? extends S> elements) {
/* 132 */     if (this.list == elements) return this; 
/* 133 */     List<S> newValue = createUnmodifiableList(false, createSafeList(elements, true, false));
/* 134 */     return new SuggestionsImpl(this, this.commandContext, newValue, this.commandInput);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SuggestionsImpl<C, S> withCommandInput(CommandInput value) {
/* 144 */     if (this.commandInput == value) return this; 
/* 145 */     CommandInput newValue = Objects.<CommandInput>requireNonNull(value, "commandInput");
/* 146 */     return new SuggestionsImpl(this, this.commandContext, this.list, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 155 */     if (this == another) return true; 
/* 156 */     return (another instanceof SuggestionsImpl && 
/* 157 */       equalTo(0, (SuggestionsImpl<?, ?>)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, SuggestionsImpl<?, ?> another) {
/* 161 */     return (this.commandContext.equals(another.commandContext) && this.list
/* 162 */       .equals(another.list) && this.commandInput
/* 163 */       .equals(another.commandInput));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 172 */     int h = 5381;
/* 173 */     h += (h << 5) + this.commandContext.hashCode();
/* 174 */     h += (h << 5) + this.list.hashCode();
/* 175 */     h += (h << 5) + this.commandInput.hashCode();
/* 176 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 185 */     return "Suggestions{commandContext=" + this.commandContext + ", list=" + this.list + ", commandInput=" + this.commandInput + "}";
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
/*     */   public static <C, S extends Suggestion> SuggestionsImpl<C, S> of(CommandContext<C> commandContext, List<S> list, CommandInput commandInput) {
/* 202 */     return of(commandContext, list, commandInput);
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
/*     */   public static <C, S extends Suggestion> SuggestionsImpl<C, S> of(CommandContext<C> commandContext, Iterable<? extends S> list, CommandInput commandInput) {
/* 215 */     return new SuggestionsImpl<>(commandContext, list, commandInput);
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
/*     */   public static <C, S extends Suggestion> SuggestionsImpl<C, S> copyOf(Suggestions<C, S> instance) {
/* 228 */     if (instance instanceof SuggestionsImpl) {
/* 229 */       return (SuggestionsImpl<C, S>)instance;
/*     */     }
/* 231 */     return of(instance.commandContext(), instance.list(), instance.commandInput());
/*     */   }
/*     */   
/*     */   private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
/*     */     ArrayList<T> list;
/* 236 */     if (iterable instanceof Collection) {
/* 237 */       int size = ((Collection)iterable).size();
/* 238 */       if (size == 0) return Collections.emptyList(); 
/* 239 */       list = new ArrayList<>(size);
/*     */     } else {
/* 241 */       list = new ArrayList<>();
/*     */     } 
/* 243 */     for (T element : iterable) {
/* 244 */       if (skipNulls && element == null)
/* 245 */         continue;  if (checkNulls) Objects.requireNonNull(element, "element"); 
/* 246 */       list.add(element);
/*     */     } 
/* 248 */     return list;
/*     */   }
/*     */   
/*     */   private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
/* 252 */     switch (list.size()) { case 0:
/* 253 */         return Collections.emptyList();
/* 254 */       case 1: return Collections.singletonList(list.get(0)); }
/*     */     
/* 256 */     if (clone) {
/* 257 */       return Collections.unmodifiableList(new ArrayList<>(list));
/*     */     }
/* 259 */     if (list instanceof ArrayList) {
/* 260 */       ((ArrayList)list).trimToSize();
/*     */     }
/* 262 */     return Collections.unmodifiableList(list);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\SuggestionsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */