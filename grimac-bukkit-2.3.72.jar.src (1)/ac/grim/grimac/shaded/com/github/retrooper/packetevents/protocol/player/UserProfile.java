/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.UUID;
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
/*    */ public class UserProfile
/*    */ {
/*    */   private UUID uuid;
/*    */   private String name;
/*    */   private List<TextureProperty> textureProperties;
/*    */   
/*    */   public UserProfile(UUID uuid, String name) {
/* 35 */     this.uuid = uuid;
/* 36 */     this.name = name;
/* 37 */     this.textureProperties = new ArrayList<>();
/*    */   }
/*    */   
/*    */   public UserProfile(UUID uuid, String name, List<TextureProperty> textureProperties) {
/* 41 */     this.uuid = uuid;
/* 42 */     this.name = name;
/* 43 */     this.textureProperties = textureProperties;
/*    */   }
/*    */   
/*    */   public UUID getUUID() {
/* 47 */     return this.uuid;
/*    */   }
/*    */   
/*    */   public void setUUID(UUID uuid) {
/* 51 */     this.uuid = uuid;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 55 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 59 */     this.name = name;
/*    */   }
/*    */   
/*    */   public List<TextureProperty> getTextureProperties() {
/* 63 */     return this.textureProperties;
/*    */   }
/*    */   
/*    */   public void setTextureProperties(List<TextureProperty> textureProperties) {
/* 67 */     this.textureProperties = textureProperties;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\player\UserProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */