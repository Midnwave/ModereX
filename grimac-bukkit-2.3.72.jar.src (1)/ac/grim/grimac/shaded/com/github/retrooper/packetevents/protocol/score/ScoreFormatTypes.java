/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.score;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Collection;
/*     */ import java.util.Objects;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Function;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ScoreFormatTypes
/*     */ {
/*  34 */   private static final VersionedRegistry<ScoreFormatType<?>> REGISTRY = new VersionedRegistry("number_format_type");
/*     */   
/*  36 */   public static final ScoreFormatType<BlankScoreFormat> BLANK = define("blank", BlankScoreFormat::read, BlankScoreFormat::write);
/*     */   
/*  38 */   public static final ScoreFormatType<StyledScoreFormat> STYLED = define("styled", StyledScoreFormat::read, StyledScoreFormat::write);
/*     */   
/*  40 */   public static final ScoreFormatType<FixedScoreFormat> FIXED = define("fixed", FixedScoreFormat::read, FixedScoreFormat::write);
/*     */ 
/*     */   
/*     */   static {
/*  44 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<ScoreFormatType<?>> getRegistry() {
/*  51 */     return REGISTRY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<ScoreFormatType<?>> values() {
/*  60 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static ScoreFormat read(PacketWrapper<?> wrapper) {
/*  65 */     return ScoreFormat.readTyped(wrapper);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static void write(PacketWrapper<?> wrapper, ScoreFormat format) {
/*  70 */     ScoreFormat.writeTyped(wrapper, format);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @Internal
/*     */   public static <T extends ScoreFormat> ScoreFormatType<T> define(int id, String name, Class<T> formatClass, Function<PacketWrapper<?>, T> reader, BiConsumer<PacketWrapper<?>, T> writer) {
/*  80 */     Objects.requireNonNull(reader); Objects.requireNonNull(writer); return define(name, reader::apply, writer::accept);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static <T extends ScoreFormat> ScoreFormatType<T> define(String name, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/*  87 */     return (ScoreFormatType<T>)REGISTRY.define(name, data -> new StaticScoreFormatType<>(data, reader, writer));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public static ScoreFormatType<?> getById(ClientVersion version, int id) {
/*  92 */     return (ScoreFormatType)REGISTRY.getById(version, id);
/*     */   }
/*     */   @Nullable
/*     */   public static ScoreFormatType<?> getByName(String name) {
/*  96 */     return (ScoreFormatType)REGISTRY.getByName(name);
/*     */   }
/*     */   @Nullable
/*     */   public static ScoreFormatType<?> getByName(ResourceLocation name) {
/* 100 */     return (ScoreFormatType)REGISTRY.getByName(name);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\score\ScoreFormatTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */