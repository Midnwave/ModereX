/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemAttributeModifiers;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureIndexUtil;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import org.jspecify.annotations.NullMarked;
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
/*     */ @NullMarked
/*     */ public final class EnchantmentDefinition
/*     */ {
/*     */   private final MappedEntitySet<ItemType> supportedItems;
/*     */   private final Optional<MappedEntitySet<ItemType>> primaryItems;
/*     */   private final int weight;
/*     */   private final int maxLevel;
/*     */   private final EnchantmentCost minCost;
/*     */   private final EnchantmentCost maxCost;
/*     */   private final int anvilCost;
/*     */   private final List<ItemAttributeModifiers.EquipmentSlotGroup> slots;
/*     */   
/*     */   public EnchantmentDefinition(MappedEntitySet<ItemType> supportedItems, Optional<MappedEntitySet<ItemType>> primaryItems, int weight, int maxLevel, EnchantmentCost minCost, EnchantmentCost maxCost, int anvilCost, List<ItemAttributeModifiers.EquipmentSlotGroup> slots) {
/*  61 */     this.supportedItems = supportedItems;
/*  62 */     this.primaryItems = primaryItems;
/*  63 */     this.weight = weight;
/*  64 */     this.maxLevel = maxLevel;
/*  65 */     this.minCost = minCost;
/*  66 */     this.maxCost = maxCost;
/*  67 */     this.anvilCost = anvilCost;
/*  68 */     this.slots = slots;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static EnchantmentDefinition decode(NBT nbt, ClientVersion version) {
/*  73 */     return decode(nbt, PacketWrapper.createDummyWrapper(version));
/*     */   }
/*     */   public static EnchantmentDefinition decode(NBT nbt, PacketWrapper<?> wrapper) {
/*     */     List<ItemAttributeModifiers.EquipmentSlotGroup> slots;
/*  77 */     NBTCompound compound = (NBTCompound)nbt;
/*  78 */     MappedEntitySet<ItemType> supportedItems = (MappedEntitySet<ItemType>)compound.getOrThrow("supported_items", (tag, ew) -> MappedEntitySet.decode(tag, ew, (IRegistry)ItemTypes.getRegistry()), wrapper);
/*     */     
/*  80 */     Optional<MappedEntitySet<ItemType>> primaryItems = Optional.ofNullable((MappedEntitySet<ItemType>)compound.getOrNull("primary_items", (tag, ew) -> MappedEntitySet.decode(tag, ew, (IRegistry)ItemTypes.getRegistry()), wrapper));
/*     */     
/*  82 */     int weight = compound.getNumberTagOrThrow("weight").getAsInt();
/*  83 */     int maxLevel = compound.getNumberTagOrThrow("max_level").getAsInt();
/*  84 */     EnchantmentCost minCost = (EnchantmentCost)compound.getOrThrow("min_cost", EnchantmentCost::decode, wrapper);
/*  85 */     EnchantmentCost maxCost = (EnchantmentCost)compound.getOrThrow("max_cost", EnchantmentCost::decode, wrapper);
/*  86 */     int anvilCost = compound.getNumberTagOrThrow("anvil_cost").getAsInt();
/*     */     
/*  88 */     NBT slotsTag = compound.getTagOrThrow("slots");
/*     */     
/*  90 */     if (slotsTag instanceof NBTList) {
/*  91 */       NBTList<?> slotsTagList = (NBTList)slotsTag;
/*  92 */       slots = new ArrayList<>(slotsTagList.size());
/*  93 */       for (NBT tag : slotsTagList.getTags()) {
/*  94 */         String slotGroupId = ((NBTString)tag).getValue();
/*  95 */         slots.add((ItemAttributeModifiers.EquipmentSlotGroup)AdventureIndexUtil.indexValueOrThrow(ItemAttributeModifiers.EquipmentSlotGroup.ID_INDEX, slotGroupId));
/*     */       } 
/*     */     } else {
/*  98 */       String slotGroupId = ((NBTString)slotsTag).getValue();
/*  99 */       ItemAttributeModifiers.EquipmentSlotGroup slotGroup = (ItemAttributeModifiers.EquipmentSlotGroup)AdventureIndexUtil.indexValueOrThrow(ItemAttributeModifiers.EquipmentSlotGroup.ID_INDEX, slotGroupId);
/* 100 */       slots = Collections.singletonList(slotGroup);
/*     */     } 
/*     */     
/* 103 */     return new EnchantmentDefinition(supportedItems, primaryItems, weight, maxLevel, minCost, maxCost, anvilCost, slots);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static NBT encode(EnchantmentDefinition definition, ClientVersion version) {
/* 109 */     return encode(PacketWrapper.createDummyWrapper(version), definition);
/*     */   }
/*     */   
/*     */   public static NBT encode(PacketWrapper<?> wrapper, EnchantmentDefinition definition) {
/* 113 */     NBTCompound compound = new NBTCompound();
/* 114 */     encode(compound, wrapper, definition);
/* 115 */     return (NBT)compound;
/*     */   }
/*     */   
/*     */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, EnchantmentDefinition definition) {
/* 119 */     NBTList<NBTString> slotsTag = NBTList.createStringList();
/* 120 */     for (ItemAttributeModifiers.EquipmentSlotGroup slot : definition.slots) {
/* 121 */       slotsTag.addTag((NBT)new NBTString(slot.getId()));
/*     */     }
/*     */     
/* 124 */     compound.set("supported_items", definition.supportedItems, MappedEntitySet::encode, wrapper);
/* 125 */     definition.primaryItems.ifPresent(set -> compound.set("primary_items", set, MappedEntitySet::encode, wrapper));
/*     */     
/* 127 */     compound.setTag("weight", (NBT)new NBTInt(definition.weight));
/* 128 */     compound.setTag("max_level", (NBT)new NBTInt(definition.maxLevel));
/* 129 */     compound.set("min_cost", definition.minCost, EnchantmentCost::encode, wrapper);
/* 130 */     compound.set("max_cost", definition.maxCost, EnchantmentCost::encode, wrapper);
/* 131 */     compound.setTag("anvil_cost", (NBT)new NBTInt(definition.anvilCost));
/* 132 */     compound.setTag("slots", (NBT)slotsTag);
/*     */   }
/*     */   
/*     */   public MappedEntitySet<ItemType> getSupportedItems() {
/* 136 */     return this.supportedItems;
/*     */   }
/*     */   
/*     */   public Optional<MappedEntitySet<ItemType>> getPrimaryItems() {
/* 140 */     return this.primaryItems;
/*     */   }
/*     */   
/*     */   public int getWeight() {
/* 144 */     return this.weight;
/*     */   }
/*     */   
/*     */   public int getMaxLevel() {
/* 148 */     return this.maxLevel;
/*     */   }
/*     */   
/*     */   public EnchantmentCost getMinCost() {
/* 152 */     return this.minCost;
/*     */   }
/*     */   
/*     */   public EnchantmentCost getMaxCost() {
/* 156 */     return this.maxCost;
/*     */   }
/*     */   
/*     */   public int getAnvilCost() {
/* 160 */     return this.anvilCost;
/*     */   }
/*     */   
/*     */   public List<ItemAttributeModifiers.EquipmentSlotGroup> getSlots() {
/* 164 */     return this.slots;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 169 */     if (this == obj) return true; 
/* 170 */     if (!(obj instanceof EnchantmentDefinition)) return false; 
/* 171 */     EnchantmentDefinition that = (EnchantmentDefinition)obj;
/* 172 */     if (this.weight != that.weight) return false; 
/* 173 */     if (this.maxLevel != that.maxLevel) return false; 
/* 174 */     if (this.anvilCost != that.anvilCost) return false; 
/* 175 */     if (!this.supportedItems.equals(that.supportedItems)) return false; 
/* 176 */     if (!this.primaryItems.equals(that.primaryItems)) return false; 
/* 177 */     if (!this.minCost.equals(that.minCost)) return false; 
/* 178 */     if (!this.maxCost.equals(that.maxCost)) return false; 
/* 179 */     return this.slots.equals(that.slots);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 184 */     return Objects.hash(new Object[] { this.supportedItems, this.primaryItems, Integer.valueOf(this.weight), Integer.valueOf(this.maxLevel), this.minCost, this.maxCost, Integer.valueOf(this.anvilCost), this.slots });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 189 */     return "EnchantmentDefinition{supportedItems=" + this.supportedItems + ", primaryItems=" + this.primaryItems + ", weight=" + this.weight + ", maxLevel=" + this.maxLevel + ", minCost=" + this.minCost + ", maxCost=" + this.maxCost + ", anvilCost=" + this.anvilCost + ", slots=" + this.slots + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\enchantment\EnchantmentDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */