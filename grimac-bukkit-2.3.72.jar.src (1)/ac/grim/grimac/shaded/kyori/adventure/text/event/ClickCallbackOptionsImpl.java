/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.event;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*    */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*    */ import java.time.Duration;
/*    */ import java.time.temporal.TemporalAmount;
/*    */ import java.util.Objects;
/*    */ import java.util.stream.Stream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class ClickCallbackOptionsImpl
/*    */   implements ClickCallback.Options
/*    */ {
/* 36 */   static final ClickCallback.Options DEFAULT = (new BuilderImpl()).build();
/*    */   
/*    */   private final int uses;
/*    */   private final Duration lifetime;
/*    */   
/*    */   ClickCallbackOptionsImpl(int uses, Duration lifetime) {
/* 42 */     this.uses = uses;
/* 43 */     this.lifetime = lifetime;
/*    */   }
/*    */ 
/*    */   
/*    */   public int uses() {
/* 48 */     return this.uses;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Duration lifetime() {
/* 53 */     return this.lifetime;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 58 */     return Stream.of(new ExaminableProperty[] {
/* 59 */           ExaminableProperty.of("uses", this.uses), 
/* 60 */           ExaminableProperty.of("expiration", this.lifetime)
/*    */         });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 66 */     return Internals.toString(this);
/*    */   }
/*    */   
/*    */   static final class BuilderImpl
/*    */     implements ClickCallback.Options.Builder {
/*    */     private static final int DEFAULT_USES = 1;
/*    */     private int uses;
/*    */     private Duration lifetime;
/*    */     
/*    */     BuilderImpl() {
/* 76 */       this.uses = 1;
/* 77 */       this.lifetime = ClickCallback.DEFAULT_LIFETIME;
/*    */     }
/*    */     
/*    */     BuilderImpl(ClickCallback.Options existing) {
/* 81 */       this.uses = existing.uses();
/* 82 */       this.lifetime = existing.lifetime();
/*    */     }
/*    */ 
/*    */     
/*    */     public ClickCallback.Options build() {
/* 87 */       return new ClickCallbackOptionsImpl(this.uses, this.lifetime);
/*    */     }
/*    */     
/*    */     @NotNull
/*    */     public ClickCallback.Options.Builder uses(int uses) {
/* 92 */       this.uses = uses;
/* 93 */       return this;
/*    */     }
/*    */     
/*    */     @NotNull
/*    */     public ClickCallback.Options.Builder lifetime(@NotNull TemporalAmount lifetime) {
/* 98 */       this.lifetime = (lifetime instanceof Duration) ? (Duration)lifetime : Duration.from(Objects.<TemporalAmount>requireNonNull(lifetime, "lifetime"));
/* 99 */       return this;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\event\ClickCallbackOptionsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */