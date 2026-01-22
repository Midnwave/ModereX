/*     */ package ac.grim.grimac.shaded.incendo.cloud.help;
/*     */ 
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
/*     */ @Generated(from = "HelpQuery", generator = "Immutables")
/*     */ @Immutable
/*     */ final class HelpQueryImpl<C>
/*     */   implements HelpQuery<C>
/*     */ {
/*     */   private final C sender;
/*     */   private final String query;
/*     */   
/*     */   private HelpQueryImpl(C sender, String query) {
/*  54 */     this.sender = Objects.requireNonNull(sender, "sender");
/*  55 */     this.query = Objects.<String>requireNonNull(query, "query");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private HelpQueryImpl(HelpQueryImpl<C> original, C sender, String query) {
/*  62 */     this.sender = sender;
/*  63 */     this.query = query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public C sender() {
/*  71 */     return this.sender;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String query() {
/*  79 */     return this.query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final HelpQueryImpl<C> withSender(C value) {
/*  89 */     if (this.sender == value) return this; 
/*  90 */     C newValue = Objects.requireNonNull(value, "sender");
/*  91 */     return new HelpQueryImpl(this, newValue, this.query);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final HelpQueryImpl<C> withQuery(String value) {
/* 101 */     String newValue = Objects.<String>requireNonNull(value, "query");
/* 102 */     if (this.query.equals(newValue)) return this; 
/* 103 */     return new HelpQueryImpl(this, this.sender, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 112 */     if (this == another) return true; 
/* 113 */     return (another instanceof HelpQueryImpl && 
/* 114 */       equalTo(0, (HelpQueryImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, HelpQueryImpl<?> another) {
/* 118 */     return (this.sender.equals(another.sender) && this.query
/* 119 */       .equals(another.query));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 128 */     int h = 5381;
/* 129 */     h += (h << 5) + this.sender.hashCode();
/* 130 */     h += (h << 5) + this.query.hashCode();
/* 131 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 140 */     return "HelpQuery{sender=" + this.sender + ", query=" + this.query + "}";
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
/*     */   public static <C> HelpQueryImpl<C> of(C sender, String query) {
/* 154 */     return new HelpQueryImpl<>(sender, query);
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
/*     */   public static <C> HelpQueryImpl<C> copyOf(HelpQuery<C> instance) {
/* 166 */     if (instance instanceof HelpQueryImpl) {
/* 167 */       return (HelpQueryImpl<C>)instance;
/*     */     }
/* 169 */     return of(instance.sender(), instance.query());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\HelpQueryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */