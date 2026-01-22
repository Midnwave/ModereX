/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.NoSuchElementException;
/*    */ import java.util.stream.Collectors;
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
/*    */ class DescriptionDeferringAbsence
/*    */   extends AbstractAbsence<DynamicChild>
/*    */ {
/*    */   DescriptionDeferringAbsence(DescriptionDeferringAbsence parent, Object key) {
/* 26 */     super(parent, key);
/*    */   }
/*    */   
/*    */   DescriptionDeferringAbsence(IssueDescribingChild parent, Object key) {
/* 30 */     super(parent, key);
/*    */   }
/*    */ 
/*    */   
/*    */   public Dynamic get(Object key) {
/* 35 */     return new DescriptionDeferringAbsence(this, key);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object asObject() {
/* 40 */     LinkedList<DynamicChild> chainFromDescriber = DynamicChildLogic.using(this).getAscendingChainAllWith(DescriptionDeferringAbsence.class::isInstance);
/*    */     
/* 42 */     throw new NoSuchElementException(((IssueDescribingChild)((DynamicChild)chainFromDescriber.getFirst()).parent())
/* 43 */         .describeIssue((List)chainFromDescriber.stream().map(child -> child.key().asObject()).collect(Collectors.toList())));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\DescriptionDeferringAbsence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */