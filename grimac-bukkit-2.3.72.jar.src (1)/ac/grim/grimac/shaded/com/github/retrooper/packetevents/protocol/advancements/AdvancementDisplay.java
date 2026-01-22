/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.advancements;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
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
/*     */ public final class AdvancementDisplay
/*     */ {
/*     */   public static final int FLAG_HAS_BACKGROUND = 1;
/*     */   public static final int FLAG_SHOW_TOAST = 2;
/*     */   public static final int FLAG_HIDDEN = 4;
/*     */   private Component title;
/*     */   private Component description;
/*     */   private ItemStack icon;
/*     */   private AdvancementType type;
/*     */   private boolean showToast;
/*     */   private boolean hidden;
/*     */   @Nullable
/*     */   private ResourceLocation background;
/*     */   private float x;
/*     */   private float y;
/*     */   
/*     */   public AdvancementDisplay(Component title, Component description, ItemStack icon, AdvancementType type, @Nullable ResourceLocation background, boolean showToast, boolean hidden, float x, float y) {
/*  45 */     this.title = title;
/*  46 */     this.description = description;
/*  47 */     this.icon = icon;
/*  48 */     this.type = type;
/*  49 */     this.showToast = showToast;
/*  50 */     this.hidden = hidden;
/*  51 */     this.background = background;
/*  52 */     this.x = x;
/*  53 */     this.y = y;
/*     */   }
/*     */   
/*     */   public static AdvancementDisplay read(PacketWrapper<?> wrapper) {
/*  57 */     Component title = wrapper.readComponent();
/*  58 */     Component description = wrapper.readComponent();
/*  59 */     ItemStack icon = wrapper.readItemStack();
/*  60 */     AdvancementType type = (AdvancementType)wrapper.readEnum(AdvancementType.class);
/*  61 */     int flags = wrapper.readInt();
/*  62 */     ResourceLocation background = ((flags & 0x1) != 0) ? ResourceLocation.read(wrapper) : null;
/*  63 */     boolean showToast = ((flags & 0x2) != 0);
/*  64 */     boolean hidden = ((flags & 0x4) != 0);
/*  65 */     float x = wrapper.readFloat();
/*  66 */     float y = wrapper.readFloat();
/*  67 */     return new AdvancementDisplay(title, description, icon, type, background, showToast, hidden, x, y);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, AdvancementDisplay display) {
/*  71 */     wrapper.writeComponent(display.title);
/*  72 */     wrapper.writeComponent(display.description);
/*  73 */     wrapper.writeItemStack(display.icon);
/*  74 */     wrapper.writeEnum(display.type);
/*  75 */     wrapper.writeInt(display.packFlags());
/*  76 */     if (display.background != null) {
/*  77 */       ResourceLocation.write(wrapper, display.background);
/*     */     }
/*  79 */     wrapper.writeFloat(display.x);
/*  80 */     wrapper.writeFloat(display.y);
/*     */   }
/*     */   
/*     */   public int packFlags() {
/*  84 */     int flags = 0;
/*  85 */     if (this.background != null) {
/*  86 */       flags |= 0x1;
/*     */     }
/*  88 */     if (this.showToast) {
/*  89 */       flags |= 0x2;
/*     */     }
/*  91 */     if (this.hidden) {
/*  92 */       flags |= 0x4;
/*     */     }
/*  94 */     return flags;
/*     */   }
/*     */   
/*     */   public Component getTitle() {
/*  98 */     return this.title;
/*     */   }
/*     */   
/*     */   public void setTitle(Component title) {
/* 102 */     this.title = title;
/*     */   }
/*     */   
/*     */   public Component getDescription() {
/* 106 */     return this.description;
/*     */   }
/*     */   
/*     */   public void setDescription(Component description) {
/* 110 */     this.description = description;
/*     */   }
/*     */   
/*     */   public ItemStack getIcon() {
/* 114 */     return this.icon;
/*     */   }
/*     */   
/*     */   public void setIcon(ItemStack icon) {
/* 118 */     this.icon = icon;
/*     */   }
/*     */   
/*     */   public AdvancementType getType() {
/* 122 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setType(AdvancementType type) {
/* 126 */     this.type = type;
/*     */   }
/*     */   
/*     */   public boolean isShowToast() {
/* 130 */     return this.showToast;
/*     */   }
/*     */   
/*     */   public void setShowToast(boolean showToast) {
/* 134 */     this.showToast = showToast;
/*     */   }
/*     */   
/*     */   public boolean isHidden() {
/* 138 */     return this.hidden;
/*     */   }
/*     */   
/*     */   public void setHidden(boolean hidden) {
/* 142 */     this.hidden = hidden;
/*     */   }
/*     */   @Nullable
/*     */   public ResourceLocation getBackground() {
/* 146 */     return this.background;
/*     */   }
/*     */   
/*     */   public void setBackground(@Nullable ResourceLocation background) {
/* 150 */     this.background = background;
/*     */   }
/*     */   
/*     */   public float getX() {
/* 154 */     return this.x;
/*     */   }
/*     */   
/*     */   public void setX(float x) {
/* 158 */     this.x = x;
/*     */   }
/*     */   
/*     */   public float getY() {
/* 162 */     return this.y;
/*     */   }
/*     */   
/*     */   public void setY(float y) {
/* 166 */     this.y = y;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\advancements\AdvancementDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */