/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentPredicate;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*     */ public class MerchantItemCost
/*     */ {
/*     */   private ItemType item;
/*     */   private int count;
/*     */   private ComponentPredicate predicate;
/*     */   
/*     */   public MerchantItemCost(ItemType item) {
/*  37 */     this(item, 1);
/*     */   }
/*     */   
/*     */   public MerchantItemCost(ItemType item, int count) {
/*  41 */     this(item, count, ComponentPredicate.emptyPredicate());
/*     */   }
/*     */   
/*     */   public MerchantItemCost(ItemType item, int count, ComponentPredicate predicate) {
/*  45 */     this.item = item;
/*  46 */     this.count = count;
/*  47 */     this.predicate = predicate;
/*     */   }
/*     */   @Contract("null -> null; !null -> !null")
/*     */   @Nullable
/*     */   public static MerchantItemCost ofItem(@Nullable ItemStack stack) {
/*  52 */     if (stack == null) {
/*  53 */       return null;
/*     */     }
/*  55 */     if (stack.isEmpty()) {
/*  56 */       return emptyCost();
/*     */     }
/*  58 */     ComponentPredicate predicate = ComponentPredicate.fromPatches(stack.getComponents());
/*  59 */     return new MerchantItemCost(stack.getType(), stack.getAmount(), predicate);
/*     */   }
/*     */   
/*     */   public static ItemStack readItem(PacketWrapper<?> wrapper) {
/*  63 */     return wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_20_5) ? 
/*  64 */       wrapper.readItemStack() : read(wrapper).asItem();
/*     */   }
/*     */   
/*     */   public static MerchantItemCost read(PacketWrapper<?> wrapper) {
/*  68 */     ItemType item = (ItemType)wrapper.readMappedEntity(ItemTypes::getById);
/*  69 */     int count = wrapper.readVarInt();
/*  70 */     ComponentPredicate predicate = ComponentPredicate.read(wrapper);
/*  71 */     return new MerchantItemCost(item, count, predicate);
/*     */   }
/*     */   
/*     */   public static void writeItem(PacketWrapper<?> wrapper, ItemStack costItem) {
/*  75 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_20_5)) {
/*  76 */       wrapper.writeItemStack(costItem);
/*     */     } else {
/*  78 */       write(wrapper, ofItem(costItem));
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, MerchantItemCost cost) {
/*  83 */     wrapper.writeMappedEntity((MappedEntity)cost.item);
/*  84 */     wrapper.writeVarInt(cost.count);
/*  85 */     ComponentPredicate.write(wrapper, cost.predicate);
/*     */   }
/*     */   
/*     */   public static MerchantItemCost emptyCost() {
/*  89 */     return new MerchantItemCost(ItemTypes.AIR, 0);
/*     */   }
/*     */   
/*     */   public ItemStack asItem() {
/*  93 */     return ItemStack.builder().type(this.item).amount(this.count)
/*  94 */       .components(this.predicate.asPatches(this.item.getComponents()))
/*  95 */       .build();
/*     */   }
/*     */   
/*     */   public ItemType getItem() {
/*  99 */     return this.item;
/*     */   }
/*     */   
/*     */   public void setItem(ItemType item) {
/* 103 */     this.item = item;
/*     */   }
/*     */   
/*     */   public int getCount() {
/* 107 */     return this.count;
/*     */   }
/*     */   
/*     */   public void setCount(int count) {
/* 111 */     this.count = count;
/*     */   }
/*     */   
/*     */   public ComponentPredicate getPredicate() {
/* 115 */     return this.predicate;
/*     */   }
/*     */   
/*     */   public void setPredicate(ComponentPredicate predicate) {
/* 119 */     this.predicate = predicate;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\data\MerchantItemCost.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */