/*    */ package ac.grim.grimac.shaded.zaxxer.hikari.pool;
/*    */ 
/*    */ import java.sql.CallableStatement;
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
/*    */ public abstract class ProxyCallableStatement
/*    */   extends ProxyPreparedStatement
/*    */   implements CallableStatement
/*    */ {
/*    */   protected ProxyCallableStatement(ProxyConnection connection, CallableStatement statement) {
/* 30 */     super(connection, statement);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\zaxxer\hikari\pool\ProxyCallableStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */