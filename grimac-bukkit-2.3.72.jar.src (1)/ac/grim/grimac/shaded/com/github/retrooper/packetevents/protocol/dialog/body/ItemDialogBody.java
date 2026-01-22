/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.body;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTByte;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import org.jspecify.annotations.NullMarked;
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
/*     */ @NullMarked
/*     */ public class ItemDialogBody
/*     */   implements DialogBody
/*     */ {
/*     */   private final ItemStack item;
/*     */   private final PlainMessage description;
/*     */   private final boolean showDecorations;
/*     */   private final boolean showTooltip;
/*     */   private final int width;
/*     */   private final int height;
/*     */   
/*     */   public ItemDialogBody(ItemStack item, PlainMessage description, boolean showDecorations, boolean showTooltip, int width, int height) {
/*  44 */     this.item = item;
/*  45 */     this.description = description;
/*  46 */     this.showDecorations = showDecorations;
/*  47 */     this.showTooltip = showTooltip;
/*  48 */     this.width = width;
/*  49 */     this.height = height;
/*     */   }
/*     */   
/*     */   public static ItemDialogBody decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/*  53 */     ItemStack item = (ItemStack)compound.getOrThrow("item", ItemStack::decode, wrapper);
/*  54 */     PlainMessage description = (PlainMessage)compound.getOrNull("description", PlainMessage::decode, wrapper);
/*  55 */     boolean showDecorations = compound.getBooleanOr("show_decorations", true);
/*  56 */     boolean showTooltip = compound.getBooleanOr("show_tooltip", true);
/*  57 */     int width = compound.getNumberTagValueOrDefault("width", Integer.valueOf(16)).intValue();
/*  58 */     int height = compound.getNumberTagValueOrDefault("height", Integer.valueOf(16)).intValue();
/*  59 */     return new ItemDialogBody(item, description, showDecorations, showTooltip, width, height);
/*     */   }
/*     */   
/*     */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, ItemDialogBody body) {
/*  63 */     compound.set("item", body.item, ItemStack::encode, wrapper);
/*  64 */     if (body.description != null) {
/*  65 */       compound.set("description", body.description, PlainMessage::encode, wrapper);
/*     */     }
/*  67 */     if (!body.showDecorations) {
/*  68 */       compound.setTag("show_decorations", (NBT)new NBTByte(false));
/*     */     }
/*  70 */     if (!body.showTooltip) {
/*  71 */       compound.setTag("show_tooltip", (NBT)new NBTByte(false));
/*     */     }
/*  73 */     if (body.width != 16) {
/*  74 */       compound.setTag("width", (NBT)new NBTInt(body.width));
/*     */     }
/*  76 */     if (body.height != 16) {
/*  77 */       compound.setTag("height", (NBT)new NBTInt(body.height));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public DialogBodyType<?> getType() {
/*  83 */     return DialogBodyTypes.ITEM;
/*     */   }
/*     */   
/*     */   public ItemStack getItem() {
/*  87 */     return this.item;
/*     */   }
/*     */   
/*     */   public PlainMessage getDescription() {
/*  91 */     return this.description;
/*     */   }
/*     */   
/*     */   public boolean isShowDecorations() {
/*  95 */     return this.showDecorations;
/*     */   }
/*     */   
/*     */   public boolean isShowTooltip() {
/*  99 */     return this.showTooltip;
/*     */   }
/*     */   
/*     */   public int getWidth() {
/* 103 */     return this.width;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/* 107 */     return this.height;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\body\ItemDialogBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */