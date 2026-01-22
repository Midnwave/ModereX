/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.RecipeData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Obsolete
/*     */ public class Recipe<T extends RecipeData>
/*     */ {
/*     */   private final ResourceLocation key;
/*     */   private final RecipeSerializer<T> serializer;
/*     */   private final T data;
/*     */   
/*     */   @Deprecated
/*     */   public Recipe(RecipeType serializer, String key, RecipeData data) {
/*  40 */     this(new ResourceLocation(key), (RecipeSerializer)serializer
/*     */         
/*  42 */         .getSerializer(), (T)data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Recipe(ResourceLocation key, RecipeSerializer<T> serializer, T data) {
/*  48 */     this.key = key;
/*  49 */     this.serializer = serializer;
/*  50 */     this.data = data;
/*     */   }
/*     */   
/*     */   public static Recipe<?> read(PacketWrapper<?> wrapper) {
/*     */     ResourceLocation key;
/*     */     RecipeSerializer<?> serializer;
/*  56 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  57 */       key = wrapper.readIdentifier();
/*  58 */       serializer = (RecipeSerializer)wrapper.readMappedEntity(RecipeSerializers::getById);
/*     */     } else {
/*  60 */       serializer = RecipeSerializers.getByName(wrapper.readIdentifier().toString());
/*  61 */       key = wrapper.readIdentifier();
/*     */     } 
/*  63 */     return read(wrapper, key, serializer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T extends RecipeData> Recipe<T> read(PacketWrapper<?> wrapper, ResourceLocation key, RecipeSerializer<T> serializer) {
/*  71 */     T data = serializer.read(wrapper);
/*  72 */     return new Recipe<>(key, serializer, data);
/*     */   }
/*     */   
/*     */   public static <T extends RecipeData> void write(PacketWrapper<?> wrapper, Recipe<T> recipe) {
/*  76 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  77 */       wrapper.writeIdentifier(recipe.key);
/*  78 */       wrapper.writeMappedEntity(recipe.serializer);
/*     */     } else {
/*  80 */       wrapper.writeIdentifier(recipe.serializer.getName());
/*  81 */       wrapper.writeIdentifier(recipe.key);
/*     */     } 
/*  83 */     recipe.serializer.write(wrapper, recipe.data);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public RecipeType getType() {
/*  88 */     return this.serializer.getLegacyType();
/*     */   }
/*     */   
/*     */   public String getIdentifier() {
/*  92 */     return this.key.toString();
/*     */   }
/*     */   
/*     */   public ResourceLocation getKey() {
/*  96 */     return this.key;
/*     */   }
/*     */   
/*     */   public RecipeSerializer<T> getSerializer() {
/* 100 */     return this.serializer;
/*     */   }
/*     */   
/*     */   public T getData() {
/* 104 */     return this.data;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 109 */     return "Recipe{key=" + this.key + ", serializer=" + this.serializer + ", data=" + this.data + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\Recipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */