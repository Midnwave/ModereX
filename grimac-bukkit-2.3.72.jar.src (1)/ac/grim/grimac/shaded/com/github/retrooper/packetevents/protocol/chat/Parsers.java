/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Function;
/*     */ import org.jspecify.annotations.NullMarked;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @NullMarked
/*     */ public final class Parsers
/*     */ {
/*  43 */   private static final VersionedRegistry<Parser> REGISTRY = new VersionedRegistry("command_argument_type");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static Parser define(String key) {
/*  50 */     return define(key, null, null);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static Parser define(String key, @Nullable Reader reader, @Nullable Writer writer) {
/*  55 */     return (Parser)REGISTRY.define(key, data -> new Parser(data, reader, writer));
/*     */   }
/*     */   
/*     */   public static Parser getByName(String name) {
/*  59 */     return (Parser)REGISTRY.getByName(name);
/*     */   }
/*     */   
/*     */   public static Parser getById(ClientVersion version, int id) {
/*  63 */     return (Parser)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*     */   public static List<Parser> getParsers() {
/*  67 */     return new ArrayList<>(REGISTRY.getEntries());
/*     */   }
/*     */   
/*     */   public static VersionedRegistry<Parser> getRegistry() {
/*  71 */     return REGISTRY;
/*     */   }
/*     */   public static final Parser BRIGADIER_FLOAT; public static final Parser BRIGADIER_DOUBLE;
/*  74 */   public static final Parser BRIGADIER_BOOL = define("brigadier:bool", null, null); public static final Parser BRIGADIER_INTEGER; static {
/*  75 */     BRIGADIER_FLOAT = define("brigadier:float", packetWrapper -> {
/*     */           byte flags = packetWrapper.readByte();
/*     */           float min = ((flags & 0x1) != 0) ? packetWrapper.readFloat() : -3.4028235E38F;
/*     */           float max = ((flags & 0x2) != 0) ? packetWrapper.readFloat() : Float.MAX_VALUE;
/*     */           return Arrays.asList(new Object[] { Byte.valueOf(flags), Float.valueOf(min), Float.valueOf(max) }, );
/*     */         }(packetWrapper, properties) -> {
/*     */           byte flags = ((Byte)properties.get(0)).byteValue();
/*     */           packetWrapper.writeByte(flags);
/*     */           if ((flags & 0x1) != 0) {
/*     */             packetWrapper.writeFloat(((Float)properties.get(1)).floatValue());
/*     */           }
/*     */           if ((flags & 0x2) != 0)
/*     */             packetWrapper.writeFloat(((Float)properties.get(2)).floatValue()); 
/*     */         });
/*  89 */     BRIGADIER_DOUBLE = define("brigadier:double", packetWrapper -> {
/*     */           byte flags = packetWrapper.readByte();
/*     */           double min = ((flags & 0x1) != 0) ? packetWrapper.readDouble() : -1.7976931348623157E308D;
/*     */           double max = ((flags & 0x2) != 0) ? packetWrapper.readDouble() : Double.MAX_VALUE;
/*     */           return Arrays.asList(new Object[] { Byte.valueOf(flags), Double.valueOf(min), Double.valueOf(max) }, );
/*     */         }(packetWrapper, properties) -> {
/*     */           byte flags = ((Byte)properties.get(0)).byteValue();
/*     */           packetWrapper.writeByte(flags);
/*     */           if ((flags & 0x1) != 0) {
/*     */             packetWrapper.writeDouble(((Double)properties.get(1)).doubleValue());
/*     */           }
/*     */           if ((flags & 0x2) != 0)
/*     */             packetWrapper.writeDouble(((Double)properties.get(2)).doubleValue()); 
/*     */         });
/* 103 */     BRIGADIER_INTEGER = define("brigadier:integer", packetWrapper -> {
/*     */           byte flags = packetWrapper.readByte();
/*     */           int min = ((flags & 0x1) != 0) ? packetWrapper.readInt() : Integer.MIN_VALUE;
/*     */           int max = ((flags & 0x2) != 0) ? packetWrapper.readInt() : Integer.MAX_VALUE;
/*     */           return Arrays.asList(new Object[] { Byte.valueOf(flags), Integer.valueOf(min), Integer.valueOf(max) }, );
/*     */         }(packetWrapper, properties) -> {
/*     */           byte flags = ((Byte)properties.get(0)).byteValue();
/*     */           packetWrapper.writeByte(flags);
/*     */           if ((flags & 0x1) != 0) {
/*     */             packetWrapper.writeInt(((Integer)properties.get(1)).intValue());
/*     */           }
/*     */           if ((flags & 0x2) != 0)
/*     */             packetWrapper.writeInt(((Integer)properties.get(2)).intValue()); 
/*     */         });
/* 117 */     BRIGADIER_LONG = define("brigadier:long", packetWrapper -> {
/*     */           byte flags = packetWrapper.readByte();
/*     */           long min = ((flags & 0x1) != 0) ? packetWrapper.readLong() : Long.MIN_VALUE;
/*     */           long max = ((flags & 0x2) != 0) ? packetWrapper.readLong() : Long.MAX_VALUE;
/*     */           return Arrays.asList(new Object[] { Byte.valueOf(flags), Long.valueOf(min), Long.valueOf(max) }, );
/*     */         }(packetWrapper, properties) -> {
/*     */           byte flags = ((Byte)properties.get(0)).byteValue();
/*     */           packetWrapper.writeByte(flags);
/*     */           if ((flags & 0x1) != 0) {
/*     */             packetWrapper.writeLong(((Long)properties.get(1)).longValue());
/*     */           }
/*     */           if ((flags & 0x2) != 0)
/*     */             packetWrapper.writeLong(((Long)properties.get(2)).longValue()); 
/*     */         });
/* 131 */     BRIGADIER_STRING = define("brigadier:string", packetWrapper -> Collections.singletonList(Integer.valueOf(packetWrapper.readVarInt())), (packetWrapper, properties) -> packetWrapper.writeVarInt(((Integer)properties.get(0)).intValue()));
/*     */ 
/*     */ 
/*     */     
/* 135 */     ENTITY = define("entity", packetWrapper -> Collections.singletonList(Byte.valueOf(packetWrapper.readByte())), (packetWrapper, properties) -> packetWrapper.writeByte(((Byte)properties.get(0)).intValue()));
/*     */   }
/*     */   public static final Parser BRIGADIER_LONG; public static final Parser BRIGADIER_STRING;
/*     */   public static final Parser ENTITY;
/* 139 */   public static final Parser GAME_PROFILE = define("game_profile", null, null);
/* 140 */   public static final Parser BLOCK_POS = define("block_pos", null, null);
/* 141 */   public static final Parser COLUMN_POS = define("column_pos", null, null);
/* 142 */   public static final Parser VEC3 = define("vec3", null, null);
/* 143 */   public static final Parser VEC2 = define("vec2", null, null);
/* 144 */   public static final Parser BLOCK_STATE = define("block_state", null, null);
/* 145 */   public static final Parser BLOCK_PREDICATE = define("block_predicate", null, null);
/* 146 */   public static final Parser ITEM_STACK = define("item_stack", null, null);
/* 147 */   public static final Parser ITEM_PREDICATE = define("item_predicate", null, null);
/* 148 */   public static final Parser COLOR = define("color", null, null);
/* 149 */   public static final Parser COMPONENT = define("component", null, null);
/* 150 */   public static final Parser STYLE = define("style", null, null);
/* 151 */   public static final Parser MESSAGE = define("message", null, null);
/* 152 */   public static final Parser NBT_COMPOUND_TAG = define("nbt_compound_tag", null, null);
/*     */   @Obsolete
/* 154 */   public static final Parser NBT = define("nbt", null, null);
/* 155 */   public static final Parser NBT_TAG = define("nbt_tag", null, null);
/* 156 */   public static final Parser NBT_PATH = define("nbt_path", null, null);
/* 157 */   public static final Parser OBJECTIVE = define("objective", null, null);
/* 158 */   public static final Parser OBJECTIVE_CRITERIA = define("objective_criteria", null, null);
/* 159 */   public static final Parser OPERATION = define("operation", null, null);
/* 160 */   public static final Parser PARTICLE = define("particle", null, null);
/* 161 */   public static final Parser ANGLE = define("angle", null, null);
/* 162 */   public static final Parser ROTATION = define("rotation", null, null);
/* 163 */   public static final Parser SCOREBOARD_SLOT = define("scoreboard_slot", null, null); public static final Parser SCORE_HOLDER; static {
/* 164 */     SCORE_HOLDER = define("score_holder", packetWrapper -> Collections.singletonList(Byte.valueOf(packetWrapper.readByte())), (packetWrapper, properties) -> packetWrapper.writeByte(((Byte)properties.get(0)).intValue()));
/*     */   }
/*     */ 
/*     */   
/* 168 */   public static final Parser SWIZZLE = define("swizzle", null, null);
/* 169 */   public static final Parser TEAM = define("team", null, null);
/* 170 */   public static final Parser ITEM_SLOT = define("item_slot", null, null);
/* 171 */   public static final Parser ITEM_SLOTS = define("item_slots", null, null);
/* 172 */   public static final Parser RESOURCE_LOCATION = define("resource_location", null, null);
/* 173 */   public static final Parser MOB_EFFECT = define("mob_effect", null, null);
/* 174 */   public static final Parser FUNCTION = define("function", null, null);
/* 175 */   public static final Parser ENTITY_ANCHOR = define("entity_anchor", null, null);
/* 176 */   public static final Parser INT_RANGE = define("int_range", null, null);
/* 177 */   public static final Parser FLOAT_RANGE = define("float_range", null, null);
/* 178 */   public static final Parser ITEM_ENCHANTMENT = define("item_enchantment", null, null);
/* 179 */   public static final Parser ENTITY_SUMMON = define("entity_summon", null, null);
/* 180 */   public static final Parser DIMENSION = define("dimension", null, null);
/* 181 */   public static final Parser GAMEMODE = define("gamemode", null, null); public static final Parser TIME; public static final Parser RESOURCE_OR_TAG; static {
/* 182 */     TIME = define("time", wrapper -> Collections.singletonList(Integer.valueOf(wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_4) ? wrapper.readInt() : 0)), (wrapper, properties) -> {
/*     */           if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_19_4)) {
/*     */             wrapper.writeInt(((Integer)properties.get(0)).intValue());
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 191 */     RESOURCE_OR_TAG = define("resource_or_tag", packetWrapper -> Collections.singletonList(packetWrapper.readIdentifier()), (packetWrapper, properties) -> packetWrapper.writeIdentifier(properties.get(0)));
/*     */ 
/*     */ 
/*     */     
/* 195 */     RESOURCE_OR_TAG_KEY = define("resource_or_tag_key", packetWrapper -> Collections.singletonList(packetWrapper.readIdentifier()), (packetWrapper, properties) -> packetWrapper.writeIdentifier(properties.get(0)));
/*     */ 
/*     */ 
/*     */     
/* 199 */     RESOURCE = define("resource", packetWrapper -> Collections.singletonList(packetWrapper.readIdentifier()), (packetWrapper, properties) -> packetWrapper.writeIdentifier(properties.get(0)));
/*     */ 
/*     */ 
/*     */     
/* 203 */     RESOURCE_KEY = define("resource_key", packetWrapper -> Collections.singletonList(packetWrapper.readIdentifier()), (packetWrapper, properties) -> packetWrapper.writeIdentifier(properties.get(0)));
/*     */   }
/*     */   public static final Parser RESOURCE_OR_TAG_KEY; public static final Parser RESOURCE;
/*     */   public static final Parser RESOURCE_KEY;
/* 207 */   public static final Parser TEMPLATE_MIRROR = define("template_mirror", null, null);
/* 208 */   public static final Parser TEMPLATE_ROTATION = define("template_rotation", null, null);
/* 209 */   public static final Parser HEIGHTMAP = define("heightmap", null, null);
/* 210 */   public static final Parser LOOT_TABLE = define("loot_table", null, null);
/* 211 */   public static final Parser LOOT_PREDICATE = define("loot_predicate", null, null);
/* 212 */   public static final Parser LOOT_MODIFIER = define("loot_modifier", null, null);
/* 213 */   public static final Parser UUID = define("uuid", null, null);
/*     */   
/*     */   public static final Parser RESOURCE_SELECTOR;
/*     */   
/*     */   static {
/* 218 */     RESOURCE_SELECTOR = define("resource_selector", wrapper -> Collections.singletonList(wrapper.readIdentifier()), (wrapper, value) -> wrapper.writeIdentifier(value.get(0)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 226 */   public static final Parser HEX_COLOR = define("hex_color", null, null);
/*     */ 
/*     */ 
/*     */   
/* 230 */   public static final Parser DIALOG = define("dialog", null, null);
/*     */   
/*     */   static {
/* 233 */     REGISTRY.unloadMappings();
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface Reader
/*     */     extends Function<PacketWrapper<?>, List<Object>> {}
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface Writer extends BiConsumer<PacketWrapper<?>, List<Object>> {}
/*     */   
/*     */   public static final class Parser extends AbstractMappedEntity {
/*     */     private final Parsers.Reader reader;
/*     */     private final Parsers.Writer writer;
/*     */     
/*     */     @Deprecated
/*     */     public Parser(String name, @Nullable Function<PacketWrapper<?>, List<Object>> read, @Nullable BiConsumer<PacketWrapper<?>, List<Object>> write) {
/* 249 */       this(new TypesBuilderData(new ResourceLocation(name), new int[0]), 
/*     */           
/* 251 */           (read == null) ? null : read::apply, 
/* 252 */           (write == null) ? null : write::accept);
/*     */     }
/*     */ 
/*     */     
/*     */     @Internal
/*     */     public Parser(@Nullable TypesBuilderData data, @Nullable Parsers.Reader reader, @Nullable Parsers.Writer writer) {
/* 258 */       super(data);
/* 259 */       this.reader = reader;
/* 260 */       this.writer = writer;
/*     */     }
/*     */     
/*     */     public Optional<List<Object>> readProperties(PacketWrapper<?> wrapper) {
/* 264 */       if (this.reader != null) {
/* 265 */         return Optional.of(this.reader.apply(wrapper));
/*     */       }
/* 267 */       return Optional.empty();
/*     */     }
/*     */     
/*     */     public void writeProperties(PacketWrapper<?> wrapper, List<Object> properties) {
/* 271 */       if (this.writer != null)
/* 272 */         this.writer.accept(wrapper, properties); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\Parsers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */