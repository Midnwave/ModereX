/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.level.VillagerLevel;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.profession.VillagerProfession;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.profession.VillagerProfessions;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.type.VillagerType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.type.VillagerTypes;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ public class VillagerData
/*    */ {
/*    */   private VillagerType type;
/*    */   private VillagerProfession profession;
/*    */   private int level;
/*    */   
/*    */   public VillagerData(VillagerType type, VillagerProfession profession, VillagerLevel level) {
/* 35 */     this(type, profession, level.getId());
/*    */   }
/*    */   
/*    */   public VillagerData(VillagerType type, VillagerProfession profession, int level) {
/* 39 */     this.type = type;
/* 40 */     this.profession = profession;
/* 41 */     this.level = level;
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public VillagerData(int typeId, int professionId, int level) {
/* 46 */     this(VillagerTypes.getById(typeId), VillagerProfessions.getById(professionId), level);
/*    */   }
/*    */   
/*    */   public VillagerType getType() {
/* 50 */     return this.type;
/*    */   }
/*    */   
/*    */   public void setType(VillagerType type) {
/* 54 */     this.type = type;
/*    */   }
/*    */   
/*    */   public VillagerProfession getProfession() {
/* 58 */     return this.profession;
/*    */   }
/*    */   
/*    */   public void setProfession(VillagerProfession profession) {
/* 62 */     this.profession = profession;
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 66 */     return this.level;
/*    */   }
/*    */   @Nullable
/*    */   public VillagerLevel getVillagerLevel() {
/* 70 */     return VillagerLevel.getById(this.level);
/*    */   }
/*    */   
/*    */   public void setLevel(int level) {
/* 74 */     this.level = level;
/*    */   }
/*    */   
/*    */   public void setLevel(VillagerLevel level) {
/* 78 */     this.level = level.getId();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\villager\VillagerData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */