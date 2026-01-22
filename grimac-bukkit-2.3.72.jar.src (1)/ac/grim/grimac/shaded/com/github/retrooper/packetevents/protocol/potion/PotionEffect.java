/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ 
/*    */ 
/*    */ public class PotionEffect
/*    */ {
/*    */   private final PotionType type;
/*    */   private final Properties properties;
/*    */   
/*    */   public PotionEffect(PotionType type, int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, @Nullable Properties hiddenEffect) {
/* 33 */     this(type, new Properties(amplifier, duration, ambient, showParticles, showIcon, hiddenEffect));
/*    */   }
/*    */ 
/*    */   
/*    */   public PotionEffect(PotionType type, Properties properties) {
/* 38 */     this.type = type;
/* 39 */     this.properties = properties;
/*    */   }
/*    */   
/*    */   public static PotionEffect read(PacketWrapper<?> wrapper) {
/* 43 */     PotionType type = (PotionType)wrapper.readMappedEntity(PotionTypes::getById);
/* 44 */     Properties props = Properties.read(wrapper);
/* 45 */     return new PotionEffect(type, props);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, PotionEffect effect) {
/* 49 */     wrapper.writeMappedEntity(effect.type);
/* 50 */     Properties.write(wrapper, effect.properties);
/*    */   }
/*    */ 
/*    */   
/*    */   public static class Properties
/*    */   {
/*    */     private final int amplifier;
/*    */     
/*    */     private final int duration;
/*    */     private final boolean ambient;
/*    */     private final boolean showParticles;
/*    */     private final boolean showIcon;
/*    */     @Nullable
/*    */     private final Properties hiddenEffect;
/*    */     
/*    */     public Properties(int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, @Nullable Properties hiddenEffect) {
/* 66 */       this.amplifier = amplifier;
/* 67 */       this.duration = duration;
/* 68 */       this.ambient = ambient;
/* 69 */       this.showParticles = showParticles;
/* 70 */       this.showIcon = showIcon;
/* 71 */       this.hiddenEffect = hiddenEffect;
/*    */     }
/*    */     
/*    */     public static Properties read(PacketWrapper<?> wrapper) {
/* 75 */       int amplifier = wrapper.readVarInt();
/* 76 */       int duration = wrapper.readVarInt();
/* 77 */       boolean ambient = wrapper.readBoolean();
/* 78 */       boolean showParticles = wrapper.readBoolean();
/* 79 */       boolean showIcon = wrapper.readBoolean();
/* 80 */       Properties hiddenEffect = (Properties)wrapper.readOptional(Properties::read);
/* 81 */       return new Properties(amplifier, duration, ambient, showParticles, showIcon, hiddenEffect);
/*    */     }
/*    */     
/*    */     public static void write(PacketWrapper<?> wrapper, Properties props) {
/* 85 */       wrapper.writeVarInt(props.amplifier);
/* 86 */       wrapper.writeVarInt(props.duration);
/* 87 */       wrapper.writeBoolean(props.ambient);
/* 88 */       wrapper.writeBoolean(props.showParticles);
/* 89 */       wrapper.writeBoolean(props.showIcon);
/* 90 */       wrapper.writeOptional(props.hiddenEffect, Properties::write);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\potion\PotionEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */