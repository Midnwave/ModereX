/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ public abstract class AbstractMappedEntity
/*    */   implements MappedEntity
/*    */ {
/*    */   @Nullable
/*    */   protected final TypesBuilderData data;
/*    */   
/*    */   protected AbstractMappedEntity(@Nullable TypesBuilderData data) {
/* 33 */     this.data = data;
/*    */   }
/*    */   @Nullable
/*    */   public TypesBuilderData getRegistryData() {
/* 37 */     return this.data;
/*    */   }
/*    */ 
/*    */   
/*    */   public ResourceLocation getName() {
/* 42 */     if (this.data != null) {
/* 43 */       return this.data.getName();
/*    */     }
/* 45 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getId(ClientVersion version) {
/* 50 */     if (this.data != null) {
/* 51 */       return this.data.getId(version);
/*    */     }
/* 53 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isRegistered() {
/* 58 */     return (this.data != null);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 63 */     if (this == obj) return true; 
/* 64 */     if (!(obj instanceof AbstractMappedEntity)) return false; 
/* 65 */     AbstractMappedEntity that = (AbstractMappedEntity)obj;
/* 66 */     if (this.data != null && that.data != null) {
/* 67 */       return this.data.getName().equals(that.data.getName());
/*    */     }
/* 69 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 74 */     if (this.data != null) {
/* 75 */       return Objects.hashCode(this.data.getName());
/*    */     }
/* 77 */     return super.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 82 */     return getClass().getSimpleName() + "[" + ((this.data == null) ? (String)Integer.valueOf(hashCode()) : (String)this.data.getName()) + ']';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\mapper\AbstractMappedEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */