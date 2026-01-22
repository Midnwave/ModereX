/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent;
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
/*    */ @NullMarked
/*    */ public class StaticClickEventAction<T extends ClickEvent>
/*    */   extends AbstractMappedEntity
/*    */   implements ClickEventAction<T>
/*    */ {
/*    */   private final boolean allowFromServer;
/*    */   private final NbtMapDecoder<T> decoder;
/*    */   private final NbtMapEncoder<T> encoder;
/*    */   
/*    */   @Internal
/*    */   public StaticClickEventAction(@Nullable TypesBuilderData data, boolean allowFromServer, NbtMapDecoder<T> decoder, NbtMapEncoder<T> encoder) {
/* 43 */     super(data);
/* 44 */     this.allowFromServer = allowFromServer;
/* 45 */     this.decoder = decoder;
/* 46 */     this.encoder = encoder;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isAllowFromServer() {
/* 51 */     return this.allowFromServer;
/*    */   }
/*    */ 
/*    */   
/*    */   public T decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 56 */     return (T)this.decoder.decode(compound, wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   public void encode(NBTCompound compound, PacketWrapper<?> wrapper, T clickEvent) {
/* 61 */     this.encoder.encode(compound, wrapper, clickEvent);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\StaticClickEventAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */