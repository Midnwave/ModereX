/*    */ package ac.grim.grimac.manager.violationdatabase;public final class Violation extends Record { private final String server; private final UUID uuid; private final String checkName; private final String verbose;
/*    */   private final int vl;
/*    */   private final long createdAt;
/*    */   private final String grimVersion;
/*    */   private final String clientBrand;
/*    */   private final String clientVersion;
/*    */   private final String serverVersion;
/*    */   
/*  9 */   public Violation(String server, UUID uuid, String checkName, String verbose, int vl, long createdAt, String grimVersion, String clientBrand, String clientVersion, String serverVersion) { this.server = server; this.uuid = uuid; this.checkName = checkName; this.verbose = verbose; this.vl = vl; this.createdAt = createdAt; this.grimVersion = grimVersion; this.clientBrand = clientBrand; this.clientVersion = clientVersion; this.serverVersion = serverVersion; } public final String toString() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lac/grim/grimac/manager/violationdatabase/Violation;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #9	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*  9 */     //   0	7	0	this	Lac/grim/grimac/manager/violationdatabase/Violation; } public String server() { return this.server; } public final int hashCode() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lac/grim/grimac/manager/violationdatabase/Violation;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #9	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lac/grim/grimac/manager/violationdatabase/Violation; } public final boolean equals(Object o) { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lac/grim/grimac/manager/violationdatabase/Violation;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #9	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lac/grim/grimac/manager/violationdatabase/Violation;
/*  9 */     //   0	8	1	o	Ljava/lang/Object; } public UUID uuid() { return this.uuid; } public String checkName() { return this.checkName; } public String verbose() { return this.verbose; } public int vl() { return this.vl; } public long createdAt() { return this.createdAt; } public String grimVersion() { return this.grimVersion; } public String clientBrand() { return this.clientBrand; } public String clientVersion() { return this.clientVersion; } public String serverVersion() { return this.serverVersion; }
/*    */ 
/*    */   
/*    */   public static List<Violation> fromResultSet(ResultSet resultSet) throws SQLException {
/* 13 */     List<Violation> violations = new ArrayList<>();
/* 14 */     while (resultSet.next()) {
/* 15 */       String server = resultSet.getString("server_name");
/* 16 */       byte[] uuidBytes = resultSet.getBytes("uuid");
/* 17 */       UUID uuid = DatabaseUtils.bytesToUuid(uuidBytes);
/* 18 */       String checkName = resultSet.getString("check_name_string");
/* 19 */       String verbose = resultSet.getString("verbose");
/* 20 */       int vl = resultSet.getInt("vl");
/* 21 */       long createdAt = resultSet.getLong("created_at");
/* 22 */       String grimVersion = resultSet.getString("grim_version_string");
/* 23 */       String clientBrand = resultSet.getString("client_brand_string");
/* 24 */       String clientVersion = resultSet.getString("client_version_string");
/* 25 */       String serverVersion = resultSet.getString("server_version_string");
/*    */       
/* 27 */       violations.add(new Violation(server, uuid, checkName, verbose, vl, createdAt, grimVersion, clientBrand, clientVersion, serverVersion));
/*    */     } 
/* 29 */     return violations;
/*    */   } }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\violationdatabase\Violation.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */