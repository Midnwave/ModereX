/*     */ package ac.grim.grimac.shaded.incendo.cloud.context;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import java.time.Duration;
/*     */ import java.util.Objects;
/*     */ import org.apiguardian.api.API;
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
/*     */ @API(status = API.Status.MAINTAINED)
/*     */ public final class ParsingContext<C>
/*     */ {
/*     */   private final CommandComponent<C> component;
/*  37 */   private String consumed = null;
/*  38 */   private long startTime = -1L;
/*  39 */   private long endTime = -1L;
/*  40 */   private int consumedFrom = -1;
/*  41 */   private int consumedTo = -1;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean success;
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public ParsingContext(CommandComponent<C> component) {
/*  51 */     this.component = component;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandComponent<C> component() {
/*  60 */     return this.component;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Duration parseDuration() {
/*  69 */     if (this.startTime < 0L)
/*  70 */       throw new IllegalStateException("No start time has been registered"); 
/*  71 */     if (this.endTime < 0L) {
/*  72 */       throw new IllegalStateException("No end time has been registered");
/*     */     }
/*  74 */     return Duration.ofNanos(this.endTime - this.startTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public void markStart() {
/*  82 */     this.startTime = System.nanoTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public void markEnd() {
/*  90 */     this.endTime = System.nanoTime();
/*     */   }
/*     */   
/*     */   long startTime() {
/*  94 */     return this.startTime;
/*     */   }
/*     */   
/*     */   long endTime() {
/*  98 */     return this.endTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean success() {
/* 107 */     return this.success;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public void success(boolean success) {
/* 117 */     this.success = success;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public void consumedInput(CommandInput original, CommandInput postParse) {
/* 128 */     if (this.consumed != null) {
/* 129 */       throw new IllegalStateException();
/*     */     }
/* 131 */     this.consumed = original.difference(postParse);
/* 132 */     this.consumedFrom = original.cursor();
/* 133 */     this.consumedTo = original.cursor() + this.consumed.length();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public String consumedInput() {
/* 143 */     return Objects.<String>requireNonNull(this.consumed);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String exactAlias() {
/* 153 */     if (!this.success || this.component.type() != CommandComponent.ComponentType.LITERAL) {
/* 154 */       return null;
/*     */     }
/* 156 */     return this.consumed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public int consumedFrom() {
/* 166 */     return this.consumedFrom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public int consumedTo() {
/* 176 */     return this.consumedTo;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\context\ParsingContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */