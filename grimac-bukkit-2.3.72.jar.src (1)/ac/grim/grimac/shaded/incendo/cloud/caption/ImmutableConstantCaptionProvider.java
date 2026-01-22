/*     */ package ac.grim.grimac.shaded.incendo.cloud.caption;
/*     */ 
/*     */ import com.google.errorprone.annotations.CanIgnoreReturnValue;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.ParametersAreNonnullByDefault;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ import javax.annotation.concurrent.NotThreadSafe;
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
/*     */ @API(status = API.Status.STABLE, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "ConstantCaptionProvider", generator = "Immutables")
/*     */ @Immutable
/*     */ public final class ImmutableConstantCaptionProvider<C>
/*     */   extends ConstantCaptionProvider<C>
/*     */ {
/*     */   private final Map<Caption, String> captions;
/*     */   
/*     */   private ImmutableConstantCaptionProvider(Map<? extends Caption, ? extends String> captions) {
/*  60 */     this.captions = createUnmodifiableMap(true, false, captions);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ImmutableConstantCaptionProvider(ImmutableConstantCaptionProvider<C> original, Map<Caption, String> captions) {
/*  66 */     this.captions = captions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<Caption, String> captions() {
/*  75 */     return this.captions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ImmutableConstantCaptionProvider<C> withCaptions(Map<? extends Caption, ? extends String> entries) {
/*  86 */     if (this.captions == entries) return this; 
/*  87 */     Map<Caption, String> newValue = createUnmodifiableMap(true, false, entries);
/*  88 */     return new ImmutableConstantCaptionProvider(this, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/*  97 */     if (this == another) return true; 
/*  98 */     return (another instanceof ImmutableConstantCaptionProvider && 
/*  99 */       equalTo(0, (ImmutableConstantCaptionProvider)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, ImmutableConstantCaptionProvider<?> another) {
/* 103 */     return this.captions.equals(another.captions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 112 */     int h = 5381;
/* 113 */     h += (h << 5) + this.captions.hashCode();
/* 114 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 123 */     return "ConstantCaptionProvider{captions=" + this.captions + "}";
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
/*     */   public static <C> ImmutableConstantCaptionProvider<C> of(Map<? extends Caption, ? extends String> captions) {
/* 135 */     return new ImmutableConstantCaptionProvider<>(captions);
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
/*     */   public static <C> ImmutableConstantCaptionProvider<C> copyOf(ConstantCaptionProvider<C> instance) {
/* 147 */     if (instance instanceof ImmutableConstantCaptionProvider) {
/* 148 */       return (ImmutableConstantCaptionProvider<C>)instance;
/*     */     }
/* 150 */     return builder()
/* 151 */       .from(instance)
/* 152 */       .build();
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
/*     */   public static <C> Builder<C> builder() {
/* 166 */     return new Builder<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Generated(from = "ConstantCaptionProvider", generator = "Immutables")
/*     */   @NotThreadSafe
/*     */   public static final class Builder<C>
/*     */   {
/* 179 */     private Map<Caption, String> captions = null;
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
/*     */     @CanIgnoreReturnValue
/*     */     public final Builder<C> from(ConstantCaptionProvider<C> instance) {
/* 194 */       Objects.requireNonNull(instance, "instance");
/* 195 */       putAllCaptions(instance.captions());
/* 196 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @CanIgnoreReturnValue
/*     */     public final Builder<C> putCaption(Caption key, String value) {
/* 207 */       if (this.captions == null) {
/* 208 */         this.captions = new LinkedHashMap<>();
/*     */       }
/* 210 */       this.captions.put(
/* 211 */           Objects.<Caption>requireNonNull(key, "captions key"), 
/* 212 */           Objects.<String>requireNonNull(value, (value == null) ? ("captions value for key: " + key) : null));
/* 213 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @CanIgnoreReturnValue
/*     */     public final Builder<C> putCaption(Map.Entry<? extends Caption, ? extends String> entry) {
/* 223 */       if (this.captions == null) {
/* 224 */         this.captions = new LinkedHashMap<>();
/*     */       }
/* 226 */       Caption k = entry.getKey();
/* 227 */       String v = entry.getValue();
/* 228 */       this.captions.put(
/* 229 */           Objects.<Caption>requireNonNull(k, "captions key"), 
/* 230 */           Objects.<String>requireNonNull(v, (v == null) ? ("captions value for key: " + k) : null));
/* 231 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @CanIgnoreReturnValue
/*     */     public final Builder<C> captions(Map<? extends Caption, ? extends String> entries) {
/* 241 */       this.captions = new LinkedHashMap<>();
/* 242 */       return putAllCaptions(entries);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @CanIgnoreReturnValue
/*     */     public final Builder<C> putAllCaptions(Map<? extends Caption, ? extends String> entries) {
/* 252 */       if (this.captions == null) {
/* 253 */         this.captions = new LinkedHashMap<>();
/*     */       }
/* 255 */       for (Map.Entry<? extends Caption, ? extends String> e : entries.entrySet()) {
/* 256 */         Caption k = e.getKey();
/* 257 */         String v = e.getValue();
/* 258 */         this.captions.put(
/* 259 */             Objects.<Caption>requireNonNull(k, "captions key"), 
/* 260 */             Objects.<String>requireNonNull(v, (v == null) ? ("captions value for key: " + k) : null));
/*     */       } 
/* 262 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Builder() {}
/*     */ 
/*     */     
/*     */     public ImmutableConstantCaptionProvider<C> build() {
/* 271 */       return new ImmutableConstantCaptionProvider<>(null, 
/*     */           
/* 273 */           (this.captions == null) ? Collections.emptyMap() : ImmutableConstantCaptionProvider.createUnmodifiableMap(false, false, (Map)this.captions));
/*     */     } } private static <K, V> Map<K, V> createUnmodifiableMap(boolean checkNulls, boolean skipNulls, Map<? extends K, ? extends V> map) {
/*     */     Map.Entry<? extends K, ? extends V> e;
/*     */     K k;
/*     */     V v;
/* 278 */     switch (map.size()) { case 0:
/* 279 */         return Collections.emptyMap();
/*     */       case 1:
/* 281 */         e = map.entrySet().iterator().next();
/* 282 */         k = e.getKey();
/* 283 */         v = e.getValue();
/* 284 */         if (checkNulls) {
/* 285 */           Objects.requireNonNull(k, "key");
/* 286 */           Objects.requireNonNull(v, (v == null) ? ("value for key: " + k) : null);
/*     */         } 
/* 288 */         if (skipNulls && (k == null || v == null)) {
/* 289 */           return Collections.emptyMap();
/*     */         }
/* 291 */         return Collections.singletonMap(k, v); }
/*     */ 
/*     */     
/* 294 */     Map<K, V> linkedMap = new LinkedHashMap<>(map.size() * 4 / 3 + 1);
/* 295 */     if (skipNulls || checkNulls) {
/* 296 */       for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
/* 297 */         K k1 = entry.getKey();
/* 298 */         V v1 = entry.getValue();
/* 299 */         if (skipNulls)
/* 300 */         { if (k1 == null || v1 == null)
/* 301 */             continue;  } else if (checkNulls)
/* 302 */         { Objects.requireNonNull(k1, "key");
/* 303 */           Objects.requireNonNull(v1, (v1 == null) ? ("value for key: " + k1) : null); }
/*     */         
/* 305 */         linkedMap.put(k1, v1);
/*     */       } 
/*     */     } else {
/* 308 */       linkedMap.putAll(map);
/*     */     } 
/* 310 */     return Collections.unmodifiableMap(linkedMap);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\ImmutableConstantCaptionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */