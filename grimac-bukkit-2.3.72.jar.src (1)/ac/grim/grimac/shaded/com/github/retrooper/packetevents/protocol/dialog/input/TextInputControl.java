/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.input;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTByte;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
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
/*     */ @NullMarked
/*     */ public class TextInputControl
/*     */   implements InputControl
/*     */ {
/*     */   private final int width;
/*     */   private final Component label;
/*     */   private final boolean labelVisible;
/*     */   private final String initial;
/*     */   private final int maxLength;
/*     */   private final MultilineOptions multiline;
/*     */   
/*     */   public TextInputControl(int width, Component label, boolean labelVisible, String initial, int maxLength, MultilineOptions multiline) {
/*  43 */     if (initial.length() > maxLength) {
/*  44 */       throw new IllegalArgumentException("Default text length exceeds allowed size");
/*     */     }
/*  46 */     this.width = width;
/*  47 */     this.label = label;
/*  48 */     this.labelVisible = labelVisible;
/*  49 */     this.initial = initial;
/*  50 */     this.maxLength = maxLength;
/*  51 */     this.multiline = multiline;
/*     */   }
/*     */   
/*     */   public static TextInputControl decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/*  55 */     int width = compound.getNumberTagValueOrDefault("width", Integer.valueOf(200)).intValue();
/*  56 */     Component label = (Component)compound.getOrThrow("label", (NbtDecoder)AdventureSerializer.serializer(wrapper), wrapper);
/*  57 */     boolean labelVisible = compound.getBooleanOr("label_visible", true);
/*  58 */     String initial = compound.getStringTagValueOrDefault("initial", "");
/*  59 */     int maxLength = compound.getNumberTagValueOrDefault("max_length", Integer.valueOf(32)).intValue();
/*  60 */     MultilineOptions multiline = (MultilineOptions)compound.getOrNull("multiline", MultilineOptions::decode, wrapper);
/*  61 */     return new TextInputControl(width, label, labelVisible, initial, maxLength, multiline);
/*     */   }
/*     */   
/*     */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, TextInputControl control) {
/*  65 */     if (control.width != 200) {
/*  66 */       compound.setTag("width", (NBT)new NBTInt(control.width));
/*     */     }
/*  68 */     compound.set("label", control.label, (NbtEncoder)AdventureSerializer.serializer(wrapper), wrapper);
/*  69 */     if (!control.labelVisible) {
/*  70 */       compound.setTag("label_visible", (NBT)new NBTByte(false));
/*     */     }
/*  72 */     if (!control.initial.isEmpty()) {
/*  73 */       compound.setTag("initial", (NBT)new NBTString(control.initial));
/*     */     }
/*  75 */     if (control.maxLength != 32) {
/*  76 */       compound.setTag("max_length", (NBT)new NBTInt(control.maxLength));
/*     */     }
/*  78 */     if (control.multiline != null) {
/*  79 */       compound.set("multiline", control.multiline, MultilineOptions::encode, wrapper);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public InputControlType<?> getType() {
/*  85 */     return InputControlTypes.TEXT;
/*     */   }
/*     */   
/*     */   public int getWidth() {
/*  89 */     return this.width;
/*     */   }
/*     */   
/*     */   public Component getLabel() {
/*  93 */     return this.label;
/*     */   }
/*     */   
/*     */   public boolean isLabelVisible() {
/*  97 */     return this.labelVisible;
/*     */   }
/*     */   
/*     */   public String getInitial() {
/* 101 */     return this.initial;
/*     */   }
/*     */   
/*     */   public int getMaxLength() {
/* 105 */     return this.maxLength;
/*     */   }
/*     */   
/*     */   public MultilineOptions getMultiline() {
/* 109 */     return this.multiline;
/*     */   }
/*     */   
/*     */   public static final class MultilineOptions
/*     */   {
/*     */     private final Integer maxLines;
/*     */     private final Integer height;
/*     */     
/*     */     public MultilineOptions(Integer maxLines, Integer height) {
/* 118 */       this.maxLines = maxLines;
/* 119 */       this.height = height;
/*     */     }
/*     */     
/*     */     public static MultilineOptions decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 123 */       NBTCompound compound = (NBTCompound)nbt;
/* 124 */       Number maxLines = compound.getNumberTagValueOrNull("max_lines");
/* 125 */       Number height = compound.getNumberTagValueOrNull("height");
/* 126 */       return new MultilineOptions(
/* 127 */           (maxLines != null) ? Integer.valueOf(maxLines.intValue()) : null, 
/* 128 */           (height != null) ? Integer.valueOf(height.intValue()) : null);
/*     */     }
/*     */ 
/*     */     
/*     */     public static NBT encode(PacketWrapper<?> wrapper, MultilineOptions options) {
/* 133 */       NBTCompound compound = new NBTCompound();
/* 134 */       if (options.maxLines != null) {
/* 135 */         compound.setTag("max_lines", (NBT)new NBTInt(options.maxLines.intValue()));
/*     */       }
/* 137 */       if (options.height != null) {
/* 138 */         compound.setTag("height", (NBT)new NBTInt(options.height.intValue()));
/*     */       }
/* 140 */       return (NBT)compound;
/*     */     }
/*     */     
/*     */     public Integer getMaxLines() {
/* 144 */       return this.maxLines;
/*     */     }
/*     */     
/*     */     public Integer getHeight() {
/* 148 */       return this.height;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\input\TextInputControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */