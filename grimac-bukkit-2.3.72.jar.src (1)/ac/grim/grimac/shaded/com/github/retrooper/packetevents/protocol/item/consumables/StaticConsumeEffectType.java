/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*    */ 
/*    */ public class StaticConsumeEffectType<T extends ConsumeEffect<?>>
/*    */   extends AbstractMappedEntity
/*    */   implements ConsumeEffectType<T>
/*    */ {
/*    */   private final PacketWrapper.Reader<T> reader;
/*    */   private final PacketWrapper.Writer<T> writer;
/*    */   
/*    */   @Internal
/*    */   public StaticConsumeEffectType(@Nullable TypesBuilderData data, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/* 40 */     super(data);
/* 41 */     this.reader = reader;
/* 42 */     this.writer = writer;
/*    */   }
/*    */ 
/*    */   
/*    */   public T read(PacketWrapper<?> wrapper) {
/* 47 */     return (T)this.reader.apply(wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(PacketWrapper<?> wrapper, T effect) {
/* 52 */     this.writer.accept(wrapper, effect);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\consumables\StaticConsumeEffectType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */