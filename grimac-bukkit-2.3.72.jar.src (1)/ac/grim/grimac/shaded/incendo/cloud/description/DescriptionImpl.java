/*     */ package ac.grim.grimac.shaded.incendo.cloud.description;
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
/*     */ @Generated(from = "Description", generator = "Immutables")
/*     */ @Immutable
/*     */ final class DescriptionImpl
/*     */   implements Description
/*     */ {
/*     */   private final String textDescription;
/*     */   
/*     */   private DescriptionImpl(String textDescription) {
/*  53 */     this.textDescription = Objects.<String>requireNonNull(textDescription, "textDescription");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private DescriptionImpl(DescriptionImpl original, String textDescription) {
/*  59 */     this.textDescription = textDescription;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String textDescription() {
/*  67 */     return this.textDescription;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final DescriptionImpl withTextDescription(String value) {
/*  77 */     String newValue = Objects.<String>requireNonNull(value, "textDescription");
/*  78 */     if (this.textDescription.equals(newValue)) return this; 
/*  79 */     return new DescriptionImpl(this, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/*  88 */     if (this == another) return true; 
/*  89 */     return (another instanceof DescriptionImpl && 
/*  90 */       equalTo(0, (DescriptionImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, DescriptionImpl another) {
/*  94 */     return this.textDescription.equals(another.textDescription);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 103 */     int h = 5381;
/* 104 */     h += (h << 5) + this.textDescription.hashCode();
/* 105 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 114 */     return "Description{textDescription=" + this.textDescription + "}";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DescriptionImpl of(String textDescription) {
/* 125 */     return new DescriptionImpl(textDescription);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DescriptionImpl copyOf(Description instance) {
/* 136 */     if (instance instanceof DescriptionImpl) {
/* 137 */       return (DescriptionImpl)instance;
/*     */     }
/* 139 */     return of(instance.textDescription());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\description\DescriptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */