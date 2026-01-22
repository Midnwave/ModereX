/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*    */ public class TypesBuilderData
/*    */ {
/*    */   protected final TypesBuilder typesBuilder;
/*    */   protected final int[] data;
/*    */   protected final ResourceLocation name;
/*    */   
/*    */   @Deprecated
/*    */   public TypesBuilderData(ResourceLocation name, int[] data) {
/* 33 */     this(new TypesBuilder("", true), name, data);
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public TypesBuilderData(TypesBuilder typesBuilder, ResourceLocation name, int[] data) {
/* 38 */     this.typesBuilder = typesBuilder;
/* 39 */     this.name = name;
/* 40 */     this.data = data;
/*    */   }
/*    */   
/*    */   public int getId(ClientVersion version) {
/* 44 */     return this.data[this.typesBuilder.getDataIndex(version)];
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public int[] getData() {
/* 49 */     return this.data;
/*    */   }
/*    */   
/*    */   public ResourceLocation getName() {
/* 53 */     return this.name;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\mappings\TypesBuilderData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */