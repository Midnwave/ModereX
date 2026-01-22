/*    */ package ac.grim.grimac.shaded.incendo.cloud.parser;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import java.util.Objects;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public class ParserParameter<T>
/*    */ {
/*    */   private final String key;
/*    */   private final TypeToken<T> expectedType;
/*    */   
/*    */   public ParserParameter(String key, TypeToken<T> expectedType) {
/* 52 */     this.key = key;
/* 53 */     this.expectedType = expectedType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String key() {
/* 62 */     return this.key;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TypeToken<T> expectedType() {
/* 71 */     return this.expectedType;
/*    */   }
/*    */ 
/*    */   
/*    */   public final boolean equals(Object o) {
/* 76 */     if (this == o) {
/* 77 */       return true;
/*    */     }
/* 79 */     if (o == null || getClass() != o.getClass()) {
/* 80 */       return false;
/*    */     }
/* 82 */     ParserParameter<?> that = (ParserParameter)o;
/* 83 */     return (Objects.equals(this.key, that.key) && 
/* 84 */       Objects.equals(this.expectedType, that.expectedType));
/*    */   }
/*    */ 
/*    */   
/*    */   public final int hashCode() {
/* 89 */     return Objects.hash(new Object[] { this.key, this.expectedType });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\ParserParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */