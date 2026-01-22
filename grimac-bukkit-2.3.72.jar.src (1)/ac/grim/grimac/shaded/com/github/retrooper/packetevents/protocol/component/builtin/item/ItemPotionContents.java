/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.Potion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionEffect;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.Potions;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*     */ public class ItemPotionContents
/*     */ {
/*     */   @Nullable
/*     */   private Potion potion;
/*     */   @Nullable
/*     */   private Integer customColor;
/*     */   private List<PotionEffect> customEffects;
/*     */   @Nullable
/*     */   private String customName;
/*     */   
/*     */   public ItemPotionContents(@Nullable Potion potion, @Nullable Integer customColor, List<PotionEffect> customEffects) {
/*  43 */     this(potion, customColor, customEffects, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ItemPotionContents(@Nullable Potion potion, @Nullable Integer customColor, List<PotionEffect> customEffects, @Nullable String customName) {
/*  52 */     this.potion = potion;
/*  53 */     this.customColor = customColor;
/*  54 */     this.customEffects = customEffects;
/*  55 */     this.customName = customName;
/*     */   }
/*     */   
/*     */   public static ItemPotionContents read(PacketWrapper<?> wrapper) {
/*  59 */     Potion potionId = (Potion)wrapper.readOptional(ew -> (Potion)ew.readMappedEntity(Potions::getById));
/*  60 */     Integer customColor = (Integer)wrapper.readOptional(PacketWrapper::readInt);
/*  61 */     List<PotionEffect> customEffects = wrapper.readList(PotionEffect::read);
/*     */     
/*  63 */     String customName = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2) ? (String)wrapper.readOptional(PacketWrapper::readString) : null;
/*  64 */     return new ItemPotionContents(potionId, customColor, customEffects, customName);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemPotionContents contents) {
/*  68 */     wrapper.writeOptional(contents.potion, PacketWrapper::writeMappedEntity);
/*  69 */     wrapper.writeOptional(contents.customColor, PacketWrapper::writeInt);
/*  70 */     wrapper.writeList(contents.customEffects, PotionEffect::write);
/*  71 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2))
/*  72 */       wrapper.writeOptional(contents.customName, PacketWrapper::writeString); 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Potion getPotion() {
/*  77 */     return this.potion;
/*     */   }
/*     */   
/*     */   public void setPotion(@Nullable Potion potion) {
/*  81 */     this.potion = potion;
/*     */   }
/*     */   @Nullable
/*     */   public Integer getCustomColor() {
/*  85 */     return this.customColor;
/*     */   }
/*     */   
/*     */   public void setCustomColor(@Nullable Integer customColor) {
/*  89 */     this.customColor = customColor;
/*     */   }
/*     */   
/*     */   private void addCustomEffect(PotionEffect effect) {
/*  93 */     this.customEffects.add(effect);
/*     */   }
/*     */   
/*     */   public List<PotionEffect> getCustomEffects() {
/*  97 */     return this.customEffects;
/*     */   }
/*     */   
/*     */   public void setCustomEffects(List<PotionEffect> customEffects) {
/* 101 */     this.customEffects = customEffects;
/*     */   }
/*     */   @Nullable
/*     */   public String getCustomName() {
/* 105 */     return this.customName;
/*     */   }
/*     */   
/*     */   public void setCustomName(@Nullable String customName) {
/* 109 */     this.customName = customName;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 114 */     if (this == obj) return true; 
/* 115 */     if (!(obj instanceof ItemPotionContents)) return false; 
/* 116 */     ItemPotionContents that = (ItemPotionContents)obj;
/* 117 */     if (!Objects.equals(this.potion, that.potion)) return false; 
/* 118 */     if (!Objects.equals(this.customColor, that.customColor)) return false; 
/* 119 */     if (!this.customEffects.equals(that.customEffects)) return false; 
/* 120 */     return Objects.equals(this.customName, that.customName);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 125 */     return Objects.hash(new Object[] { this.potion, this.customColor, this.customEffects, this.customName });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemPotionContents.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */