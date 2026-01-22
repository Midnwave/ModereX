/*     */ package ac.grim.grimac.utils.inventory;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.utils.inventory.inventory.AbstractContainerMenu;
/*     */ import ac.grim.grimac.utils.inventory.slot.EquipmentSlot;
/*     */ import ac.grim.grimac.utils.inventory.slot.ResultSlot;
/*     */ import ac.grim.grimac.utils.inventory.slot.Slot;
/*     */ import ac.grim.grimac.utils.lists.CorrectingPlayerInventoryStorage;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public class Inventory extends AbstractContainerMenu {
/*     */   public static final int SLOT_OFFHAND = 45;
/*     */   public static final int HOTBAR_OFFSET = 36;
/*     */   public static final int ITEMS_START = 9;
/*     */   public static final int ITEMS_END = 45;
/*     */   public static final int SLOT_HELMET = 4;
/*     */   public static final int SLOT_CHESTPLATE = 5;
/*     */   public static final int SLOT_LEGGINGS = 6;
/*     */   public static final int SLOT_BOOTS = 7;
/*     */   private static final int TOTAL_SIZE = 46;
/*  24 */   public int selected = 0; @Generated
/*  25 */   public CorrectingPlayerInventoryStorage getInventoryStorage() { return this.inventoryStorage; }
/*     */   
/*     */   CorrectingPlayerInventoryStorage inventoryStorage;
/*     */   public Inventory(GrimPlayer player, CorrectingPlayerInventoryStorage inventoryStorage) {
/*  29 */     super(player);
/*  30 */     setPlayerInventory(this);
/*  31 */     this.inventoryStorage = inventoryStorage;
/*     */ 
/*     */     
/*  34 */     addSlot((Slot)new ResultSlot((InventoryStorage)inventoryStorage, 0));
/*     */     int i;
/*  36 */     for (i = 0; i < 4; i++) {
/*  37 */       addSlot(new Slot((InventoryStorage)inventoryStorage, i));
/*     */     }
/*  39 */     for (i = 0; i < 4; i++) {
/*  40 */       addSlot((Slot)new EquipmentSlot(EquipmentType.byArmorID(i), (InventoryStorage)inventoryStorage, i + 4));
/*     */     }
/*     */     
/*  43 */     for (i = 0; i < 36; i++) {
/*  44 */       addSlot(new Slot((InventoryStorage)inventoryStorage, i + 9));
/*     */     }
/*     */     
/*  47 */     addSlot(new Slot((InventoryStorage)inventoryStorage, 45));
/*     */   }
/*     */   
/*     */   public ItemStack getHelmet() {
/*  51 */     return this.inventoryStorage.getItem(4);
/*     */   }
/*     */   
/*     */   public ItemStack getChestplate() {
/*  55 */     return this.inventoryStorage.getItem(5);
/*     */   }
/*     */   
/*     */   public ItemStack getLeggings() {
/*  59 */     return this.inventoryStorage.getItem(6);
/*     */   }
/*     */   
/*     */   public ItemStack getBoots() {
/*  63 */     return this.inventoryStorage.getItem(7);
/*     */   }
/*     */   
/*     */   public ItemStack getOffhand() {
/*  67 */     return this.inventoryStorage.getItem(45);
/*     */   }
/*     */   
/*     */   public boolean hasItemType(ItemType item) {
/*  71 */     for (int i = 0; i < this.inventoryStorage.items.length; i++) {
/*  72 */       if (this.inventoryStorage.getItem(i).getType() == item) {
/*  73 */         return true;
/*     */       }
/*     */     } 
/*  76 */     return false;
/*     */   }
/*     */   
/*     */   public ItemStack getHeldItem() {
/*  80 */     return this.inventoryStorage.getItem(this.selected + 36);
/*     */   }
/*     */   
/*     */   public void setHeldItem(ItemStack item) {
/*  84 */     this.inventoryStorage.setItem(this.selected + 36, item);
/*     */   }
/*     */   
/*     */   public ItemStack getOffhandItem() {
/*  88 */     return this.inventoryStorage.getItem(45);
/*     */   }
/*     */   
/*     */   public boolean add(ItemStack p_36055_) {
/*  92 */     return add(-1, p_36055_);
/*     */   }
/*     */   
/*     */   public int getFreeSlot() {
/*  96 */     for (int i = 0; i < this.inventoryStorage.items.length; i++) {
/*  97 */       if (this.inventoryStorage.getItem(i).isEmpty()) {
/*  98 */         return i;
/*     */       }
/*     */     } 
/*     */     
/* 102 */     return -1;
/*     */   }
/*     */   
/*     */   public int getSlotWithRemainingSpace(ItemStack toAdd) {
/* 106 */     if (hasRemainingSpaceForItem(getHeldItem(), toAdd))
/* 107 */       return this.selected; 
/* 108 */     if (hasRemainingSpaceForItem(getOffhandItem(), toAdd)) {
/* 109 */       return 40;
/*     */     }
/* 111 */     for (int i = 9; i <= 45; i++) {
/* 112 */       if (hasRemainingSpaceForItem(this.inventoryStorage.getItem(i), toAdd)) {
/* 113 */         return i;
/*     */       }
/*     */     } 
/*     */     
/* 117 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean hasRemainingSpaceForItem(ItemStack one, ItemStack two) {
/* 122 */     return (!one.isEmpty() && ItemStack.isSameItemSameTags(one, two) && one.getAmount() < one.getMaxStackSize() && one.getAmount() < getMaxStackSize());
/*     */   }
/*     */   
/*     */   private int addResource(ItemStack resource) {
/* 126 */     int i = getSlotWithRemainingSpace(resource);
/* 127 */     if (i == -1) {
/* 128 */       i = getFreeSlot();
/*     */     }
/*     */     
/* 131 */     return (i == -1) ? resource.getAmount() : addResource(i, resource);
/*     */   }
/*     */   
/*     */   private int addResource(int slot, ItemStack stack) {
/* 135 */     int i = stack.getAmount();
/* 136 */     ItemStack itemstack = this.inventoryStorage.getItem(slot);
/*     */     
/* 138 */     if (itemstack.isEmpty()) {
/* 139 */       itemstack = stack.copy();
/* 140 */       itemstack.setAmount(0);
/* 141 */       this.inventoryStorage.setItem(slot, itemstack);
/*     */     } 
/*     */     
/* 144 */     int j = Math.min(i, itemstack.getMaxStackSize() - itemstack.getAmount());
/*     */     
/* 146 */     if (j > getMaxStackSize() - itemstack.getAmount()) {
/* 147 */       j = getMaxStackSize() - itemstack.getAmount();
/*     */     }
/*     */     
/* 150 */     if (j != 0) {
/* 151 */       i -= j;
/* 152 */       itemstack.grow(j);
/*     */     } 
/* 154 */     return i;
/*     */   }
/*     */   public boolean add(int p_36041_, ItemStack p_36042_) {
/*     */     int i;
/* 158 */     if (p_36042_.isEmpty()) {
/* 159 */       return false;
/*     */     }
/* 161 */     if (p_36042_.isDamaged()) {
/* 162 */       if (p_36041_ == -1) {
/* 163 */         p_36041_ = getFreeSlot();
/*     */       }
/*     */       
/* 166 */       if (p_36041_ >= 0) {
/* 167 */         this.inventoryStorage.setItem(p_36041_, p_36042_.copy());
/* 168 */         p_36042_.setAmount(0);
/* 169 */         return true;
/* 170 */       }  if (this.player.gamemode == GameMode.CREATIVE) {
/* 171 */         p_36042_.setAmount(0);
/* 172 */         return true;
/*     */       } 
/* 174 */       return false;
/*     */     } 
/*     */ 
/*     */     
/*     */     do {
/* 179 */       i = p_36042_.getAmount();
/* 180 */       if (p_36041_ == -1) {
/* 181 */         p_36042_.setAmount(addResource(p_36042_));
/*     */       } else {
/* 183 */         p_36042_.setAmount(addResource(p_36041_, p_36042_));
/*     */       } 
/* 185 */     } while (!p_36042_.isEmpty() && p_36042_.getAmount() < i);
/*     */     
/* 187 */     if (p_36042_.getAmount() == i && this.player.gamemode == GameMode.CREATIVE) {
/* 188 */       p_36042_.setAmount(0);
/* 189 */       return true;
/*     */     } 
/* 191 */     return (p_36042_.getAmount() < i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ItemStack quickMoveStack(int slotID) {
/* 199 */     ItemStack original = ItemStack.EMPTY;
/* 200 */     Slot slot = getSlots().get(slotID);
/*     */     
/* 202 */     if (slot != null && slot.hasItem()) {
/* 203 */       ItemStack toMove = slot.getItem();
/* 204 */       original = toMove.copy();
/* 205 */       EquipmentType equipmentslot = EquipmentType.getEquipmentSlotForItem(original);
/* 206 */       if (slotID == 0) {
/* 207 */         if (!moveItemStackTo(toMove, 9, 45, true)) {
/* 208 */           return ItemStack.EMPTY;
/*     */         }
/* 210 */       } else if (slotID >= 1 && slotID < 5) {
/* 211 */         if (!moveItemStackTo(toMove, 9, 45, false)) {
/* 212 */           return ItemStack.EMPTY;
/*     */         }
/* 214 */       } else if (slotID >= 5 && slotID < 9) {
/* 215 */         if (!moveItemStackTo(toMove, 9, 45, false)) {
/* 216 */           return ItemStack.EMPTY;
/*     */         }
/* 218 */       } else if (equipmentslot.isArmor() && !((Slot)getSlots().get(8 - equipmentslot.getIndex())).hasItem()) {
/* 219 */         int i = 8 - equipmentslot.getIndex();
/* 220 */         if (!moveItemStackTo(toMove, i, i + 1, false)) {
/* 221 */           return ItemStack.EMPTY;
/*     */         }
/* 223 */       } else if (equipmentslot == EquipmentType.OFFHAND && !((Slot)getSlots().get(45)).hasItem()) {
/* 224 */         if (!moveItemStackTo(toMove, 45, 46, false)) {
/* 225 */           return ItemStack.EMPTY;
/*     */         }
/* 227 */       } else if (slotID >= 9 && slotID < 36) {
/* 228 */         if (!moveItemStackTo(toMove, 36, 45, false)) {
/* 229 */           return ItemStack.EMPTY;
/*     */         }
/* 231 */       } else if (slotID >= 36 && slotID < 45) {
/* 232 */         if (!moveItemStackTo(toMove, 9, 36, false)) {
/* 233 */           return ItemStack.EMPTY;
/*     */         }
/* 235 */       } else if (!moveItemStackTo(toMove, 9, 45, false)) {
/* 236 */         return ItemStack.EMPTY;
/*     */       } 
/*     */       
/* 239 */       if (toMove.isEmpty()) {
/* 240 */         slot.set(ItemStack.EMPTY);
/*     */       }
/*     */       
/* 243 */       if (toMove.getAmount() == original.getAmount()) {
/* 244 */         return ItemStack.EMPTY;
/*     */       }
/*     */     } 
/*     */     
/* 248 */     return original;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canTakeItemForPickAll(ItemStack p_38908_, Slot p_38909_) {
/* 253 */     return (p_38909_.inventoryStorageSlot != 0);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\Inventory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */