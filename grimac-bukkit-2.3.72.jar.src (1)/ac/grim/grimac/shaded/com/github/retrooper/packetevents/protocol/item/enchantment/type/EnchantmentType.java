/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.EnchantEffectComponentTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.IComponentMap;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.StaticComponentMap;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.EnchantmentDefinition;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntityRefSet;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.util.Optional;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @NullMarked
/*    */ public interface EnchantmentType
/*    */   extends MappedEntity, CopyableEntity<EnchantmentType>, DeepComparableEntity
/*    */ {
/*    */   @Deprecated
/*    */   static EnchantmentType decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/* 56 */     return decode(nbt, PacketWrapper.createDummyWrapper(version), data);
/*    */   }
/*    */   
/*    */   static EnchantmentType decode(NBT nbt, PacketWrapper<?> wrapper, @Nullable TypesBuilderData data) {
/* 60 */     NBTCompound compound = (NBTCompound)nbt;
/* 61 */     Component description = (Component)compound.getOrThrow("description", (NbtDecoder)wrapper.getSerializers(), wrapper);
/* 62 */     EnchantmentDefinition definition = EnchantmentDefinition.decode((NBT)compound, wrapper);
/*    */ 
/*    */     
/* 65 */     MappedEntityRefSet<EnchantmentType> exclusiveSet = Optional.<NBT>ofNullable(compound.getTagOrNull("exclusive_set")).map(tag -> MappedEntitySet.decodeRefSet(tag, wrapper)).orElseGet(MappedEntitySet::createEmpty);
/*    */ 
/*    */ 
/*    */     
/* 69 */     StaticComponentMap effects = Optional.<NBT>ofNullable(compound.getTagOrNull("effects")).map(tag -> IComponentMap.decode(tag, wrapper, (IRegistry)EnchantEffectComponentTypes.getRegistry())).orElse(StaticComponentMap.EMPTY);
/* 70 */     return new StaticEnchantmentType(data, description, definition, exclusiveSet, effects);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   static NBT encode(EnchantmentType type, ClientVersion version) {
/* 75 */     return encode(type, PacketWrapper.createDummyWrapper(version));
/*    */   }
/*    */   
/*    */   static NBT encode(EnchantmentType type, PacketWrapper<?> wrapper) {
/* 79 */     NBTCompound compound = new NBTCompound();
/* 80 */     EnchantmentDefinition.encode(compound, wrapper, type.getDefinition());
/* 81 */     compound.set("description", type.getDescription(), (NbtEncoder)wrapper.getSerializers(), wrapper);
/* 82 */     if (!type.getExclusiveRefSet().isEmpty()) {
/* 83 */       compound.set("exclusive_set", type.getExclusiveRefSet(), MappedEntitySet::encodeRefSet, wrapper);
/*    */     }
/* 85 */     if (!type.getEffects().isEmpty()) {
/* 86 */       compound.set("effects", type.getEffects(), IComponentMap::encode, wrapper);
/*    */     }
/* 88 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   Component getDescription();
/*    */   
/*    */   EnchantmentDefinition getDefinition();
/*    */   
/*    */   MappedEntitySet<EnchantmentType> getExclusiveSet();
/*    */   
/*    */   MappedEntityRefSet<EnchantmentType> getExclusiveRefSet();
/*    */   
/*    */   StaticComponentMap getEffects();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\enchantment\type\EnchantmentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */