/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Objects;
/*    */ import java.util.function.BiConsumer;
/*    */ import java.util.function.Function;
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
/*    */ public class EntityDataType<T>
/*    */   extends AbstractMappedEntity
/*    */ {
/*    */   private final PacketWrapper.Reader<? extends T> reader;
/*    */   private final PacketWrapper.Writer<T> writer;
/*    */   
/*    */   @Internal
/*    */   public EntityDataType(@Nullable TypesBuilderData data, PacketWrapper.Reader<? extends T> reader, PacketWrapper.Writer<T> writer) {
/* 41 */     super(data);
/* 42 */     this.reader = reader;
/* 43 */     this.writer = writer;
/*    */   }
/*    */   
/*    */   public T read(PacketWrapper<?> wrapper) {
/* 47 */     return (T)this.reader.apply(wrapper);
/*    */   }
/*    */   
/*    */   public void write(PacketWrapper<?> wrapper, T serializer) {
/* 51 */     this.writer.accept(wrapper, serializer);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public Function<PacketWrapper<?>, T> getDataDeserializer() {
/* 56 */     Objects.requireNonNull(this.reader); return this.reader::apply;
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public BiConsumer<PacketWrapper<?>, T> getDataSerializer() {
/* 61 */     return (BiConsumer)this.writer;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\data\EntityDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */