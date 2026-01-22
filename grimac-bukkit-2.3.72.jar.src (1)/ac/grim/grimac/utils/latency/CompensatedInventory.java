/*     */ package ac.grim.grimac.utils.latency;
/*     */ 
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.type.PacketCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenHorseWindow;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
/*     */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*     */ import ac.grim.grimac.utils.inventory.EquipmentType;
/*     */ import ac.grim.grimac.utils.inventory.Inventory;
/*     */ import ac.grim.grimac.utils.inventory.inventory.AbstractContainerMenu;
/*     */ import ac.grim.grimac.utils.inventory.inventory.MenuType;
/*     */ import ac.grim.grimac.utils.lists.CorrectingPlayerInventoryStorage;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompensatedInventory
/*     */   extends Check
/*     */   implements PacketCheck
/*     */ {
/*     */   private static final int PLAYER_INVENTORY_CASE = -1;
/*     */   private static final int UNSUPPORTED_INVENTORY_CASE = -2;
/*     */   public Inventory inventory;
/*     */   public AbstractContainerMenu menu;
/*     */   public boolean isPacketInventoryActive = true;
/*     */   public boolean needResend = false;
/*  52 */   public int stateID = 0;
/*  53 */   private int openWindowID = 0;
/*     */ 
/*     */ 
/*     */   
/*  57 */   private int packetSendingInventorySize = -1;
/*     */   
/*     */   public CompensatedInventory(GrimPlayer playerData) {
/*  60 */     super(playerData);
/*     */     
/*  62 */     CorrectingPlayerInventoryStorage storage = new CorrectingPlayerInventoryStorage(this.player, 46);
/*  63 */     this.inventory = new Inventory(playerData, storage);
/*     */     
/*  65 */     this.menu = (AbstractContainerMenu)this.inventory;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBukkitSlot(int packetSlot) {
/*  71 */     if (packetSlot <= 4) {
/*  72 */       return -1;
/*     */     }
/*     */     
/*  75 */     if (packetSlot <= 8)
/*     */     {
/*  77 */       return 7 - packetSlot + 36;
/*     */     }
/*     */     
/*  80 */     if (packetSlot <= 35) {
/*  81 */       return packetSlot;
/*     */     }
/*     */     
/*  84 */     if (packetSlot <= 44)
/*     */     {
/*  86 */       return packetSlot - 36;
/*     */     }
/*     */     
/*  89 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9) && packetSlot == 45) {
/*  90 */       return 40;
/*     */     }
/*  92 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void markPlayerSlotAsChanged(int clicked) {
/*  98 */     if (this.openWindowID == 0) {
/*  99 */       this.inventory.getInventoryStorage().handleClientClaimedSlotSet(clicked);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 105 */     if (this.menu instanceof ac.grim.grimac.utils.inventory.inventory.NotImplementedMenu) {
/*     */       return;
/*     */     }
/*     */     
/* 109 */     int nonPlayerInvSize = this.menu.getSlots().size() - 36 + 9;
/* 110 */     int playerInvSlotclicked = clicked - nonPlayerInvSize;
/*     */     
/* 112 */     this.inventory.getInventoryStorage().handleClientClaimedSlotSet(playerInvSlotclicked);
/*     */   }
/*     */   
/*     */   public ItemStack getItemInHand(InteractionHand hand) {
/* 116 */     return (hand == InteractionHand.MAIN_HAND) ? getHeldItem() : getOffHand();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void markServerForChangingSlot(int clicked, int windowID) {
/* 126 */     if (this.packetSendingInventorySize == -2)
/*     */       return; 
/* 128 */     if (this.packetSendingInventorySize == -1 || windowID == 0) {
/*     */       
/* 130 */       this.inventory.getInventoryStorage().handleServerCorrectSlot(clicked);
/*     */       
/*     */       return;
/*     */     } 
/* 134 */     int nonPlayerInvSize = this.menu.getSlots().size() - 36 + 9;
/* 135 */     int playerInvSlotclicked = clicked - nonPlayerInvSize;
/*     */     
/* 137 */     this.inventory.getInventoryStorage().handleServerCorrectSlot(playerInvSlotclicked);
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack getHeldItem() {
/* 142 */     ItemStack item = (this.isPacketInventoryActive || this.player.platformPlayer == null) ? this.inventory.getHeldItem() : this.player.platformPlayer.getInventory().getItemInHand();
/* 143 */     return (item == null) ? ItemStack.EMPTY : item;
/*     */   }
/*     */   
/*     */   public ItemStack getOffHand() {
/* 147 */     if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) {
/* 148 */       return ItemStack.EMPTY;
/*     */     }
/* 150 */     ItemStack item = (this.isPacketInventoryActive || this.player.platformPlayer == null) ? this.inventory.getOffhand() : this.player.platformPlayer.getInventory().getItemInOffHand();
/* 151 */     return (item == null) ? ItemStack.EMPTY : item;
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack getHelmet() {
/* 156 */     ItemStack item = (this.isPacketInventoryActive || this.player.platformPlayer == null) ? this.inventory.getHelmet() : this.player.platformPlayer.getInventory().getHelmet();
/* 157 */     return (item == null) ? ItemStack.EMPTY : item;
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack getChestplate() {
/* 162 */     ItemStack item = (this.isPacketInventoryActive || this.player.platformPlayer == null) ? this.inventory.getChestplate() : this.player.platformPlayer.getInventory().getChestplate();
/* 163 */     return (item == null) ? ItemStack.EMPTY : item;
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack getLeggings() {
/* 168 */     ItemStack item = (this.isPacketInventoryActive || this.player.platformPlayer == null) ? this.inventory.getLeggings() : this.player.platformPlayer.getInventory().getLeggings();
/* 169 */     return (item == null) ? ItemStack.EMPTY : item;
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack getBoots() {
/* 174 */     ItemStack item = (this.isPacketInventoryActive || this.player.platformPlayer == null) ? this.inventory.getBoots() : this.player.platformPlayer.getInventory().getBoots();
/* 175 */     return (item == null) ? ItemStack.EMPTY : item;
/*     */   }
/*     */   
/*     */   private ItemStack getByEquipmentType(EquipmentType type) {
/* 179 */     switch (type) { default: throw new IncompatibleClassChangeError();case HEAD: case CHEST: case LEGS: case FEET: case OFFHAND: case MAINHAND: break; }  return 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 185 */       getHeldItem();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasItemType(ItemType type) {
/* 190 */     if (this.isPacketInventoryActive || this.player.platformPlayer == null) {
/* 191 */       return this.inventory.hasItemType(type);
/*     */     }
/*     */     
/* 194 */     for (ItemStack itemStack : this.player.platformPlayer.getInventory().getContents()) {
/* 195 */       if (itemStack != null && itemStack.getType() == type) return true; 
/*     */     } 
/* 197 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/* 202 */     if (event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
/* 203 */       WrapperPlayClientUseItem item = new WrapperPlayClientUseItem(event);
/*     */       
/* 205 */       ItemStack use = (item.getHand() == InteractionHand.MAIN_HAND) ? getHeldItem() : getOffHand();
/*     */       
/* 207 */       EquipmentType equipmentType = EquipmentType.getEquipmentSlotForItem(use);
/* 208 */       if (equipmentType != null) {
/*     */         int slot;
/* 210 */         switch (equipmentType) { case HEAD:
/* 211 */             slot = 4; break;
/* 212 */           case CHEST: slot = 5; break;
/* 213 */           case LEGS: slot = 6; break;
/* 214 */           case FEET: slot = 7;
/*     */             break;
/*     */           
/*     */           default:
/*     */             return; }
/*     */         
/* 220 */         ItemStack currentEquippedItem = getByEquipmentType(equipmentType);
/*     */         
/* 222 */         if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_19_4) && !currentEquippedItem.isEmpty()) {
/*     */           return;
/*     */         }
/*     */         
/* 226 */         int swapItemSlot = (item.getHand() == InteractionHand.MAIN_HAND) ? (this.inventory.selected + 36) : 45;
/*     */ 
/*     */ 
/*     */         
/* 230 */         this.inventory.getInventoryStorage().handleClientClaimedSlotSet(swapItemSlot);
/* 231 */         this.inventory.getInventoryStorage().setItem(swapItemSlot, currentEquippedItem);
/*     */ 
/*     */         
/* 234 */         this.inventory.getInventoryStorage().handleClientClaimedSlotSet(slot);
/* 235 */         this.inventory.getInventoryStorage().setItem(slot, use);
/*     */       } 
/*     */     } 
/*     */     
/* 239 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
/* 240 */       WrapperPlayClientPlayerDigging dig = new WrapperPlayClientPlayerDigging(event);
/*     */ 
/*     */       
/* 243 */       if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8))
/*     */         return; 
/* 245 */       if (dig.getAction() == DiggingAction.DROP_ITEM) {
/* 246 */         ItemStack heldItem = getHeldItem();
/* 247 */         if (heldItem != null) {
/* 248 */           heldItem.setAmount(heldItem.getAmount() - 1);
/* 249 */           if (heldItem.getAmount() <= 0) {
/* 250 */             heldItem = null;
/*     */           }
/*     */         } 
/* 253 */         this.inventory.setHeldItem(heldItem);
/* 254 */         this.inventory.getInventoryStorage().handleClientClaimedSlotSet(36 + this.player.packetStateData.lastSlotSelected);
/*     */       } 
/*     */       
/* 257 */       if (dig.getAction() == DiggingAction.DROP_ITEM_STACK) {
/* 258 */         this.inventory.setHeldItem(null);
/* 259 */         this.inventory.getInventoryStorage().handleClientClaimedSlotSet(36 + this.player.packetStateData.lastSlotSelected);
/*     */       } 
/*     */     } 
/*     */     
/* 263 */     if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
/* 264 */       int slot = (new WrapperPlayClientHeldItemChange(event)).getSlot();
/*     */ 
/*     */       
/* 267 */       if (slot > 8 || slot < 0)
/*     */         return; 
/* 269 */       this.inventory.selected = slot;
/*     */     } 
/*     */     
/* 272 */     if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
/* 273 */       WrapperPlayClientCreativeInventoryAction action = new WrapperPlayClientCreativeInventoryAction(event);
/* 274 */       if (this.player.gamemode != GameMode.CREATIVE) {
/*     */         return;
/*     */       }
/*     */       
/* 278 */       boolean valid = (action.getSlot() >= 1 && (PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_8) ? (action.getSlot() <= 45) : (action.getSlot() < 45)));
/*     */       
/* 280 */       if (valid) {
/* 281 */         this.inventory.getSlot(action.getSlot()).set(action.getItemStack());
/* 282 */         this.inventory.getInventoryStorage().handleClientClaimedSlotSet(action.getSlot());
/*     */       } 
/*     */     } 
/*     */     
/* 286 */     if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW && !event.isCancelled()) {
/* 287 */       WrapperPlayClientClickWindow click = new WrapperPlayClientClickWindow(event);
/*     */ 
/*     */       
/* 290 */       if (click.getWindowId() != this.openWindowID) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 295 */       if (this.menu instanceof ac.grim.grimac.utils.inventory.inventory.NotImplementedMenu) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 300 */       Optional<Map<Integer, ItemStack>> slots = click.getSlots();
/* 301 */       slots.ifPresent(integerItemStackMap -> integerItemStackMap.keySet().forEach(this::markPlayerSlotAsChanged));
/*     */ 
/*     */ 
/*     */       
/* 305 */       int button = click.getButton();
/*     */ 
/*     */       
/* 308 */       int slot = click.getSlot();
/*     */       
/* 310 */       WrapperPlayClientClickWindow.WindowClickType clickType = click.getWindowClickType();
/*     */       
/* 312 */       if (slot == -1 || slot == -999 || slot < this.menu.getSlots().size()) {
/* 313 */         this.menu.doClick(button, slot, clickType);
/*     */       }
/*     */     } 
/*     */     
/* 317 */     if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
/* 318 */       closeActiveInventory();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void markSlotAsResyncing(BlockPlace place) {
/* 324 */     if (place.hand == InteractionHand.MAIN_HAND) {
/* 325 */       this.inventory.getInventoryStorage().handleClientClaimedSlotSet(36 + this.player.packetStateData.lastSlotSelected);
/*     */     } else {
/* 327 */       this.inventory.getInventoryStorage().handleServerCorrectSlot(45);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void onBlockPlace(BlockPlace place) {
/* 332 */     if (this.player.gamemode != GameMode.CREATIVE && place.itemStack.getType() != ItemTypes.POWDER_SNOW_BUCKET) {
/* 333 */       markSlotAsResyncing(place);
/* 334 */       place.itemStack.setAmount(place.itemStack.getAmount() - 1);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/* 343 */     if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
/* 344 */       AbstractContainerMenu newMenu; WrapperPlayServerOpenWindow open = new WrapperPlayServerOpenWindow(event);
/*     */       
/* 346 */       MenuType menuType = MenuType.getMenuType(open.getType());
/*     */ 
/*     */       
/* 349 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_14)) {
/* 350 */         newMenu = MenuType.getMenuFromID(this.player, this.inventory, menuType);
/*     */       } else {
/* 352 */         newMenu = MenuType.getMenuFromString(this.player, this.inventory, open.getLegacyType(), open.getLegacySlots(), open.getHorseId());
/*     */       } 
/*     */       
/* 355 */       this.packetSendingInventorySize = (newMenu instanceof ac.grim.grimac.utils.inventory.inventory.NotImplementedMenu) ? -2 : newMenu.getSlots().size();
/*     */ 
/*     */ 
/*     */       
/* 359 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             this.openWindowID = open.getContainerId();
/*     */             
/*     */             this.menu = newMenu;
/*     */             
/*     */             this.isPacketInventoryActive = !(newMenu instanceof ac.grim.grimac.utils.inventory.inventory.NotImplementedMenu);
/*     */             this.needResend = newMenu instanceof ac.grim.grimac.utils.inventory.inventory.NotImplementedMenu;
/*     */           });
/*     */     } 
/* 368 */     if (event.getPacketType() == PacketType.Play.Server.OPEN_HORSE_WINDOW) {
/* 369 */       WrapperPlayServerOpenHorseWindow open = new WrapperPlayServerOpenHorseWindow(event);
/*     */       
/* 371 */       this.packetSendingInventorySize = -2;
/* 372 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             this.isPacketInventoryActive = false;
/*     */             
/*     */             this.needResend = true;
/*     */             
/*     */             this.openWindowID = open.getWindowId();
/*     */           });
/*     */     } 
/* 380 */     if (event.getPacketType() == PacketType.Play.Server.CLOSE_WINDOW) {
/* 381 */       this.packetSendingInventorySize = -1;
/*     */ 
/*     */ 
/*     */       
/* 385 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), this::closeActiveInventory);
/*     */     } 
/*     */ 
/*     */     
/* 389 */     if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
/* 390 */       WrapperPlayServerWindowItems items = new WrapperPlayServerWindowItems(event);
/* 391 */       this.stateID = items.getStateId();
/*     */       
/* 393 */       List<ItemStack> slots = items.getItems();
/* 394 */       for (int i = 0; i < slots.size(); i++) {
/* 395 */         markServerForChangingSlot(i, items.getWindowId());
/*     */       }
/*     */       
/* 398 */       int cachedPacketInvSize = this.packetSendingInventorySize;
/* 399 */       AtomicBoolean updatedValue = new AtomicBoolean();
/* 400 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             if (slots.size() == cachedPacketInvSize || items.getWindowId() == 0) {
/*     */               this.isPacketInventoryActive = true;
/*     */ 
/*     */ 
/*     */               
/*     */               updatedValue.set(true);
/*     */             } 
/*     */           });
/*     */ 
/*     */ 
/*     */       
/* 412 */       if (items.getWindowId() == 0) {
/* 413 */         this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */               if (!this.isPacketInventoryActive) {
/*     */                 return;
/*     */               }
/*     */               for (int i = 0; i < slots.size(); i++)
/*     */                 this.inventory.getSlot(i).set(slots.get(i)); 
/*     */               if (items.getCarriedItem().isPresent())
/*     */                 this.inventory.setCarried(items.getCarriedItem().get()); 
/*     */             });
/*     */       } else {
/* 423 */         this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */               if (!this.isPacketInventoryActive) {
/*     */                 return;
/*     */               }
/*     */               if (items.getWindowId() == this.openWindowID) {
/*     */                 for (int i = 0; i < slots.size(); i++) {
/*     */                   this.menu.getSlot(i).set(slots.get(i));
/*     */                 }
/*     */               }
/*     */               if (items.getCarriedItem().isPresent())
/*     */                 this.inventory.setCarried(items.getCarriedItem().get()); 
/*     */             });
/*     */       } 
/* 436 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             if (updatedValue.get() && !this.menu.equals(this.inventory)) {
/*     */               this.isPacketInventoryActive = false;
/*     */             }
/*     */           });
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 445 */     if (event.getPacketType() == PacketType.Play.Server.SET_PLAYER_INVENTORY) {
/* 446 */       WrapperPlayServerSetPlayerInventory slot = new WrapperPlayServerSetPlayerInventory(event);
/* 447 */       int slotID = slot.getSlot();
/* 448 */       ItemStack item = slot.getStack();
/*     */       
/* 450 */       this.inventory.getInventoryStorage().handleServerCorrectSlot(slotID);
/*     */       
/* 452 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             if (!this.isPacketInventoryActive) {
/*     */               return;
/*     */             }
/*     */             this.inventory.getSlot(slotID).set(item);
/*     */           });
/*     */     } 
/* 459 */     if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
/*     */ 
/*     */ 
/*     */       
/* 463 */       WrapperPlayServerSetSlot slot = new WrapperPlayServerSetSlot(event);
/* 464 */       int slotID = slot.getSlot();
/* 465 */       int inventoryID = slot.getWindowId();
/* 466 */       ItemStack item = slot.getItem();
/*     */       
/* 468 */       if (inventoryID == -2) {
/* 469 */         this.inventory.getInventoryStorage().handleServerCorrectSlot(slotID);
/* 470 */       } else if (inventoryID == 0) {
/* 471 */         this.inventory.getInventoryStorage().handleServerCorrectSlot(slotID);
/*     */       } else {
/* 473 */         markServerForChangingSlot(slotID, inventoryID);
/*     */       } 
/*     */       
/* 476 */       this.stateID = slot.getStateId();
/*     */       
/* 478 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             if (!this.isPacketInventoryActive) {
/*     */               return;
/*     */             }
/*     */             if (inventoryID == -1) {
/*     */               this.inventory.setCarried(item);
/*     */             } else if (inventoryID == -2) {
/*     */               if (this.inventory.getInventoryStorage().getSize() > slotID && slotID >= 0) {
/*     */                 this.inventory.getInventoryStorage().setItem(slotID, item);
/*     */               }
/*     */             } else if (inventoryID == 0) {
/*     */               if (slotID >= 0 && slotID <= 45) {
/*     */                 this.inventory.getSlot(slotID).set(item);
/*     */               }
/*     */             } else if (inventoryID == this.openWindowID) {
/*     */               this.menu.getSlot(slotID).set(item);
/*     */             } 
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void closeActiveInventory() {
/* 504 */     this.openWindowID = 0;
/* 505 */     this.menu = (AbstractContainerMenu)this.inventory;
/* 506 */     this.menu.setCarried(ItemStack.EMPTY);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\latency\CompensatedInventory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */