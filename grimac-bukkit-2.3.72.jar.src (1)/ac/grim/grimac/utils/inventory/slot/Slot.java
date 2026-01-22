/*     */ package ac.grim.grimac.utils.inventory.slot;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.utils.inventory.InventoryStorage;
/*     */ import java.util.Optional;
/*     */ 
/*     */ public class Slot
/*     */ {
/*     */   public final int inventoryStorageSlot;
/*     */   public int slotListIndex;
/*     */   InventoryStorage container;
/*     */   
/*     */   public Slot(InventoryStorage container, int slot) {
/*  15 */     this.container = container;
/*  16 */     this.inventoryStorageSlot = slot;
/*     */   }
/*     */   
/*     */   public ItemStack getItem() {
/*  20 */     return this.container.getItem(this.inventoryStorageSlot);
/*     */   }
/*     */   
/*     */   public boolean hasItem() {
/*  24 */     return !getItem().isEmpty();
/*     */   }
/*     */   
/*     */   public boolean mayPlace(ItemStack itemstack) {
/*  28 */     return true;
/*     */   }
/*     */   
/*     */   public void set(ItemStack itemStack) {
/*  32 */     this.container.setItem(this.inventoryStorageSlot, itemStack);
/*     */   }
/*     */   
/*     */   public int getMaxStackSize() {
/*  36 */     return this.container.getMaxStackSize();
/*     */   }
/*     */   
/*     */   public int getMaxStackSize(ItemStack itemStack) {
/*  40 */     return Math.min(itemStack.getMaxStackSize(), getMaxStackSize());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean mayPickup() {
/*  46 */     return true;
/*     */   }
/*     */   
/*     */   public ItemStack safeTake(int p_150648_, int p_150649_, GrimPlayer p_150650_) {
/*  50 */     Optional<ItemStack> optional = tryRemove(p_150648_, p_150649_, p_150650_);
/*  51 */     optional.ifPresent(p_150655_ -> onTake(p_150650_, p_150655_));
/*  52 */     return optional.orElse(ItemStack.EMPTY);
/*     */   }
/*     */   
/*     */   public Optional<ItemStack> tryRemove(int p_150642_, int p_150643_, GrimPlayer player) {
/*  56 */     if (!mayPickup(player))
/*  57 */       return Optional.empty(); 
/*  58 */     if (!allowModification(player) && p_150643_ < getItem().getAmount()) {
/*  59 */       return Optional.empty();
/*     */     }
/*  61 */     p_150642_ = Math.min(p_150642_, p_150643_);
/*  62 */     ItemStack itemstack = remove(p_150642_);
/*  63 */     if (itemstack.isEmpty()) {
/*  64 */       return Optional.empty();
/*     */     }
/*  66 */     if (getItem().isEmpty()) {
/*  67 */       set(ItemStack.EMPTY);
/*     */     }
/*     */     
/*  70 */     return Optional.of(itemstack);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ItemStack safeInsert(ItemStack stack, int amount) {
/*  76 */     if (!stack.isEmpty() && mayPlace(stack)) {
/*  77 */       ItemStack itemstack = getItem();
/*  78 */       int i = Math.min(Math.min(amount, stack.getAmount()), getMaxStackSize(stack) - itemstack.getAmount());
/*  79 */       if (itemstack.isEmpty()) {
/*  80 */         set(stack.split(i));
/*  81 */       } else if (ItemStack.isSameItemSameTags(itemstack, stack)) {
/*  82 */         stack.shrink(i);
/*  83 */         itemstack.grow(i);
/*  84 */         set(itemstack);
/*     */       } 
/*     */     } 
/*  87 */     return stack;
/*     */   }
/*     */   
/*     */   public ItemStack remove(int p_40227_) {
/*  91 */     return this.container.removeItem(this.inventoryStorageSlot, p_40227_);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onTake(GrimPlayer player, ItemStack itemStack) {}
/*     */ 
/*     */   
/*     */   public boolean allowModification(GrimPlayer player) {
/* 100 */     return (mayPickup(player) && mayPlace(getItem()));
/*     */   }
/*     */   
/*     */   public boolean mayPickup(GrimPlayer player) {
/* 104 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\slot\Slot.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */