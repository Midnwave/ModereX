/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.legacy;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Objects;
/*     */ import java.util.stream.Stream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class LegacyFormat
/*     */   implements Examinable
/*     */ {
/*  46 */   static final LegacyFormat RESET = new LegacyFormat(true);
/*     */   
/*     */   @Nullable
/*     */   private final NamedTextColor color;
/*     */   
/*     */   @Nullable
/*     */   private final TextDecoration decoration;
/*     */   private final boolean reset;
/*     */   
/*     */   LegacyFormat(@Nullable NamedTextColor color) {
/*  56 */     this.color = color;
/*  57 */     this.decoration = null;
/*  58 */     this.reset = false;
/*     */   }
/*     */   
/*     */   LegacyFormat(@Nullable TextDecoration decoration) {
/*  62 */     this.color = null;
/*  63 */     this.decoration = decoration;
/*  64 */     this.reset = false;
/*     */   }
/*     */   
/*     */   private LegacyFormat(boolean reset) {
/*  68 */     this.color = null;
/*  69 */     this.decoration = null;
/*  70 */     this.reset = reset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public TextColor color() {
/*  80 */     return (TextColor)this.color;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public TextDecoration decoration() {
/*  90 */     return this.decoration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean reset() {
/* 100 */     return this.reset;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 105 */     if (this == other) return true; 
/* 106 */     if (other == null || getClass() != other.getClass()) return false; 
/* 107 */     LegacyFormat that = (LegacyFormat)other;
/* 108 */     return (this.color == that.color && this.decoration == that.decoration && this.reset == that.reset);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 113 */     int result = Objects.hashCode(this.color);
/* 114 */     result = 31 * result + Objects.hashCode(this.decoration);
/* 115 */     result = 31 * result + Boolean.hashCode(this.reset);
/* 116 */     return result;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 121 */     return Stream.of(new ExaminableProperty[] {
/* 122 */           ExaminableProperty.of("color", this.color), 
/* 123 */           ExaminableProperty.of("decoration", this.decoration), 
/* 124 */           ExaminableProperty.of("reset", this.reset)
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\legacy\LegacyFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */