/*     */ package ac.grim.grimac.utils.inventory.inventory;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*     */ import ac.grim.grimac.utils.inventory.ClickAction;
/*     */ import ac.grim.grimac.utils.inventory.Inventory;
/*     */ import ac.grim.grimac.utils.inventory.InventoryStorage;
/*     */ import ac.grim.grimac.utils.inventory.slot.Slot;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ import lombok.Generated;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractContainerMenu
/*     */ {
/*     */   protected final GrimPlayer player;
/*  28 */   private int quickcraftStatus = 0;
/*  29 */   private int quickcraftType = -1;
/*  30 */   private final Set<Slot> quickcraftSlots = Sets.newHashSet(); private Inventory playerInventory; @Generated
/*  31 */   protected void setPlayerInventory(Inventory playerInventory) { this.playerInventory = playerInventory; }
/*     */   
/*  33 */   List<Slot> slots = new ArrayList<>(); @Generated public List<Slot> getSlots() { return this.slots; }
/*     */    @NotNull
/*  35 */   ItemStack carriedItem = ItemStack.EMPTY; @NotNull @Generated public ItemStack getCarriedItem() { return this.carriedItem; }
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractContainerMenu(GrimPlayer player, Inventory playerInventory) {
/*  40 */     this.player = player;
/*  41 */     this.playerInventory = playerInventory;
/*     */   }
/*     */   
/*     */   public AbstractContainerMenu(GrimPlayer player) {
/*  45 */     this.player = player;
/*     */   }
/*     */   
/*     */   public static int calculateQuickcraftHeader(int p_38948_) {
/*  49 */     return p_38948_ & 0x3;
/*     */   }
/*     */   
/*     */   public static int calculateQuickcraftMask(int p_38931_, int p_38932_) {
/*  53 */     return p_38931_ & 0x3 | (p_38932_ & 0x3) << 2;
/*     */   }
/*     */   
/*     */   public static int calculateQuickcraftType(int p_38929_) {
/*  57 */     return p_38929_ >> 2 & 0x3;
/*     */   }
/*     */   
/*     */   public static boolean canItemQuickReplace(@Nullable Slot p_38900_, ItemStack p_38901_, boolean p_38902_) {
/*  61 */     boolean flag = (p_38900_ == null || !p_38900_.hasItem());
/*  62 */     if (!flag && ItemStack.isSameItemSameTags(p_38901_, p_38900_.getItem())) {
/*  63 */       return (p_38900_.getItem().getAmount() + (p_38902_ ? 0 : p_38901_.getAmount()) <= p_38901_.getMaxStackSize());
/*     */     }
/*  65 */     return flag;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void getQuickCraftSlotCount(Set<Slot> p_38923_, int p_38924_, ItemStack p_38925_, int p_38926_) {
/*  70 */     switch (p_38924_) {
/*     */       case 0:
/*  72 */         p_38925_.setAmount(GrimMath.floor((p_38925_.getAmount() / p_38923_.size())));
/*     */         break;
/*     */       case 1:
/*  75 */         p_38925_.setAmount(1);
/*     */         break;
/*     */       case 2:
/*  78 */         p_38925_.setAmount(p_38925_.getType().getMaxAmount());
/*     */         break;
/*     */     } 
/*     */     
/*  82 */     p_38925_.grow(p_38926_);
/*     */   }
/*     */   
/*     */   public Slot addSlot(Slot slot) {
/*  86 */     slot.slotListIndex = this.slots.size();
/*  87 */     this.slots.add(slot);
/*  88 */     return slot;
/*     */   }
/*     */   
/*     */   public void addFourRowPlayerInventory() {
/*  92 */     for (int slot = 9; slot < 45; slot++) {
/*  93 */       addSlot(new Slot((InventoryStorage)this.playerInventory.getInventoryStorage(), slot));
/*     */     }
/*     */   }
/*     */   
/*     */   protected void resetQuickCraft() {
/*  98 */     this.quickcraftStatus = 0;
/*  99 */     this.quickcraftSlots.clear();
/*     */   }
/*     */   
/*     */   public boolean isValidQuickcraftType(int p_38863_) {
/* 103 */     if (p_38863_ == 0)
/* 104 */       return true; 
/* 105 */     if (p_38863_ == 1) {
/* 106 */       return true;
/*     */     }
/* 108 */     return (p_38863_ == 2 && this.player.gamemode == GameMode.CREATIVE);
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack getCarried() {
/* 113 */     return getCarriedItem();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCarried(ItemStack stack) {
/* 118 */     this.carriedItem = (stack == null) ? ItemStack.EMPTY : stack;
/*     */   }
/*     */   
/*     */   public ItemStack getPlayerInventoryItem(int slot) {
/* 122 */     return this.playerInventory.getInventoryStorage().getItem(slot);
/*     */   }
/*     */   
/*     */   public void setPlayerInventoryItem(int slot, ItemStack stack) {
/* 126 */     this.playerInventory.getInventoryStorage().setItem(slot, stack);
/*     */   }
/*     */   
/*     */   public void doClick(int button, int slotID, WrapperPlayClientClickWindow.WindowClickType clickType) {
/* 130 */     if (clickType == WrapperPlayClientClickWindow.WindowClickType.QUICK_CRAFT) {
/* 131 */       int i = this.quickcraftStatus;
/* 132 */       this.quickcraftStatus = calculateQuickcraftHeader(button);
/* 133 */       if ((i != 1 || this.quickcraftStatus != 2) && i != this.quickcraftStatus) {
/* 134 */         resetQuickCraft();
/* 135 */       } else if (getCarried().isEmpty()) {
/* 136 */         resetQuickCraft();
/* 137 */       } else if (this.quickcraftStatus == 0) {
/* 138 */         this.quickcraftType = calculateQuickcraftType(button);
/* 139 */         if (isValidQuickcraftType(this.quickcraftType)) {
/* 140 */           this.quickcraftStatus = 1;
/* 141 */           this.quickcraftSlots.clear();
/*     */         } else {
/* 143 */           resetQuickCraft();
/*     */         } 
/* 145 */       } else if (this.quickcraftStatus == 1) {
/* 146 */         if (slotID < 0)
/* 147 */           return;  Slot slot = this.slots.get(slotID);
/* 148 */         ItemStack itemstack = getCarried();
/* 149 */         if (canItemQuickReplace(slot, itemstack, true) && slot.mayPlace(itemstack) && (this.quickcraftType == 2 || itemstack.getAmount() > this.quickcraftSlots.size()) && canDragTo(slot)) {
/* 150 */           this.quickcraftSlots.add(slot);
/*     */         }
/* 152 */       } else if (this.quickcraftStatus == 2) {
/* 153 */         if (!this.quickcraftSlots.isEmpty()) {
/* 154 */           if (this.quickcraftSlots.size() == 1) {
/* 155 */             int l = ((Slot)this.quickcraftSlots.iterator().next()).slotListIndex;
/* 156 */             resetQuickCraft();
/* 157 */             doClick(this.quickcraftType, l, WrapperPlayClientClickWindow.WindowClickType.PICKUP);
/*     */             
/*     */             return;
/*     */           } 
/* 161 */           ItemStack itemstack3 = getCarried().copy();
/* 162 */           int j1 = getCarried().getAmount();
/*     */           
/* 164 */           for (Slot slot1 : this.quickcraftSlots) {
/* 165 */             ItemStack itemstack1 = getCarried();
/* 166 */             if (slot1 != null && canItemQuickReplace(slot1, itemstack1, true) && slot1.mayPlace(itemstack1) && (this.quickcraftType == 2 || itemstack1.getAmount() >= this.quickcraftSlots.size()) && canDragTo(slot1)) {
/* 167 */               ItemStack itemstack2 = itemstack3.copy();
/* 168 */               int j = slot1.hasItem() ? slot1.getItem().getAmount() : 0;
/* 169 */               getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, itemstack2, j);
/* 170 */               int k = Math.min(itemstack2.getMaxStackSize(), slot1.getMaxStackSize(itemstack2));
/* 171 */               if (itemstack2.getAmount() > k) {
/* 172 */                 itemstack2.setAmount(k);
/*     */               }
/*     */               
/* 175 */               j1 -= itemstack2.getAmount() - j;
/* 176 */               slot1.set(itemstack2);
/*     */             } 
/*     */           } 
/*     */           
/* 180 */           itemstack3.setAmount(j1);
/* 181 */           setCarried(itemstack3);
/*     */         } 
/*     */         
/* 184 */         resetQuickCraft();
/*     */       } else {
/* 186 */         resetQuickCraft();
/*     */       } 
/* 188 */     } else if (this.quickcraftStatus != 0) {
/* 189 */       resetQuickCraft();
/* 190 */     } else if ((clickType == WrapperPlayClientClickWindow.WindowClickType.PICKUP || clickType == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE) && (button == 0 || button == 1)) {
/* 191 */       ClickAction clickAction = ClickAction.values()[button];
/* 192 */       if (slotID == -999) {
/* 193 */         if (!getCarried().isEmpty()) {
/* 194 */           if (clickAction == ClickAction.PRIMARY) {
/* 195 */             setCarried(ItemStack.EMPTY);
/*     */           } else {
/* 197 */             getCarried().split(1);
/*     */           } 
/*     */         }
/* 200 */       } else if (clickType == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE) {
/* 201 */         if (slotID < 0)
/*     */           return; 
/* 203 */         Slot stack = getSlot(slotID);
/* 204 */         if (!stack.mayPickup()) {
/*     */           return;
/*     */         }
/*     */         
/* 208 */         ItemStack itemstack9 = quickMoveStack(slotID);
/* 209 */         while (!itemstack9.isEmpty() && ItemStack.isSameItemSameTags(stack.getItem(), itemstack9)) {
/* 210 */           itemstack9 = quickMoveStack(slotID);
/*     */         }
/*     */       } else {
/* 213 */         if (slotID < 0)
/*     */           return; 
/* 215 */         Slot slot = getSlot(slotID);
/* 216 */         ItemStack slotItem = slot.getItem();
/* 217 */         ItemStack carriedItem = getCarried();
/*     */ 
/*     */ 
/*     */         
/* 221 */         if (slot instanceof ac.grim.grimac.utils.inventory.slot.ResultSlot) {
/* 222 */           this.player.inventory.isPacketInventoryActive = false;
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 227 */         if (slotItem.isEmpty()) {
/* 228 */           if (!carriedItem.isEmpty()) {
/* 229 */             int l2 = (clickAction == ClickAction.PRIMARY) ? carriedItem.getAmount() : 1;
/* 230 */             setCarried(slot.safeInsert(carriedItem, l2));
/*     */           } 
/* 232 */         } else if (slot.mayPickup()) {
/* 233 */           if (carriedItem.isEmpty()) {
/* 234 */             int i3 = (clickAction == ClickAction.PRIMARY) ? slotItem.getAmount() : ((slotItem.getAmount() + 1) / 2);
/* 235 */             Optional<ItemStack> optional1 = slot.tryRemove(i3, 2147483647, this.player);
/* 236 */             optional1.ifPresent(p_150421_ -> {
/*     */                   setCarried(p_150421_);
/*     */                   slot.onTake(this.player, p_150421_);
/*     */                 });
/* 240 */           } else if (slot.mayPlace(carriedItem)) {
/* 241 */             if (ItemStack.isSameItemSameTags(slotItem, carriedItem)) {
/* 242 */               int j3 = (clickAction == ClickAction.PRIMARY) ? carriedItem.getAmount() : 1;
/* 243 */               setCarried(slot.safeInsert(carriedItem, j3));
/* 244 */             } else if (carriedItem.getAmount() <= slot.getMaxStackSize(carriedItem)) {
/* 245 */               slot.set(carriedItem);
/* 246 */               setCarried(slotItem);
/*     */             } 
/* 248 */           } else if (ItemStack.isSameItemSameTags(slotItem, carriedItem)) {
/* 249 */             Optional<ItemStack> optional = slot.tryRemove(slotItem.getAmount(), carriedItem.getMaxStackSize() - carriedItem.getAmount(), this.player);
/* 250 */             optional.ifPresent(p_150428_ -> {
/*     */                   carriedItem.grow(p_150428_.getAmount());
/*     */                   
/*     */                   slot.onTake(this.player, p_150428_);
/*     */                 });
/*     */           } 
/*     */         } 
/*     */       } 
/* 258 */     } else if (clickType == WrapperPlayClientClickWindow.WindowClickType.SWAP) {
/* 259 */       Slot hoveringSlot = this.slots.get(slotID);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 264 */       if (button != 40 && (button < 0 || button >= 9))
/*     */         return; 
/* 266 */       button = (button == 40) ? 45 : (button + 36);
/*     */ 
/*     */       
/* 269 */       ItemStack hotbarKeyStack = getPlayerInventoryItem(button);
/* 270 */       ItemStack hoveringItem2 = hoveringSlot.getItem();
/*     */       
/* 272 */       if (!hotbarKeyStack.isEmpty() || !hoveringItem2.isEmpty()) {
/* 273 */         if (hotbarKeyStack.isEmpty()) {
/* 274 */           if (hoveringSlot.mayPickup(this.player)) {
/* 275 */             setPlayerInventoryItem(button, hoveringItem2);
/* 276 */             hoveringSlot.set(ItemStack.EMPTY);
/* 277 */             hoveringSlot.onTake(this.player, hoveringItem2);
/*     */           } 
/* 279 */         } else if (hoveringItem2.isEmpty()) {
/* 280 */           if (hoveringSlot.mayPlace(hotbarKeyStack)) {
/* 281 */             int l1 = hoveringSlot.getMaxStackSize(hotbarKeyStack);
/* 282 */             if (hotbarKeyStack.getAmount() > l1) {
/* 283 */               hoveringSlot.set(hotbarKeyStack.split(l1));
/*     */             } else {
/* 285 */               hoveringSlot.set(hotbarKeyStack);
/* 286 */               setPlayerInventoryItem(button, ItemStack.EMPTY);
/*     */             } 
/*     */           } 
/* 289 */         } else if (hoveringSlot.mayPickup(this.player) && hoveringSlot.mayPlace(hotbarKeyStack)) {
/* 290 */           int i2 = hoveringSlot.getMaxStackSize(hotbarKeyStack);
/* 291 */           if (hotbarKeyStack.getAmount() > i2) {
/* 292 */             hoveringSlot.set(hotbarKeyStack.split(i2));
/* 293 */             hoveringSlot.onTake(this.player, hoveringItem2);
/* 294 */             this.playerInventory.add(hoveringItem2);
/*     */           } else {
/* 296 */             hoveringSlot.set(hotbarKeyStack);
/* 297 */             setPlayerInventoryItem(button, hoveringItem2);
/* 298 */             hoveringSlot.onTake(this.player, hoveringItem2);
/*     */           } 
/*     */         } 
/*     */       }
/* 302 */     } else if (clickType == WrapperPlayClientClickWindow.WindowClickType.CLONE && this.player.gamemode == GameMode.CREATIVE && slotID >= 0 && this.carriedItem.isEmpty()) {
/* 303 */       Slot slot5 = getSlot(slotID);
/* 304 */       if (slot5.hasItem()) {
/* 305 */         ItemStack itemstack6 = slot5.getItem().copy();
/* 306 */         itemstack6.setAmount(itemstack6.getMaxStackSize());
/* 307 */         setCarried(itemstack6);
/*     */       } 
/* 309 */     } else if (clickType == WrapperPlayClientClickWindow.WindowClickType.THROW && getCarried().isEmpty() && slotID >= 0) {
/* 310 */       Slot slot4 = getSlot(slotID);
/* 311 */       int i1 = (button == 0) ? 1 : slot4.getItem().getAmount();
/* 312 */       ItemStack itemStack = slot4.safeTake(i1, 2147483647, this.player);
/* 313 */     } else if (clickType == WrapperPlayClientClickWindow.WindowClickType.PICKUP_ALL && slotID >= 0) {
/* 314 */       Slot slot3 = getSlot(slotID);
/*     */       
/* 316 */       if (!getCarried().isEmpty() && (!slot3.hasItem() || !slot3.mayPickup(this.player))) {
/* 317 */         int k1 = (button == 0) ? 0 : (this.slots.size() - 1);
/* 318 */         int j2 = (button == 0) ? 1 : -1;
/*     */         
/* 320 */         for (int k2 = 0; k2 < 2; k2++) {
/* 321 */           int k3; for (k3 = k1; k3 >= 0 && k3 < this.slots.size() && getCarried().getAmount() < getCarried().getMaxStackSize(); k3 += j2) {
/* 322 */             Slot slot8 = this.slots.get(k3);
/* 323 */             if (slot8.hasItem() && canItemQuickReplace(slot8, getCarried(), true) && slot8.mayPickup(this.player) && canTakeItemForPickAll(getCarried(), slot8)) {
/* 324 */               ItemStack itemstack12 = slot8.getItem();
/* 325 */               if (k2 != 0 || itemstack12.getAmount() != itemstack12.getMaxStackSize()) {
/* 326 */                 ItemStack itemstack13 = slot8.safeTake(itemstack12.getAmount(), getCarried().getMaxStackSize() - getCarried().getAmount(), this.player);
/* 327 */                 getCarried().grow(itemstack13.getAmount());
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean moveItemStackTo(ItemStack toMove, int min, int max, boolean reverse) {
/* 337 */     boolean flag = false;
/* 338 */     int i = min;
/* 339 */     if (reverse) {
/* 340 */       i = max - 1;
/*     */     }
/*     */     
/* 343 */     if (toMove.getType().getMaxAmount() > 1) {
/* 344 */       while (!toMove.isEmpty() && (
/* 345 */         reverse ? (
/* 346 */         i < min) : (
/*     */ 
/*     */         
/* 349 */         i >= max))) {
/*     */ 
/*     */ 
/*     */         
/* 353 */         Slot slot = this.slots.get(i);
/* 354 */         ItemStack itemstack = slot.getItem();
/* 355 */         if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(toMove, itemstack)) {
/* 356 */           int j = itemstack.getAmount() + toMove.getAmount();
/* 357 */           if (j <= toMove.getMaxStackSize()) {
/* 358 */             toMove.setAmount(0);
/* 359 */             itemstack.setAmount(j);
/* 360 */             flag = true;
/* 361 */           } else if (itemstack.getAmount() < toMove.getMaxStackSize()) {
/* 362 */             toMove.shrink(toMove.getMaxStackSize() - itemstack.getAmount());
/* 363 */             itemstack.setAmount(toMove.getMaxStackSize());
/* 364 */             flag = true;
/*     */           } 
/*     */         } 
/*     */         
/* 368 */         if (reverse) {
/* 369 */           i--; continue;
/*     */         } 
/* 371 */         i++;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 376 */     if (!toMove.isEmpty()) {
/* 377 */       if (reverse) {
/* 378 */         i = max - 1;
/*     */       } else {
/* 380 */         i = min;
/*     */       } 
/*     */ 
/*     */       
/* 384 */       while (reverse ? (
/* 385 */         i < min) : (
/*     */ 
/*     */         
/* 388 */         i >= max)) {
/*     */ 
/*     */ 
/*     */         
/* 392 */         Slot slot1 = this.slots.get(i);
/* 393 */         ItemStack itemstack1 = slot1.getItem();
/* 394 */         if (itemstack1.isEmpty() && slot1.mayPlace(toMove)) {
/* 395 */           if (toMove.getAmount() > slot1.getMaxStackSize()) {
/* 396 */             slot1.set(toMove.split(slot1.getMaxStackSize()));
/*     */           } else {
/* 398 */             slot1.set(toMove.split(toMove.getAmount()));
/*     */           } 
/*     */           
/* 401 */           flag = true;
/*     */           
/*     */           break;
/*     */         } 
/* 405 */         if (reverse) {
/* 406 */           i--; continue;
/*     */         } 
/* 408 */         i++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 413 */     return flag;
/*     */   }
/*     */   
/*     */   public boolean canTakeItemForPickAll(ItemStack p_38908_, Slot p_38909_) {
/* 417 */     return true;
/*     */   }
/*     */   
/*     */   public ItemStack quickMoveStack(int slotID) {
/* 421 */     return ((Slot)this.slots.get(slotID)).getItem();
/*     */   }
/*     */   
/*     */   public Slot getSlot(int slotID) {
/*     */     try {
/* 426 */       return this.slots.get(slotID);
/* 427 */     } catch (IndexOutOfBoundsException e) {
/* 428 */       LogUtil.error("Tried to get slot " + slotID + " in a container with only " + this.slots.size() + " slots, container type: " + getClass().getName(), e);
/* 429 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean canDragTo(Slot slot) {
/* 434 */     return true;
/*     */   }
/*     */   
/*     */   public int getMaxStackSize() {
/* 438 */     return 64;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\inventory\AbstractContainerMenu.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */