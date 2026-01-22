/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
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
/*     */ public class PatchableComponentMap
/*     */   implements IComponentMap
/*     */ {
/*  31 */   public static final PatchableComponentMap EMPTY = new PatchableComponentMap(
/*  32 */       Collections.emptyMap(), Collections.emptyMap());
/*     */   
/*     */   private final Map<ComponentType<?>, ?> base;
/*     */   private final Map<ComponentType<?>, Optional<?>> patches;
/*     */   
/*     */   public PatchableComponentMap(StaticComponentMap base) {
/*  38 */     this(base.getDelegate(), new HashMap<>());
/*     */   }
/*     */   
/*     */   public PatchableComponentMap(Map<ComponentType<?>, ?> base) {
/*  42 */     this(base, new HashMap<>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PatchableComponentMap(StaticComponentMap base, Map<ComponentType<?>, Optional<?>> patches) {
/*  49 */     this(base.getDelegate(), patches);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PatchableComponentMap(Map<ComponentType<?>, ?> base, Map<ComponentType<?>, Optional<?>> patches) {
/*  56 */     this.base = Collections.unmodifiableMap(new HashMap<>(base));
/*  57 */     this.patches = patches;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public <T> T get(ComponentType<T> type) {
/*  63 */     Optional<?> patched = this.patches.get(type);
/*  64 */     if (patched != null) {
/*  65 */       return (T)patched.orElse(null);
/*     */     }
/*  67 */     return (T)this.base.get(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> void set(ComponentType<T> type, Optional<T> value) {
/*  72 */     Object baseVal = this.base.get(type);
/*  73 */     T newVal = value.orElse(null);
/*  74 */     if (Objects.equals(baseVal, newVal)) {
/*  75 */       this.patches.remove(type);
/*     */     } else {
/*  77 */       this.patches.put(type, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean has(ComponentType<?> type) {
/*  83 */     Optional<?> patched = this.patches.get(type);
/*  84 */     return (patched != null) ? patched.isPresent() : this.base.containsKey(type);
/*     */   }
/*     */   
/*     */   public PatchableComponentMap copy() {
/*  88 */     return new PatchableComponentMap(this.base, new HashMap<>(this.patches));
/*     */   }
/*     */   
/*     */   public Map<ComponentType<?>, ?> getBase() {
/*  92 */     return this.base;
/*     */   }
/*     */   
/*     */   public Map<ComponentType<?>, Optional<?>> getPatches() {
/*  96 */     return this.patches;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 101 */     if (this == obj) return true; 
/* 102 */     if (!(obj instanceof PatchableComponentMap)) return false; 
/* 103 */     PatchableComponentMap that = (PatchableComponentMap)obj;
/* 104 */     if (!this.base.equals(that.base)) return false; 
/* 105 */     return this.patches.equals(that.patches);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 110 */     return Objects.hash(new Object[] { this.base, this.patches });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 115 */     return "PatchableComponentMap{base=" + this.base + ", patches=" + this.patches + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\PatchableComponentMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */