/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ public class ItemBees
/*     */ {
/*     */   private List<BeeEntry> bees;
/*     */   
/*     */   public ItemBees(List<BeeEntry> bees) {
/*  32 */     this.bees = bees;
/*     */   }
/*     */   
/*     */   public static ItemBees read(PacketWrapper<?> wrapper) {
/*  36 */     List<BeeEntry> bees = wrapper.readList(BeeEntry::read);
/*  37 */     return new ItemBees(bees);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemBees bees) {
/*  41 */     wrapper.writeList(bees.bees, BeeEntry::write);
/*     */   }
/*     */   
/*     */   public void addBee(BeeEntry bee) {
/*  45 */     this.bees.add(bee);
/*     */   }
/*     */   
/*     */   public List<BeeEntry> getBees() {
/*  49 */     return this.bees;
/*     */   }
/*     */   
/*     */   public void setBees(List<BeeEntry> bees) {
/*  53 */     this.bees = bees;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  58 */     if (this == obj) return true; 
/*  59 */     if (!(obj instanceof ItemBees)) return false; 
/*  60 */     ItemBees itemBees = (ItemBees)obj;
/*  61 */     return this.bees.equals(itemBees.bees);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  66 */     return Objects.hashCode(this.bees);
/*     */   }
/*     */   
/*     */   public static class BeeEntry
/*     */   {
/*     */     private NBTCompound entityData;
/*     */     private int ticksInHive;
/*     */     private int minTicksInHive;
/*     */     
/*     */     public BeeEntry(NBTCompound entityData, int ticksInHive, int minTicksInHive) {
/*  76 */       this.entityData = entityData;
/*  77 */       this.ticksInHive = ticksInHive;
/*  78 */       this.minTicksInHive = minTicksInHive;
/*     */     }
/*     */     
/*     */     public static BeeEntry read(PacketWrapper<?> wrapper) {
/*  82 */       NBTCompound entityData = wrapper.readNBT();
/*  83 */       int ticksInHive = wrapper.readVarInt();
/*  84 */       int minTicksInHive = wrapper.readVarInt();
/*  85 */       return new BeeEntry(entityData, ticksInHive, minTicksInHive);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, BeeEntry bee) {
/*  89 */       wrapper.writeNBT(bee.entityData);
/*  90 */       wrapper.writeVarInt(bee.ticksInHive);
/*  91 */       wrapper.writeVarInt(bee.minTicksInHive);
/*     */     }
/*     */     
/*     */     public NBTCompound getEntityData() {
/*  95 */       return this.entityData;
/*     */     }
/*     */     
/*     */     public void setEntityData(NBTCompound entityData) {
/*  99 */       this.entityData = entityData;
/*     */     }
/*     */     
/*     */     public int getTicksInHive() {
/* 103 */       return this.ticksInHive;
/*     */     }
/*     */     
/*     */     public void setTicksInHive(int ticksInHive) {
/* 107 */       this.ticksInHive = ticksInHive;
/*     */     }
/*     */     
/*     */     public int getMinTicksInHive() {
/* 111 */       return this.minTicksInHive;
/*     */     }
/*     */     
/*     */     public void setMinTicksInHive(int minTicksInHive) {
/* 115 */       this.minTicksInHive = minTicksInHive;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 120 */       if (this == obj) return true; 
/* 121 */       if (!(obj instanceof BeeEntry)) return false; 
/* 122 */       BeeEntry beeEntry = (BeeEntry)obj;
/* 123 */       if (this.ticksInHive != beeEntry.ticksInHive) return false; 
/* 124 */       if (this.minTicksInHive != beeEntry.minTicksInHive) return false; 
/* 125 */       return this.entityData.equals(beeEntry.entityData);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 130 */       return Objects.hash(new Object[] { this.entityData, Integer.valueOf(this.ticksInHive), Integer.valueOf(this.minTicksInHive) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemBees.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */