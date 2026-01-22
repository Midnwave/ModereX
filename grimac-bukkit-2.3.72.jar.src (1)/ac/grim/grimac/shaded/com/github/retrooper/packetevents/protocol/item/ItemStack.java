/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.PatchableComponentMap;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTShort;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.MathUtil;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.GlobalRegistryHolder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistryHolder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Function;
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
/*     */ 
/*     */ @NullMarked
/*     */ public class ItemStack
/*     */ {
/*  67 */   public static final ItemStack EMPTY = builder().nbt(new NBTCompound()).build();
/*     */ 
/*     */ 
/*     */   
/*     */   private final ClientVersion version;
/*     */ 
/*     */ 
/*     */   
/*     */   private final IRegistryHolder registryHolder;
/*     */ 
/*     */ 
/*     */   
/*     */   private final ItemType type;
/*     */ 
/*     */ 
/*     */   
/*     */   private int amount;
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   private NBTCompound nbt;
/*     */ 
/*     */ 
/*     */   
/*     */   private PatchableComponentMap components;
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   private int legacyData;
/*     */ 
/*     */ 
/*     */   
/*     */   private ItemStack(ItemType type, int amount, NBTCompound nbt, PatchableComponentMap components, int legacyData, ClientVersion version, IRegistryHolder registryHolder) {
/* 101 */     this.type = type;
/* 102 */     this.amount = amount;
/* 103 */     this.nbt = nbt;
/* 104 */     this.components = components;
/* 105 */     this.legacyData = legacyData;
/* 106 */     this.version = version;
/* 107 */     this.registryHolder = registryHolder;
/*     */   }
/*     */   
/*     */   public static ItemStack decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 111 */     return decode(nbt, wrapper.getServerVersion().toClientVersion());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static ItemStack decode(NBT nbt, ClientVersion version) {
/* 116 */     if (nbt instanceof NBTString) {
/* 117 */       ResourceLocation resourceLocation = new ResourceLocation(((NBTString)nbt).getValue());
/* 118 */       return builder().type(ItemTypes.getByName(resourceLocation.toString())).build();
/*     */     } 
/* 120 */     NBTCompound compound = (NBTCompound)nbt;
/* 121 */     Builder builder = builder();
/*     */ 
/*     */ 
/*     */     
/* 125 */     ResourceLocation itemName = (ResourceLocation)((Optional)Optional.<String>ofNullable(compound.getStringTagValueOrNull("id")).map(Optional::of).orElseGet(() -> Optional.ofNullable(compound.getStringTagValueOrNull("item")))).map(ResourceLocation::new).orElseThrow(() -> new IllegalArgumentException("No item type specified: " + compound.getTags().keySet()));
/*     */     
/* 127 */     builder.type(ItemTypes.getByName(itemName.toString()));
/* 128 */     builder.nbt(compound.getCompoundTagOrNull("tag"));
/*     */ 
/*     */ 
/*     */     
/* 132 */     Objects.requireNonNull(builder); ((Optional)Optional.<NBTNumber>ofNullable(compound.getNumberTagOrNull("Count")).map(Optional::of).orElseGet(() -> Optional.ofNullable(compound.getNumberTagOrNull("count")))).map(NBTNumber::getAsInt).ifPresent(builder::amount);
/*     */ 
/*     */ 
/*     */     
/* 136 */     return builder.build();
/*     */   }
/*     */   
/*     */   public static NBT encode(PacketWrapper<?> wrapper, ItemStack itemStack) {
/* 140 */     return encodeForParticle(itemStack, wrapper.getServerVersion().toClientVersion());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static NBT encodeForParticle(ItemStack itemStack, ClientVersion version) {
/* 145 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/*     */ 
/*     */       
/* 148 */       boolean simple = (itemStack.isEmpty() || itemStack.components == null || itemStack.components.getPatches().isEmpty());
/* 149 */       if (simple) {
/* 150 */         return (NBT)new NBTString(itemStack.type.getName().toString());
/*     */       }
/*     */     } 
/*     */     
/* 154 */     NBTCompound compound = new NBTCompound();
/* 155 */     compound.setTag("id", (NBT)new NBTString(itemStack.type.getName().toString()));
/* 156 */     if (version.isOlderThan(ClientVersion.V_1_20_5)) {
/* 157 */       compound.setTag("Count", (NBT)new NBTInt(itemStack.getAmount()));
/* 158 */       if (itemStack.nbt != null) {
/* 159 */         compound.setTag("tag", (NBT)itemStack.nbt);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 165 */     return (NBT)compound;
/*     */   }
/*     */   
/*     */   public int getMaxStackSize() {
/* 169 */     if (this.version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/* 170 */       return ((Integer)getComponentOr(ComponentTypes.MAX_STACK_SIZE, Integer.valueOf(1))).intValue();
/*     */     }
/* 172 */     return getType().getMaxAmount();
/*     */   }
/*     */   
/*     */   public boolean isStackable() {
/* 176 */     return (getMaxStackSize() > 1 && (!isDamageableItem() || !isDamaged()));
/*     */   }
/*     */   
/*     */   public boolean isDamageableItem() {
/* 180 */     if (this.version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/* 181 */       return (hasComponent(ComponentTypes.MAX_DAMAGE) && 
/* 182 */         !hasComponent(ComponentTypes.UNBREAKABLE_MODERN) && 
/* 183 */         hasComponent(ComponentTypes.DAMAGE));
/*     */     }
/* 185 */     return (!isEmpty() && getMaxDamage() > 0 && (this.nbt == null || 
/* 186 */       !this.nbt.getBoolean("Unbreakable")));
/*     */   }
/*     */   
/*     */   public boolean isDamaged() {
/* 190 */     return (isDamageableItem() && getDamageValue() > 0);
/*     */   }
/*     */   
/*     */   public int getDamageValue() {
/* 194 */     if (this.version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/* 195 */       int value = ((Integer)getComponentOr(ComponentTypes.DAMAGE, Integer.valueOf(0))).intValue();
/* 196 */       return MathUtil.clamp(value, 0, getMaxDamage());
/* 197 */     }  if (this.version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 198 */       NBTNumber damage = (this.nbt != null) ? this.nbt.getNumberTagOrNull("Damage") : null;
/* 199 */       return (damage == null) ? 0 : damage.getAsInt();
/*     */     } 
/* 201 */     return Math.max(0, this.legacyData);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDamageValue(int damage) {
/* 206 */     if (this.version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/* 207 */       setComponent(ComponentTypes.DAMAGE, Integer.valueOf(MathUtil.clamp(damage, 0, getMaxDamage())));
/* 208 */     } else if (this.version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 209 */       getOrCreateTag().setTag("Damage", (NBT)new NBTInt(Math.max(0, damage)));
/*     */     } else {
/* 211 */       this.legacyData = Math.max(0, damage);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getMaxDamage() {
/* 216 */     if (this.version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/* 217 */       return ((Integer)getComponentOr(ComponentTypes.MAX_DAMAGE, Integer.valueOf(0))).intValue();
/*     */     }
/* 219 */     return getType().getMaxDurability();
/*     */   }
/*     */ 
/*     */   
/*     */   public NBTCompound getOrCreateTag() {
/* 224 */     if (this.nbt == null) {
/* 225 */       this.nbt = new NBTCompound();
/*     */     }
/* 227 */     return this.nbt;
/*     */   }
/*     */   
/*     */   public ItemType getType() {
/* 231 */     if (this.version.isNewerThanOrEquals(ClientVersion.V_1_11))
/*     */     {
/* 233 */       return isEmpty() ? ItemTypes.AIR : this.type;
/*     */     }
/* 235 */     return this.type;
/*     */   }
/*     */   
/*     */   public int getAmount() {
/* 239 */     if (this.version.isNewerThanOrEquals(ClientVersion.V_1_11))
/*     */     {
/* 241 */       return isEmpty() ? 0 : this.amount;
/*     */     }
/* 243 */     return this.amount;
/*     */   }
/*     */   
/*     */   public void shrink(int amount) {
/* 247 */     this.amount -= amount;
/*     */   }
/*     */   
/*     */   public void grow(int amount) {
/* 251 */     this.amount += amount;
/*     */   }
/*     */   
/*     */   public void setAmount(int amount) {
/* 255 */     this.amount = amount;
/*     */   }
/*     */   
/*     */   public ItemStack split(int toTake) {
/* 259 */     int i = Math.min(toTake, getAmount());
/* 260 */     ItemStack itemstack = copy();
/* 261 */     itemstack.setAmount(i);
/* 262 */     shrink(i);
/* 263 */     return itemstack;
/*     */   }
/*     */   
/*     */   public ItemStack copy() {
/* 267 */     if (isEmpty()) {
/* 268 */       return EMPTY;
/*     */     }
/* 270 */     return new ItemStack(this.type, this.amount, 
/*     */         
/* 272 */         (this.nbt == null) ? null : this.nbt.copy(), 
/* 273 */         (this.components == null) ? null : this.components.copy(), this.legacyData, this.version, this.registryHolder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NBTCompound getNBT() {
/* 280 */     return this.nbt;
/*     */   }
/*     */   
/*     */   public void setNBT(NBTCompound nbt) {
/* 284 */     this.nbt = nbt;
/*     */   }
/*     */   
/*     */   public <T> T getComponentOr(ComponentType<T> type, T otherValue) {
/* 288 */     if (hasComponentPatches()) {
/* 289 */       return (T)getComponents().getOr(type, otherValue);
/*     */     }
/* 291 */     return (T)getType().getComponents().getOr(type, otherValue);
/*     */   }
/*     */   
/*     */   public <T> Optional<T> getComponent(ComponentType<T> type) {
/* 295 */     if (hasComponentPatches()) {
/* 296 */       return getComponents().getOptional(type);
/*     */     }
/* 298 */     return getType().getComponents().getOptional(type);
/*     */   }
/*     */   
/*     */   public <T> void setComponent(ComponentType<T> type, T value) {
/* 302 */     getComponents().set(type, value);
/*     */   }
/*     */   
/*     */   public <T> void unsetComponent(ComponentType<T> type) {
/* 306 */     getComponents().unset(type);
/*     */   }
/*     */   
/*     */   public <T> void setComponent(ComponentType<T> type, Optional<T> value) {
/* 310 */     getComponents().set(type, value);
/*     */   }
/*     */   
/*     */   public boolean hasComponent(ComponentType<?> type) {
/* 314 */     if (hasComponentPatches()) {
/* 315 */       return getComponents().has(type);
/*     */     }
/* 317 */     return getType().getComponents().has(type);
/*     */   }
/*     */   
/*     */   public boolean hasComponentPatches() {
/* 321 */     return (this.components != null && !this.components.getPatches().isEmpty());
/*     */   }
/*     */   
/*     */   public PatchableComponentMap getComponents() {
/* 325 */     if (this.components == null) {
/* 326 */       this
/* 327 */         .components = new PatchableComponentMap(this.type.getComponents(), new HashMap<>(4));
/*     */     }
/* 329 */     return this.components;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setComponents(PatchableComponentMap components) {
/* 336 */     this.components = components;
/*     */   }
/*     */   
/*     */   public int getLegacyData() {
/* 340 */     return this.legacyData;
/*     */   }
/*     */   
/*     */   public void setLegacyData(int legacyData) {
/* 344 */     this.legacyData = legacyData;
/*     */   }
/*     */   
/*     */   public boolean isEnchantable() {
/* 348 */     return isEnchantable(this.version);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean isEnchantable(ClientVersion version) {
/* 356 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_20_5))
/*     */     {
/* 358 */       return (hasComponent(ComponentTypes.ENCHANTABLE) && !isEnchanted(version));
/*     */     }
/*     */     
/* 361 */     if (this.type == ItemTypes.BOOK)
/* 362 */       return (getAmount() == 1); 
/* 363 */     if (this.type == ItemTypes.ENCHANTED_BOOK) {
/* 364 */       return false;
/*     */     }
/* 366 */     return (getMaxStackSize() == 1 && canBeDepleted() && !isEnchanted(version));
/*     */   }
/*     */   
/*     */   public boolean isEnchanted() {
/* 370 */     return isEnchanted(this.version);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean isEnchanted(ClientVersion version) {
/* 378 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_20_5))
/*     */     {
/* 380 */       return (!((ItemEnchantments)getComponentOr(ComponentTypes.ENCHANTMENTS, ItemEnchantments.EMPTY)).isEmpty() || 
/* 381 */         !((ItemEnchantments)getComponentOr(ComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)).isEmpty());
/*     */     }
/*     */     
/* 384 */     if (this.nbt != null) {
/* 385 */       String tagName = getEnchantmentsTagName(version);
/* 386 */       NBTList<NBTCompound> enchantments = this.nbt.getCompoundListTagOrNull(tagName);
/* 387 */       return (enchantments != null && !enchantments.getTags().isEmpty());
/*     */     } 
/* 389 */     return false;
/*     */   }
/*     */   
/*     */   public List<Enchantment> getEnchantments() {
/* 393 */     return getEnchantments(this.version);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public List<Enchantment> getEnchantments(ClientVersion version) {
/* 401 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/*     */       
/* 403 */       ItemEnchantments enchantmentsComp = getComponentOr(ComponentTypes.ENCHANTMENTS, ItemEnchantments.EMPTY);
/* 404 */       ItemEnchantments storedEnchantmentsComp = getComponentOr(ComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
/*     */ 
/*     */       
/* 407 */       List<Enchantment> enchantmentsList = new ArrayList<>(enchantmentsComp.getEnchantmentCount() + storedEnchantmentsComp.getEnchantmentCount());
/* 408 */       for (Map.Entry<EnchantmentType, Integer> enchantment : (Iterable<Map.Entry<EnchantmentType, Integer>>)enchantmentsComp) {
/* 409 */         enchantmentsList.add(new Enchantment(enchantment.getKey(), ((Integer)enchantment.getValue()).intValue()));
/*     */       }
/* 411 */       for (Map.Entry<EnchantmentType, Integer> enchantment : (Iterable<Map.Entry<EnchantmentType, Integer>>)storedEnchantmentsComp) {
/* 412 */         enchantmentsList.add(new Enchantment(enchantment.getKey(), ((Integer)enchantment.getValue()).intValue()));
/*     */       }
/* 414 */       return enchantmentsList;
/*     */     } 
/*     */     
/* 417 */     if (this.nbt != null) {
/* 418 */       String tagName = getEnchantmentsTagName(version);
/* 419 */       NBTList<NBTCompound> nbtList = this.nbt.getCompoundListTagOrNull(tagName);
/* 420 */       if (nbtList != null) {
/* 421 */         List<NBTCompound> compounds = nbtList.getTags();
/* 422 */         List<Enchantment> enchantments = new ArrayList<>(compounds.size());
/*     */         
/* 424 */         for (NBTCompound compound : compounds) {
/* 425 */           EnchantmentType type = getEnchantmentTypeFromTag(compound, version);
/*     */           
/* 427 */           if (type != null) {
/* 428 */             NBTNumber levelTag = compound.getNumberTagOrNull("lvl");
/* 429 */             if (levelTag != null) {
/* 430 */               int level = levelTag.getAsInt();
/* 431 */               Enchantment enchantment = Enchantment.builder().type(type).level(level).build();
/* 432 */               enchantments.add(enchantment);
/*     */             } 
/*     */           } 
/*     */         } 
/* 436 */         return enchantments;
/*     */       } 
/*     */     } 
/*     */     
/* 440 */     return new ArrayList<>(0);
/*     */   }
/*     */   
/*     */   public int getEnchantmentLevel(EnchantmentType enchantment) {
/* 444 */     return getEnchantmentLevel(enchantment, this.version);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getEnchantmentLevel(EnchantmentType enchantment, ClientVersion version) {
/* 452 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/*     */       
/* 454 */       ItemEnchantments enchantmentsComp = getComponentOr(ComponentTypes.ENCHANTMENTS, ItemEnchantments.EMPTY);
/* 455 */       if (!enchantmentsComp.isEmpty()) {
/* 456 */         int level = enchantmentsComp.getEnchantmentLevel(enchantment);
/* 457 */         if (level > 0) {
/* 458 */           return level;
/*     */         }
/*     */       } 
/* 461 */       ItemEnchantments storedEnchantmentsComp = getComponentOr(ComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
/* 462 */       if (!storedEnchantmentsComp.isEmpty()) {
/* 463 */         return storedEnchantmentsComp.getEnchantmentLevel(enchantment);
/*     */       }
/* 465 */       return 0;
/*     */     } 
/*     */ 
/*     */     
/* 469 */     if (this.nbt != null) {
/* 470 */       String tagName = getEnchantmentsTagName(version);
/* 471 */       NBTList<NBTCompound> nbtList = this.nbt.getCompoundListTagOrNull(tagName);
/* 472 */       if (nbtList != null) {
/* 473 */         for (NBTCompound base : nbtList.getTags()) {
/* 474 */           EnchantmentType type = getEnchantmentTypeFromTag(base, version);
/* 475 */           if (Objects.equals(type, enchantment)) {
/* 476 */             NBTNumber nbtLevel = base.getNumberTagOrNull("lvl");
/* 477 */             return (nbtLevel != null) ? nbtLevel.getAsInt() : 0;
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 483 */     return 0;
/*     */   }
/*     */   
/*     */   private static EnchantmentType getEnchantmentTypeFromTag(NBTCompound tag, ClientVersion version) {
/* 487 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 488 */       String id = tag.getStringTagValueOrNull("id");
/* 489 */       return EnchantmentTypes.getByName(id);
/*     */     } 
/* 491 */     NBTShort idTag = (NBTShort)tag.getTagOfTypeOrNull("id", NBTShort.class);
/* 492 */     return (idTag != null) ? EnchantmentTypes.getById(version, idTag.getAsInt()) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnchantments(List<Enchantment> enchantments) {
/* 497 */     setEnchantments(enchantments, this.version);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setEnchantments(List<Enchantment> enchantments, ClientVersion version) {
/* 505 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/*     */       
/* 507 */       Map<EnchantmentType, Integer> enchantmentsMap = new HashMap<>(enchantments.size());
/* 508 */       for (Enchantment enchantment : enchantments) {
/* 509 */         enchantmentsMap.put(enchantment.getType(), Integer.valueOf(enchantment.getLevel()));
/*     */       }
/*     */       
/* 512 */       ComponentType<ItemEnchantments> componentType = hasComponent(ComponentTypes.STORED_ENCHANTMENTS) ? ComponentTypes.STORED_ENCHANTMENTS : ComponentTypes.ENCHANTMENTS;
/* 513 */       Optional<ItemEnchantments> prevEnchantments = getComponent(componentType);
/* 514 */       boolean showInTooltip = ((Boolean)prevEnchantments.<Boolean>map(ItemEnchantments::isShowInTooltip).orElse(Boolean.valueOf(true))).booleanValue();
/* 515 */       setComponent(componentType, new ItemEnchantments(enchantmentsMap, showInTooltip));
/*     */     } else {
/*     */       
/* 518 */       String tagName = getEnchantmentsTagName(version);
/* 519 */       if (enchantments.isEmpty()) {
/*     */         
/* 521 */         if (this.nbt != null && this.nbt.getTagOrNull(tagName) != null) {
/* 522 */           this.nbt.removeTag(tagName);
/*     */         }
/*     */       } else {
/* 525 */         List<NBTCompound> list = new ArrayList<>();
/* 526 */         for (Enchantment enchantment : enchantments) {
/* 527 */           NBTCompound compound = new NBTCompound();
/* 528 */           if (version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 529 */             compound.setTag("id", (NBT)new NBTString(enchantment.getType().getName().toString()));
/*     */           } else {
/* 531 */             compound.setTag("id", (NBT)new NBTShort((short)enchantment.getType().getId(version)));
/*     */           } 
/* 533 */           compound.setTag("lvl", (NBT)new NBTShort((short)enchantment.getLevel()));
/* 534 */           list.add(compound);
/*     */         } 
/* 536 */         getOrCreateTag().setTag(tagName, (NBT)new NBTList(NBTType.COMPOUND, list));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String getEnchantmentsTagName(ClientVersion version) {
/* 543 */     String tagName = version.isNewerThanOrEquals(ClientVersion.V_1_13) ? "Enchantments" : "ench";
/* 544 */     if (this.type == ItemTypes.ENCHANTED_BOOK) {
/* 545 */       tagName = "StoredEnchantments";
/*     */     }
/* 547 */     return tagName;
/*     */   }
/*     */   
/*     */   public boolean canBeDepleted() {
/* 551 */     return (getMaxDamage() > 0);
/*     */   }
/*     */   
/*     */   public boolean is(ItemType type) {
/* 555 */     return (getType() == type);
/*     */   }
/*     */   
/*     */   public static boolean isSameItemSameTags(ItemStack stack, ItemStack otherStack) {
/* 559 */     return isSameItemSameComponents(stack, otherStack);
/*     */   }
/*     */   
/*     */   public static boolean isSameItemSameComponents(ItemStack stack, ItemStack otherStack) {
/* 563 */     if (stack.version != otherStack.version) {
/* 564 */       throw new IllegalArgumentException("Can't compare two ItemStacks across versions: " + stack.version + " != " + otherStack.version);
/*     */     }
/* 566 */     if (stack.version.isNewerThanOrEquals(ClientVersion.V_1_20_5))
/*     */     {
/* 568 */       return (stack.is(otherStack.getType()) && ((stack
/* 569 */         .isEmpty() && otherStack.isEmpty()) || stack
/* 570 */         .getComponents().equals(otherStack.getComponents())));
/*     */     }
/*     */     
/* 573 */     return (stack.is(otherStack.getType()) && ((stack
/* 574 */       .isEmpty() && otherStack.isEmpty()) || 
/* 575 */       Objects.equals(stack.nbt, otherStack.nbt)));
/*     */   }
/*     */   
/*     */   public static boolean tagMatches(ItemStack stack, ItemStack otherStack) {
/* 579 */     if (stack == otherStack)
/* 580 */       return true; 
/* 581 */     if (stack == null)
/* 582 */       return otherStack.isEmpty(); 
/* 583 */     if (otherStack == null) {
/* 584 */       return stack.isEmpty();
/*     */     }
/* 586 */     if (stack.version != otherStack.version) {
/* 587 */       throw new IllegalArgumentException("Can't compare two ItemStacks across versions: " + stack.version + " != " + otherStack.version);
/*     */     }
/* 589 */     if (stack.version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/* 590 */       return stack.getComponents().equals(otherStack.getComponents());
/*     */     }
/* 592 */     return Objects.equals(stack.nbt, otherStack.nbt);
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 596 */     boolean baseEmpty = (this.type == ItemTypes.AIR || this.amount <= 0);
/* 597 */     if (this.version.isOlderThanOrEquals(ClientVersion.V_1_12_2)) {
/* 598 */       return (baseEmpty || this.legacyData < -32768 || this.legacyData > 65536);
/*     */     }
/* 600 */     return baseEmpty;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientVersion getVersion() {
/* 605 */     return this.version;
/*     */   }
/*     */   
/*     */   public IRegistryHolder getRegistryHolder() {
/* 609 */     return this.registryHolder;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 614 */     if (this == obj)
/* 615 */       return true; 
/* 616 */     if (obj instanceof ItemStack) {
/* 617 */       ItemStack itemStack = (ItemStack)obj;
/* 618 */       return (this.type.equals(itemStack.type) && this.amount == itemStack.amount && 
/*     */         
/* 620 */         Objects.equals(this.nbt, itemStack.nbt) && 
/* 621 */         Objects.equals(this.components, itemStack.components) && this.legacyData == itemStack.legacyData);
/*     */     } 
/*     */     
/* 624 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 629 */     if (isEmpty()) {
/* 630 */       return "ItemStack[EMPTY]";
/*     */     }
/* 632 */     return "ItemStack[" + 
/* 633 */       getAmount() + "x/" + getMaxStackSize() + "x " + this.type.getName() + (
/* 634 */       (this.nbt != null) ? (", nbt tag names=" + this.nbt.getTagNames()) : "") + (
/* 635 */       (this.legacyData != -1) ? (", legacy data=" + this.legacyData) : "") + (
/* 636 */       (this.components != null) ? (", components=" + this.components.getPatches()) : "") + "]";
/*     */   }
/*     */ 
/*     */   
/*     */   public static Builder builder() {
/* 641 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */   {
/* 646 */     private ClientVersion version = PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
/* 647 */     private IRegistryHolder registryHolder = GlobalRegistryHolder.INSTANCE;
/*     */     
/* 649 */     private ItemType type = ItemTypes.AIR;
/* 650 */     private int amount = 1;
/* 651 */     private NBTCompound nbt = null;
/* 652 */     private PatchableComponentMap components = null;
/* 653 */     private int legacyData = -1;
/*     */     
/*     */     public Builder type(ItemType type) {
/* 656 */       this.type = type;
/* 657 */       return this;
/*     */     }
/*     */     
/*     */     public Builder amount(int amount) {
/* 661 */       this.amount = amount;
/* 662 */       return this;
/*     */     }
/*     */     
/*     */     public Builder nbt(NBTCompound nbt) {
/* 666 */       this.nbt = nbt;
/* 667 */       return this;
/*     */     }
/*     */     
/*     */     public Builder nbt(String key, NBT tag) {
/* 671 */       if (this.nbt == null) {
/* 672 */         this.nbt = new NBTCompound();
/*     */       }
/* 674 */       this.nbt.setTag(key, tag);
/* 675 */       return this;
/*     */     }
/*     */     
/*     */     public Builder components(PatchableComponentMap components) {
/* 679 */       this.components = components;
/* 680 */       return this;
/*     */     }
/*     */     
/*     */     public <T> Builder component(ComponentType<T> type, T value) {
/* 684 */       if (this.components == null) {
/* 685 */         this.components = new PatchableComponentMap(this.type.getComponents(this.version));
/*     */       }
/* 687 */       this.components.set(type, value);
/* 688 */       return this;
/*     */     }
/*     */     
/*     */     public Builder legacyData(int legacyData) {
/* 692 */       this.legacyData = legacyData;
/* 693 */       return this;
/*     */     }
/*     */     
/*     */     public Builder user(User user) {
/* 697 */       return version(user.getPacketVersion()).registryHolder((IRegistryHolder)user);
/*     */     }
/*     */     
/*     */     public Builder wrapper(PacketWrapper<?> wrapper) {
/* 701 */       ClientVersion version = wrapper.getServerVersion().toClientVersion();
/* 702 */       return version(version).registryHolder(wrapper.getRegistryHolder());
/*     */     }
/*     */     
/*     */     public Builder version(ClientVersion version) {
/* 706 */       this.version = version;
/* 707 */       return this;
/*     */     }
/*     */     
/*     */     public Builder registryHolder(IRegistryHolder registryHolder) {
/* 711 */       this.registryHolder = registryHolder;
/* 712 */       return this;
/*     */     }
/*     */     
/*     */     public ItemStack build() {
/* 716 */       return new ItemStack(this.type, this.amount, this.nbt, this.components, this.legacyData, this.version, this.registryHolder);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\ItemStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */