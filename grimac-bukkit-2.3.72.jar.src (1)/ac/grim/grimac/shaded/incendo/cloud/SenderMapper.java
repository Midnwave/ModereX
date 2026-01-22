/*    */ package ac.grim.grimac.shaded.incendo.cloud;
/*    */ 
/*    */ import java.util.function.Function;
/*    */ import org.apiguardian.api.API;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface SenderMapper<B, M>
/*    */ {
/*    */   M map(B paramB);
/*    */   
/*    */   B reverse(M paramM);
/*    */   
/*    */   static <B, M> SenderMapper<B, M> create(Function<B, M> map, Function<M, B> reverse) {
/* 68 */     return new SenderMapperImpl<>(map, reverse);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static <S> SenderMapper<S, S> identity() {
/* 79 */     return (SenderMapper)SenderMapperImpl.IDENTITY;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\SenderMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */