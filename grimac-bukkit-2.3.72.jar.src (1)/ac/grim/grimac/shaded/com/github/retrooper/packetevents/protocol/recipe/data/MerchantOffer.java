/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.data;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MerchantOffer
/*     */   implements RecipeData
/*     */ {
/*     */   private ItemStack firstInputItem;
/*     */   @Nullable
/*     */   private ItemStack secondInputItem;
/*     */   private ItemStack outputItem;
/*     */   private int uses;
/*     */   private int maxUses;
/*     */   private int xp;
/*     */   private int specialPrice;
/*     */   private float priceMultiplier;
/*     */   private int demand;
/*     */   
/*     */   private MerchantOffer(MerchantItemCost firstInputItem, @Nullable MerchantItemCost secondInputItem, ItemStack outputItem, int uses, int maxUses, int xp, int specialPrice, float priceMultiplier, int demand) {
/*  42 */     this(firstInputItem
/*  43 */         .asItem(), 
/*  44 */         (secondInputItem == null) ? null : secondInputItem.asItem(), outputItem, uses, maxUses, xp, specialPrice, priceMultiplier, demand);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MerchantOffer(ItemStack firstInputItem, @Nullable ItemStack secondInputItem, ItemStack outputItem, int uses, int maxUses, int xp, int specialPrice, float priceMultiplier, int demand) {
/*  54 */     this.firstInputItem = firstInputItem;
/*  55 */     this.secondInputItem = secondInputItem;
/*  56 */     this.outputItem = outputItem;
/*  57 */     this.uses = uses;
/*  58 */     this.maxUses = maxUses;
/*  59 */     this.xp = xp;
/*  60 */     this.priceMultiplier = priceMultiplier;
/*  61 */     this.demand = demand;
/*  62 */     this.specialPrice = specialPrice;
/*     */   }
/*     */   
/*     */   public static MerchantOffer of(ItemStack buyItem1, @Nullable ItemStack buyItem2, ItemStack sellItem, int uses, int maxUses, int xp, int specialPrice, float priceMultiplier, int demand) {
/*  66 */     return new MerchantOffer(buyItem1, buyItem2, sellItem, uses, maxUses, xp, specialPrice, priceMultiplier, demand);
/*     */   }
/*     */   
/*     */   public static MerchantOffer of(ItemStack buyItem1, ItemStack sellItem, int uses, int maxUses, int xp, int specialPrice, float priceMultiplier, int demand) {
/*  70 */     return new MerchantOffer(buyItem1, null, sellItem, uses, maxUses, xp, specialPrice, priceMultiplier, demand);
/*     */   }
/*     */   
/*     */   public static MerchantOffer of(ItemStack buyItem1, ItemStack sellItem, int uses, int maxUses, int xp, float priceMultiplier, int demand) {
/*  74 */     return new MerchantOffer(buyItem1, null, sellItem, uses, maxUses, xp, 0, priceMultiplier, demand);
/*     */   }
/*     */   
/*     */   public static MerchantOffer of(MerchantItemCost buyCost1, @Nullable MerchantItemCost buyCost2, ItemStack sellItem, int uses, int maxUses, int xp, int specialPrice, float priceMultiplier, int demand) {
/*  78 */     return new MerchantOffer(buyCost1, buyCost2, sellItem, uses, maxUses, xp, specialPrice, priceMultiplier, demand);
/*     */   }
/*     */   
/*     */   public static MerchantOffer of(MerchantItemCost buyCost1, ItemStack sellItem, int uses, int maxUses, int xp, int specialPrice, float priceMultiplier, int demand) {
/*  82 */     return new MerchantOffer(buyCost1, null, sellItem, uses, maxUses, xp, specialPrice, priceMultiplier, demand);
/*     */   }
/*     */   
/*     */   public static MerchantOffer of(MerchantItemCost buyCost1, ItemStack sellItem, int uses, int maxUses, int xp, float priceMultiplier, int demand) {
/*  86 */     return new MerchantOffer(buyCost1, null, sellItem, uses, maxUses, xp, 0, priceMultiplier, demand);
/*     */   }
/*     */   
/*     */   public MerchantItemCost getFirstInputCost() {
/*  90 */     return MerchantItemCost.ofItem(this.firstInputItem);
/*     */   }
/*     */   
/*     */   public void setFirstInputCost(MerchantItemCost firstInputCost) {
/*  94 */     this.firstInputItem = firstInputCost.asItem();
/*     */   }
/*     */   
/*     */   @Obsolete
/*     */   public ItemStack getFirstInputItem() {
/*  99 */     return this.firstInputItem;
/*     */   }
/*     */   
/*     */   @Obsolete
/*     */   public void setFirstInputItem(ItemStack firstInputItem) {
/* 104 */     this.firstInputItem = firstInputItem;
/*     */   }
/*     */   @Nullable
/*     */   public MerchantItemCost getSecondInputCost() {
/* 108 */     return MerchantItemCost.ofItem(this.firstInputItem);
/*     */   }
/*     */   
/*     */   public void setSecondInputCost(@Nullable MerchantItemCost secondInputCost) {
/* 112 */     this.secondInputItem = (secondInputCost == null) ? null : secondInputCost.asItem();
/*     */   }
/*     */   @Obsolete
/*     */   @Nullable
/*     */   public ItemStack getSecondInputItem() {
/* 117 */     return this.secondInputItem;
/*     */   }
/*     */   
/*     */   @Obsolete
/*     */   public void setSecondInputItem(@Nullable ItemStack secondInputItem) {
/* 122 */     this.secondInputItem = secondInputItem;
/*     */   }
/*     */   
/*     */   public ItemStack getOutputItem() {
/* 126 */     return this.outputItem;
/*     */   }
/*     */   
/*     */   public void setOutputItem(ItemStack outputItem) {
/* 130 */     this.outputItem = outputItem;
/*     */   }
/*     */   
/*     */   public int getUses() {
/* 134 */     return this.uses;
/*     */   }
/*     */   
/*     */   public void setUses(int uses) {
/* 138 */     this.uses = uses;
/*     */   }
/*     */   
/*     */   public int getMaxUses() {
/* 142 */     return this.maxUses;
/*     */   }
/*     */   
/*     */   public void setMaxUses(int maxUses) {
/* 146 */     this.maxUses = maxUses;
/*     */   }
/*     */   
/*     */   public int getXp() {
/* 150 */     return this.xp;
/*     */   }
/*     */   
/*     */   public void setXp(int xp) {
/* 154 */     this.xp = xp;
/*     */   }
/*     */   
/*     */   public float getPriceMultiplier() {
/* 158 */     return this.priceMultiplier;
/*     */   }
/*     */   
/*     */   public void setPriceMultiplier(float priceMultiplier) {
/* 162 */     this.priceMultiplier = priceMultiplier;
/*     */   }
/*     */   
/*     */   public int getDemand() {
/* 166 */     return this.demand;
/*     */   }
/*     */   
/*     */   public void setDemand(int demand) {
/* 170 */     this.demand = demand;
/*     */   }
/*     */   
/*     */   public int getSpecialPrice() {
/* 174 */     return this.specialPrice;
/*     */   }
/*     */   
/*     */   public void setSpecialPrice(int specialPrice) {
/* 178 */     this.specialPrice = specialPrice;
/*     */   }
/*     */   
/*     */   public boolean isOutOfStock() {
/* 182 */     return (this.uses >= this.maxUses);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\data\MerchantOffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */