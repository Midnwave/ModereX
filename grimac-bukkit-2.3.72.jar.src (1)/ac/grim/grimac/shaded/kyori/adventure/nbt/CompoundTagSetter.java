/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Map;
/*     */ import java.util.function.Consumer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface CompoundTagSetter<R>
/*     */ {
/*     */   @NotNull
/*     */   R put(@NotNull String paramString, @NotNull BinaryTag paramBinaryTag);
/*     */   
/*     */   @NotNull
/*     */   R put(@NotNull CompoundBinaryTag paramCompoundBinaryTag);
/*     */   
/*     */   @NotNull
/*     */   R put(@NotNull Map<String, ? extends BinaryTag> paramMap);
/*     */   
/*     */   @NotNull
/*     */   default R remove(@NotNull String key) {
/*  74 */     return remove(key, null);
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
/*     */   R remove(@NotNull String paramString, @Nullable Consumer<? super BinaryTag> paramConsumer);
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
/*     */   default R putBoolean(@NotNull String key, boolean value) {
/*  98 */     return put(key, value ? ByteBinaryTag.ONE : ByteBinaryTag.ZERO);
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
/*     */   default R putByte(@NotNull String key, byte value) {
/* 110 */     return put(key, ByteBinaryTag.byteBinaryTag(value));
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
/*     */   default R putShort(@NotNull String key, short value) {
/* 122 */     return put(key, ShortBinaryTag.shortBinaryTag(value));
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
/*     */   default R putInt(@NotNull String key, int value) {
/* 134 */     return put(key, IntBinaryTag.intBinaryTag(value));
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
/*     */   default R putLong(@NotNull String key, long value) {
/* 146 */     return put(key, LongBinaryTag.longBinaryTag(value));
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
/*     */   default R putFloat(@NotNull String key, float value) {
/* 158 */     return put(key, FloatBinaryTag.floatBinaryTag(value));
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
/*     */   default R putDouble(@NotNull String key, double value) {
/* 170 */     return put(key, DoubleBinaryTag.doubleBinaryTag(value));
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
/*     */   default R putByteArray(@NotNull String key, byte[] value) {
/* 182 */     return put(key, ByteArrayBinaryTag.byteArrayBinaryTag(value));
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
/*     */   default R putString(@NotNull String key, @NotNull String value) {
/* 194 */     return put(key, StringBinaryTag.stringBinaryTag(value));
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
/*     */   default R putIntArray(@NotNull String key, int[] value) {
/* 206 */     return put(key, IntArrayBinaryTag.intArrayBinaryTag(value));
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
/*     */   default R putLongArray(@NotNull String key, long[] value) {
/* 218 */     return put(key, LongArrayBinaryTag.longArrayBinaryTag(value));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\CompoundTagSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */