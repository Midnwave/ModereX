/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data.RecipeData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StaticRecipeSerializer<T extends RecipeData>
/*    */   extends AbstractMappedEntity
/*    */   implements RecipeSerializer<T>
/*    */ {
/*    */   private final PacketWrapper.Reader<T> reader;
/*    */   private final PacketWrapper.Writer<T> writer;
/*    */   @Nullable
/*    */   private final RecipeType legacyType;
/*    */   
/*    */   @Internal
/*    */   public StaticRecipeSerializer(@Nullable TypesBuilderData data, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer, @Nullable RecipeType legacyType) {
/* 41 */     super(data);
/* 42 */     this.reader = reader;
/* 43 */     this.writer = writer;
/* 44 */     this.legacyType = legacyType;
/*    */   }
/*    */ 
/*    */   
/*    */   public RecipeType getLegacyType() {
/* 49 */     return this.legacyType;
/*    */   }
/*    */ 
/*    */   
/*    */   public T read(PacketWrapper<?> wrapper) {
/* 54 */     return (T)this.reader.apply(wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(PacketWrapper<?> wrapper, T data) {
/* 59 */     this.writer.accept(wrapper, data);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\StaticRecipeSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */