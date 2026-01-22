/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.PatchableComponentMap;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
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
/*     */ public final class ItemStackSerialization
/*     */ {
/*     */   public static ItemStack read(PacketWrapper<?> wrapper) {
/*  43 */     return wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5) ? 
/*  44 */       readModern(wrapper) : readLegacy(wrapper);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, @Nullable ItemStack stack) {
/*  48 */     ItemStack replacedStack = (stack == null) ? ItemStack.EMPTY : stack;
/*  49 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  50 */       writeModern(wrapper, replacedStack);
/*     */     } else {
/*  52 */       writeLegacy(wrapper, replacedStack);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ItemStack readLegacy(PacketWrapper<?> wrapper) {
/*  60 */     boolean v1_13_2 = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_13_2);
/*  61 */     if (v1_13_2 && !wrapper.readBoolean()) {
/*  62 */       return ItemStack.EMPTY;
/*     */     }
/*  64 */     int typeId = v1_13_2 ? wrapper.readVarInt() : wrapper.readShort();
/*  65 */     if (typeId < 0 && !v1_13_2) {
/*  66 */       return ItemStack.EMPTY;
/*     */     }
/*     */     
/*  69 */     ClientVersion version = wrapper.getServerVersion().toClientVersion();
/*  70 */     ItemType type = (ItemType)ItemTypes.getRegistry().getByIdOrThrow(version, typeId);
/*  71 */     int amount = wrapper.readByte();
/*  72 */     int legacyData = version.isOlderThan(ClientVersion.V_1_13) ? wrapper.readShort() : -1;
/*  73 */     NBTCompound nbt = wrapper.readNBT();
/*  74 */     return ItemStack.builder().type(type).amount(amount)
/*  75 */       .nbt(nbt).legacyData(legacyData)
/*  76 */       .wrapper(wrapper).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void writeLegacy(PacketWrapper<?> wrapper, ItemStack stack) {
/*  83 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_13_2)) {
/*  84 */       int typeId = stack.isEmpty() ? -1 : stack.getType().getId(wrapper.getServerVersion().toClientVersion());
/*  85 */       wrapper.writeShort(typeId);
/*  86 */       if (typeId != -1) {
/*  87 */         wrapper.writeByte(stack.getAmount());
/*  88 */         if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_13)) {
/*  89 */           wrapper.writeShort(stack.getLegacyData());
/*     */         }
/*  91 */         wrapper.writeNBT(stack.getNBT());
/*     */       } 
/*  93 */     } else if (stack.isEmpty()) {
/*  94 */       wrapper.writeBoolean(false);
/*     */     } else {
/*  96 */       wrapper.writeBoolean(true);
/*  97 */       wrapper.writeMappedEntity((MappedEntity)stack.getType());
/*  98 */       wrapper.writeByte(stack.getAmount());
/*  99 */       wrapper.writeNBT(stack.getNBT());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ItemStack readModern(PacketWrapper<?> wrapper) {
/* 107 */     return readModern(wrapper, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ItemStack readUntrusted(PacketWrapper<?> wrapper) {
/* 114 */     return readModern(wrapper, true);
/*     */   }
/*     */ 
/*     */   
/*     */   private static ItemStack readModern(PacketWrapper<?> wrapper, boolean lengthPrefixed) {
/* 119 */     int count = wrapper.readVarInt();
/* 120 */     if (count <= 0) {
/* 121 */       return ItemStack.EMPTY;
/*     */     }
/* 123 */     ItemType itemType = (ItemType)wrapper.readMappedEntity((IRegistry)ItemTypes.getRegistry());
/*     */ 
/*     */     
/* 126 */     int presentCount = wrapper.readVarInt();
/* 127 */     int absentCount = wrapper.readVarInt();
/* 128 */     if (presentCount == 0 && absentCount == 0) {
/* 129 */       return ItemStack.builder().type(itemType).amount(count).wrapper(wrapper).build();
/*     */     }
/*     */ 
/*     */     
/* 133 */     PatchableComponentMap components = new PatchableComponentMap(itemType.getComponents(wrapper.getServerVersion().toClientVersion()), new HashMap<>(presentCount + absentCount));
/*     */     int i;
/* 135 */     for (i = 0; i < presentCount; i++) {
/* 136 */       int expectedReaderIndex; ComponentType<?> type = (ComponentType)wrapper.readMappedEntity((IRegistry)ComponentTypes.getRegistry());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 141 */       if (lengthPrefixed) {
/* 142 */         int size = wrapper.readVarInt();
/* 143 */         if (size > ByteBufHelper.readableBytes(wrapper.buffer)) {
/* 144 */           throw new RuntimeException("Component size " + size + " for " + type.getName() + " out of bounds");
/*     */         }
/* 146 */         expectedReaderIndex = ByteBufHelper.readerIndex(wrapper.buffer) + size;
/*     */       } else {
/* 148 */         expectedReaderIndex = -1;
/*     */       } 
/*     */       
/* 151 */       Object value = type.read(wrapper);
/*     */       
/* 153 */       if (expectedReaderIndex != -1) {
/* 154 */         int readerIndex = ByteBufHelper.readerIndex(wrapper.buffer);
/* 155 */         if (readerIndex != expectedReaderIndex) {
/* 156 */           throw new RuntimeException("Invalid component read for " + type.getName() + "; expected reader index " + expectedReaderIndex + ", got reader index " + readerIndex);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 161 */       components.set(type, value);
/*     */     } 
/* 163 */     for (i = 0; i < absentCount; i++) {
/* 164 */       components.unset((ComponentType)wrapper.readMappedEntity((IRegistry)ComponentTypes.getRegistry()));
/*     */     }
/*     */     
/* 167 */     return ItemStack.builder().type(itemType).amount(count).components(components).wrapper(wrapper).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeModern(PacketWrapper<?> wrapper, ItemStack stack) {
/* 174 */     writeModern(wrapper, stack, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeUntrusted(PacketWrapper<?> wrapper, ItemStack stack) {
/* 181 */     writeModern(wrapper, stack, true);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void writeModern(PacketWrapper<?> wrapper, ItemStack stack, boolean lengthPrefixed) {
/* 186 */     if (stack.isEmpty()) {
/* 187 */       wrapper.writeByte(0);
/*     */       return;
/*     */     } 
/* 190 */     wrapper.writeVarInt(stack.getAmount());
/* 191 */     wrapper.writeMappedEntity((MappedEntity)stack.getType());
/*     */     
/* 193 */     if (!stack.hasComponentPatches()) {
/* 194 */       wrapper.writeShort(0);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 199 */     Map<ComponentType<?>, Optional<?>> allPatches = stack.getComponents().getPatches();
/* 200 */     int presentCount = 0, absentCount = 0;
/* 201 */     for (Map.Entry<ComponentType<?>, Optional<?>> patch : allPatches.entrySet()) {
/* 202 */       if (((Optional)patch.getValue()).isPresent()) {
/* 203 */         presentCount++; continue;
/*     */       } 
/* 205 */       absentCount++;
/*     */     } 
/*     */     
/* 208 */     wrapper.writeVarInt(presentCount);
/* 209 */     wrapper.writeVarInt(absentCount);
/*     */ 
/*     */     
/* 212 */     for (Map.Entry<ComponentType<?>, Optional<?>> patch : allPatches.entrySet()) {
/* 213 */       if (((Optional)patch.getValue()).isPresent()) {
/* 214 */         wrapper.writeVarInt(((ComponentType)patch.getKey()).getId(wrapper.getServerVersion().toClientVersion()));
/* 215 */         Runnable writer = () -> ((ComponentType)patch.getKey()).write(wrapper, ((Optional)patch.getValue()).get());
/* 216 */         if (lengthPrefixed) {
/*     */           
/* 218 */           Object originalBuffer = wrapper.buffer;
/* 219 */           wrapper.buffer = ByteBufHelper.allocateNewBuffer(originalBuffer);
/* 220 */           writer.run();
/* 221 */           Object componentBuffer = wrapper.buffer;
/* 222 */           wrapper.buffer = originalBuffer;
/*     */           
/* 224 */           wrapper.writeVarInt(ByteBufHelper.readableBytes(componentBuffer));
/*     */           
/* 226 */           ByteBufHelper.writeBytes(wrapper.buffer, componentBuffer);
/*     */           
/* 228 */           ByteBufHelper.release(componentBuffer); continue;
/*     */         } 
/* 230 */         writer.run();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 236 */     for (Map.Entry<ComponentType<?>, Optional<?>> patch : allPatches.entrySet()) {
/* 237 */       if (!((Optional)patch.getValue()).isPresent())
/* 238 */         wrapper.writeVarInt(((ComponentType)patch.getKey()).getId(wrapper.getServerVersion().toClientVersion())); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\ItemStackSerialization.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */