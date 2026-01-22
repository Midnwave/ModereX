/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.predicates;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentPredicate;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ 
/*    */ public class ComponentMatchers
/*    */ {
/*    */   private ComponentPredicate components;
/*    */   private List<TypedComponentPredicate<?>> predicates;
/*    */   
/*    */   public ComponentMatchers() {
/* 34 */     this(new ComponentPredicate(), new ArrayList<>());
/*    */   }
/*    */   
/*    */   public ComponentMatchers(ComponentPredicate components, List<TypedComponentPredicate<?>> predicates) {
/* 38 */     this.components = components;
/* 39 */     this.predicates = predicates;
/*    */   }
/*    */   
/*    */   public static ComponentMatchers read(PacketWrapper<?> wrapper) {
/* 43 */     ComponentPredicate components = ComponentPredicate.read(wrapper);
/* 44 */     List<TypedComponentPredicate<?>> predicates = wrapper.readList(TypedComponentPredicate::read);
/* 45 */     return new ComponentMatchers(components, predicates);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ComponentMatchers matchers) {
/* 49 */     ComponentPredicate.write(wrapper, matchers.components);
/* 50 */     wrapper.writeList(matchers.predicates, TypedComponentPredicate::write);
/*    */   }
/*    */   
/*    */   public ComponentPredicate getComponents() {
/* 54 */     return this.components;
/*    */   }
/*    */   
/*    */   public void setComponents(ComponentPredicate components) {
/* 58 */     this.components = components;
/*    */   }
/*    */   
/*    */   public List<TypedComponentPredicate<?>> getPredicates() {
/* 62 */     return this.predicates;
/*    */   }
/*    */   
/*    */   public void setPredicates(List<TypedComponentPredicate<?>> predicates) {
/* 66 */     this.predicates = predicates;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 71 */     if (!(obj instanceof ComponentMatchers)) return false; 
/* 72 */     ComponentMatchers that = (ComponentMatchers)obj;
/* 73 */     if (!this.components.equals(that.components)) return false; 
/* 74 */     return this.predicates.equals(that.predicates);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 79 */     return Objects.hash(new Object[] { this.components, this.predicates });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\predicates\ComponentMatchers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */