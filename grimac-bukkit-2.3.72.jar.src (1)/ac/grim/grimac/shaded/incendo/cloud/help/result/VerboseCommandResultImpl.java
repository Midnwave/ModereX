/*     */ package ac.grim.grimac.shaded.incendo.cloud.help.result;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.help.HelpQuery;
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
/*     */ @Generated(from = "VerboseCommandResult", generator = "Immutables")
/*     */ @Immutable
/*     */ final class VerboseCommandResultImpl<C>
/*     */   implements VerboseCommandResult<C>
/*     */ {
/*     */   private final HelpQuery<C> query;
/*     */   private final CommandEntry<C> entry;
/*     */   
/*     */   private VerboseCommandResultImpl(HelpQuery<C> query, CommandEntry<C> entry) {
/*  57 */     this.query = Objects.<HelpQuery<C>>requireNonNull(query, "query");
/*  58 */     this.entry = Objects.<CommandEntry<C>>requireNonNull(entry, "entry");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private VerboseCommandResultImpl(VerboseCommandResultImpl<C> original, HelpQuery<C> query, CommandEntry<C> entry) {
/*  65 */     this.query = query;
/*  66 */     this.entry = entry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HelpQuery<C> query() {
/*  74 */     return this.query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandEntry<C> entry() {
/*  82 */     return this.entry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final VerboseCommandResultImpl<C> withQuery(HelpQuery<C> value) {
/*  92 */     if (this.query == value) return this; 
/*  93 */     HelpQuery<C> newValue = Objects.<HelpQuery<C>>requireNonNull(value, "query");
/*  94 */     return new VerboseCommandResultImpl(this, newValue, this.entry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final VerboseCommandResultImpl<C> withEntry(CommandEntry<C> value) {
/* 104 */     if (this.entry == value) return this; 
/* 105 */     CommandEntry<C> newValue = Objects.<CommandEntry<C>>requireNonNull(value, "entry");
/* 106 */     return new VerboseCommandResultImpl(this, this.query, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 115 */     if (this == another) return true; 
/* 116 */     return (another instanceof VerboseCommandResultImpl && 
/* 117 */       equalTo(0, (VerboseCommandResultImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, VerboseCommandResultImpl<?> another) {
/* 121 */     return (this.query.equals(another.query) && this.entry
/* 122 */       .equals(another.entry));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 131 */     int h = 5381;
/* 132 */     h += (h << 5) + this.query.hashCode();
/* 133 */     h += (h << 5) + this.entry.hashCode();
/* 134 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 143 */     return "VerboseCommandResult{query=" + this.query + ", entry=" + this.entry + "}";
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
/*     */   public static <C> VerboseCommandResultImpl<C> of(HelpQuery<C> query, CommandEntry<C> entry) {
/* 157 */     return new VerboseCommandResultImpl<>(query, entry);
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
/*     */   public static <C> VerboseCommandResultImpl<C> copyOf(VerboseCommandResult<C> instance) {
/* 169 */     if (instance instanceof VerboseCommandResultImpl) {
/* 170 */       return (VerboseCommandResultImpl<C>)instance;
/*     */     }
/* 172 */     return of(instance.query(), instance.entry());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\result\VerboseCommandResultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */