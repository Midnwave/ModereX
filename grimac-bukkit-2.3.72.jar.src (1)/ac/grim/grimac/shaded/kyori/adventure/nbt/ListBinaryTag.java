/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.Consumer;
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
/*     */ public interface ListBinaryTag
/*     */   extends ListTagSetter<ListBinaryTag, BinaryTag>, BinaryTag, Iterable<BinaryTag>
/*     */ {
/*     */   @NotNull
/*     */   static ListBinaryTag empty() {
/*  49 */     return ListBinaryTagImpl.EMPTY;
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
/*     */   @NotNull
/*     */   static ListBinaryTag from(@NotNull Iterable<? extends BinaryTag> tags) {
/*  63 */     return builder().add(tags).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Builder<BinaryTag> builder() {
/*  73 */     return new ListTagBuilder<>(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Builder<BinaryTag> heterogeneousListBinaryTag() {
/*  83 */     return new ListTagBuilder<>(true);
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
/*     */   static <T extends BinaryTag> Builder<T> builder(@NotNull BinaryTagType<T> type) {
/*  96 */     if (type == BinaryTagTypes.END) throw new IllegalArgumentException("Cannot create a list of " + BinaryTagTypes.END); 
/*  97 */     return new ListTagBuilder<>(false, type);
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
/*     */   static ListBinaryTag listBinaryTag(@NotNull BinaryTagType<? extends BinaryTag> type, @NotNull List<BinaryTag> tags) {
/* 112 */     if (tags.isEmpty()) return empty(); 
/* 113 */     if (type == BinaryTagTypes.END) throw new IllegalArgumentException("Cannot create a list of " + BinaryTagTypes.END); 
/* 114 */     ListBinaryTagImpl.validateTagType(tags, (type == BinaryTagTypes.LIST_WILDCARD));
/* 115 */     return new ListBinaryTagImpl(type, (type == BinaryTagTypes.LIST_WILDCARD), new ArrayList<>(tags));
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
/*     */   @Deprecated
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   @NotNull
/*     */   static ListBinaryTag of(@NotNull BinaryTagType<? extends BinaryTag> type, @NotNull List<BinaryTag> tags) {
/* 133 */     return listBinaryTag(type, tags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Collector<BinaryTag, ?, ListBinaryTag> toListTag() {
/* 143 */     return toListTag(null);
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
/*     */   static Collector<BinaryTag, ?, ListBinaryTag> toListTag(@Nullable ListBinaryTag initial) {
/* 156 */     return Collector.of(
/* 157 */         (initial == null) ? ListBinaryTag::builder : (() -> builder().add(initial)), ListTagSetter::add, (l, r) -> l.add(r.build()), Builder::build, new Collector.Characteristics[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default BinaryTagType<ListBinaryTag> type() {
/* 166 */     return BinaryTagTypes.LIST;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   default BinaryTagType<? extends BinaryTag> listType() {
/* 178 */     return elementType();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte getByte(int index) {
/* 244 */     return getByte(index, (byte)0);
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
/*     */   default byte getByte(int index, byte defaultValue) {
/* 256 */     BinaryTag tag = get(index);
/* 257 */     if (tag.type().numeric()) {
/* 258 */       return ((NumberBinaryTag)tag).byteValue();
/*     */     }
/* 260 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default short getShort(int index) {
/* 271 */     return getShort(index, (short)0);
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
/*     */   default short getShort(int index, short defaultValue) {
/* 283 */     BinaryTag tag = get(index);
/* 284 */     if (tag.type().numeric()) {
/* 285 */       return ((NumberBinaryTag)tag).shortValue();
/*     */     }
/* 287 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default int getInt(int index) {
/* 298 */     return getInt(index, 0);
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
/*     */   default int getInt(int index, int defaultValue) {
/* 310 */     BinaryTag tag = get(index);
/* 311 */     if (tag.type().numeric()) {
/* 312 */       return ((NumberBinaryTag)tag).intValue();
/*     */     }
/* 314 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default long getLong(int index) {
/* 325 */     return getLong(index, 0L);
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
/*     */   default long getLong(int index, long defaultValue) {
/* 337 */     BinaryTag tag = get(index);
/* 338 */     if (tag.type().numeric()) {
/* 339 */       return ((NumberBinaryTag)tag).longValue();
/*     */     }
/* 341 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default float getFloat(int index) {
/* 352 */     return getFloat(index, 0.0F);
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
/*     */   default float getFloat(int index, float defaultValue) {
/* 364 */     BinaryTag tag = get(index);
/* 365 */     if (tag.type().numeric()) {
/* 366 */       return ((NumberBinaryTag)tag).floatValue();
/*     */     }
/* 368 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default double getDouble(int index) {
/* 379 */     return getDouble(index, 0.0D);
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
/*     */   default double getDouble(int index, double defaultValue) {
/* 391 */     BinaryTag tag = get(index);
/* 392 */     if (tag.type().numeric()) {
/* 393 */       return ((NumberBinaryTag)tag).doubleValue();
/*     */     }
/* 395 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte[] getByteArray(int index) {
/* 406 */     BinaryTag tag = get(index);
/* 407 */     if (tag.type() == BinaryTagTypes.BYTE_ARRAY) {
/* 408 */       return ((ByteArrayBinaryTag)tag).value();
/*     */     }
/* 410 */     return new byte[0];
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
/*     */   default byte[] getByteArray(int index, byte[] defaultValue) {
/* 422 */     BinaryTag tag = get(index);
/* 423 */     if (tag.type() == BinaryTagTypes.BYTE_ARRAY) {
/* 424 */       return ((ByteArrayBinaryTag)tag).value();
/*     */     }
/* 426 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default String getString(int index) {
/* 437 */     return getString(index, "");
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
/*     */   default String getString(int index, @NotNull String defaultValue) {
/* 449 */     BinaryTag tag = get(index);
/* 450 */     if (tag.type() == BinaryTagTypes.STRING) {
/* 451 */       return ((StringBinaryTag)tag).value();
/*     */     }
/* 453 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default ListBinaryTag getList(int index) {
/* 464 */     return getList(index, null, empty());
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
/*     */   default ListBinaryTag getList(int index, @Nullable BinaryTagType<?> elementType) {
/* 476 */     return getList(index, elementType, empty());
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
/*     */   default ListBinaryTag getList(int index, @NotNull ListBinaryTag defaultValue) {
/* 488 */     return getList(index, null, defaultValue);
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
/*     */   default ListBinaryTag getList(int index, @Nullable BinaryTagType<?> elementType, @NotNull ListBinaryTag defaultValue) {
/* 503 */     BinaryTag tag = get(index);
/* 504 */     if (tag.type() == BinaryTagTypes.LIST) {
/* 505 */       ListBinaryTag list = (ListBinaryTag)tag;
/* 506 */       if (elementType == null || list.elementType() == elementType) {
/* 507 */         return list;
/*     */       }
/*     */     } 
/* 510 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default CompoundBinaryTag getCompound(int index) {
/* 521 */     return getCompound(index, CompoundBinaryTag.empty());
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
/*     */   default CompoundBinaryTag getCompound(int index, @NotNull CompoundBinaryTag defaultValue) {
/* 533 */     BinaryTag tag = get(index);
/* 534 */     if (tag.type() == BinaryTagTypes.COMPOUND) {
/* 535 */       return (CompoundBinaryTag)tag;
/*     */     }
/* 537 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default int[] getIntArray(int index) {
/* 548 */     BinaryTag tag = get(index);
/* 549 */     if (tag.type() == BinaryTagTypes.INT_ARRAY) {
/* 550 */       return ((IntArrayBinaryTag)tag).value();
/*     */     }
/* 552 */     return new int[0];
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
/*     */   default int[] getIntArray(int index, int[] defaultValue) {
/* 564 */     BinaryTag tag = get(index);
/* 565 */     if (tag.type() == BinaryTagTypes.INT_ARRAY) {
/* 566 */       return ((IntArrayBinaryTag)tag).value();
/*     */     }
/* 568 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default long[] getLongArray(int index) {
/* 579 */     BinaryTag tag = get(index);
/* 580 */     if (tag.type() == BinaryTagTypes.LONG_ARRAY) {
/* 581 */       return ((LongArrayBinaryTag)tag).value();
/*     */     }
/* 583 */     return new long[0];
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
/*     */   default long[] getLongArray(int index, long[] defaultValue) {
/* 595 */     BinaryTag tag = get(index);
/* 596 */     if (tag.type() == BinaryTagTypes.LONG_ARRAY) {
/* 597 */       return ((LongArrayBinaryTag)tag).value();
/*     */     }
/* 599 */     return defaultValue;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   BinaryTagType<? extends BinaryTag> elementType();
/*     */   
/*     */   int size();
/*     */   
/*     */   boolean isEmpty();
/*     */   
/*     */   @NotNull
/*     */   BinaryTag get(int paramInt);
/*     */   
/*     */   @NotNull
/*     */   ListBinaryTag set(int paramInt, @NotNull BinaryTag paramBinaryTag, @Nullable Consumer<? super BinaryTag> paramConsumer);
/*     */   
/*     */   @NotNull
/*     */   ListBinaryTag remove(int paramInt, @Nullable Consumer<? super BinaryTag> paramConsumer);
/*     */   
/*     */   @NotNull
/*     */   Stream<BinaryTag> stream();
/*     */   
/*     */   @NotNull
/*     */   ListBinaryTag unwrapHeterogeneity();
/*     */   
/*     */   @NotNull
/*     */   ListBinaryTag wrapHeterogeneity();
/*     */   
/*     */   public static interface Builder<T extends BinaryTag> extends ListTagSetter<Builder<T>, T> {
/*     */     @NotNull
/*     */     ListBinaryTag build();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\ListBinaryTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */