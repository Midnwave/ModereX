/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public class ParticleItemStackData
/*    */   extends ParticleData
/*    */   implements LegacyConvertible
/*    */ {
/*    */   private ItemStack itemStack;
/*    */   
/*    */   public ParticleItemStackData(ItemStack itemStack) {
/* 32 */     this.itemStack = itemStack;
/*    */   }
/*    */   
/*    */   public ItemStack getItemStack() {
/* 36 */     return this.itemStack;
/*    */   }
/*    */   
/*    */   public void setItemStack(ItemStack itemStack) {
/* 40 */     this.itemStack = itemStack;
/*    */   }
/*    */   
/*    */   public static ParticleItemStackData read(PacketWrapper<?> wrapper) {
/* 44 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 45 */       return new ParticleItemStackData(wrapper.readItemStack());
/*    */     }
/* 47 */     return new ParticleItemStackData(ItemStack.builder()
/* 48 */         .type((ItemType)ItemTypes.getRegistry().getByIdOrThrow(wrapper.getClientVersion(), wrapper.readVarInt()))
/* 49 */         .wrapper(wrapper).build());
/*    */   }
/*    */ 
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ParticleItemStackData data) {
/* 54 */     wrapper.writeItemStack(data.getItemStack());
/*    */   }
/*    */   
/*    */   public static ParticleItemStackData decode(NBTCompound compound, ClientVersion version) {
/* 58 */     String key = version.isNewerThanOrEquals(ClientVersion.V_1_20_5) ? "item" : "value";
/* 59 */     ItemStack stack = ItemStack.decode(compound.getTagOrThrow(key), version);
/* 60 */     return new ParticleItemStackData(stack);
/*    */   }
/*    */   
/*    */   public static void encode(ParticleItemStackData data, ClientVersion version, NBTCompound compound) {
/* 64 */     String key = version.isNewerThanOrEquals(ClientVersion.V_1_20_5) ? "item" : "value";
/* 65 */     compound.setTag(key, ItemStack.encodeForParticle(data.itemStack, version));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 70 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public LegacyParticleData toLegacy(ClientVersion version) {
/* 75 */     return LegacyParticleData.ofTwo(this.itemStack.getType().getId(version), this.itemStack.getLegacyData());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\data\ParticleItemStackData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */