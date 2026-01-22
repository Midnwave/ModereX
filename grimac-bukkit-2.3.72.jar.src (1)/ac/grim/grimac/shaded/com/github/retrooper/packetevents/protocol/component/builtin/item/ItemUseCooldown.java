/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCooldown;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Objects;
/*    */ import java.util.Optional;
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
/*    */ public class ItemUseCooldown
/*    */ {
/*    */   private float seconds;
/*    */   private Optional<ResourceLocation> cooldownGroup;
/*    */   
/*    */   public ItemUseCooldown(float seconds, @Nullable ResourceLocation cooldownGroup) {
/* 37 */     this(seconds, Optional.ofNullable(cooldownGroup));
/*    */   }
/*    */   
/*    */   public ItemUseCooldown(float seconds, Optional<ResourceLocation> cooldownGroup) {
/* 41 */     this.seconds = seconds;
/* 42 */     this.cooldownGroup = cooldownGroup;
/*    */   }
/*    */   
/*    */   public static ItemUseCooldown read(PacketWrapper<?> wrapper) {
/* 46 */     float seconds = wrapper.readFloat();
/* 47 */     ResourceLocation cooldownGroup = (ResourceLocation)wrapper.readOptional(PacketWrapper::readIdentifier);
/* 48 */     return new ItemUseCooldown(seconds, cooldownGroup);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemUseCooldown cooldown) {
/* 52 */     wrapper.writeFloat(cooldown.seconds);
/* 53 */     wrapper.writeOptional(cooldown.cooldownGroup.orElse(null), PacketWrapper::writeIdentifier);
/*    */   }
/*    */   
/*    */   public WrapperPlayServerSetCooldown buildWrapper(ItemStack fallbackStack) {
/* 57 */     return buildWrapper(fallbackStack.getType());
/*    */   }
/*    */   
/*    */   public WrapperPlayServerSetCooldown buildWrapper(ItemType fallbackItem) {
/* 61 */     int ticks = (int)(this.seconds * 20.0F);
/* 62 */     return this.cooldownGroup
/* 63 */       .<WrapperPlayServerSetCooldown>map(resourceLocation -> new WrapperPlayServerSetCooldown(resourceLocation, ticks))
/* 64 */       .orElseGet(() -> new WrapperPlayServerSetCooldown(fallbackItem, ticks));
/*    */   }
/*    */   
/*    */   public float getSeconds() {
/* 68 */     return this.seconds;
/*    */   }
/*    */   
/*    */   public void setSeconds(float seconds) {
/* 72 */     this.seconds = seconds;
/*    */   }
/*    */   
/*    */   public Optional<ResourceLocation> getCooldownGroup() {
/* 76 */     return this.cooldownGroup;
/*    */   }
/*    */   
/*    */   public void setCooldownGroup(Optional<ResourceLocation> cooldownGroup) {
/* 80 */     this.cooldownGroup = cooldownGroup;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 85 */     if (this == obj) return true; 
/* 86 */     if (!(obj instanceof ItemUseCooldown)) return false; 
/* 87 */     ItemUseCooldown that = (ItemUseCooldown)obj;
/* 88 */     if (Float.compare(that.seconds, this.seconds) != 0) return false; 
/* 89 */     return this.cooldownGroup.equals(that.cooldownGroup);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 94 */     return Objects.hash(new Object[] { Float.valueOf(this.seconds), this.cooldownGroup });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 99 */     return "ItemUseCooldown{seconds=" + this.seconds + ", cooldownGroup=" + this.cooldownGroup + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemUseCooldown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */