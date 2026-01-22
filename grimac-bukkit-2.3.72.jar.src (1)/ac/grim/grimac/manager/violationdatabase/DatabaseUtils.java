/*    */ package ac.grim.grimac.manager.violationdatabase;
/*    */ 
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.sql.Connection;
/*    */ import java.sql.PreparedStatement;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class DatabaseUtils
/*    */ {
/*    */   public static byte[] uuidToBytes(UUID uuid) {
/* 14 */     ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
/* 15 */     bb.putLong(uuid.getMostSignificantBits());
/* 16 */     bb.putLong(uuid.getLeastSignificantBits());
/* 17 */     return bb.array();
/*    */   }
/*    */   
/*    */   public static UUID bytesToUuid(byte[] bytes) {
/* 21 */     if (bytes == null || bytes.length != 16) {
/* 22 */       throw new IllegalArgumentException("UUID bytes must be 16 bytes long. Received: " + ((bytes == null) ? "null" : ("" + bytes.length + " bytes")));
/*    */     }
/* 24 */     ByteBuffer bb = ByteBuffer.wrap(bytes);
/* 25 */     long msb = bb.getLong();
/* 26 */     long lsb = bb.getLong();
/* 27 */     return new UUID(msb, lsb);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static long getOrCreateId(Connection connection, DatabaseDialect dialect, String tableName, String stringColumnName, String value) throws SQLException {
/* 33 */     String insertSql = dialect.getInsertOrIgnoreSyntax(tableName, stringColumnName);
/*    */     
/* 35 */     try { PreparedStatement insertStmt = connection.prepareStatement(insertSql); 
/* 36 */       try { insertStmt.setString(1, value);
/* 37 */         insertStmt.executeUpdate();
/* 38 */         if (insertStmt != null) insertStmt.close();  } catch (Throwable throwable) { if (insertStmt != null) try { insertStmt.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (SQLException e)
/*    */     
/* 40 */     { if (!e.getSQLState().equals(dialect.getUniqueConstraintViolationSQLState()) || e
/* 41 */         .getErrorCode() != dialect.getUniqueConstraintViolationErrorCode()) {
/* 42 */         LogUtil.error("Failed to insert into " + tableName + ": " + value, e);
/* 43 */         throw e;
/*    */       }  }
/*    */ 
/*    */ 
/*    */     
/* 48 */     PreparedStatement selectStmt = connection.prepareStatement("SELECT id FROM " + tableName + " WHERE " + stringColumnName + " = ?");
/*    */     
/*    */     try {
/* 51 */       selectStmt.setString(1, value);
/* 52 */       ResultSet rs = selectStmt.executeQuery(); try {
/* 53 */         if (rs.next()) {
/* 54 */           long l = rs.getLong("id");
/*    */ 
/*    */ 
/*    */           
/* 58 */           if (rs != null) rs.close(); 
/* 59 */           if (selectStmt != null) selectStmt.close(); 
/*    */           return l;
/*    */         } 
/*    */         throw new SQLException("Failed to retrieve ID for " + value + " from " + tableName);
/*    */       } catch (Throwable throwable) {
/*    */         if (rs != null)
/*    */           try {
/*    */             rs.close();
/*    */           } catch (Throwable throwable1) {
/*    */             throwable.addSuppressed(throwable1);
/*    */           }  
/*    */         throw throwable;
/*    */       } 
/*    */     } catch (Throwable throwable) {
/*    */       if (selectStmt != null)
/*    */         try {
/*    */           selectStmt.close();
/*    */         } catch (Throwable throwable1) {
/*    */           throwable.addSuppressed(throwable1);
/*    */         }  
/*    */       throw throwable;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\violationdatabase\DatabaseUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */