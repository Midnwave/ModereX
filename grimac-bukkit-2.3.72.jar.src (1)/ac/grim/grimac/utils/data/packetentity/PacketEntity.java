/*     */ package ac.grim.grimac.utils.data.packetentity;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attribute;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.Object2IntMap;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.Object2IntOpenHashMap;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.ReachInterpolationData;
/*     */ import ac.grim.grimac.utils.data.TrackedPosition;
/*     */ import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EnumMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.OptionalInt;
/*     */ import java.util.UUID;
/*     */ import lombok.Generated;
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
/*     */ public class PacketEntity
/*     */   extends TypedPacketEntity
/*     */ {
/*     */   public final TrackedPosition trackedServerPosition;
/*     */   private final UUID uuid;
/*     */   public PacketEntity riding;
/*  48 */   protected final Map<Attribute, ValuedAttribute> attributeMap = new IdentityHashMap<>(); @Generated
/*     */   public UUID getUuid() {
/*  50 */     return this.uuid; } @Generated
/*     */   public PacketEntity getRiding() {
/*  52 */     return this.riding;
/*     */   }
/*  54 */   public List<PacketEntity> passengers = new ArrayList<>(0);
/*     */   public boolean isDead = false;
/*     */   public boolean isBaby = false;
/*     */   public boolean hasGravity = true;
/*     */   private ReachInterpolationData oldPacketLocation;
/*     */   private ReachInterpolationData newPacketLocation;
/*  60 */   private Object2IntMap<PotionType> potionsMap = null;
/*     */   public boolean trackEntityEquipment = false;
/*  62 */   private EnumMap<EquipmentSlot, ItemStack> equipment = null;
/*     */   
/*     */   public PacketEntity(GrimPlayer player, EntityType type) {
/*  65 */     super(type);
/*  66 */     this.uuid = null;
/*  67 */     initAttributes(player);
/*  68 */     this.trackedServerPosition = new TrackedPosition();
/*     */   }
/*     */   
/*     */   public PacketEntity(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z) {
/*  72 */     super(type);
/*  73 */     this.uuid = uuid;
/*  74 */     initAttributes(player);
/*  75 */     this.trackedServerPosition = new TrackedPosition();
/*  76 */     this.trackedServerPosition.setPos(new Vector3d(x, y, z));
/*  77 */     if (player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
/*  78 */       this.trackedServerPosition.setPos(new Vector3d((int)(x * 32.0D) / 32.0D, (int)(y * 32.0D) / 32.0D, (int)(z * 32.0D) / 32.0D));
/*     */     }
/*  80 */     Vector3d pos = this.trackedServerPosition.getPos();
/*  81 */     this.newPacketLocation = new ReachInterpolationData(player, new SimpleCollisionBox(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z, false), this.trackedServerPosition, this);
/*     */   }
/*     */   
/*     */   protected void trackAttribute(ValuedAttribute valuedAttribute) {
/*  85 */     if (this.attributeMap.containsKey(valuedAttribute.attribute())) {
/*  86 */       throw new IllegalArgumentException("Attribute already exists on entity!");
/*     */     }
/*  88 */     this.attributeMap.put(valuedAttribute.attribute(), valuedAttribute);
/*     */   }
/*     */   
/*     */   protected void initAttributes(GrimPlayer player) {
/*  92 */     trackAttribute(ValuedAttribute.ranged(Attributes.SCALE, 1.0D, 0.0625D, 16.0D)
/*  93 */         .requiredVersion(player, ClientVersion.V_1_20_5));
/*  94 */     trackAttribute(ValuedAttribute.ranged(Attributes.STEP_HEIGHT, 0.6000000238418579D, 0.0D, 10.0D)
/*  95 */         .requiredVersion(player, ClientVersion.V_1_20_5));
/*  96 */     trackAttribute(ValuedAttribute.ranged(Attributes.GRAVITY, 0.08D, -1.0D, 1.0D)
/*  97 */         .requiredVersion(player, ClientVersion.V_1_20_5));
/*     */   }
/*     */   
/*     */   public Optional<ValuedAttribute> getAttribute(Attribute attribute) {
/* 101 */     if (attribute == null) return Optional.empty(); 
/* 102 */     return Optional.ofNullable(this.attributeMap.get(attribute));
/*     */   }
/*     */   
/*     */   public void setAttribute(Attribute attribute, double value) {
/* 106 */     ValuedAttribute property = this.attributeMap.get(attribute);
/* 107 */     if (property == null) {
/* 108 */       throw new IllegalArgumentException("Cannot set attribute " + String.valueOf(attribute.getName()) + " for entity " + String.valueOf(this.type.getName()) + "!");
/*     */     }
/* 110 */     property.override(value);
/*     */   }
/*     */   
/*     */   public double getAttributeValue(Attribute attribute) {
/* 114 */     ValuedAttribute property = this.attributeMap.get(attribute);
/* 115 */     if (property == null) {
/* 116 */       throw new IllegalArgumentException("Cannot get attribute " + String.valueOf(attribute.getName()) + " for entity " + String.valueOf(this.type.getName()) + "!");
/*     */     }
/* 118 */     return property.get();
/*     */   }
/*     */   
/*     */   public void resetAttributes() {
/* 122 */     this.attributeMap.values().forEach(ValuedAttribute::reset);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onFirstTransaction(boolean relative, boolean hasPos, double relX, double relY, double relZ, GrimPlayer player) {
/* 128 */     if (hasPos) {
/* 129 */       if (relative) {
/*     */         Vector3d vec3d;
/* 131 */         double scale = this.trackedServerPosition.getScale();
/*     */         
/* 133 */         if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16)) {
/* 134 */           vec3d = this.trackedServerPosition.withDelta(TrackedPosition.pack(relX, scale), TrackedPosition.pack(relY, scale), TrackedPosition.pack(relZ, scale));
/*     */         } else {
/* 136 */           vec3d = this.trackedServerPosition.withDeltaLegacy(TrackedPosition.packLegacy(relX, scale), TrackedPosition.packLegacy(relY, scale), TrackedPosition.packLegacy(relZ, scale));
/*     */         } 
/* 138 */         this.trackedServerPosition.setPos(vec3d);
/*     */       } else {
/* 140 */         this.trackedServerPosition.setPos(new Vector3d(relX, relY, relZ));
/*     */ 
/*     */ 
/*     */         
/* 144 */         if (player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
/* 145 */           this.trackedServerPosition.setPos(new Vector3d((int)(relX * 32.0D) / 32.0D, (int)(relY * 32.0D) / 32.0D, (int)(relZ * 32.0D) / 32.0D));
/*     */         }
/*     */       } 
/*     */     }
/* 149 */     this.oldPacketLocation = this.newPacketLocation;
/* 150 */     this.newPacketLocation = new ReachInterpolationData(player, this.oldPacketLocation.getPossibleLocationCombined(), this.trackedServerPosition, this);
/*     */ 
/*     */ 
/*     */     
/* 154 */     if (!hasPos && (player
/* 155 */       .getClientVersion().isNewerThan(ClientVersion.V_1_21_4) || (player
/* 156 */       .getClientVersion().isOlderThan(ClientVersion.V_1_20_2) && player.getClientVersion().isNewerThan(ClientVersion.V_1_14_4))))
/*     */     {
/* 158 */       this.newPacketLocation.cancelLerp();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 163 */     if (hasPos && !relative && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_16_1)) {
/* 164 */       SimpleCollisionBox clientArea = this.newPacketLocation.getPossibleLocationCombined();
/* 165 */       if (clientArea.distanceX(relX) < 0.03125D && clientArea
/* 166 */         .distanceY(relY) < 0.015625D && clientArea
/* 167 */         .distanceZ(relZ) < 0.03125D) {
/* 168 */         this.newPacketLocation.expandNonRelative();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onSecondTransaction() {
/* 175 */     this.oldPacketLocation = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onMovement(boolean tickingReliably) {
/* 180 */     this.newPacketLocation.tickMovement((this.oldPacketLocation == null), tickingReliably);
/*     */ 
/*     */     
/* 183 */     if (this.oldPacketLocation != null) {
/* 184 */       this.oldPacketLocation.tickMovement(true, tickingReliably);
/* 185 */       this.newPacketLocation.updatePossibleStartingLocation(this.oldPacketLocation.getPossibleLocationCombined());
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean hasPassenger(PacketEntity entity) {
/* 190 */     return this.passengers.contains(entity);
/*     */   }
/*     */   
/*     */   public void mount(PacketEntity vehicle) {
/* 194 */     if (this.riding != null) eject(); 
/* 195 */     vehicle.passengers.add(this);
/* 196 */     this.riding = vehicle;
/*     */   }
/*     */   
/*     */   public void eject() {
/* 200 */     if (this.riding != null) {
/* 201 */       this.riding.passengers.remove(this);
/*     */     }
/* 203 */     this.riding = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPositionRaw(GrimPlayer player, SimpleCollisionBox box) {
/* 210 */     this.trackedServerPosition.setPos(new Vector3d((box.maxX - box.minX) / 2.0D + box.minX, box.minY, (box.maxZ - box.minZ) / 2.0D + box.minZ));
/*     */     
/* 212 */     this.newPacketLocation = new ReachInterpolationData(player, box, this);
/*     */   }
/*     */   
/*     */   public SimpleCollisionBox getPossibleLocationBoxes() {
/* 216 */     if (this.oldPacketLocation == null) {
/* 217 */       return this.newPacketLocation.getPossibleLocationCombined();
/*     */     }
/*     */     
/* 220 */     return ReachInterpolationData.combineCollisionBox(this.oldPacketLocation.getPossibleLocationCombined(), this.newPacketLocation.getPossibleLocationCombined());
/*     */   }
/*     */   
/*     */   public SimpleCollisionBox getPossibleCollisionBoxes() {
/* 224 */     if (this.oldPacketLocation == null) {
/* 225 */       return this.newPacketLocation.getPossibleHitboxCombined();
/*     */     }
/*     */     
/* 228 */     return ReachInterpolationData.combineCollisionBox(this.oldPacketLocation.getPossibleHitboxCombined(), this.newPacketLocation.getPossibleHitboxCombined());
/*     */   }
/*     */   
/*     */   public OptionalInt getPotionEffectLevel(PotionType effect) {
/* 232 */     int amplifier = (this.potionsMap == null) ? -1 : this.potionsMap.getInt(effect);
/* 233 */     return (amplifier == -1) ? OptionalInt.empty() : OptionalInt.of(amplifier);
/*     */   }
/*     */   
/*     */   public boolean hasPotionEffect(PotionType effect) {
/* 237 */     return (this.potionsMap != null && this.potionsMap.containsKey(effect));
/*     */   }
/*     */   
/*     */   public void addPotionEffect(PotionType effect, int amplifier) {
/* 241 */     if (this.potionsMap == null) {
/* 242 */       this.potionsMap = (Object2IntMap<PotionType>)new Object2IntOpenHashMap();
/* 243 */       this.potionsMap.defaultReturnValue(-1);
/*     */     } 
/* 245 */     this.potionsMap.put(effect, amplifier);
/*     */   }
/*     */   
/*     */   public void removePotionEffect(PotionType effect) {
/* 249 */     if (this.potionsMap == null)
/* 250 */       return;  this.potionsMap.removeInt(effect);
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
/*     */   public boolean canHit() {
/* 267 */     return !this.isDead;
/*     */   }
/*     */   
/*     */   public void setItemBySlot(EquipmentSlot slot, ItemStack item) {
/* 271 */     if (item == ItemStack.EMPTY && getItemBySlot(slot) == ItemStack.EMPTY) {
/*     */       return;
/*     */     }
/*     */     
/* 275 */     if (this.equipment == null) {
/* 276 */       this.equipment = new EnumMap<>(EquipmentSlot.class);
/*     */     }
/*     */     
/* 279 */     this.equipment.put(slot, item);
/*     */   }
/*     */   
/*     */   public ItemStack getItemBySlot(EquipmentSlot slot) {
/* 283 */     if (this.equipment == null) {
/* 284 */       return ItemStack.EMPTY;
/*     */     }
/*     */     
/* 287 */     return this.equipment.getOrDefault(slot, ItemStack.EMPTY);
/*     */   }
/*     */   
/*     */   public boolean hasItemInSlot(EquipmentSlot slot) {
/* 291 */     if (this.equipment == null) {
/* 292 */       return false;
/*     */     }
/*     */     
/* 295 */     ItemStack item = this.equipment.get(slot);
/* 296 */     return (item != null && !item.isEmpty());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntity.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */