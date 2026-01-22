/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Node
/*     */ {
/*     */   public static final byte TYPE_MASK = 3;
/*     */   public static final byte TYPE_ROOT = 0;
/*     */   public static final byte TYPE_LITERAL = 1;
/*     */   public static final byte TYPE_ARGUMENT = 2;
/*     */   public static final byte FLAG_MASK = -4;
/*     */   public static final byte FLAG_EXECUTABLE = 4;
/*     */   public static final byte FLAG_REDIRECT = 8;
/*     */   public static final byte FLAG_CUSTOM_SUGGESTIONS = 16;
/*     */   public static final byte FLAG_RESTRICTED = 32;
/*     */   private byte flags;
/*     */   private List<Integer> children;
/*     */   private int redirectNodeIndex;
/*     */   private Optional<String> name;
/*     */   private Optional<Parsers.Parser> parser;
/*     */   private Optional<List<Object>> properties;
/*     */   private Optional<ResourceLocation> suggestionsType;
/*     */   
/*     */   public Node(byte flags, List<Integer> children, int redirectNodeIndex, @Nullable String name, @Nullable Integer parserID, @Nullable List<Object> properties, @Nullable ResourceLocation suggestionsType) {
/*  57 */     this(flags, children, redirectNodeIndex, name, (parserID == null) ? null : Parsers.getById(
/*  58 */           PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(), parserID.intValue()), properties, suggestionsType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Node(byte flags, List<Integer> children, int redirectNodeIndex, @Nullable String name, @Nullable Parsers.Parser parser, @Nullable List<Object> properties, @Nullable ResourceLocation suggestionsType) {
/*  66 */     this.flags = flags;
/*  67 */     this.children = children;
/*  68 */     this.redirectNodeIndex = redirectNodeIndex;
/*  69 */     this.name = Optional.ofNullable(name);
/*  70 */     this.parser = Optional.ofNullable(parser);
/*  71 */     this.properties = Optional.ofNullable(properties);
/*  72 */     this.suggestionsType = Optional.ofNullable(suggestionsType);
/*     */   }
/*     */   
/*     */   public byte getFlags() {
/*  76 */     return this.flags;
/*     */   }
/*     */   
/*     */   public void setFlags(byte flags) {
/*  80 */     this.flags = flags;
/*     */   }
/*     */   
/*     */   public List<Integer> getChildren() {
/*  84 */     return this.children;
/*     */   }
/*     */   
/*     */   public void setChildren(List<Integer> children) {
/*  88 */     this.children = children;
/*     */   }
/*     */   
/*     */   public int getRedirectNodeIndex() {
/*  92 */     return this.redirectNodeIndex;
/*     */   }
/*     */   
/*     */   public void setRedirectNodeIndex(int redirectNodeIndex) {
/*  96 */     this.redirectNodeIndex = redirectNodeIndex;
/*     */   }
/*     */   
/*     */   public Optional<String> getName() {
/* 100 */     return this.name;
/*     */   }
/*     */   
/*     */   public void setName(Optional<String> name) {
/* 104 */     this.name = name;
/*     */   }
/*     */   
/*     */   public Optional<Parsers.Parser> getParser() {
/* 108 */     return this.parser;
/*     */   }
/*     */   
/*     */   public void setParser(Optional<Parsers.Parser> parser) {
/* 112 */     this.parser = parser;
/*     */   }
/*     */   
/*     */   public Optional<Integer> getParserID() {
/* 116 */     return this.parser.map(parser -> Integer.valueOf(parser.getId(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion())));
/*     */   }
/*     */ 
/*     */   
/*     */   public void setParserID(Optional<Integer> parserID) {
/* 121 */     this.parser = parserID.map(id -> Parsers.getById(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(), id.intValue()));
/*     */   }
/*     */ 
/*     */   
/*     */   public Optional<List<Object>> getProperties() {
/* 126 */     return this.properties;
/*     */   }
/*     */   
/*     */   public void setProperties(Optional<List<Object>> properties) {
/* 130 */     this.properties = properties;
/*     */   }
/*     */   
/*     */   public Optional<ResourceLocation> getSuggestionsType() {
/* 134 */     return this.suggestionsType;
/*     */   }
/*     */   
/*     */   public void setSuggestionsType(Optional<ResourceLocation> suggestionsType) {
/* 138 */     this.suggestionsType = suggestionsType;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */