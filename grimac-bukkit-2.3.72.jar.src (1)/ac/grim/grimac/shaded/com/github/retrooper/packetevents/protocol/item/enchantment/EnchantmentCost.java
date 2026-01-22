/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
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
/*    */ 
/*    */ public final class EnchantmentCost
/*    */ {
/*    */   private final int base;
/*    */   private final int perLevelAboveFirst;
/*    */   
/*    */   public EnchantmentCost(int base, int perLevelAboveFirst) {
/* 35 */     this.base = base;
/* 36 */     this.perLevelAboveFirst = perLevelAboveFirst;
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public static EnchantmentCost decode(NBT nbt, ClientVersion version) {
/* 41 */     return decode(nbt, PacketWrapper.createDummyWrapper(version));
/*    */   }
/*    */   
/*    */   public static EnchantmentCost decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 45 */     NBTCompound compound = (NBTCompound)nbt;
/* 46 */     int base = compound.getNumberTagOrThrow("base").getAsInt();
/* 47 */     int perLevelAboveFirst = compound.getNumberTagOrThrow("per_level_above_first").getAsInt();
/* 48 */     return new EnchantmentCost(base, perLevelAboveFirst);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public static NBT encode(EnchantmentCost cost, ClientVersion version) {
/* 53 */     return encode(PacketWrapper.createDummyWrapper(version), cost);
/*    */   }
/*    */   
/*    */   public static NBT encode(PacketWrapper<?> wrapper, EnchantmentCost cost) {
/* 57 */     NBTCompound compound = new NBTCompound();
/* 58 */     compound.setTag("base", (NBT)new NBTInt(cost.base));
/* 59 */     compound.setTag("per_level_above_first", (NBT)new NBTInt(cost.perLevelAboveFirst));
/* 60 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   public int getBase() {
/* 64 */     return this.base;
/*    */   }
/*    */   
/*    */   public int getPerLevelAboveFirst() {
/* 68 */     return this.perLevelAboveFirst;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 73 */     if (this == obj) return true; 
/* 74 */     if (!(obj instanceof EnchantmentCost)) return false; 
/* 75 */     EnchantmentCost that = (EnchantmentCost)obj;
/* 76 */     if (this.base != that.base) return false; 
/* 77 */     return (this.perLevelAboveFirst == that.perLevelAboveFirst);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 82 */     return Objects.hash(new Object[] { Integer.valueOf(this.base), Integer.valueOf(this.perLevelAboveFirst) });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 87 */     return "EnchantmentCost{base=" + this.base + ", perLevelAboveFirst=" + this.perLevelAboveFirst + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\enchantment\EnchantmentCost.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */