/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.color;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.MathUtil;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.RGBLike;
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
/*     */ public class Color
/*     */   implements RGBLike
/*     */ {
/*     */   protected static final int BIT_MASK = 255;
/*     */   protected final int red;
/*     */   protected final int green;
/*     */   protected final int blue;
/*     */   
/*     */   public Color(int red, int green, int blue) {
/*  41 */     this.red = red;
/*  42 */     this.green = green;
/*  43 */     this.blue = blue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Color(float red, float green, float blue) {
/*  51 */     this(MathUtil.floor(red * 255.0F), 
/*  52 */         MathUtil.floor(green * 255.0F), 
/*  53 */         MathUtil.floor(blue * 255.0F));
/*     */   }
/*     */   
/*     */   public Color(int rgb) {
/*  57 */     this(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF);
/*     */   }
/*     */   
/*     */   public static Color read(PacketWrapper<?> wrapper) {
/*  61 */     return new Color(wrapper.readInt());
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, Color color) {
/*  65 */     wrapper.writeInt(color.asRGB());
/*     */   }
/*     */   
/*     */   public static Color readShort(PacketWrapper<?> wrapper) {
/*  69 */     return new Color(wrapper.readUnsignedByte(), wrapper.readUnsignedByte(), wrapper.readUnsignedByte());
/*     */   }
/*     */   
/*     */   public static void writeShort(PacketWrapper<?> wrapper, Color color) {
/*  73 */     wrapper.writeByte(color.red);
/*  74 */     wrapper.writeByte(color.green);
/*  75 */     wrapper.writeByte(color.blue);
/*     */   }
/*     */   
/*     */   public static Color decode(NBT nbt, ClientVersion version) {
/*  79 */     if (nbt instanceof NBTNumber) {
/*  80 */       return new Color(((NBTNumber)nbt).getAsInt());
/*     */     }
/*  82 */     NBTList<?> list = (NBTList)nbt;
/*  83 */     float red = ((NBTNumber)list.getTag(0)).getAsFloat();
/*  84 */     float green = ((NBTNumber)list.getTag(1)).getAsFloat();
/*  85 */     float blue = ((NBTNumber)list.getTag(2)).getAsFloat();
/*  86 */     return new Color(red, green, blue);
/*     */   }
/*     */   
/*     */   public static NBT encode(Color color, ClientVersion version) {
/*  90 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_21_2)) {
/*  91 */       return (NBT)new NBTInt(color.asRGB());
/*     */     }
/*  93 */     NBTList<NBTFloat> list = new NBTList(NBTType.FLOAT, 3);
/*  94 */     list.addTag((NBT)new NBTFloat(color.red));
/*  95 */     list.addTag((NBT)new NBTFloat(color.green));
/*  96 */     list.addTag((NBT)new NBTFloat(color.blue));
/*  97 */     return (NBT)list;
/*     */   }
/*     */   @NotNull
/*     */   public Color withRed(int red) {
/* 101 */     return new Color(red, this.green, this.blue);
/*     */   }
/*     */   @NotNull
/*     */   public Color withGreen(int green) {
/* 105 */     return new Color(this.red, green, this.blue);
/*     */   }
/*     */   @NotNull
/*     */   public Color withBlue(int blue) {
/* 109 */     return new Color(this.red, this.green, blue);
/*     */   }
/*     */   
/*     */   public int asRGB() {
/* 113 */     return this.red << 16 | this.green << 8 | this.blue;
/*     */   }
/*     */ 
/*     */   
/*     */   public int red() {
/* 118 */     return this.red;
/*     */   }
/*     */ 
/*     */   
/*     */   public int green() {
/* 123 */     return this.green;
/*     */   }
/*     */ 
/*     */   
/*     */   public int blue() {
/* 128 */     return this.blue;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\color\Color.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */