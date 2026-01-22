/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sounds;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*     */ public class ItemEquippable
/*     */ {
/*     */   private EquipmentSlot slot;
/*     */   private Sound equipSound;
/*     */   @Nullable
/*     */   private ResourceLocation assetId;
/*     */   @Nullable
/*     */   private ResourceLocation cameraOverlay;
/*     */   @Nullable
/*     */   private MappedEntitySet<EntityType> allowedEntities;
/*     */   private boolean dispensable;
/*     */   private boolean swappable;
/*     */   private boolean damageOnHurt;
/*     */   private boolean equipOnInteract;
/*     */   private boolean canBeSheared;
/*     */   private Sound shearingSound;
/*     */   
/*     */   @Obsolete
/*     */   public ItemEquippable(EquipmentSlot slot, Sound equipSound, @Nullable ResourceLocation assetId, @Nullable ResourceLocation cameraOverlay, @Nullable MappedEntitySet<EntityType> allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt) {
/*  69 */     this(slot, equipSound, assetId, cameraOverlay, allowedEntities, dispensable, swappable, damageOnHurt, false);
/*     */   }
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
/*     */   @Obsolete
/*     */   public ItemEquippable(EquipmentSlot slot, Sound equipSound, @Nullable ResourceLocation assetId, @Nullable ResourceLocation cameraOverlay, @Nullable MappedEntitySet<EntityType> allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt, boolean equipOnInteract) {
/*  85 */     this(slot, equipSound, assetId, cameraOverlay, allowedEntities, dispensable, swappable, damageOnHurt, equipOnInteract, false, Sounds.ITEM_SHEARS_SNIP);
/*     */   }
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
/*     */   public ItemEquippable(EquipmentSlot slot, Sound equipSound, @Nullable ResourceLocation assetId, @Nullable ResourceLocation cameraOverlay, @Nullable MappedEntitySet<EntityType> allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt, boolean equipOnInteract, boolean canBeSheared, Sound shearingSound) {
/* 103 */     this.slot = slot;
/* 104 */     this.equipSound = equipSound;
/* 105 */     this.assetId = assetId;
/* 106 */     this.cameraOverlay = cameraOverlay;
/* 107 */     this.allowedEntities = allowedEntities;
/* 108 */     this.dispensable = dispensable;
/* 109 */     this.swappable = swappable;
/* 110 */     this.damageOnHurt = damageOnHurt;
/* 111 */     this.equipOnInteract = equipOnInteract;
/* 112 */     this.canBeSheared = canBeSheared;
/* 113 */     this.shearingSound = shearingSound;
/*     */   }
/*     */   
/*     */   public static ItemEquippable read(PacketWrapper<?> wrapper) {
/* 117 */     EquipmentSlot slot = (EquipmentSlot)wrapper.readEnum((Enum[])EquipmentSlot.values());
/* 118 */     Sound equipSound = Sound.read(wrapper);
/* 119 */     ResourceLocation assetId = (ResourceLocation)wrapper.readOptional(PacketWrapper::readIdentifier);
/* 120 */     ResourceLocation cameraOverlay = (ResourceLocation)wrapper.readOptional(PacketWrapper::readIdentifier);
/* 121 */     MappedEntitySet<EntityType> allowedEntities = (MappedEntitySet<EntityType>)wrapper.readOptional(ew -> MappedEntitySet.read(ew, EntityTypes::getById));
/*     */     
/* 123 */     boolean dispensable = wrapper.readBoolean();
/* 124 */     boolean swappable = wrapper.readBoolean();
/* 125 */     boolean damageOnHurt = wrapper.readBoolean();
/* 126 */     boolean equipOnInteract = false;
/* 127 */     boolean canBeSheared = false;
/* 128 */     Sound shearingSound = Sounds.ITEM_SHEARS_SNIP;
/* 129 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/* 130 */       equipOnInteract = wrapper.readBoolean();
/* 131 */       if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_6)) {
/* 132 */         canBeSheared = wrapper.readBoolean();
/* 133 */         shearingSound = Sound.read(wrapper);
/*     */       } 
/*     */     } 
/* 136 */     return new ItemEquippable(slot, equipSound, assetId, cameraOverlay, allowedEntities, dispensable, swappable, damageOnHurt, equipOnInteract, canBeSheared, shearingSound);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemEquippable equippable) {
/* 142 */     wrapper.writeEnum((Enum)equippable.slot);
/* 143 */     Sound.write(wrapper, equippable.equipSound);
/* 144 */     wrapper.writeOptional(equippable.assetId, PacketWrapper::writeIdentifier);
/* 145 */     wrapper.writeOptional(equippable.cameraOverlay, PacketWrapper::writeIdentifier);
/* 146 */     wrapper.writeOptional(equippable.allowedEntities, MappedEntitySet::write);
/* 147 */     wrapper.writeBoolean(equippable.dispensable);
/* 148 */     wrapper.writeBoolean(equippable.swappable);
/* 149 */     wrapper.writeBoolean(equippable.damageOnHurt);
/* 150 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/* 151 */       wrapper.writeBoolean(equippable.equipOnInteract);
/* 152 */       if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_6)) {
/* 153 */         wrapper.writeBoolean(equippable.canBeSheared);
/* 154 */         Sound.write(wrapper, equippable.shearingSound);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public EquipmentSlot getSlot() {
/* 160 */     return this.slot;
/*     */   }
/*     */   
/*     */   public void setSlot(EquipmentSlot slot) {
/* 164 */     this.slot = slot;
/*     */   }
/*     */   
/*     */   public Sound getEquipSound() {
/* 168 */     return this.equipSound;
/*     */   }
/*     */   
/*     */   public void setEquipSound(Sound equipSound) {
/* 172 */     this.equipSound = equipSound;
/*     */   }
/*     */   @Nullable
/*     */   public ResourceLocation getAssetId() {
/* 176 */     return this.assetId;
/*     */   }
/*     */   
/*     */   public void setAssetId(@Nullable ResourceLocation assetId) {
/* 180 */     this.assetId = assetId;
/*     */   }
/*     */   @Nullable
/*     */   public ResourceLocation getCameraOverlay() {
/* 184 */     return this.cameraOverlay;
/*     */   }
/*     */   
/*     */   public void setCameraOverlay(@Nullable ResourceLocation cameraOverlay) {
/* 188 */     this.cameraOverlay = cameraOverlay;
/*     */   }
/*     */   @Nullable
/*     */   public MappedEntitySet<EntityType> getAllowedEntities() {
/* 192 */     return this.allowedEntities;
/*     */   }
/*     */   
/*     */   public void setAllowedEntities(@Nullable MappedEntitySet<EntityType> allowedEntities) {
/* 196 */     this.allowedEntities = allowedEntities;
/*     */   }
/*     */   
/*     */   public boolean isDispensable() {
/* 200 */     return this.dispensable;
/*     */   }
/*     */   
/*     */   public void setDispensable(boolean dispensable) {
/* 204 */     this.dispensable = dispensable;
/*     */   }
/*     */   
/*     */   public boolean isSwappable() {
/* 208 */     return this.swappable;
/*     */   }
/*     */   
/*     */   public void setSwappable(boolean swappable) {
/* 212 */     this.swappable = swappable;
/*     */   }
/*     */   
/*     */   public boolean isDamageOnHurt() {
/* 216 */     return this.damageOnHurt;
/*     */   }
/*     */   
/*     */   public void setDamageOnHurt(boolean damageOnHurt) {
/* 220 */     this.damageOnHurt = damageOnHurt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEquipOnInteract() {
/* 227 */     return this.equipOnInteract;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEquipOnInteract(boolean equipOnInteract) {
/* 234 */     this.equipOnInteract = equipOnInteract;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCanBeSheared() {
/* 241 */     return this.canBeSheared;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCanBeSheared(boolean canBeSheared) {
/* 248 */     this.canBeSheared = canBeSheared;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sound getShearingSound() {
/* 255 */     return this.shearingSound;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShearingSound(Sound shearingSound) {
/* 262 */     this.shearingSound = shearingSound;
/*     */   }
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public ResourceLocation getModel() {
/* 267 */     return this.assetId;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setModel(@Nullable ResourceLocation assetId) {
/* 272 */     this.assetId = assetId;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 277 */     if (!(obj instanceof ItemEquippable)) return false; 
/* 278 */     ItemEquippable that = (ItemEquippable)obj;
/* 279 */     if (this.dispensable != that.dispensable) return false; 
/* 280 */     if (this.swappable != that.swappable) return false; 
/* 281 */     if (this.damageOnHurt != that.damageOnHurt) return false; 
/* 282 */     if (this.equipOnInteract != that.equipOnInteract) return false; 
/* 283 */     if (this.canBeSheared != that.canBeSheared) return false; 
/* 284 */     if (this.slot != that.slot) return false; 
/* 285 */     if (!this.equipSound.equals(that.equipSound)) return false; 
/* 286 */     if (!Objects.equals(this.assetId, that.assetId)) return false; 
/* 287 */     if (!Objects.equals(this.cameraOverlay, that.cameraOverlay)) return false; 
/* 288 */     if (!Objects.equals(this.allowedEntities, that.allowedEntities)) return false; 
/* 289 */     return this.shearingSound.equals(that.shearingSound);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 294 */     return Objects.hash(new Object[] { this.slot, this.equipSound, this.assetId, this.cameraOverlay, this.allowedEntities, Boolean.valueOf(this.dispensable), Boolean.valueOf(this.swappable), Boolean.valueOf(this.damageOnHurt), Boolean.valueOf(this.equipOnInteract), Boolean.valueOf(this.canBeSheared), this.shearingSound });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 299 */     return "ItemEquippable{slot=" + this.slot + ", equipSound=" + this.equipSound + ", assetId=" + this.assetId + ", cameraOverlay=" + this.cameraOverlay + ", allowedEntities=" + this.allowedEntities + ", dispensable=" + this.dispensable + ", swappable=" + this.swappable + ", damageOnHurt=" + this.damageOnHurt + ", equipOnInteract=" + this.equipOnInteract + ", canBeSheared=" + this.canBeSheared + ", shearingSound=" + this.shearingSound + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemEquippable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */