/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLong;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public class ItemContainerLoot
/*    */ {
/*    */   private ResourceLocation lootTable;
/*    */   private long seed;
/*    */   
/*    */   public ItemContainerLoot(ResourceLocation lootTable, long seed) {
/* 36 */     this.lootTable = lootTable;
/* 37 */     this.seed = seed;
/*    */   }
/*    */   
/*    */   public static ItemContainerLoot read(PacketWrapper<?> wrapper) {
/* 41 */     NBTCompound compound = wrapper.readNBT();
/* 42 */     ResourceLocation lootTable = new ResourceLocation(compound.getStringTagValueOrThrow("loot_table"));
/* 43 */     NBTNumber seedTag = compound.getNumberTagOrNull("seed");
/* 44 */     long seed = (seedTag == null) ? 0L : seedTag.getAsLong();
/* 45 */     return new ItemContainerLoot(lootTable, seed);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemContainerLoot loot) {
/* 49 */     NBTCompound compound = new NBTCompound();
/* 50 */     compound.setTag("loot_table", (NBT)new NBTString(loot.lootTable.toString()));
/* 51 */     if (loot.seed != 0L) {
/* 52 */       compound.setTag("seed", (NBT)new NBTLong(loot.seed));
/*    */     }
/* 54 */     wrapper.writeNBT(compound);
/*    */   }
/*    */   
/*    */   public ResourceLocation getLootTable() {
/* 58 */     return this.lootTable;
/*    */   }
/*    */   
/*    */   public void setLootTable(ResourceLocation lootTable) {
/* 62 */     this.lootTable = lootTable;
/*    */   }
/*    */   
/*    */   public long getSeed() {
/* 66 */     return this.seed;
/*    */   }
/*    */   
/*    */   public void setSeed(long seed) {
/* 70 */     this.seed = seed;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 75 */     if (this == obj) return true; 
/* 76 */     if (!(obj instanceof ItemContainerLoot)) return false; 
/* 77 */     ItemContainerLoot that = (ItemContainerLoot)obj;
/* 78 */     if (this.seed != that.seed) return false; 
/* 79 */     return this.lootTable.equals(that.lootTable);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 84 */     return Objects.hash(new Object[] { this.lootTable, Long.valueOf(this.seed) });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemContainerLoot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */