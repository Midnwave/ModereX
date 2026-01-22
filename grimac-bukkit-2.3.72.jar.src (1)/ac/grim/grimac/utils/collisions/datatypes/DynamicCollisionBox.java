/*    */ package ac.grim.grimac.utils.collisions.datatypes;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class DynamicCollisionBox implements CollisionBox {
/*    */   private final GrimPlayer player;
/*    */   private final CollisionFactory box;
/*    */   private ClientVersion version;
/*    */   
/*    */   @Generated
/* 14 */   public void setVersion(ClientVersion version) { this.version = version; } private WrappedBlockState block; private int x; private int y; private int z; @Generated
/*    */   public void setBlock(WrappedBlockState block) {
/* 16 */     this.block = block;
/*    */   }
/*    */ 
/*    */   
/*    */   public DynamicCollisionBox(GrimPlayer player, ClientVersion version, CollisionFactory box, WrappedBlockState block) {
/* 21 */     this.player = player;
/* 22 */     this.version = version;
/* 23 */     this.box = box;
/* 24 */     this.block = block;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CollisionBox union(SimpleCollisionBox other) {
/* 31 */     CollisionBox dynamicBox = this.box.fetch(this.player, this.version, this.block, this.x, this.y, this.z).offset(this.x, this.y, this.z);
/* 32 */     return dynamicBox.union(other);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCollided(SimpleCollisionBox other) {
/* 37 */     return this.box.fetch(this.player, this.version, this.block, this.x, this.y, this.z).offset(this.x, this.y, this.z).isCollided(other);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isIntersected(SimpleCollisionBox other) {
/* 42 */     return this.box.fetch(this.player, this.version, this.block, this.x, this.y, this.z).offset(this.x, this.y, this.z).isIntersected(other);
/*    */   }
/*    */ 
/*    */   
/*    */   public CollisionBox copy() {
/* 47 */     return (new DynamicCollisionBox(this.player, this.version, this.box, this.block)).offset(this.x, this.y, this.z);
/*    */   }
/*    */ 
/*    */   
/*    */   public CollisionBox offset(double x, double y, double z) {
/* 52 */     this.x = (int)(this.x + x);
/* 53 */     this.y = (int)(this.y + y);
/* 54 */     this.z = (int)(this.z + z);
/* 55 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public int downCast(SimpleCollisionBox[] list) {
/* 60 */     return this.box.fetch(this.player, this.version, this.block, this.x, this.y, this.z).offset(this.x, this.y, this.z).downCast(list);
/*    */   }
/*    */ 
/*    */   
/*    */   public void downCast(List<SimpleCollisionBox> list) {
/* 65 */     this.box.fetch(this.player, this.version, this.block, this.x, this.y, this.z).offset(this.x, this.y, this.z).downCast(list);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isNull() {
/* 70 */     return this.box.fetch(this.player, this.version, this.block, this.x, this.y, this.z).isNull();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isFullBlock() {
/* 75 */     return this.box.fetch(this.player, this.version, this.block, this.x, this.y, this.z).offset(this.x, this.y, this.z).isFullBlock();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\datatypes\DynamicCollisionBox.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */