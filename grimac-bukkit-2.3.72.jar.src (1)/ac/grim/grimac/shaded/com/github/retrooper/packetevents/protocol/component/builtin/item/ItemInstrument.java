/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.instrument.Instrument;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.instrument.Instruments;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MaybeMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ 
/*    */ public class ItemInstrument
/*    */ {
/*    */   private MaybeMappedEntity<Instrument> instrument;
/*    */   
/*    */   public ItemInstrument(MaybeMappedEntity<Instrument> instrument) {
/* 34 */     this.instrument = instrument;
/*    */   }
/*    */   
/*    */   public static ItemInstrument read(PacketWrapper<?> wrapper) {
/*    */     MaybeMappedEntity<Instrument> instrument;
/* 39 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/* 40 */       instrument = MaybeMappedEntity.read(wrapper, (IRegistry)Instruments.getRegistry(), Instrument::read);
/*    */     } else {
/* 42 */       instrument = new MaybeMappedEntity((MappedEntity)Instrument.read(wrapper));
/*    */     } 
/* 44 */     return new ItemInstrument(instrument);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemInstrument instrument) {
/* 48 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/* 49 */       MaybeMappedEntity.write(wrapper, instrument.instrument, Instrument::write);
/*    */     } else {
/* 51 */       Instrument.write(wrapper, (Instrument)instrument.instrument.getValueOrThrow());
/*    */     } 
/*    */   }
/*    */   
/*    */   public MaybeMappedEntity<Instrument> getInstrument() {
/* 56 */     return this.instrument;
/*    */   }
/*    */   
/*    */   public void setInstrument(MaybeMappedEntity<Instrument> instrument) {
/* 60 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 65 */     if (!(obj instanceof ItemInstrument)) return false; 
/* 66 */     ItemInstrument that = (ItemInstrument)obj;
/* 67 */     return this.instrument.equals(that.instrument);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 72 */     return Objects.hashCode(this.instrument);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */