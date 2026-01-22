/*     */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import com.mojang.brigadier.Message;
/*     */ import java.util.Objects;
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
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "TooltipSuggestion", generator = "Immutables")
/*     */ final class TooltipSuggestionImpl
/*     */   implements TooltipSuggestion
/*     */ {
/*     */   private final String suggestion;
/*     */   private final Message tooltip;
/*     */   
/*     */   private TooltipSuggestionImpl(String suggestion, Message tooltip) {
/*  50 */     this.suggestion = Objects.<String>requireNonNull(suggestion, "suggestion");
/*  51 */     this.tooltip = tooltip;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TooltipSuggestionImpl(TooltipSuggestionImpl original, String suggestion, Message tooltip) {
/*  58 */     this.suggestion = suggestion;
/*  59 */     this.tooltip = tooltip;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String suggestion() {
/*  67 */     return this.suggestion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message tooltip() {
/*  75 */     return this.tooltip;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final TooltipSuggestionImpl withSuggestion(String value) {
/*  85 */     String newValue = Objects.<String>requireNonNull(value, "suggestion");
/*  86 */     if (this.suggestion.equals(newValue)) return this; 
/*  87 */     return new TooltipSuggestionImpl(this, newValue, this.tooltip);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final TooltipSuggestionImpl withTooltip(Message value) {
/*  97 */     if (this.tooltip == value) return this; 
/*  98 */     return new TooltipSuggestionImpl(this, this.suggestion, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object another) {
/* 107 */     if (this == another) return true; 
/* 108 */     return (another instanceof TooltipSuggestionImpl && 
/* 109 */       equalsByValue((TooltipSuggestionImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalsByValue(TooltipSuggestionImpl another) {
/* 113 */     return (this.suggestion.equals(another.suggestion) && 
/* 114 */       Objects.equals(this.tooltip, another.tooltip));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 123 */     int h = 5381;
/* 124 */     h += (h << 5) + this.suggestion.hashCode();
/* 125 */     h += (h << 5) + Objects.hashCode(this.tooltip);
/* 126 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 135 */     return "TooltipSuggestion{suggestion=" + this.suggestion + ", tooltip=" + this.tooltip + "}";
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
/*     */   public static TooltipSuggestionImpl of(String suggestion, Message tooltip) {
/* 148 */     return new TooltipSuggestionImpl(suggestion, tooltip);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TooltipSuggestionImpl copyOf(TooltipSuggestion instance) {
/* 159 */     if (instance instanceof TooltipSuggestionImpl) {
/* 160 */       return (TooltipSuggestionImpl)instance;
/*     */     }
/* 162 */     return of(instance.suggestion(), instance.tooltip());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\suggestion\TooltipSuggestionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */