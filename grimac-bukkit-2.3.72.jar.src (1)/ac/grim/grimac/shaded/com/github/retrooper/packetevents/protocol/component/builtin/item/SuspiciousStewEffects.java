/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
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
/*     */ 
/*     */ public class SuspiciousStewEffects
/*     */ {
/*     */   private List<EffectEntry> effects;
/*     */   
/*     */   public SuspiciousStewEffects(List<EffectEntry> effects) {
/*  33 */     this.effects = effects;
/*     */   }
/*     */   
/*     */   public static SuspiciousStewEffects read(PacketWrapper<?> wrapper) {
/*  37 */     List<EffectEntry> effects = wrapper.readList(EffectEntry::read);
/*  38 */     return new SuspiciousStewEffects(effects);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, SuspiciousStewEffects effects) {
/*  42 */     wrapper.writeList(effects.effects, EffectEntry::write);
/*     */   }
/*     */   
/*     */   public void addEffect(EffectEntry effect) {
/*  46 */     this.effects.add(effect);
/*     */   }
/*     */   
/*     */   public List<EffectEntry> getEffects() {
/*  50 */     return this.effects;
/*     */   }
/*     */   
/*     */   public void setEffects(List<EffectEntry> effects) {
/*  54 */     this.effects = effects;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  59 */     if (this == obj) return true; 
/*  60 */     if (!(obj instanceof SuspiciousStewEffects)) return false; 
/*  61 */     SuspiciousStewEffects that = (SuspiciousStewEffects)obj;
/*  62 */     return this.effects.equals(that.effects);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  67 */     return Objects.hashCode(this.effects);
/*     */   }
/*     */   
/*     */   public static class EffectEntry
/*     */   {
/*     */     private PotionType type;
/*     */     private int duration;
/*     */     
/*     */     public EffectEntry(PotionType type, int duration) {
/*  76 */       this.type = type;
/*  77 */       this.duration = duration;
/*     */     }
/*     */     
/*     */     public static EffectEntry read(PacketWrapper<?> wrapper) {
/*  81 */       PotionType type = (PotionType)wrapper.readMappedEntity(PotionTypes::getById);
/*  82 */       int duration = wrapper.readVarInt();
/*  83 */       return new EffectEntry(type, duration);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, EffectEntry effect) {
/*  87 */       wrapper.writeMappedEntity((MappedEntity)effect.type);
/*  88 */       wrapper.writeVarInt(effect.duration);
/*     */     }
/*     */     
/*     */     public PotionType getType() {
/*  92 */       return this.type;
/*     */     }
/*     */     
/*     */     public void setType(PotionType type) {
/*  96 */       this.type = type;
/*     */     }
/*     */     
/*     */     public int getDuration() {
/* 100 */       return this.duration;
/*     */     }
/*     */     
/*     */     public void setDuration(int duration) {
/* 104 */       this.duration = duration;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 109 */       if (this == obj) return true; 
/* 110 */       if (!(obj instanceof EffectEntry)) return false; 
/* 111 */       EffectEntry that = (EffectEntry)obj;
/* 112 */       if (this.duration != that.duration) return false; 
/* 113 */       return this.type.equals(that.type);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 118 */       return Objects.hash(new Object[] { this.type, Integer.valueOf(this.duration) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\SuspiciousStewEffects.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */