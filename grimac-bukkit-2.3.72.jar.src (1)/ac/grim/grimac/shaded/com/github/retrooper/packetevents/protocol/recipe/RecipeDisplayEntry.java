/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.category.RecipeBookCategories;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.category.RecipeBookCategory;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.RecipeDisplay;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.BiFunction;
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
/*     */ public final class RecipeDisplayEntry
/*     */ {
/*     */   private RecipeDisplayId id;
/*     */   private RecipeDisplay<?> display;
/*     */   @Nullable
/*     */   private Integer group;
/*     */   private RecipeBookCategory category;
/*     */   @Nullable
/*     */   private List<MappedEntitySet<ItemType>> ingredients;
/*     */   
/*     */   public RecipeDisplayEntry(RecipeDisplayId id, RecipeDisplay<?> display, @Nullable Integer group, RecipeBookCategory category, @Nullable List<MappedEntitySet<ItemType>> ingredients) {
/*  48 */     this.id = id;
/*  49 */     this.display = display;
/*  50 */     this.group = group;
/*  51 */     this.category = category;
/*  52 */     this.ingredients = ingredients;
/*     */   }
/*     */   
/*     */   public static RecipeDisplayEntry read(PacketWrapper<?> wrapper) {
/*  56 */     RecipeDisplayId id = RecipeDisplayId.read(wrapper);
/*  57 */     RecipeDisplay<?> display = RecipeDisplay.read(wrapper);
/*  58 */     Integer group = wrapper.readNullableVarInt();
/*  59 */     RecipeBookCategory category = (RecipeBookCategory)wrapper.readMappedEntity((IRegistry)RecipeBookCategories.getRegistry());
/*  60 */     List<MappedEntitySet<ItemType>> ingredients = (List<MappedEntitySet<ItemType>>)wrapper.readOptional(ew -> ew.readList(()));
/*     */     
/*  62 */     return new RecipeDisplayEntry(id, display, group, category, ingredients);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, RecipeDisplayEntry entry) {
/*  66 */     RecipeDisplayId.write(wrapper, entry.id);
/*  67 */     RecipeDisplay.write(wrapper, entry.display);
/*  68 */     wrapper.writeNullableVarInt(entry.group);
/*  69 */     wrapper.writeMappedEntity((MappedEntity)entry.category);
/*  70 */     wrapper.writeOptional(entry.ingredients, (ew, list) -> ew.writeList(list, MappedEntitySet::write));
/*     */   }
/*     */ 
/*     */   
/*     */   public RecipeDisplayId getId() {
/*  75 */     return this.id;
/*     */   }
/*     */   
/*     */   public void setId(RecipeDisplayId id) {
/*  79 */     this.id = id;
/*     */   }
/*     */   
/*     */   public RecipeDisplay<?> getDisplay() {
/*  83 */     return this.display;
/*     */   }
/*     */   
/*     */   public void setDisplay(RecipeDisplay<?> display) {
/*  87 */     this.display = display;
/*     */   }
/*     */   @Nullable
/*     */   public Integer getGroup() {
/*  91 */     return this.group;
/*     */   }
/*     */   
/*     */   public void setGroup(@Nullable Integer group) {
/*  95 */     this.group = group;
/*     */   }
/*     */   
/*     */   public RecipeBookCategory getCategory() {
/*  99 */     return this.category;
/*     */   }
/*     */   
/*     */   public void setCategory(RecipeBookCategory category) {
/* 103 */     this.category = category;
/*     */   }
/*     */   @Nullable
/*     */   public List<MappedEntitySet<ItemType>> getIngredients() {
/* 107 */     return this.ingredients;
/*     */   }
/*     */   
/*     */   public void setIngredients(@Nullable List<MappedEntitySet<ItemType>> ingredients) {
/* 111 */     this.ingredients = ingredients;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 116 */     if (this == obj) return true; 
/* 117 */     if (!(obj instanceof RecipeDisplayEntry)) return false; 
/* 118 */     RecipeDisplayEntry that = (RecipeDisplayEntry)obj;
/* 119 */     if (this.id != that.id) return false; 
/* 120 */     if (!this.display.equals(that.display)) return false; 
/* 121 */     if (!Objects.equals(this.group, that.group)) return false; 
/* 122 */     if (!this.category.equals(that.category)) return false; 
/* 123 */     return Objects.equals(this.ingredients, that.ingredients);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 128 */     return Objects.hash(new Object[] { this.id, this.display, this.group, this.category, this.ingredients });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 133 */     return "RecipeDisplayEntry{id=" + this.id + ", display=" + this.display + ", group=" + this.group + ", category=" + this.category + ", ingredients=" + this.ingredients + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\RecipeDisplayEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */