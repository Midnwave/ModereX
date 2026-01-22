/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.input;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
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
/*     */ @NullMarked
/*     */ public class NumberRangeInputControl
/*     */   implements InputControl
/*     */ {
/*     */   private final int width;
/*     */   private final Component label;
/*     */   private final String labelFormat;
/*     */   private final RangeInfo rangeInfo;
/*     */   
/*     */   public NumberRangeInputControl(int width, Component label, String labelFormat, RangeInfo rangeInfo) {
/*  41 */     this.width = width;
/*  42 */     this.label = label;
/*  43 */     this.labelFormat = labelFormat;
/*  44 */     this.rangeInfo = rangeInfo;
/*     */   }
/*     */   
/*     */   public static NumberRangeInputControl decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/*  48 */     int width = compound.getNumberTagValueOrDefault("width", Integer.valueOf(200)).intValue();
/*  49 */     Component label = (Component)compound.getOrThrow("label", (NbtDecoder)AdventureSerializer.serializer(wrapper), wrapper);
/*  50 */     String labelFormat = compound.getStringTagValueOrDefault("label_format", "options.generic_value");
/*  51 */     RangeInfo rangeInfo = RangeInfo.decode(compound, wrapper);
/*  52 */     return new NumberRangeInputControl(width, label, labelFormat, rangeInfo);
/*     */   }
/*     */   
/*     */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, NumberRangeInputControl control) {
/*  56 */     if (control.width != 200) {
/*  57 */       compound.setTag("width", (NBT)new NBTInt(control.width));
/*     */     }
/*  59 */     compound.set("label", control.label, (NbtEncoder)AdventureSerializer.serializer(wrapper), wrapper);
/*  60 */     if (!"options.generic_value".equals(control.labelFormat)) {
/*  61 */       compound.setTag("label_format", (NBT)new NBTString(control.labelFormat));
/*     */     }
/*  63 */     RangeInfo.encode(compound, wrapper, control.rangeInfo);
/*     */   }
/*     */ 
/*     */   
/*     */   public InputControlType<?> getType() {
/*  68 */     return InputControlTypes.NUMBER_RANGE;
/*     */   }
/*     */   
/*     */   public int getWidth() {
/*  72 */     return this.width;
/*     */   }
/*     */   
/*     */   public Component getLabel() {
/*  76 */     return this.label;
/*     */   }
/*     */   
/*     */   public String getLabelFormat() {
/*  80 */     return this.labelFormat;
/*     */   }
/*     */   
/*     */   public RangeInfo getRangeInfo() {
/*  84 */     return this.rangeInfo;
/*     */   }
/*     */   
/*     */   public static final class RangeInfo
/*     */   {
/*     */     private final float start;
/*     */     private final float end;
/*     */     private final Float initial;
/*     */     private final Float step;
/*     */     
/*     */     public RangeInfo(float start, float end, Float initial, Float step) {
/*  95 */       if (initial != null) {
/*  96 */         float min = Math.min(start, end);
/*  97 */         float max = Math.max(start, end);
/*  98 */         if (initial.floatValue() < min || initial.floatValue() > max) {
/*  99 */           throw new IllegalArgumentException("Initial value " + initial + " is outside of range [" + min + ", " + max + "]");
/*     */         }
/*     */       } 
/*     */       
/* 103 */       this.start = start;
/* 104 */       this.end = end;
/* 105 */       this.initial = initial;
/* 106 */       this.step = step;
/*     */     }
/*     */     
/*     */     public static RangeInfo decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 110 */       float start = compound.getNumberTagValueOrThrow("start").floatValue();
/* 111 */       float end = compound.getNumberTagValueOrThrow("end").floatValue();
/* 112 */       NBTNumber initialTag = compound.getNumberTagOrNull("initial");
/* 113 */       Float initial = (initialTag != null) ? Float.valueOf(initialTag.getAsFloat()) : null;
/* 114 */       NBTNumber stepTag = compound.getNumberTagOrNull("step");
/* 115 */       Float step = (stepTag != null) ? Float.valueOf(stepTag.getAsFloat()) : null;
/* 116 */       return new RangeInfo(start, end, initial, step);
/*     */     }
/*     */     
/*     */     public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, RangeInfo rangeInfo) {
/* 120 */       compound.setTag("start", (NBT)new NBTFloat(rangeInfo.start));
/* 121 */       compound.setTag("end", (NBT)new NBTFloat(rangeInfo.end));
/* 122 */       if (rangeInfo.initial != null) {
/* 123 */         compound.setTag("initial", (NBT)new NBTFloat(rangeInfo.initial.floatValue()));
/*     */       }
/* 125 */       if (rangeInfo.step != null) {
/* 126 */         compound.setTag("step", (NBT)new NBTFloat(rangeInfo.step.floatValue()));
/*     */       }
/*     */     }
/*     */     
/*     */     public float getStart() {
/* 131 */       return this.start;
/*     */     }
/*     */     
/*     */     public float getEnd() {
/* 135 */       return this.end;
/*     */     }
/*     */     
/*     */     public Float getInitial() {
/* 139 */       return this.initial;
/*     */     }
/*     */     
/*     */     public Float getStep() {
/* 143 */       return this.step;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\input\NumberRangeInputControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */