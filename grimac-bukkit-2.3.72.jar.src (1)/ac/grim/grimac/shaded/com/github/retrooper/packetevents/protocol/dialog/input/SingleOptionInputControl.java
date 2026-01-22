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
/*     */ import java.util.List;
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
/*     */ @NullMarked
/*     */ public class SingleOptionInputControl
/*     */   implements InputControl
/*     */ {
/*     */   private final int width;
/*     */   private final List<Entry> options;
/*     */   private final Component label;
/*     */   private final boolean labelVisible;
/*     */   
/*     */   public SingleOptionInputControl(int width, List<Entry> options, Component label, boolean labelVisible) {
/*  43 */     boolean initial = false;
/*  44 */     for (Entry entry : options) {
/*  45 */       if (entry.initial) {
/*  46 */         if (initial) {
/*  47 */           throw new IllegalArgumentException("Multiple initial values");
/*     */         }
/*  49 */         initial = true;
/*     */       } 
/*     */     } 
/*  52 */     this.width = width;
/*  53 */     this.options = options;
/*  54 */     this.label = label;
/*  55 */     this.labelVisible = labelVisible;
/*     */   }
/*     */   
/*     */   public static SingleOptionInputControl decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/*  59 */     int width = compound.getNumberTagValueOrDefault("width", Integer.valueOf(200)).intValue();
/*  60 */     List<Entry> options = compound.getListOrThrow("options", Entry::decode, wrapper);
/*  61 */     Component label = (Component)compound.getOrThrow("label", (NbtDecoder)AdventureSerializer.serializer(wrapper), wrapper);
/*  62 */     boolean labelVisible = compound.getBooleanOr("label_visible", true);
/*  63 */     return new SingleOptionInputControl(width, options, label, labelVisible);
/*     */   }
/*     */   
/*     */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, SingleOptionInputControl control) {
/*  67 */     if (control.width != 200) {
/*  68 */       compound.setTag("width", (NBT)new NBTInt(control.width));
/*     */     }
/*  70 */     compound.setList("options", control.options, Entry::encode, wrapper);
/*  71 */     compound.set("label", control.label, (NbtEncoder)AdventureSerializer.serializer(wrapper), wrapper);
/*  72 */     if (!control.labelVisible) {
/*  73 */       compound.setTag("label_visible", (NBT)new NBTByte(false));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public InputControlType<?> getType() {
/*  79 */     return InputControlTypes.SINGLE_OPTION;
/*     */   }
/*     */   
/*     */   public int getWidth() {
/*  83 */     return this.width;
/*     */   }
/*     */   
/*     */   public List<Entry> getOptions() {
/*  87 */     return this.options;
/*     */   }
/*     */   
/*     */   public Component getLabel() {
/*  91 */     return this.label;
/*     */   }
/*     */   
/*     */   public boolean isLabelVisible() {
/*  95 */     return this.labelVisible;
/*     */   }
/*     */   
/*     */   public static final class Entry
/*     */   {
/*     */     private final String id;
/*     */     private final Component display;
/*     */     private final boolean initial;
/*     */     
/*     */     public Entry(String id, Component display, boolean initial) {
/* 105 */       this.id = id;
/* 106 */       this.display = display;
/* 107 */       this.initial = initial;
/*     */     }
/*     */     
/*     */     public static Entry decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 111 */       if (nbt instanceof NBTString) {
/* 112 */         return new Entry(((NBTString)nbt).getValue(), null, false);
/*     */       }
/* 114 */       NBTCompound compound = (NBTCompound)nbt;
/* 115 */       String id = compound.getStringTagValueOrThrow("id");
/* 116 */       Component display = (Component)compound.getOrNull("display", (NbtDecoder)AdventureSerializer.serializer(wrapper), wrapper);
/* 117 */       boolean initial = compound.getBooleanOr("initial", false);
/* 118 */       return new Entry(id, display, initial);
/*     */     }
/*     */     
/*     */     public static NBT encode(PacketWrapper<?> wrapper, Entry entry) {
/* 122 */       NBTCompound compound = new NBTCompound();
/* 123 */       compound.setTag("id", (NBT)new NBTString(entry.id));
/* 124 */       if (entry.display != null) {
/* 125 */         compound.set("display", entry.display, (NbtEncoder)AdventureSerializer.serializer(wrapper), wrapper);
/*     */       }
/* 127 */       if (entry.initial) {
/* 128 */         compound.setTag("initial", (NBT)new NBTByte(true));
/*     */       }
/* 130 */       return (NBT)compound;
/*     */     }
/*     */     
/*     */     public String getId() {
/* 134 */       return this.id;
/*     */     }
/*     */     
/*     */     public Component getDisplay() {
/* 138 */       return this.display;
/*     */     }
/*     */     
/*     */     public boolean isInitial() {
/* 142 */       return this.initial;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\input\SingleOptionInputControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */