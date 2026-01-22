/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ItemTool
/*     */ {
/*     */   private List<Rule> rules;
/*     */   private float defaultMiningSpeed;
/*     */   private int damagePerBlock;
/*     */   private boolean canDestroyBlocksInCreative;
/*     */   
/*     */   public ItemTool(List<Rule> rules, float defaultMiningSpeed, int damagePerBlock) {
/*  42 */     this(rules, defaultMiningSpeed, damagePerBlock, true);
/*     */   }
/*     */   
/*     */   public ItemTool(List<Rule> rules, float defaultMiningSpeed, int damagePerBlock, boolean canDestroyBlocksInCreative) {
/*  46 */     this.rules = rules;
/*  47 */     this.defaultMiningSpeed = defaultMiningSpeed;
/*  48 */     this.damagePerBlock = damagePerBlock;
/*  49 */     this.canDestroyBlocksInCreative = canDestroyBlocksInCreative;
/*     */   }
/*     */   
/*     */   public static ItemTool read(PacketWrapper<?> wrapper) {
/*  53 */     List<Rule> rules = wrapper.readList(Rule::read);
/*  54 */     float defaultMiningSpeed = wrapper.readFloat();
/*  55 */     int damagePerBlock = wrapper.readVarInt();
/*  56 */     boolean canDestroyBlocksInCreative = (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5) || wrapper.readBoolean());
/*  57 */     return new ItemTool(rules, defaultMiningSpeed, damagePerBlock, canDestroyBlocksInCreative);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemTool tool) {
/*  61 */     wrapper.writeList(tool.rules, Rule::write);
/*  62 */     wrapper.writeFloat(tool.defaultMiningSpeed);
/*  63 */     wrapper.writeVarInt(tool.damagePerBlock);
/*  64 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/*  65 */       wrapper.writeBoolean(tool.canDestroyBlocksInCreative);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addRule(Rule rule) {
/*  70 */     this.rules.add(rule);
/*     */   }
/*     */   
/*     */   public List<Rule> getRules() {
/*  74 */     return this.rules;
/*     */   }
/*     */   
/*     */   public void setRules(List<Rule> rules) {
/*  78 */     this.rules = rules;
/*     */   }
/*     */   
/*     */   public float getDefaultMiningSpeed() {
/*  82 */     return this.defaultMiningSpeed;
/*     */   }
/*     */   
/*     */   public void setDefaultMiningSpeed(float defaultMiningSpeed) {
/*  86 */     this.defaultMiningSpeed = defaultMiningSpeed;
/*     */   }
/*     */   
/*     */   public int getDamagePerBlock() {
/*  90 */     return this.damagePerBlock;
/*     */   }
/*     */   
/*     */   public void setDamagePerBlock(int damagePerBlock) {
/*  94 */     this.damagePerBlock = damagePerBlock;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCanDestroyBlocksInCreative() {
/* 101 */     return this.canDestroyBlocksInCreative;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCanDestroyBlocksInCreative(boolean canDestroyBlocksInCreative) {
/* 108 */     this.canDestroyBlocksInCreative = canDestroyBlocksInCreative;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 113 */     if (this == obj) return true; 
/* 114 */     if (!(obj instanceof ItemTool)) return false; 
/* 115 */     ItemTool itemTool = (ItemTool)obj;
/* 116 */     if (Float.compare(itemTool.defaultMiningSpeed, this.defaultMiningSpeed) != 0) return false; 
/* 117 */     if (this.damagePerBlock != itemTool.damagePerBlock) return false; 
/* 118 */     if (!this.rules.equals(itemTool.rules)) return false; 
/* 119 */     return (this.canDestroyBlocksInCreative == itemTool.canDestroyBlocksInCreative);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 124 */     return Objects.hash(new Object[] { this.rules, Float.valueOf(this.defaultMiningSpeed), Integer.valueOf(this.damagePerBlock), Boolean.valueOf(this.canDestroyBlocksInCreative) });
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Rule
/*     */   {
/*     */     private MappedEntitySet<StateType.Mapped> blocks;
/*     */     
/*     */     @Nullable
/*     */     private Float speed;
/*     */     @Nullable
/*     */     private Boolean correctForDrops;
/*     */     
/*     */     public Rule(MappedEntitySet<StateType.Mapped> blocks, @Nullable Float speed, @Nullable Boolean correctForDrops) {
/* 138 */       this.blocks = blocks;
/* 139 */       this.speed = speed;
/* 140 */       this.correctForDrops = correctForDrops;
/*     */     }
/*     */     
/*     */     public static Rule read(PacketWrapper<?> wrapper) {
/* 144 */       MappedEntitySet<StateType.Mapped> blocks = MappedEntitySet.read(wrapper, StateTypes::getMappedById);
/* 145 */       Float speed = (Float)wrapper.readOptional(PacketWrapper::readFloat);
/* 146 */       Boolean correctForDrops = (Boolean)wrapper.readOptional(PacketWrapper::readBoolean);
/* 147 */       return new Rule(blocks, speed, correctForDrops);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, Rule rule) {
/* 151 */       MappedEntitySet.write(wrapper, rule.blocks);
/* 152 */       wrapper.writeOptional(rule.speed, PacketWrapper::writeFloat);
/* 153 */       wrapper.writeOptional(rule.correctForDrops, PacketWrapper::writeBoolean);
/*     */     }
/*     */     
/*     */     public MappedEntitySet<StateType.Mapped> getBlocks() {
/* 157 */       return this.blocks;
/*     */     }
/*     */     
/*     */     public void setBlocks(MappedEntitySet<StateType.Mapped> blocks) {
/* 161 */       this.blocks = blocks;
/*     */     }
/*     */     @Nullable
/*     */     public Float getSpeed() {
/* 165 */       return this.speed;
/*     */     }
/*     */     
/*     */     public void setSpeed(@Nullable Float speed) {
/* 169 */       this.speed = speed;
/*     */     }
/*     */     @Nullable
/*     */     public Boolean getCorrectForDrops() {
/* 173 */       return this.correctForDrops;
/*     */     }
/*     */     
/*     */     public void setCorrectForDrops(@Nullable Boolean correctForDrops) {
/* 177 */       this.correctForDrops = correctForDrops;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 182 */       if (this == obj) return true; 
/* 183 */       if (!(obj instanceof Rule)) return false; 
/* 184 */       Rule rule = (Rule)obj;
/* 185 */       if (!this.blocks.equals(rule.blocks)) return false; 
/* 186 */       if (!Objects.equals(this.speed, rule.speed)) return false; 
/* 187 */       return Objects.equals(this.correctForDrops, rule.correctForDrops);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 192 */       return Objects.hash(new Object[] { this.blocks, this.speed, this.correctForDrops });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemTool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */