/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimmaterial.TrimMaterial;
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
/*     */ public class ArmorTrim
/*     */ {
/*     */   private TrimMaterial material;
/*     */   private TrimPattern pattern;
/*     */   @Obsolete
/*     */   private boolean showInTooltip;
/*     */   
/*     */   public ArmorTrim(TrimMaterial material, TrimPattern pattern) {
/*  40 */     this(material, pattern, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public ArmorTrim(TrimMaterial material, TrimPattern pattern, boolean showInTooltip) {
/*  48 */     this.material = material;
/*  49 */     this.pattern = pattern;
/*  50 */     this.showInTooltip = showInTooltip;
/*     */   }
/*     */   
/*     */   public static ArmorTrim read(PacketWrapper<?> wrapper) {
/*  54 */     TrimMaterial material = TrimMaterial.read(wrapper);
/*  55 */     TrimPattern pattern = TrimPattern.read(wrapper);
/*  56 */     boolean showInTooltip = (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) || wrapper.readBoolean());
/*  57 */     return new ArmorTrim(material, pattern, showInTooltip);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ArmorTrim trim) {
/*  61 */     TrimMaterial.write(wrapper, trim.material);
/*  62 */     TrimPattern.write(wrapper, trim.pattern);
/*  63 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/*  64 */       wrapper.writeBoolean(trim.showInTooltip);
/*     */     }
/*     */   }
/*     */   
/*     */   public TrimMaterial getMaterial() {
/*  69 */     return this.material;
/*     */   }
/*     */   
/*     */   public void setMaterial(TrimMaterial material) {
/*  73 */     this.material = material;
/*     */   }
/*     */   
/*     */   public TrimPattern getPattern() {
/*  77 */     return this.pattern;
/*     */   }
/*     */   
/*     */   public void setPattern(TrimPattern pattern) {
/*  81 */     this.pattern = pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public boolean isShowInTooltip() {
/*  89 */     return this.showInTooltip;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public void setShowInTooltip(boolean showInTooltip) {
/*  97 */     this.showInTooltip = showInTooltip;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 102 */     if (this == obj) return true; 
/* 103 */     if (!(obj instanceof ArmorTrim)) return false; 
/* 104 */     ArmorTrim armorTrim = (ArmorTrim)obj;
/* 105 */     if (this.showInTooltip != armorTrim.showInTooltip) return false; 
/* 106 */     if (!this.material.equals(armorTrim.material)) return false; 
/* 107 */     return this.pattern.equals(armorTrim.pattern);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 112 */     return Objects.hash(new Object[] { this.material, this.pattern, Boolean.valueOf(this.showInTooltip) });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 117 */     return "ArmorTrim{material=" + this.material + ", pattern=" + this.pattern + ", showInTooltip=" + this.showInTooltip + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ArmorTrim.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */