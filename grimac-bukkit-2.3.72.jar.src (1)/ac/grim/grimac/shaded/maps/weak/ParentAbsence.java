/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import ac.grim.grimac.shaded.maps.LiteJoiner;
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.NoSuchElementException;
/*    */ import java.util.stream.Stream;
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
/*    */ public abstract class ParentAbsence<Parent extends Dynamic>
/*    */   extends AbstractAbsence<Parent>
/*    */   implements IssueDescribingChild
/*    */ {
/*    */   public ParentAbsence(Parent parent, Object key) {
/* 29 */     super(parent, key);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String describeIssue(List<Object> childKeys) {
/* 36 */     LinkedList<Object> keyChain = DynamicChildLogic.using(this).getAscendingKeyChainWithRoot();
/* 37 */     keyChain.set(keyChain.size() - 2, "*" + keyChain.get(keyChain.size() - 2).toString() + "*");
/* 38 */     keyChain.addAll(childKeys);
/* 39 */     return describeIssue(keyChain, this.parent.key().asObject());
/*    */   }
/*    */ 
/*    */   
/*    */   public Dynamic get(Object key) {
/* 44 */     return new DescriptionDeferringAbsence(this, key);
/*    */   }
/*    */   protected abstract String describeIssue(LinkedList<Object> paramLinkedList, Object paramObject);
/*    */   
/*    */   public Object asObject() {
/* 49 */     throw new NoSuchElementException(describeIssue(Collections.emptyList()));
/*    */   }
/*    */   
/*    */   public static class Empty<P extends Dynamic & Describer>
/*    */     extends ParentAbsence<P> {
/*    */     public Empty(P parent, Object key) {
/* 55 */       super(parent, key);
/*    */     }
/*    */ 
/*    */     
/*    */     protected String describeIssue(LinkedList<Object> ascendingMarkedKeyChain, Object parentKey) {
/* 60 */       return String.format("%s '%s' premature end of path %s", new Object[] { ((Describer)this.parent).describe(), parentKey, 
/* 61 */             LiteJoiner.on("->").join(ascendingMarkedKeyChain) });
/*    */     }
/*    */   }
/*    */   
/*    */   public static class Barren<P extends Dynamic & Describer>
/*    */     extends ParentAbsence<P> {
/*    */     public Barren(P parent, Object key) {
/* 68 */       super(parent, key);
/*    */     }
/*    */ 
/*    */     
/*    */     protected String describeIssue(LinkedList<Object> ascendingMarkedKeyChain, Object parentKey) {
/* 73 */       return String.format("%s '%s' premature end of path %s", new Object[] { ((Describer)this.parent).describe(), parentKey, 
/* 74 */             LiteJoiner.on("->").join(ascendingMarkedKeyChain) });
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\ParentAbsence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */