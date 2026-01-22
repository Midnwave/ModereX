/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class LegacyParticleData
/*    */   extends ParticleData
/*    */   implements LegacyConvertible {
/*    */   private final int[] legacyData;
/*    */   
/*    */   public static LegacyParticleData nullValue(int id) {
/* 15 */     int[] data = new int[getSize(id)];
/* 16 */     Arrays.fill(data, 0);
/* 17 */     return new LegacyParticleData(data);
/*    */   }
/*    */   
/*    */   public static LegacyParticleData ofTwo(int a, int b) {
/* 21 */     return new LegacyParticleData(new int[] { a, b });
/*    */   }
/*    */   
/*    */   public static LegacyParticleData ofOne(int a) {
/* 25 */     return new LegacyParticleData(new int[] { a });
/*    */   }
/*    */   
/*    */   public static LegacyParticleData zero() {
/* 29 */     return new LegacyParticleData(new int[0]);
/*    */   }
/*    */   
/*    */   public static LegacyParticleData ofBlock(ItemType type, byte data) {
/* 33 */     return new LegacyParticleData(new int[] { type.getId(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion()) | data << 12 });
/*    */   }
/*    */   
/*    */   public static LegacyParticleData ofItem(ItemType type, byte data) {
/* 37 */     return new LegacyParticleData(new int[] { type.getId(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion()), data });
/*    */   }
/*    */   
/*    */   LegacyParticleData(int... legacyData) {
/* 41 */     this.legacyData = legacyData;
/*    */   }
/*    */   
/*    */   public int[] getLegacyData() {
/* 45 */     return this.legacyData;
/*    */   }
/*    */   
/*    */   public void validate(int id) {
/* 49 */     if (this.legacyData.length != getSize(id))
/* 50 */       throw new IllegalArgumentException("Invalid size for type " + id + ": " + this.legacyData.length); 
/*    */   }
/*    */   
/*    */   public static int getSize(int id) {
/* 54 */     if (id == 36) return 2; 
/* 55 */     if (id == 37 || id == 38 || id == 46) return 1; 
/* 56 */     return 0;
/*    */   }
/*    */   
/*    */   public static LegacyParticleData read(PacketWrapper<?> wrapper, int typeId) {
/* 60 */     int[] data = new int[getSize(typeId)];
/* 61 */     for (int i = 0; i < data.length; i++) {
/* 62 */       data[i] = wrapper.readVarInt();
/*    */     }
/* 64 */     return new LegacyParticleData(data);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, int typeId, LegacyParticleData data) {
/* 68 */     data.validate(typeId);
/* 69 */     for (int i = 0; i < data.legacyData.length; i++) {
/* 70 */       wrapper.writeVarInt(data.legacyData[i]);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 76 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public LegacyParticleData toLegacy(ClientVersion version) {
/* 81 */     return new LegacyParticleData(this.legacyData);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\data\LegacyParticleData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */