/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.color.DyeColor;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.banner.BannerPattern;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.banner.BannerPatterns;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*     */ public class BannerLayers
/*     */ {
/*     */   private List<Layer> layers;
/*     */   
/*     */   public BannerLayers(List<Layer> layers) {
/*  34 */     this.layers = layers;
/*     */   }
/*     */   
/*     */   public static BannerLayers read(PacketWrapper<?> wrapper) {
/*  38 */     List<Layer> layers = wrapper.readList(Layer::read);
/*  39 */     return new BannerLayers(layers);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, BannerLayers patterns) {
/*  43 */     wrapper.writeList(patterns.layers, Layer::write);
/*     */   }
/*     */   
/*     */   public void addLayer(Layer layer) {
/*  47 */     this.layers.add(layer);
/*     */   }
/*     */   
/*     */   public List<Layer> getLayers() {
/*  51 */     return this.layers;
/*     */   }
/*     */   
/*     */   public void setLayers(List<Layer> layers) {
/*  55 */     this.layers = layers;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  60 */     if (this == obj) return true; 
/*  61 */     if (!(obj instanceof BannerLayers)) return false; 
/*  62 */     BannerLayers that = (BannerLayers)obj;
/*  63 */     return this.layers.equals(that.layers);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  68 */     return Objects.hashCode(this.layers);
/*     */   }
/*     */   
/*     */   public static class Layer
/*     */   {
/*     */     private BannerPattern pattern;
/*     */     private DyeColor color;
/*     */     
/*     */     public Layer(BannerPattern pattern, DyeColor color) {
/*  77 */       this.pattern = pattern;
/*  78 */       this.color = color;
/*     */     }
/*     */     
/*     */     public static Layer read(PacketWrapper<?> wrapper) {
/*  82 */       BannerPattern pattern = (BannerPattern)wrapper.readMappedEntityOrDirect(
/*  83 */           (IRegistry)BannerPatterns.getRegistry(), BannerPattern::readDirect);
/*  84 */       DyeColor color = DyeColor.read(wrapper);
/*  85 */       return new Layer(pattern, color);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, Layer layer) {
/*  89 */       wrapper.writeMappedEntityOrDirect((MappedEntity)layer.pattern, BannerPattern::writeDirect);
/*  90 */       DyeColor.write(wrapper, layer.color);
/*     */     }
/*     */     
/*     */     public BannerPattern getPattern() {
/*  94 */       return this.pattern;
/*     */     }
/*     */     
/*     */     public void setPattern(BannerPattern pattern) {
/*  98 */       this.pattern = pattern;
/*     */     }
/*     */     
/*     */     public DyeColor getColor() {
/* 102 */       return this.color;
/*     */     }
/*     */     
/*     */     public void setColor(DyeColor color) {
/* 106 */       this.color = color;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 111 */       if (this == obj) return true; 
/* 112 */       if (!(obj instanceof Layer)) return false; 
/* 113 */       Layer layer = (Layer)obj;
/* 114 */       if (!this.pattern.equals(layer.pattern)) return false; 
/* 115 */       return (this.color == layer.color);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 120 */       return Objects.hash(new Object[] { this.pattern, this.color });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\BannerLayers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */