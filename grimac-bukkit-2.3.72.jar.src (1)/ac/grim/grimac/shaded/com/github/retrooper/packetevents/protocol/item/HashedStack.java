/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.HashedComponentPatchMap;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.Set;
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
/*     */ public final class HashedStack
/*     */ {
/*     */   private final ItemType item;
/*     */   private final int count;
/*     */   private final HashedComponentPatchMap components;
/*     */   
/*     */   public HashedStack(ItemType item, int count, HashedComponentPatchMap components) {
/*  37 */     this.item = item;
/*  38 */     this.count = count;
/*  39 */     this.components = components;
/*     */   }
/*     */   
/*     */   public static Optional<HashedStack> readOptional(PacketWrapper<?> wrapper) {
/*  43 */     return Optional.ofNullable(read(wrapper));
/*     */   }
/*     */   
/*     */   public static Optional<HashedStack> toOptionalFromItemStack(ItemStack itemStack) {
/*  47 */     return Optional.ofNullable(fromItemStack(itemStack));
/*     */   }
/*     */   @Nullable
/*     */   public static HashedStack read(PacketWrapper<?> wrapper) {
/*  51 */     if (!wrapper.readBoolean()) return null; 
/*  52 */     ItemType item = (ItemType)wrapper.readMappedEntity((IRegistry)ItemTypes.getRegistry());
/*  53 */     int count = wrapper.readVarInt();
/*  54 */     HashedComponentPatchMap components = HashedComponentPatchMap.read(wrapper);
/*  55 */     return new HashedStack(item, count, components);
/*     */   }
/*     */   
/*     */   public static void writeOptional(PacketWrapper<?> wrapper, Optional<HashedStack> stack) {
/*  59 */     write(wrapper, stack.orElse(null));
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, HashedStack stack) {
/*  63 */     if (stack == null) {
/*  64 */       wrapper.writeBoolean(false);
/*     */     } else {
/*  66 */       wrapper.writeBoolean(true);
/*  67 */       wrapper.writeMappedEntity((MappedEntity)stack.item);
/*  68 */       wrapper.writeVarInt(stack.count);
/*  69 */       HashedComponentPatchMap.write(wrapper, stack.components);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static HashedStack fromItemStack(ItemStack stack) {
/*  74 */     if (stack == null) return null;
/*     */     
/*  76 */     Map<ComponentType<?>, Optional<?>> patches = stack.getComponents().getPatches();
/*  77 */     Map<ComponentType<?>, Integer> addedComponents = new HashMap<>(patches.size());
/*  78 */     Set<ComponentType<?>> removedComponents = new HashSet<>(patches.size());
/*  79 */     for (Map.Entry<ComponentType<?>, Optional<?>> patch : patches.entrySet()) {
/*  80 */       if (((Optional)patch.getValue()).isPresent()) {
/*     */         
/*  82 */         addedComponents.put(patch.getKey(), Integer.valueOf(0)); continue;
/*     */       } 
/*  84 */       removedComponents.add(patch.getKey());
/*     */     } 
/*     */ 
/*     */     
/*  88 */     HashedComponentPatchMap map = new HashedComponentPatchMap(addedComponents, removedComponents);
/*  89 */     return new HashedStack(stack.getType(), stack.getAmount(), map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ItemStack asItemStack() {
/*  97 */     ItemStack stack = ItemStack.builder().type(this.item).amount(this.count).build();
/*     */ 
/*     */     
/* 100 */     for (ComponentType<?> component : (Iterable<ComponentType<?>>)this.components.getRemovedComponents()) {
/* 101 */       stack.unsetComponent(component);
/*     */     }
/* 103 */     return stack;
/*     */   }
/*     */   
/*     */   public ItemType getItem() {
/* 107 */     return this.item;
/*     */   }
/*     */   
/*     */   public int getCount() {
/* 111 */     return this.count;
/*     */   }
/*     */   
/*     */   public HashedComponentPatchMap getComponents() {
/* 115 */     return this.components;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\HashedStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */