/*     */ package ac.grim.grimac.shaded.kyori.adventure.resource;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.Formatter;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.Executor;
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
/*     */ final class ResourcePackInfoImpl
/*     */   implements ResourcePackInfo
/*     */ {
/*     */   private final UUID id;
/*     */   private final URI uri;
/*     */   private final String hash;
/*     */   
/*     */   ResourcePackInfoImpl(@NotNull UUID id, @NotNull URI uri, @NotNull String hash) {
/*  53 */     this.id = Objects.<UUID>requireNonNull(id, "id");
/*  54 */     this.uri = Objects.<URI>requireNonNull(uri, "uri");
/*  55 */     this.hash = Objects.<String>requireNonNull(hash, "hash");
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public UUID id() {
/*  60 */     return this.id;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public URI uri() {
/*  65 */     return this.uri;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String hash() {
/*  70 */     return this.hash;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/*  75 */     return Stream.of(new ExaminableProperty[] {
/*  76 */           ExaminableProperty.of("id", this.id), 
/*  77 */           ExaminableProperty.of("uri", this.uri), 
/*  78 */           ExaminableProperty.of("hash", this.hash)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  84 */     return Internals.toString(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/*  89 */     if (this == other) return true; 
/*  90 */     if (!(other instanceof ResourcePackInfoImpl)) return false; 
/*  91 */     ResourcePackInfoImpl that = (ResourcePackInfoImpl)other;
/*  92 */     return (this.id.equals(that.id) && this.uri
/*  93 */       .equals(that.uri) && this.hash
/*  94 */       .equals(that.hash));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  99 */     int result = this.id.hashCode();
/* 100 */     result = 31 * result + this.uri.hashCode();
/* 101 */     result = 31 * result + this.hash.hashCode();
/* 102 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   static final class BuilderImpl
/*     */     implements ResourcePackInfo.Builder
/*     */   {
/*     */     private UUID id;
/*     */     private URI uri;
/*     */     private String hash;
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackInfo.Builder id(@NotNull UUID id) {
/* 115 */       this.id = Objects.<UUID>requireNonNull(id, "id");
/* 116 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackInfo.Builder uri(@NotNull URI uri) {
/* 121 */       this.uri = Objects.<URI>requireNonNull(uri, "uri");
/* 122 */       if (this.id == null) {
/* 123 */         this.id = UUID.nameUUIDFromBytes(uri.toString().getBytes(StandardCharsets.UTF_8));
/*     */       }
/* 125 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackInfo.Builder hash(@NotNull String hash) {
/* 130 */       this.hash = Objects.<String>requireNonNull(hash, "hash");
/* 131 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ResourcePackInfo build() {
/* 136 */       return new ResourcePackInfoImpl(this.id, this.uri, this.hash);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public CompletableFuture<ResourcePackInfo> computeHashAndBuild(@NotNull Executor executor) {
/* 141 */       return ResourcePackInfoImpl.computeHash(Objects.<URI>requireNonNull(this.uri, "uri"), executor)
/* 142 */         .thenApply(hash -> {
/*     */             hash(hash);
/*     */             return build();
/*     */           });
/*     */     }
/*     */   }
/*     */   
/*     */   static CompletableFuture<String> computeHash(URI uri, Executor exec) {
/* 150 */     CompletableFuture<String> result = new CompletableFuture<>();
/*     */     
/* 152 */     exec.execute(() -> { try {
/*     */             URL url = uri.toURL(); URLConnection conn = url.openConnection(); conn.addRequestProperty("User-Agent", "adventure/" + ResourcePackInfoImpl.class.getPackage().getSpecificationVersion() + " (pack-fetcher)"); InputStream is = conn.getInputStream(); 
/*     */             try { MessageDigest digest = MessageDigest.getInstance("SHA-1"); byte[] buf = new byte[8192]; int read; while ((read = is.read(buf)) != -1)
/*     */                 digest.update(buf, 0, read);  result.complete(bytesToString(digest.digest())); if (is != null)
/*     */                 is.close();  }
/* 157 */             catch (Throwable throwable) { if (is != null) try { is.close(); } catch (Throwable throwable1)
/*     */                 { throwable.addSuppressed(throwable1); }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               throw throwable; }
/*     */           
/* 166 */           } catch (IOException|java.security.NoSuchAlgorithmException ex) {
/*     */             result.completeExceptionally(ex);
/*     */           } 
/*     */         });
/*     */     
/* 171 */     return result;
/*     */   }
/*     */   
/*     */   static String bytesToString(byte[] arr) {
/* 175 */     StringBuilder builder = new StringBuilder(arr.length * 2);
/* 176 */     Formatter fmt = new Formatter(builder, Locale.ROOT);
/* 177 */     for (int i = 0; i < arr.length; i++) {
/* 178 */       fmt.format("%02x", new Object[] { Integer.valueOf(arr[i] & 0xFF) });
/*     */     } 
/* 180 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\resource\ResourcePackInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */