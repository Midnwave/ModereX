/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.predicates;
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
/*    */ public class StaticComponentPredicateType<T extends IComponentPredicate>
/*    */   extends AbstractMappedEntity
/*    */   implements ComponentPredicateType<T>
/*    */ {
/*    */   private final PacketWrapper.Reader<T> reader;
/*    */   private final PacketWrapper.Writer<T> writer;
/*    */   
/*    */   public StaticComponentPredicateType(PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/* 33 */     this(null, reader, writer);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public StaticComponentPredicateType(@Nullable TypesBuilderData data, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/* 42 */     super(data);
/* 43 */     this.reader = reader;
/* 44 */     this.writer = writer;
/*    */   }
/*    */ 
/*    */   
/*    */   public T read(PacketWrapper<?> wrapper) {
/* 49 */     return (T)this.reader.apply(wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(PacketWrapper<?> wrapper, T predicate) {
/* 54 */     this.writer.accept(wrapper, predicate);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\predicates\StaticComponentPredicateType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */