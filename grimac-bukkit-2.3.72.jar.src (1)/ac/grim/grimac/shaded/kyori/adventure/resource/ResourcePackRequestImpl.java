/*     */ package ac.grim.grimac.shaded.kyori.adventure.resource;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.MonkeyBars;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ final class ResourcePackRequestImpl
/*     */   implements ResourcePackRequest
/*     */ {
/*     */   private final List<ResourcePackInfo> packs;
/*     */   private final ResourcePackCallback cb;
/*     */   private final boolean replace;
/*     */   private final boolean required;
/*     */   @Nullable
/*     */   private final Component prompt;
/*     */   
/*     */   ResourcePackRequestImpl(List<ResourcePackInfo> packs, ResourcePackCallback cb, boolean replace, boolean required, @Nullable Component prompt) {
/*  47 */     this.packs = packs;
/*  48 */     this.cb = cb;
/*  49 */     this.replace = replace;
/*  50 */     this.required = required;
/*  51 */     this.prompt = prompt;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public List<ResourcePackInfo> packs() {
/*  56 */     return this.packs;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ResourcePackRequest packs(@NotNull Iterable<? extends ResourcePackInfoLike> packs) {
/*  62 */     if (this.packs.equals(packs)) return this;
/*     */     
/*  64 */     return new ResourcePackRequestImpl(
/*  65 */         MonkeyBars.toUnmodifiableList(ResourcePackInfoLike::asResourcePackInfo, packs), this.cb, this.replace, this.required, this.prompt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ResourcePackCallback callback() {
/*  75 */     return this.cb;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ResourcePackRequest callback(@NotNull ResourcePackCallback cb) {
/*  80 */     if (cb == this.cb) return this;
/*     */     
/*  82 */     return new ResourcePackRequestImpl(this.packs, 
/*     */         
/*  84 */         Objects.<ResourcePackCallback>requireNonNull(cb, "cb"), this.replace, this.required, this.prompt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean replace() {
/*  93 */     return this.replace;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean required() {
/*  98 */     return this.required;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component prompt() {
/* 103 */     return this.prompt;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ResourcePackRequest replace(boolean replace) {
/* 108 */     if (replace == this.replace) return this;
/*     */     
/* 110 */     return new ResourcePackRequestImpl(this.packs, this.cb, replace, this.required, this.prompt);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 115 */     if (this == other) return true; 
/* 116 */     if (other == null || getClass() != other.getClass()) return false; 
/* 117 */     ResourcePackRequestImpl that = (ResourcePackRequestImpl)other;
/* 118 */     return (this.replace == that.replace && 
/* 119 */       Objects.equals(this.packs, that.packs) && 
/* 120 */       Objects.equals(this.cb, that.cb) && this.required == that.required && 
/*     */       
/* 122 */       Objects.equals(this.prompt, that.prompt));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 127 */     return Objects.hash(new Object[] { this.packs, this.cb, Boolean.valueOf(this.replace), Boolean.valueOf(this.required), this.prompt });
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String toString() {
/* 132 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 137 */     return Stream.of(new ExaminableProperty[] {
/* 138 */           ExaminableProperty.of("packs", this.packs), 
/* 139 */           ExaminableProperty.of("callback", this.cb), 
/* 140 */           ExaminableProperty.of("replace", this.replace), 
/* 141 */           ExaminableProperty.of("required", this.required), 
/* 142 */           ExaminableProperty.of("prompt", this.prompt) });
/*     */   }
/*     */   
/*     */   static final class BuilderImpl implements ResourcePackRequest.Builder {
/*     */     private List<ResourcePackInfo> packs;
/*     */     private ResourcePackCallback cb;
/*     */     private boolean replace;
/*     */     private boolean required;
/*     */     @Nullable
/*     */     private Component prompt;
/*     */     
/*     */     BuilderImpl() {
/* 154 */       this.packs = Collections.emptyList();
/* 155 */       this.cb = ResourcePackCallback.noOp();
/* 156 */       this.replace = false;
/*     */     }
/*     */     
/*     */     BuilderImpl(@NotNull ResourcePackRequest req) {
/* 160 */       this.packs = req.packs();
/* 161 */       this.cb = req.callback();
/* 162 */       this.replace = req.replace();
/* 163 */       this.required = req.required();
/* 164 */       this.prompt = req.prompt();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackRequest.Builder packs(@NotNull ResourcePackInfoLike first, @NotNull ResourcePackInfoLike... others) {
/* 169 */       this.packs = MonkeyBars.nonEmptyArrayToList(ResourcePackInfoLike::asResourcePackInfo, first, (Object[])others);
/* 170 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackRequest.Builder packs(@NotNull Iterable<? extends ResourcePackInfoLike> packs) {
/* 175 */       this.packs = MonkeyBars.toUnmodifiableList(ResourcePackInfoLike::asResourcePackInfo, packs);
/* 176 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackRequest.Builder callback(@NotNull ResourcePackCallback cb) {
/* 181 */       this.cb = Objects.<ResourcePackCallback>requireNonNull(cb, "cb");
/* 182 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackRequest.Builder replace(boolean replace) {
/* 187 */       this.replace = replace;
/* 188 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackRequest.Builder required(boolean required) {
/* 193 */       this.required = required;
/* 194 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackRequest.Builder prompt(@Nullable Component prompt) {
/* 199 */       this.prompt = prompt;
/* 200 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackRequest build() {
/* 205 */       return new ResourcePackRequestImpl(this.packs, this.cb, this.replace, this.required, this.prompt);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\resource\ResourcePackRequestImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */