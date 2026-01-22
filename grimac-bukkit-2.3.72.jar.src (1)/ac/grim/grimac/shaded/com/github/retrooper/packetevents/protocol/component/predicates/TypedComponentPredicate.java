/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.predicates;
/*    */ 
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
/*    */ 
/*    */ public class TypedComponentPredicate<T extends IComponentPredicate>
/*    */ {
/*    */   private final ComponentPredicateType<T> type;
/*    */   private final T predicate;
/*    */   
/*    */   public TypedComponentPredicate(ComponentPredicateType<T> type, T predicate) {
/* 31 */     this.type = type;
/* 32 */     this.predicate = predicate;
/*    */   }
/*    */ 
/*    */   
/*    */   public static TypedComponentPredicate<?> read(PacketWrapper<?> wrapper) {
/* 37 */     ComponentPredicateType<?> type = (ComponentPredicateType)wrapper.readMappedEntity((IRegistry)ComponentPredicateTypes.getRegistry());
/* 38 */     IComponentPredicate predicate = (IComponentPredicate)type.read(wrapper);
/* 39 */     return new TypedComponentPredicate(type, predicate);
/*    */   }
/*    */   
/*    */   public static <T extends IComponentPredicate> void write(PacketWrapper<?> wrapper, TypedComponentPredicate<T> predicate) {
/* 43 */     wrapper.writeMappedEntity(predicate.type);
/* 44 */     predicate.type.write(wrapper, predicate.predicate);
/*    */   }
/*    */   
/*    */   public ComponentPredicateType<T> getType() {
/* 48 */     return this.type;
/*    */   }
/*    */   
/*    */   public T getPredicate() {
/* 52 */     return this.predicate;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 57 */     if (!(obj instanceof TypedComponentPredicate)) return false; 
/* 58 */     TypedComponentPredicate<?> that = (TypedComponentPredicate)obj;
/* 59 */     if (!this.type.equals(that.type)) return false; 
/* 60 */     return this.predicate.equals(that.predicate);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 65 */     return Objects.hash(new Object[] { this.type, this.predicate });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\predicates\TypedComponentPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */