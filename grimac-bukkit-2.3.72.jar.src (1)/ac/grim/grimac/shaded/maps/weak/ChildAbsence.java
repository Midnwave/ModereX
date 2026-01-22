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
/*    */ public abstract class ChildAbsence<Parent extends Dynamic>
/*    */   extends AbstractAbsence<Parent>
/*    */   implements IssueDescribingChild
/*    */ {
/*    */   protected ChildAbsence(Parent parent, Object key) {
/* 28 */     super(parent, key);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String describeIssue(List<Object> childKeys) {
/* 35 */     LinkedList<Object> keyChainUntilSelf = DynamicChildLogic.using(this).getAscendingKeyChainWithRoot();
/* 36 */     keyChainUntilSelf.removeLast();
/*    */     
/* 38 */     LinkedList<Object> fullKeyChain = new LinkedList(keyChainUntilSelf);
/* 39 */     fullKeyChain.addLast("*" + this.key + "*");
/* 40 */     fullKeyChain.addAll(childKeys);
/* 41 */     return describeIssue(fullKeyChain, keyChainUntilSelf);
/*    */   }
/*    */ 
/*    */   
/*    */   public Dynamic get(Object key) {
/* 46 */     return new DescriptionDeferringAbsence(this, key);
/*    */   }
/*    */   protected abstract String describeIssue(LinkedList<Object> paramLinkedList1, LinkedList<Object> paramLinkedList2);
/*    */   
/*    */   public Object asObject() {
/* 51 */     throw new NoSuchElementException(describeIssue(Collections.emptyList()));
/*    */   }
/*    */   
/*    */   public static class Null
/*    */     extends ChildAbsence<Dynamic> {
/*    */     public Null(Dynamic parent, Object key) {
/* 57 */       super(parent, key);
/*    */     }
/*    */ 
/*    */     
/*    */     protected String describeIssue(LinkedList<Object> ascendingMarkedKeyChain, LinkedList<Object> ascendingKeyChainBeforeSelf) {
/* 62 */       return String.format("null '%s' premature end of path %s", new Object[] { this.key, LiteJoiner.on("->").join(ascendingMarkedKeyChain) });
/*    */     }
/*    */   }
/*    */   
/*    */   public static class Missing<P extends Dynamic & Describer>
/*    */     extends ChildAbsence<P> {
/*    */     public Missing(P parent, Object key) {
/* 69 */       super(parent, key);
/*    */     }
/*    */ 
/*    */     
/*    */     protected String describeIssue(LinkedList<Object> ascendingMarkedKeyChain, LinkedList<Object> ascendingKeyChainBeforeSelf) {
/* 74 */       return String.format("'%s' key is missing in path %s, from %s: %s", new Object[] { this.key, 
/* 75 */             LiteJoiner.on("->").join(ascendingMarkedKeyChain), LiteJoiner.on("->").join(ascendingKeyChainBeforeSelf), ((Describer)this.parent)
/* 76 */             .describe() });
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\ChildAbsence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */