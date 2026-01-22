/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTDouble;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class ItemMapDecorations
/*     */ {
/*     */   private Map<String, Decoration> decorations;
/*     */   
/*     */   public ItemMapDecorations(Map<String, Decoration> decorations) {
/*  41 */     this.decorations = decorations;
/*     */   }
/*     */   
/*     */   public static ItemMapDecorations read(PacketWrapper<?> wrapper) {
/*  45 */     NBTCompound compound = wrapper.readNBT();
/*  46 */     Map<String, Decoration> decorations = new HashMap<>(compound.size());
/*  47 */     for (Map.Entry<String, NBT> tag : (Iterable<Map.Entry<String, NBT>>)compound.getTags().entrySet()) {
/*  48 */       Decoration decoration = Decoration.readCompound((NBTCompound)tag.getValue());
/*  49 */       decorations.put(tag.getKey(), decoration);
/*     */     } 
/*  51 */     return new ItemMapDecorations(decorations);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemMapDecorations decorations) {
/*  55 */     NBTCompound compound = new NBTCompound();
/*  56 */     for (Map.Entry<String, Decoration> decoration : decorations.decorations.entrySet()) {
/*  57 */       NBTCompound entry = new NBTCompound();
/*  58 */       Decoration.writeCompound(entry, decoration.getValue());
/*  59 */       compound.setTag(decoration.getKey(), (NBT)entry);
/*     */     } 
/*  61 */     wrapper.writeNBT(compound);
/*     */   }
/*     */   @Nullable
/*     */   public Decoration getDecoration(String key) {
/*  65 */     return this.decorations.get(key);
/*     */   }
/*     */   
/*     */   public void setDecoration(String key, @Nullable Decoration decoration) {
/*  69 */     if (decoration != null) {
/*  70 */       this.decorations.put(key, decoration);
/*     */     } else {
/*  72 */       this.decorations.remove(key);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Map<String, Decoration> getDecorations() {
/*  77 */     return this.decorations;
/*     */   }
/*     */   
/*     */   public void setDecorations(Map<String, Decoration> decorations) {
/*  81 */     this.decorations = decorations;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  86 */     if (this == obj) return true; 
/*  87 */     if (!(obj instanceof ItemMapDecorations)) return false; 
/*  88 */     ItemMapDecorations that = (ItemMapDecorations)obj;
/*  89 */     return this.decorations.equals(that.decorations);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  94 */     return Objects.hashCode(this.decorations);
/*     */   }
/*     */   
/*     */   public static final class Decoration
/*     */   {
/*     */     private MapDecorationType type;
/*     */     private double x;
/*     */     private double z;
/*     */     private float rotation;
/*     */     
/*     */     public Decoration(MapDecorationType type, double x, double z, float rotation) {
/* 105 */       this.type = type;
/* 106 */       this.x = x;
/* 107 */       this.z = z;
/* 108 */       this.rotation = rotation;
/*     */     }
/*     */     
/*     */     @Internal
/*     */     public static Decoration readCompound(NBTCompound compound) {
/* 113 */       MapDecorationType type = MapDecorationTypes.getByName(compound.getStringTagValueOrThrow("type"));
/* 114 */       double x = compound.getNumberTagOrThrow("x").getAsDouble();
/* 115 */       double z = compound.getNumberTagOrThrow("z").getAsDouble();
/* 116 */       float rotation = compound.getNumberTagOrThrow("rotation").getAsFloat();
/* 117 */       return new Decoration(type, x, z, rotation);
/*     */     }
/*     */     
/*     */     @Internal
/*     */     public static void writeCompound(NBTCompound compound, Decoration decoration) {
/* 122 */       compound.setTag("type", (NBT)new NBTString(decoration.type.getName().toString()));
/* 123 */       compound.setTag("x", (NBT)new NBTDouble(decoration.x));
/* 124 */       compound.setTag("z", (NBT)new NBTDouble(decoration.z));
/* 125 */       compound.setTag("rotation", (NBT)new NBTFloat(decoration.rotation));
/*     */     }
/*     */     
/*     */     public MapDecorationType getType() {
/* 129 */       return this.type;
/*     */     }
/*     */     
/*     */     public void setType(MapDecorationType type) {
/* 133 */       this.type = type;
/*     */     }
/*     */     
/*     */     public double getX() {
/* 137 */       return this.x;
/*     */     }
/*     */     
/*     */     public void setX(double x) {
/* 141 */       this.x = x;
/*     */     }
/*     */     
/*     */     public double getZ() {
/* 145 */       return this.z;
/*     */     }
/*     */     
/*     */     public void setZ(double z) {
/* 149 */       this.z = z;
/*     */     }
/*     */     
/*     */     public float getRotation() {
/* 153 */       return this.rotation;
/*     */     }
/*     */     
/*     */     public void setRotation(float rotation) {
/* 157 */       this.rotation = rotation;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 162 */       if (this == obj) return true; 
/* 163 */       if (!(obj instanceof Decoration)) return false; 
/* 164 */       Decoration that = (Decoration)obj;
/* 165 */       if (Double.compare(that.x, this.x) != 0) return false; 
/* 166 */       if (Double.compare(that.z, this.z) != 0) return false; 
/* 167 */       if (Float.compare(that.rotation, this.rotation) != 0) return false; 
/* 168 */       return this.type.equals(that.type);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 173 */       return Objects.hash(new Object[] { this.type, Double.valueOf(this.x), Double.valueOf(this.z), Float.valueOf(this.rotation) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemMapDecorations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */