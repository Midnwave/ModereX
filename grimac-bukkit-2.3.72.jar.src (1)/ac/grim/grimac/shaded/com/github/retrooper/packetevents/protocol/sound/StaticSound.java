/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Objects;
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
/*    */ public class StaticSound
/*    */   extends AbstractMappedEntity
/*    */   implements Sound
/*    */ {
/*    */   private final ResourceLocation soundId;
/*    */   @Nullable
/*    */   private final Float range;
/*    */   
/*    */   public StaticSound(ResourceLocation soundId, @Nullable Float range) {
/* 35 */     this(null, soundId, range);
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public StaticSound(@Nullable TypesBuilderData data, ResourceLocation soundId, @Nullable Float range) {
/* 40 */     super(data);
/* 41 */     this.soundId = soundId;
/* 42 */     this.range = range;
/*    */   }
/*    */ 
/*    */   
/*    */   public ResourceLocation getSoundId() {
/* 47 */     return this.soundId;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Float getRange() {
/* 52 */     return this.range;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 57 */     if (this == obj) return true; 
/* 58 */     if (!(obj instanceof StaticSound)) return false; 
/* 59 */     StaticSound that = (StaticSound)obj;
/* 60 */     if (isRegistered()) {
/* 61 */       return super.equals(obj);
/*    */     }
/* 63 */     if (!this.soundId.equals(that.soundId)) return false; 
/* 64 */     return Objects.equals(this.range, that.range);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 69 */     return isRegistered() ? super.hashCode() : Objects.hash(new Object[] { this.soundId, this.range });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\sound\StaticSound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */