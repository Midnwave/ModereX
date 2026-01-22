/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Objects;
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
/*    */ public class ItemRecipes
/*    */ {
/*    */   private List<ResourceLocation> recipes;
/*    */   
/*    */   public ItemRecipes(List<ResourceLocation> recipes) {
/* 35 */     this.recipes = recipes;
/*    */   }
/*    */   
/*    */   public static ItemRecipes read(PacketWrapper<?> wrapper) {
/* 39 */     NBTList<?> recipes = (NBTList)wrapper.readNBTRaw();
/* 40 */     List<ResourceLocation> recipeKeys = new ArrayList<>(recipes.size());
/* 41 */     for (int i = 0; i < recipes.size(); i++) {
/* 42 */       NBTString tag = (NBTString)recipes.getTag(i);
/* 43 */       recipeKeys.add(new ResourceLocation(tag.getValue()));
/*    */     } 
/* 45 */     return new ItemRecipes(recipeKeys);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemRecipes recipes) {
/* 49 */     NBTList<NBTString> recipesTag = NBTList.createStringList();
/* 50 */     for (ResourceLocation recipeKey : recipes.recipes) {
/* 51 */       recipesTag.addTag((NBT)new NBTString(recipeKey.toString()));
/*    */     }
/* 53 */     wrapper.writeNBTRaw((NBT)recipesTag);
/*    */   }
/*    */   
/*    */   public void addRecipe(ResourceLocation recipeKey) {
/* 57 */     this.recipes.add(recipeKey);
/*    */   }
/*    */   
/*    */   public void removeRecipe(ResourceLocation recipeKey) {
/* 61 */     this.recipes.remove(recipeKey);
/*    */   }
/*    */   
/*    */   public List<ResourceLocation> getRecipes() {
/* 65 */     return this.recipes;
/*    */   }
/*    */   
/*    */   public void setRecipes(List<ResourceLocation> recipes) {
/* 69 */     this.recipes = recipes;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 74 */     if (this == obj) return true; 
/* 75 */     if (!(obj instanceof ItemRecipes)) return false; 
/* 76 */     ItemRecipes that = (ItemRecipes)obj;
/* 77 */     return this.recipes.equals(that.recipes);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 82 */     return Objects.hashCode(this.recipes);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemRecipes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */