/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.damagetype.DamageType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.damagetype.DamageTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.BiFunction;
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
/*     */ public class ItemBlocksAttacks
/*     */ {
/*     */   private float blockDelaySeconds;
/*     */   private float disableCooldownScale;
/*     */   private List<DamageReduction> damageReductions;
/*     */   private ItemDamageFunction itemDamage;
/*     */   @Nullable
/*     */   private ResourceLocation bypassedBy;
/*     */   @Nullable
/*     */   private Sound blockSound;
/*     */   @Nullable
/*     */   private Sound disableSound;
/*     */   
/*     */   public ItemBlocksAttacks(float blockDelaySeconds, float disableCooldownScale, List<DamageReduction> damageReductions, ItemDamageFunction itemDamage, @Nullable ResourceLocation bypassedBy, @Nullable Sound blockSound, @Nullable Sound disableSound) {
/*  47 */     this.blockDelaySeconds = blockDelaySeconds;
/*  48 */     this.disableCooldownScale = disableCooldownScale;
/*  49 */     this.damageReductions = damageReductions;
/*  50 */     this.itemDamage = itemDamage;
/*  51 */     this.bypassedBy = bypassedBy;
/*  52 */     this.blockSound = blockSound;
/*  53 */     this.disableSound = disableSound;
/*     */   }
/*     */   
/*     */   public static ItemBlocksAttacks read(PacketWrapper<?> wrapper) {
/*  57 */     float blockDelaySeconds = wrapper.readFloat();
/*  58 */     float disableCooldownScale = wrapper.readFloat();
/*  59 */     List<DamageReduction> damageReductions = wrapper.readList(DamageReduction::read);
/*  60 */     ItemDamageFunction itemDamage = ItemDamageFunction.read(wrapper);
/*  61 */     ResourceLocation bypassedBy = (ResourceLocation)wrapper.readOptional(PacketWrapper::readIdentifier);
/*  62 */     Sound blockSound = (Sound)wrapper.readOptional(Sound::read);
/*  63 */     Sound disableSound = (Sound)wrapper.readOptional(Sound::read);
/*  64 */     return new ItemBlocksAttacks(blockDelaySeconds, disableCooldownScale, damageReductions, itemDamage, bypassedBy, blockSound, disableSound);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemBlocksAttacks attack) {
/*  70 */     wrapper.writeFloat(attack.blockDelaySeconds);
/*  71 */     wrapper.writeFloat(attack.disableCooldownScale);
/*  72 */     wrapper.writeList(attack.damageReductions, DamageReduction::write);
/*  73 */     ItemDamageFunction.write(wrapper, attack.itemDamage);
/*  74 */     wrapper.writeOptional(attack.bypassedBy, PacketWrapper::writeIdentifier);
/*  75 */     wrapper.writeOptional(attack.blockSound, Sound::write);
/*  76 */     wrapper.writeOptional(attack.disableSound, Sound::write);
/*     */   }
/*     */   
/*     */   public float getBlockDelaySeconds() {
/*  80 */     return this.blockDelaySeconds;
/*     */   }
/*     */   
/*     */   public void setBlockDelaySeconds(float blockDelaySeconds) {
/*  84 */     this.blockDelaySeconds = blockDelaySeconds;
/*     */   }
/*     */   
/*     */   public float getDisableCooldownScale() {
/*  88 */     return this.disableCooldownScale;
/*     */   }
/*     */   
/*     */   public void setDisableCooldownScale(float disableCooldownScale) {
/*  92 */     this.disableCooldownScale = disableCooldownScale;
/*     */   }
/*     */   
/*     */   public List<DamageReduction> getDamageReductions() {
/*  96 */     return this.damageReductions;
/*     */   }
/*     */   
/*     */   public void setDamageReductions(List<DamageReduction> damageReductions) {
/* 100 */     this.damageReductions = damageReductions;
/*     */   }
/*     */   
/*     */   public ItemDamageFunction getItemDamage() {
/* 104 */     return this.itemDamage;
/*     */   }
/*     */   
/*     */   public void setItemDamage(ItemDamageFunction itemDamage) {
/* 108 */     this.itemDamage = itemDamage;
/*     */   }
/*     */   @Nullable
/*     */   public ResourceLocation getBypassedBy() {
/* 112 */     return this.bypassedBy;
/*     */   }
/*     */   
/*     */   public void setBypassedBy(@Nullable ResourceLocation bypassedBy) {
/* 116 */     this.bypassedBy = bypassedBy;
/*     */   }
/*     */   @Nullable
/*     */   public Sound getBlockSound() {
/* 120 */     return this.blockSound;
/*     */   }
/*     */   
/*     */   public void setBlockSound(@Nullable Sound blockSound) {
/* 124 */     this.blockSound = blockSound;
/*     */   }
/*     */   @Nullable
/*     */   public Sound getDisableSound() {
/* 128 */     return this.disableSound;
/*     */   }
/*     */   
/*     */   public void setDisableSound(@Nullable Sound disableSound) {
/* 132 */     this.disableSound = disableSound;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 137 */     if (!(obj instanceof ItemBlocksAttacks)) return false; 
/* 138 */     ItemBlocksAttacks that = (ItemBlocksAttacks)obj;
/* 139 */     if (Float.compare(that.blockDelaySeconds, this.blockDelaySeconds) != 0) return false; 
/* 140 */     if (Float.compare(that.disableCooldownScale, this.disableCooldownScale) != 0) return false; 
/* 141 */     if (!this.damageReductions.equals(that.damageReductions)) return false; 
/* 142 */     if (!this.itemDamage.equals(that.itemDamage)) return false; 
/* 143 */     if (!Objects.equals(this.bypassedBy, that.bypassedBy)) return false; 
/* 144 */     if (!Objects.equals(this.blockSound, that.blockSound)) return false; 
/* 145 */     return Objects.equals(this.disableSound, that.disableSound);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 150 */     return Objects.hash(new Object[] { Float.valueOf(this.blockDelaySeconds), Float.valueOf(this.disableCooldownScale), this.damageReductions, this.itemDamage, this.bypassedBy, this.blockSound, this.disableSound });
/*     */   }
/*     */   
/*     */   public static final class DamageReduction {
/*     */     private float horizontalBlockingAngle;
/*     */     @Nullable
/*     */     private MappedEntitySet<DamageType> type;
/*     */     private float base;
/*     */     private float factor;
/*     */     
/*     */     public DamageReduction(float horizontalBlockingAngle, @Nullable MappedEntitySet<DamageType> type, float base, float factor) {
/* 161 */       this.horizontalBlockingAngle = horizontalBlockingAngle;
/* 162 */       this.type = type;
/* 163 */       this.base = base;
/* 164 */       this.factor = factor;
/*     */     }
/*     */     
/*     */     public static DamageReduction read(PacketWrapper<?> wrapper) {
/* 168 */       float horizontalBlockingAngle = wrapper.readFloat();
/* 169 */       MappedEntitySet<DamageType> type = (MappedEntitySet<DamageType>)wrapper.readOptional(ew -> MappedEntitySet.read(ew, (BiFunction)DamageTypes.getRegistry()));
/*     */       
/* 171 */       float base = wrapper.readFloat();
/* 172 */       float factor = wrapper.readFloat();
/* 173 */       return new DamageReduction(horizontalBlockingAngle, type, base, factor);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, DamageReduction reduction) {
/* 177 */       wrapper.writeFloat(reduction.horizontalBlockingAngle);
/* 178 */       wrapper.writeOptional(reduction.type, MappedEntitySet::write);
/* 179 */       wrapper.writeFloat(reduction.base);
/* 180 */       wrapper.writeFloat(reduction.factor);
/*     */     }
/*     */     
/*     */     public float getHorizontalBlockingAngle() {
/* 184 */       return this.horizontalBlockingAngle;
/*     */     }
/*     */     
/*     */     public void setHorizontalBlockingAngle(float horizontalBlockingAngle) {
/* 188 */       this.horizontalBlockingAngle = horizontalBlockingAngle;
/*     */     }
/*     */     @Nullable
/*     */     public MappedEntitySet<DamageType> getType() {
/* 192 */       return this.type;
/*     */     }
/*     */     
/*     */     public void setType(@Nullable MappedEntitySet<DamageType> type) {
/* 196 */       this.type = type;
/*     */     }
/*     */     
/*     */     public float getBase() {
/* 200 */       return this.base;
/*     */     }
/*     */     
/*     */     public void setBase(float base) {
/* 204 */       this.base = base;
/*     */     }
/*     */     
/*     */     public float getFactor() {
/* 208 */       return this.factor;
/*     */     }
/*     */     
/*     */     public void setFactor(float factor) {
/* 212 */       this.factor = factor;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 217 */       if (!(obj instanceof DamageReduction)) return false; 
/* 218 */       DamageReduction that = (DamageReduction)obj;
/* 219 */       if (Float.compare(that.horizontalBlockingAngle, this.horizontalBlockingAngle) != 0) return false; 
/* 220 */       if (Float.compare(that.base, this.base) != 0) return false; 
/* 221 */       if (Float.compare(that.factor, this.factor) != 0) return false; 
/* 222 */       return Objects.equals(this.type, that.type);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 227 */       return Objects.hash(new Object[] { Float.valueOf(this.horizontalBlockingAngle), this.type, Float.valueOf(this.base), Float.valueOf(this.factor) });
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class ItemDamageFunction
/*     */   {
/*     */     private float threshold;
/*     */     private float base;
/*     */     private float factor;
/*     */     
/*     */     public ItemDamageFunction(float threshold, float base, float factor) {
/* 238 */       this.threshold = threshold;
/* 239 */       this.base = base;
/* 240 */       this.factor = factor;
/*     */     }
/*     */     
/*     */     public static ItemDamageFunction read(PacketWrapper<?> wrapper) {
/* 244 */       float threshold = wrapper.readFloat();
/* 245 */       float base = wrapper.readFloat();
/* 246 */       float factor = wrapper.readFloat();
/* 247 */       return new ItemDamageFunction(threshold, base, factor);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, ItemDamageFunction function) {
/* 251 */       wrapper.writeFloat(function.threshold);
/* 252 */       wrapper.writeFloat(function.base);
/* 253 */       wrapper.writeFloat(function.factor);
/*     */     }
/*     */     
/*     */     public float getThreshold() {
/* 257 */       return this.threshold;
/*     */     }
/*     */     
/*     */     public void setThreshold(float threshold) {
/* 261 */       this.threshold = threshold;
/*     */     }
/*     */     
/*     */     public float getBase() {
/* 265 */       return this.base;
/*     */     }
/*     */     
/*     */     public void setBase(float base) {
/* 269 */       this.base = base;
/*     */     }
/*     */     
/*     */     public float getFactor() {
/* 273 */       return this.factor;
/*     */     }
/*     */     
/*     */     public void setFactor(float factor) {
/* 277 */       this.factor = factor;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 282 */       if (!(obj instanceof ItemDamageFunction)) return false; 
/* 283 */       ItemDamageFunction that = (ItemDamageFunction)obj;
/* 284 */       if (Float.compare(that.threshold, this.threshold) != 0) return false; 
/* 285 */       if (Float.compare(that.base, this.base) != 0) return false; 
/* 286 */       return (Float.compare(that.factor, this.factor) == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 291 */       return Objects.hash(new Object[] { Float.valueOf(this.threshold), Float.valueOf(this.base), Float.valueOf(this.factor) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemBlocksAttacks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */