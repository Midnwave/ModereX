/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
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
/*    */ public class StaticRecipeDisplayType<T extends RecipeDisplay<?>>
/*    */   extends AbstractMappedEntity
/*    */   implements RecipeDisplayType<T>
/*    */ {
/*    */   private final PacketWrapper.Reader<T> reader;
/*    */   private final PacketWrapper.Writer<T> writer;
/*    */   
/*    */   @Internal
/*    */   public StaticRecipeDisplayType(@Nullable TypesBuilderData data, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/* 38 */     super(data);
/* 39 */     this.reader = reader;
/* 40 */     this.writer = writer;
/*    */   }
/*    */ 
/*    */   
/*    */   public T read(PacketWrapper<?> wrapper) {
/* 45 */     return (T)this.reader.apply(wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(PacketWrapper<?> wrapper, T display) {
/* 50 */     this.writer.accept(wrapper, display);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\StaticRecipeDisplayType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */