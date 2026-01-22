/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ import java.util.stream.Collector;
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
/*     */ 
/*     */ 
/*     */ public interface CompoundBinaryTag
/*     */   extends BinaryTag, CompoundTagSetter<CompoundBinaryTag>, Iterable<Map.Entry<String, ? extends BinaryTag>>
/*     */ {
/*     */   @NotNull
/*     */   static CompoundBinaryTag empty() {
/*  50 */     return CompoundBinaryTagImpl.EMPTY;
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
/*     */   @NotNull
/*     */   static CompoundBinaryTag from(@NotNull Map<String, ? extends BinaryTag> tags) {
/*  63 */     if (tags.isEmpty()) return empty(); 
/*  64 */     return new CompoundBinaryTagImpl(new HashMap<>(tags));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Collector<Map.Entry<String, ? extends BinaryTag>, ?, CompoundBinaryTag> toCompoundTag() {
/*  76 */     return toCompoundTag(Map.Entry::getKey, Map.Entry::getValue);
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
/*     */   @NotNull
/*     */   static <T> Collector<T, ?, CompoundBinaryTag> toCompoundTag(@NotNull Function<T, String> keyLens, @NotNull Function<T, ? extends BinaryTag> valueLens) {
/*  91 */     Objects.requireNonNull(keyLens, "keyLens");
/*  92 */     Objects.requireNonNull(valueLens, "valueLens");
/*     */     
/*  94 */     return Collector.of(CompoundBinaryTag::builder, (b, ent) -> b.put(keyLens.apply(ent), valueLens.apply(ent)), (l, r) -> l.put(r.build()), Builder::build, new Collector.Characteristics[] { Collector.Characteristics.UNORDERED });
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Collector<Map.Entry<String, ? extends BinaryTag>, ?, CompoundBinaryTag> toCompoundTag(@NotNull CompoundBinaryTag initial) {
/* 113 */     return toCompoundTag(initial, Map.Entry::getKey, Map.Entry::getValue);
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
/*     */   
/*     */   @NotNull
/*     */   static <T> Collector<T, ?, CompoundBinaryTag> toCompoundTag(@NotNull CompoundBinaryTag initial, @NotNull Function<T, String> keyLens, @NotNull Function<T, ? extends BinaryTag> valueLens) {
/* 129 */     Objects.requireNonNull(initial, "initial");
/* 130 */     Objects.requireNonNull(keyLens, "keyLens");
/* 131 */     Objects.requireNonNull(valueLens, "valueLens");
/*     */     
/* 133 */     return Collector.of(() -> builder().put(initial), (b, ent) -> b.put(keyLens.apply(ent), valueLens.apply(ent)), (l, r) -> l.put(r.build()), Builder::build, new Collector.Characteristics[] { Collector.Characteristics.UNORDERED });
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
/*     */   
/*     */   @NotNull
/*     */   static Builder builder() {
/* 149 */     return new CompoundTagBuilder();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   default BinaryTagType<CompoundBinaryTag> type() {
/* 154 */     return BinaryTagTypes.COMPOUND;
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
/*     */   default boolean getBoolean(@NotNull String key) {
/* 201 */     return getBoolean(key, false);
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
/*     */   
/*     */   default boolean getBoolean(@NotNull String key, boolean defaultValue) {
/* 216 */     BinaryTag tag = get(key);
/* 217 */     if (tag instanceof ByteBinaryTag)
/*     */     {
/* 219 */       return (((ByteBinaryTag)tag).value() != 0);
/*     */     }
/* 221 */     return defaultValue;
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
/*     */   default byte getByte(@NotNull String key) {
/* 233 */     return getByte(key, (byte)0);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default short getShort(@NotNull String key) {
/* 256 */     return getShort(key, (short)0);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default int getInt(@NotNull String key) {
/* 279 */     return getInt(key, 0);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default long getLong(@NotNull String key) {
/* 302 */     return getLong(key, 0L);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default float getFloat(@NotNull String key) {
/* 325 */     return getFloat(key, 0.0F);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default double getDouble(@NotNull String key) {
/* 348 */     return getDouble(key, 0.0D);
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
/*     */   @NotNull
/*     */   default String getString(@NotNull String key) {
/* 391 */     return getString(key, "");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default ListBinaryTag getList(@NotNull String key) {
/* 414 */     return getList(key, ListBinaryTag.empty());
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
/*     */   @NotNull
/*     */   default ListBinaryTag getList(@NotNull String key, @NotNull BinaryTagType<? extends BinaryTag> expectedType) {
/* 439 */     return getList(key, expectedType, ListBinaryTag.empty());
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
/*     */   @NotNull
/*     */   default CompoundBinaryTag getCompound(@NotNull String key) {
/* 464 */     return getCompound(key, empty());
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   Set<String> keySet();
/*     */   
/*     */   @Nullable
/*     */   BinaryTag get(String paramString);
/*     */   
/*     */   int size();
/*     */   
/*     */   boolean isEmpty();
/*     */   
/*     */   byte getByte(@NotNull String paramString, byte paramByte);
/*     */   
/*     */   short getShort(@NotNull String paramString, short paramShort);
/*     */   
/*     */   int getInt(@NotNull String paramString, int paramInt);
/*     */   
/*     */   long getLong(@NotNull String paramString, long paramLong);
/*     */   
/*     */   float getFloat(@NotNull String paramString, float paramFloat);
/*     */   
/*     */   double getDouble(@NotNull String paramString, double paramDouble);
/*     */   
/*     */   byte[] getByteArray(@NotNull String paramString);
/*     */   
/*     */   byte[] getByteArray(@NotNull String paramString, byte[] paramArrayOfbyte);
/*     */   
/*     */   @NotNull
/*     */   String getString(@NotNull String paramString1, @NotNull String paramString2);
/*     */   
/*     */   @NotNull
/*     */   ListBinaryTag getList(@NotNull String paramString, @NotNull ListBinaryTag paramListBinaryTag);
/*     */   
/*     */   @NotNull
/*     */   ListBinaryTag getList(@NotNull String paramString, @NotNull BinaryTagType<? extends BinaryTag> paramBinaryTagType, @NotNull ListBinaryTag paramListBinaryTag);
/*     */   
/*     */   @NotNull
/*     */   CompoundBinaryTag getCompound(@NotNull String paramString, @NotNull CompoundBinaryTag paramCompoundBinaryTag);
/*     */   
/*     */   int[] getIntArray(@NotNull String paramString);
/*     */   
/*     */   int[] getIntArray(@NotNull String paramString, int[] paramArrayOfint);
/*     */   
/*     */   long[] getLongArray(@NotNull String paramString);
/*     */   
/*     */   long[] getLongArray(@NotNull String paramString, long[] paramArrayOflong);
/*     */   
/*     */   Stream<Map.Entry<String, ? extends BinaryTag>> stream();
/*     */   
/*     */   public static interface Builder extends CompoundTagSetter<Builder> {
/*     */     @NotNull
/*     */     CompoundBinaryTag build();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\CompoundBinaryTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */