/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.gson;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Keyed;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.nbt.api.BinaryTagHolder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Codec;
/*     */ import java.util.Collections;
/*     */ import java.util.UUID;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BackwardCompatUtil
/*     */ {
/*     */   public static final boolean IS_4_10_0_OR_NEWER;
/*     */   public static final boolean IS_4_13_0_OR_NEWER;
/*     */   public static final boolean IS_4_15_0_OR_NEWER;
/*     */   public static final boolean IS_4_17_0_OR_NEWER;
/*     */   public static final boolean IS_4_18_0_OR_NEWER;
/*     */   public static final boolean IS_4_22_0_OR_NEWER;
/*     */   
/*     */   static {
/*  44 */     boolean is4_10_0OrNewer = false;
/*     */     
/*     */     try {
/*  47 */       BinaryTagHolder.binaryTagHolder("");
/*  48 */       is4_10_0OrNewer = true;
/*  49 */     } catch (Throwable throwable) {}
/*     */     
/*  51 */     IS_4_10_0_OR_NEWER = is4_10_0OrNewer;
/*     */     
/*  53 */     boolean is4_13_0OrNewer = false;
/*     */     
/*     */     try {
/*  56 */       Component.translatable().fallback("");
/*  57 */       is4_13_0OrNewer = true;
/*  58 */     } catch (Throwable throwable) {}
/*     */     
/*  60 */     IS_4_13_0_OR_NEWER = is4_13_0OrNewer;
/*     */     
/*  62 */     boolean is4_15_0OrNewer = false;
/*     */     try {
/*  64 */       Component.translatable().arguments(new ComponentLike[] { (ComponentLike)Component.empty() });
/*  65 */       is4_15_0OrNewer = true;
/*  66 */     } catch (Throwable throwable) {}
/*     */     
/*  68 */     IS_4_15_0_OR_NEWER = is4_15_0OrNewer;
/*     */     
/*  70 */     boolean is4_17_0OrNewer = false;
/*     */     
/*     */     try {
/*  73 */       HoverEvent.ShowItem.showItem((Keyed)Key.key("air"), 1, Collections.emptyMap());
/*  74 */       is4_17_0OrNewer = true;
/*  75 */     } catch (Throwable throwable) {}
/*     */     
/*  77 */     IS_4_17_0_OR_NEWER = is4_17_0OrNewer;
/*     */     
/*  79 */     boolean is4_18_0OrNewer = false;
/*     */     
/*     */     try {
/*  82 */       Style.empty().shadowColor();
/*  83 */       is4_18_0OrNewer = true;
/*  84 */     } catch (Throwable throwable) {}
/*     */     
/*  86 */     IS_4_18_0_OR_NEWER = is4_18_0OrNewer;
/*     */     
/*  88 */     boolean is4_22_0OrNewer = false;
/*     */     
/*     */     try {
/*  91 */       ClickEvent.custom(Key.key("test"), "{test:true}");
/*  92 */       is4_22_0OrNewer = true;
/*  93 */     } catch (Throwable throwable) {}
/*     */     
/*  95 */     IS_4_22_0_OR_NEWER = is4_22_0OrNewer;
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
/*     */   public static HoverEvent.ShowItem createShowItem(@NotNull Key item, int count, @Nullable BinaryTagHolder nbt) {
/*     */     try {
/* 110 */       return HoverEvent.ShowItem.showItem(item, count, nbt);
/* 111 */     } catch (NoSuchMethodError ignored) {
/* 112 */       return HoverEvent.ShowItem.of(item, count, nbt);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static HoverEvent.ShowEntity createShowEntity(@NotNull Key type, @NotNull UUID id, @Nullable Component name) {
/*     */     try {
/* 118 */       return HoverEvent.ShowEntity.showEntity(type, id, name);
/* 119 */     } catch (NoSuchMethodError ignored) {
/* 120 */       return HoverEvent.ShowEntity.of(type, id, name);
/*     */     } 
/*     */   }
/*     */   @NotNull
/*     */   public static <D, E, DX extends Throwable, EX extends Throwable> Codec<D, E, DX, EX> createCodec(@NotNull Codec.Decoder<D, E, DX> decoder, @NotNull Codec.Encoder<D, E, EX> encoder) {
/*     */     try {
/* 126 */       return Codec.codec(decoder, encoder);
/* 127 */     } catch (NoSuchMethodError ignored) {
/* 128 */       return Codec.of(decoder, encoder);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static interface ShowAchievementToComponent {
/*     */     @NotNull
/*     */     Component convert(@NotNull String param1String);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\serializer\gson\BackwardCompatUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */