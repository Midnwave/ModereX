/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Debug.Renderer;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.Consumer;
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
/*     */ @Renderer(text = "\"CompoundBinaryTag[length=\" + this.tags.size() + \"]\"", childrenArray = "this.tags.entrySet().toArray()", hasChildren = "!this.tags.isEmpty()")
/*     */ final class CompoundBinaryTagImpl
/*     */   extends AbstractBinaryTag
/*     */   implements CompoundBinaryTag
/*     */ {
/*  42 */   static final CompoundBinaryTag EMPTY = new CompoundBinaryTagImpl(Collections.emptyMap());
/*     */   private final Map<String, BinaryTag> tags;
/*     */   private final int hashCode;
/*     */   
/*     */   CompoundBinaryTagImpl(Map<String, BinaryTag> tags) {
/*  47 */     this.tags = Collections.unmodifiableMap(tags);
/*  48 */     this.hashCode = tags.hashCode();
/*     */   }
/*     */   
/*     */   public boolean contains(@NotNull String key, @NotNull BinaryTagType<?> type) {
/*  52 */     BinaryTag tag = this.tags.get(key);
/*  53 */     return (tag != null && type.test(tag.type()));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Set<String> keySet() {
/*  58 */     return Collections.unmodifiableSet(this.tags.keySet());
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public BinaryTag get(String key) {
/*  63 */     return this.tags.get(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  68 */     return this.tags.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  73 */     return this.tags.isEmpty();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public CompoundBinaryTag put(@NotNull String key, @NotNull BinaryTag tag) {
/*  78 */     return edit(map -> map.put(key, tag));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public CompoundBinaryTag put(@NotNull CompoundBinaryTag tag) {
/*  83 */     return edit(map -> {
/*     */           for (String key : tag.keySet()) {
/*     */             map.put(key, tag.get(key));
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public CompoundBinaryTag put(@NotNull Map<String, ? extends BinaryTag> tags) {
/*  92 */     return edit(map -> map.putAll(tags));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public CompoundBinaryTag remove(@NotNull String key, @Nullable Consumer<? super BinaryTag> removed) {
/*  97 */     if (!this.tags.containsKey(key)) {
/*  98 */       return this;
/*     */     }
/* 100 */     return edit(map -> {
/*     */           BinaryTag tag = (BinaryTag)map.remove(key);
/*     */           if (removed != null) {
/*     */             removed.accept(tag);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(@NotNull String key, byte defaultValue) {
/* 110 */     if (contains(key, BinaryTagTypes.BYTE)) {
/* 111 */       return ((NumberBinaryTag)this.tags.get(key)).byteValue();
/*     */     }
/* 113 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(@NotNull String key, short defaultValue) {
/* 118 */     if (contains(key, BinaryTagTypes.SHORT)) {
/* 119 */       return ((NumberBinaryTag)this.tags.get(key)).shortValue();
/*     */     }
/* 121 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(@NotNull String key, int defaultValue) {
/* 126 */     if (contains(key, BinaryTagTypes.INT)) {
/* 127 */       return ((NumberBinaryTag)this.tags.get(key)).intValue();
/*     */     }
/* 129 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(@NotNull String key, long defaultValue) {
/* 134 */     if (contains(key, BinaryTagTypes.LONG)) {
/* 135 */       return ((NumberBinaryTag)this.tags.get(key)).longValue();
/*     */     }
/* 137 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat(@NotNull String key, float defaultValue) {
/* 142 */     if (contains(key, BinaryTagTypes.FLOAT)) {
/* 143 */       return ((NumberBinaryTag)this.tags.get(key)).floatValue();
/*     */     }
/* 145 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble(@NotNull String key, double defaultValue) {
/* 150 */     if (contains(key, BinaryTagTypes.DOUBLE)) {
/* 151 */       return ((NumberBinaryTag)this.tags.get(key)).doubleValue();
/*     */     }
/* 153 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getByteArray(@NotNull String key) {
/* 158 */     if (contains(key, BinaryTagTypes.BYTE_ARRAY)) {
/* 159 */       return ((ByteArrayBinaryTag)this.tags.get(key)).value();
/*     */     }
/* 161 */     return new byte[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getByteArray(@NotNull String key, byte[] defaultValue) {
/* 166 */     if (contains(key, BinaryTagTypes.BYTE_ARRAY)) {
/* 167 */       return ((ByteArrayBinaryTag)this.tags.get(key)).value();
/*     */     }
/* 169 */     return defaultValue;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String getString(@NotNull String key, @NotNull String defaultValue) {
/* 174 */     if (contains(key, BinaryTagTypes.STRING)) {
/* 175 */       return ((StringBinaryTag)this.tags.get(key)).value();
/*     */     }
/* 177 */     return defaultValue;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ListBinaryTag getList(@NotNull String key, @NotNull ListBinaryTag defaultValue) {
/* 182 */     if (contains(key, BinaryTagTypes.LIST)) {
/* 183 */       return (ListBinaryTag)this.tags.get(key);
/*     */     }
/* 185 */     return defaultValue;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ListBinaryTag getList(@NotNull String key, @NotNull BinaryTagType<? extends BinaryTag> expectedType, @NotNull ListBinaryTag defaultValue) {
/* 190 */     if (contains(key, BinaryTagTypes.LIST)) {
/* 191 */       ListBinaryTag tag = (ListBinaryTag)this.tags.get(key);
/* 192 */       if (expectedType.test(tag.elementType())) {
/* 193 */         return tag;
/*     */       }
/*     */     } 
/* 196 */     return defaultValue;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public CompoundBinaryTag getCompound(@NotNull String key, @NotNull CompoundBinaryTag defaultValue) {
/* 201 */     if (contains(key, BinaryTagTypes.COMPOUND)) {
/* 202 */       return (CompoundBinaryTag)this.tags.get(key);
/*     */     }
/* 204 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] getIntArray(@NotNull String key) {
/* 209 */     if (contains(key, BinaryTagTypes.INT_ARRAY)) {
/* 210 */       return ((IntArrayBinaryTag)this.tags.get(key)).value();
/*     */     }
/* 212 */     return new int[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] getIntArray(@NotNull String key, int[] defaultValue) {
/* 217 */     if (contains(key, BinaryTagTypes.INT_ARRAY)) {
/* 218 */       return ((IntArrayBinaryTag)this.tags.get(key)).value();
/*     */     }
/* 220 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] getLongArray(@NotNull String key) {
/* 225 */     if (contains(key, BinaryTagTypes.LONG_ARRAY)) {
/* 226 */       return ((LongArrayBinaryTag)this.tags.get(key)).value();
/*     */     }
/* 228 */     return new long[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] getLongArray(@NotNull String key, long[] defaultValue) {
/* 233 */     if (contains(key, BinaryTagTypes.LONG_ARRAY)) {
/* 234 */       return ((LongArrayBinaryTag)this.tags.get(key)).value();
/*     */     }
/* 236 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Stream<Map.Entry<String, ? extends BinaryTag>> stream() {
/* 242 */     return this.tags.entrySet().stream();
/*     */   }
/*     */   
/*     */   private CompoundBinaryTag edit(Consumer<Map<String, BinaryTag>> consumer) {
/* 246 */     Map<String, BinaryTag> tags = new HashMap<>(this.tags);
/* 247 */     consumer.accept(tags);
/* 248 */     return new CompoundBinaryTagImpl(new HashMap<>(tags));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object that) {
/* 253 */     return (this == that || (that instanceof CompoundBinaryTagImpl && this.tags.equals(((CompoundBinaryTagImpl)that).tags)));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 258 */     return this.hashCode;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 263 */     return Stream.of(ExaminableProperty.of("tags", this.tags));
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Iterator<Map.Entry<String, ? extends BinaryTag>> iterator() {
/* 269 */     return this.tags.entrySet().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public void forEach(@NotNull Consumer<? super Map.Entry<String, ? extends BinaryTag>> action) {
/* 274 */     this.tags.entrySet().forEach(Objects.<Consumer>requireNonNull(action, "action"));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\CompoundBinaryTagImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */