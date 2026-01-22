/*     */ package ac.grim.grimac.shaded.kyori.adventure.title;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.time.Duration;
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
/*     */ final class TitleImpl
/*     */   implements Title
/*     */ {
/*     */   private final Component title;
/*     */   private final Component subtitle;
/*     */   @Nullable
/*     */   private final Title.Times times;
/*     */   
/*     */   TitleImpl(@NotNull Component title, @NotNull Component subtitle, @Nullable Title.Times times) {
/*  44 */     this.title = Objects.<Component>requireNonNull(title, "title");
/*  45 */     this.subtitle = Objects.<Component>requireNonNull(subtitle, "subtitle");
/*  46 */     this.times = times;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component title() {
/*  51 */     return this.title;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component subtitle() {
/*  56 */     return this.subtitle;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Title.Times times() {
/*  61 */     return this.times;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T part(@NotNull TitlePart<T> part) {
/*  67 */     Objects.requireNonNull(part, "part");
/*  68 */     if (part == TitlePart.TITLE)
/*  69 */       return (T)this.title; 
/*  70 */     if (part == TitlePart.SUBTITLE)
/*  71 */       return (T)this.subtitle; 
/*  72 */     if (part == TitlePart.TIMES) {
/*  73 */       return (T)this.times;
/*     */     }
/*     */     
/*  76 */     throw new IllegalArgumentException("Don't know what " + part + " is.");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/*  81 */     if (this == other) return true; 
/*  82 */     if (other == null || getClass() != other.getClass()) return false; 
/*  83 */     TitleImpl that = (TitleImpl)other;
/*  84 */     return (this.title.equals(that.title) && this.subtitle
/*  85 */       .equals(that.subtitle) && 
/*  86 */       Objects.equals(this.times, that.times));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  91 */     int result = this.title.hashCode();
/*  92 */     result = 31 * result + this.subtitle.hashCode();
/*  93 */     result = 31 * result + Objects.hashCode(this.times);
/*  94 */     return result;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/*  99 */     return Stream.of(new ExaminableProperty[] {
/* 100 */           ExaminableProperty.of("title", this.title), 
/* 101 */           ExaminableProperty.of("subtitle", this.subtitle), 
/* 102 */           ExaminableProperty.of("times", this.times)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 108 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   static class TimesImpl implements Title.Times {
/*     */     private final Duration fadeIn;
/*     */     private final Duration stay;
/*     */     private final Duration fadeOut;
/*     */     
/*     */     TimesImpl(@NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut) {
/* 117 */       this.fadeIn = Objects.<Duration>requireNonNull(fadeIn, "fadeIn");
/* 118 */       this.stay = Objects.<Duration>requireNonNull(stay, "stay");
/* 119 */       this.fadeOut = Objects.<Duration>requireNonNull(fadeOut, "fadeOut");
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Duration fadeIn() {
/* 124 */       return this.fadeIn;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Duration stay() {
/* 129 */       return this.stay;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Duration fadeOut() {
/* 134 */       return this.fadeOut;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 139 */       if (this == other) return true; 
/* 140 */       if (!(other instanceof TimesImpl)) return false; 
/* 141 */       TimesImpl that = (TimesImpl)other;
/* 142 */       return (this.fadeIn.equals(that.fadeIn) && this.stay
/* 143 */         .equals(that.stay) && this.fadeOut
/* 144 */         .equals(that.fadeOut));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 149 */       int result = this.fadeIn.hashCode();
/* 150 */       result = 31 * result + this.stay.hashCode();
/* 151 */       result = 31 * result + this.fadeOut.hashCode();
/* 152 */       return result;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Stream<? extends ExaminableProperty> examinableProperties() {
/* 157 */       return Stream.of(new ExaminableProperty[] {
/* 158 */             ExaminableProperty.of("fadeIn", this.fadeIn), 
/* 159 */             ExaminableProperty.of("stay", this.stay), 
/* 160 */             ExaminableProperty.of("fadeOut", this.fadeOut)
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 166 */       return Internals.toString(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\title\TitleImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */