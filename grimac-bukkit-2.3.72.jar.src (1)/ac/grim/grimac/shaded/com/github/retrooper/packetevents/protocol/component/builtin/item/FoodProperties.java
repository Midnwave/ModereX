/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionEffect;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public class FoodProperties
/*     */ {
/*     */   private int nutrition;
/*     */   private float saturation;
/*     */   private boolean canAlwaysEat;
/*     */   private float eatSeconds;
/*     */   private List<PossibleEffect> effects;
/*     */   @Nullable
/*     */   private ItemStack usingConvertsTo;
/*     */   
/*     */   public FoodProperties(int nutrition, float saturation, boolean canAlwaysEat) {
/*  44 */     this(nutrition, saturation, canAlwaysEat, 1.6F, Collections.emptyList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public FoodProperties(int nutrition, float saturation, boolean canAlwaysEat, float eatSeconds, List<PossibleEffect> effects) {
/*  56 */     this(nutrition, saturation, canAlwaysEat, eatSeconds, effects, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public FoodProperties(int nutrition, float saturation, boolean canAlwaysEat, float eatSeconds, List<PossibleEffect> effects, @Nullable ItemStack usingConvertsTo) {
/*  68 */     this.nutrition = nutrition;
/*  69 */     this.saturation = saturation;
/*  70 */     this.canAlwaysEat = canAlwaysEat;
/*  71 */     this.eatSeconds = eatSeconds;
/*  72 */     this.effects = effects;
/*  73 */     this.usingConvertsTo = usingConvertsTo;
/*     */   }
/*     */   
/*     */   public static FoodProperties read(PacketWrapper<?> wrapper) {
/*  77 */     int nutrition = wrapper.readVarInt();
/*  78 */     float saturation = wrapper.readFloat();
/*  79 */     boolean canAlwaysEat = wrapper.readBoolean();
/*  80 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/*  81 */       return new FoodProperties(nutrition, saturation, canAlwaysEat);
/*     */     }
/*  83 */     float eatSeconds = wrapper.readFloat();
/*     */     
/*  85 */     ItemStack usingConvertsTo = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21) ? (ItemStack)wrapper.readOptional(PacketWrapper::readItemStack) : null;
/*  86 */     List<PossibleEffect> effects = wrapper.readList(PossibleEffect::read);
/*  87 */     return new FoodProperties(nutrition, saturation, canAlwaysEat, eatSeconds, effects, usingConvertsTo);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, FoodProperties props) {
/*  91 */     wrapper.writeVarInt(props.nutrition);
/*  92 */     wrapper.writeFloat(props.saturation);
/*  93 */     wrapper.writeBoolean(props.canAlwaysEat);
/*  94 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_2)) {
/*  95 */       wrapper.writeFloat(props.eatSeconds);
/*  96 */       if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21)) {
/*  97 */         wrapper.writeOptional(props.usingConvertsTo, PacketWrapper::writeItemStack);
/*     */       }
/*  99 */       wrapper.writeList(props.effects, PossibleEffect::write);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getNutrition() {
/* 104 */     return this.nutrition;
/*     */   }
/*     */   
/*     */   public void setNutrition(int nutrition) {
/* 108 */     this.nutrition = nutrition;
/*     */   }
/*     */   
/*     */   public float getSaturation() {
/* 112 */     return this.saturation;
/*     */   }
/*     */   
/*     */   public void setSaturation(float saturation) {
/* 116 */     this.saturation = saturation;
/*     */   }
/*     */   
/*     */   public boolean isCanAlwaysEat() {
/* 120 */     return this.canAlwaysEat;
/*     */   }
/*     */   
/*     */   public void setCanAlwaysEat(boolean canAlwaysEat) {
/* 124 */     this.canAlwaysEat = canAlwaysEat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public float getEatSeconds() {
/* 132 */     return this.eatSeconds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public void setEatSeconds(float eatSeconds) {
/* 140 */     this.eatSeconds = eatSeconds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public void addEffect(PossibleEffect effect) {
/* 148 */     this.effects.add(effect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public List<PossibleEffect> getEffects() {
/* 156 */     return this.effects;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public void setEffects(List<PossibleEffect> effects) {
/* 164 */     this.effects = effects;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   @Nullable
/*     */   public ItemStack getUsingConvertsTo() {
/* 172 */     return this.usingConvertsTo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public void setUsingConvertsTo(@Nullable ItemStack usingConvertsTo) {
/* 180 */     this.usingConvertsTo = usingConvertsTo;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 185 */     if (this == obj) return true; 
/* 186 */     if (!(obj instanceof FoodProperties)) return false; 
/* 187 */     FoodProperties that = (FoodProperties)obj;
/* 188 */     if (this.nutrition != that.nutrition) return false; 
/* 189 */     if (Float.compare(that.saturation, this.saturation) != 0) return false; 
/* 190 */     if (this.canAlwaysEat != that.canAlwaysEat) return false; 
/* 191 */     if (Float.compare(that.eatSeconds, this.eatSeconds) != 0) return false; 
/* 192 */     if (!this.effects.equals(that.effects)) return false; 
/* 193 */     return Objects.equals(this.usingConvertsTo, that.usingConvertsTo);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 198 */     return Objects.hash(new Object[] { Integer.valueOf(this.nutrition), Float.valueOf(this.saturation), Boolean.valueOf(this.canAlwaysEat), Float.valueOf(this.eatSeconds), this.effects, this.usingConvertsTo });
/*     */   }
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public static class PossibleEffect
/*     */   {
/*     */     private PotionEffect effect;
/*     */     
/*     */     private float probability;
/*     */ 
/*     */     
/*     */     public PossibleEffect(PotionEffect effect, float probability) {
/* 211 */       this.effect = effect;
/* 212 */       this.probability = probability;
/*     */     }
/*     */     
/*     */     public static PossibleEffect read(PacketWrapper<?> wrapper) {
/* 216 */       PotionEffect effect = PotionEffect.read(wrapper);
/* 217 */       float probability = wrapper.readFloat();
/* 218 */       return new PossibleEffect(effect, probability);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, PossibleEffect effect) {
/* 222 */       PotionEffect.write(wrapper, effect.effect);
/* 223 */       wrapper.writeFloat(effect.probability);
/*     */     }
/*     */     
/*     */     public PotionEffect getEffect() {
/* 227 */       return this.effect;
/*     */     }
/*     */     
/*     */     public void setEffect(PotionEffect effect) {
/* 231 */       this.effect = effect;
/*     */     }
/*     */     
/*     */     public float getProbability() {
/* 235 */       return this.probability;
/*     */     }
/*     */     
/*     */     public void setProbability(float probability) {
/* 239 */       this.probability = probability;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 244 */       if (this == obj) return true; 
/* 245 */       if (!(obj instanceof PossibleEffect)) return false; 
/* 246 */       PossibleEffect that = (PossibleEffect)obj;
/* 247 */       if (Float.compare(that.probability, this.probability) != 0) return false; 
/* 248 */       return this.effect.equals(that.effect);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 253 */       return Objects.hash(new Object[] { this.effect, Float.valueOf(this.probability) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\FoodProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */