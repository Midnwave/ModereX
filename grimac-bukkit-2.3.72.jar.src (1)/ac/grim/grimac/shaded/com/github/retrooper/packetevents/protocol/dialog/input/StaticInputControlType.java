/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.input;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtMapDecoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtMapEncoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ @NullMarked
/*    */ public class StaticInputControlType<T extends InputControl>
/*    */   extends AbstractMappedEntity
/*    */   implements InputControlType<T>
/*    */ {
/*    */   private final NbtMapDecoder<T> decoder;
/*    */   private final NbtMapEncoder<T> encoder;
/*    */   
/*    */   @Internal
/*    */   public StaticInputControlType(@Nullable TypesBuilderData data, NbtMapDecoder<T> decoder, NbtMapEncoder<T> encoder) {
/* 43 */     super(data);
/* 44 */     this.decoder = decoder;
/* 45 */     this.encoder = encoder;
/*    */   }
/*    */ 
/*    */   
/*    */   public T decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 50 */     return (T)this.decoder.decode(compound, wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   public void encode(NBTCompound compound, PacketWrapper<?> wrapper, T control) {
/* 55 */     this.encoder.encode(compound, wrapper, control);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\input\StaticInputControlType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */