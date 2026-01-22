/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimpattern.TrimPattern;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
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
/*     */ public class SmithingTrimSlotDisplay
/*     */   extends SlotDisplay<SmithingTrimSlotDisplay>
/*     */ {
/*     */   private SlotDisplay<?> base;
/*     */   private SlotDisplay<?> material;
/*     */   private TrimPattern trimPattern;
/*     */   @Obsolete
/*     */   private SlotDisplay<?> pattern;
/*     */   
/*     */   public SmithingTrimSlotDisplay(SlotDisplay<?> base, SlotDisplay<?> material, TrimPattern trimPattern) {
/*  50 */     super(SlotDisplayTypes.SMITHING_TRIM);
/*  51 */     this.base = base;
/*  52 */     this.material = material;
/*  53 */     this.trimPattern = trimPattern;
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
/*     */   public SmithingTrimSlotDisplay(SlotDisplay<?> base, SlotDisplay<?> material, SlotDisplay<?> pattern) {
/*  65 */     super(SlotDisplayTypes.SMITHING_TRIM);
/*  66 */     this.base = base;
/*  67 */     this.material = material;
/*  68 */     this.pattern = pattern;
/*     */   }
/*     */   
/*     */   public static SmithingTrimSlotDisplay read(PacketWrapper<?> wrapper) {
/*  72 */     SlotDisplay<?> base = SlotDisplay.read(wrapper);
/*  73 */     SlotDisplay<?> material = SlotDisplay.read(wrapper);
/*  74 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/*  75 */       TrimPattern trimPattern = TrimPattern.read(wrapper);
/*  76 */       return new SmithingTrimSlotDisplay(base, material, trimPattern);
/*     */     } 
/*  78 */     SlotDisplay<?> pattern = SlotDisplay.read(wrapper);
/*  79 */     return new SmithingTrimSlotDisplay(base, material, pattern);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, SmithingTrimSlotDisplay display) {
/*  84 */     SlotDisplay.write(wrapper, display.base);
/*  85 */     SlotDisplay.write(wrapper, display.material);
/*  86 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/*  87 */       TrimPattern.write(wrapper, display.trimPattern);
/*     */     } else {
/*  89 */       SlotDisplay.write(wrapper, display.pattern);
/*     */     } 
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getBase() {
/*  94 */     return this.base;
/*     */   }
/*     */   
/*     */   public void setBase(SlotDisplay<?> base) {
/*  98 */     this.base = base;
/*     */   }
/*     */   
/*     */   public SlotDisplay<?> getMaterial() {
/* 102 */     return this.material;
/*     */   }
/*     */   
/*     */   public void setMaterial(SlotDisplay<?> material) {
/* 106 */     this.material = material;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public SlotDisplay<?> getPattern() {
/* 114 */     return this.pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public void setPattern(SlotDisplay<?> pattern) {
/* 122 */     this.pattern = pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TrimPattern getTrimPattern() {
/* 129 */     return this.trimPattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTrimPattern(TrimPattern trimPattern) {
/* 136 */     this.trimPattern = trimPattern;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 141 */     if (this == obj) return true; 
/* 142 */     if (!(obj instanceof SmithingTrimSlotDisplay)) return false; 
/* 143 */     SmithingTrimSlotDisplay that = (SmithingTrimSlotDisplay)obj;
/* 144 */     if (!this.base.equals(that.base)) return false; 
/* 145 */     if (!this.material.equals(that.material)) return false; 
/* 146 */     if (!Objects.equals(this.pattern, that.pattern)) return false; 
/* 147 */     return Objects.equals(this.trimPattern, that.trimPattern);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 152 */     return Objects.hash(new Object[] { this.base, this.material, this.pattern, this.trimPattern });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 157 */     return "SmithingTrimSlotDisplay{base=" + this.base + ", material=" + this.material + ", trimPattern=" + this.trimPattern + ", pattern=" + this.pattern + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\SmithingTrimSlotDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */