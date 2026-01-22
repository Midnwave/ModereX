/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class RandomWeightedList<T>
/*     */   implements Iterable<RandomWeightedList.Entry<T>>
/*     */ {
/*     */   private List<Entry<T>> entries;
/*     */   
/*     */   public RandomWeightedList() {
/*  40 */     this(new ArrayList<>());
/*     */   }
/*     */   public RandomWeightedList(List<Entry<T>> entries) {
/*  43 */     this.entries = entries;
/*     */   }
/*     */   
/*     */   public RandomWeightedList(T entry, int weight) {
/*  47 */     this(new Entry<>(entry, weight));
/*     */   }
/*     */   
/*     */   public RandomWeightedList(Entry<T> entry) {
/*  51 */     this.entries = new ArrayList<>(1);
/*  52 */     this.entries.add(entry);
/*     */   }
/*     */   
/*     */   public static <T> RandomWeightedList<T> decode(NBT nbt, ClientVersion version, ComponentType.Decoder<T> decoder) {
/*     */     List<Entry<T>> entries;
/*  57 */     if (nbt instanceof NBTCompound) {
/*  58 */       entries = new ArrayList<>(1);
/*  59 */       entries.add(Entry.decode(nbt, version, decoder));
/*  60 */     } else if (nbt instanceof NBTList) {
/*  61 */       NBTList<?> list = (NBTList)nbt;
/*  62 */       entries = new ArrayList<>(list.size());
/*  63 */       for (NBT tag : list.getTags()) {
/*  64 */         entries.add(Entry.decode(tag, version, decoder));
/*     */       }
/*     */     } else {
/*  67 */       throw new UnsupportedOperationException("Can't decode " + nbt + " as random weighted list");
/*     */     } 
/*  69 */     return new RandomWeightedList<>(entries);
/*     */   }
/*     */   
/*     */   public static <T> NBT encode(RandomWeightedList<T> list, ClientVersion version, ComponentType.Encoder<T> encoder) {
/*  73 */     NBTList<NBTCompound> nbt = new NBTList(NBTType.COMPOUND, list.entries.size());
/*  74 */     for (Entry<T> entry : list.entries) {
/*  75 */       nbt.addTag((NBT)Entry.encode(entry, version, encoder));
/*     */     }
/*  77 */     return (NBT)nbt;
/*     */   }
/*     */   
/*     */   public List<Entry<T>> getEntries() {
/*  81 */     return this.entries;
/*     */   }
/*     */   
/*     */   public void setEntries(List<Entry<T>> entries) {
/*  85 */     this.entries = entries;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Iterator<Entry<T>> iterator() {
/*  90 */     return this.entries.iterator();
/*     */   }
/*     */   
/*     */   public static final class Entry<T>
/*     */   {
/*     */     private final T data;
/*     */     private final int weight;
/*     */     
/*     */     public Entry(T data, int weight) {
/*  99 */       this.data = data;
/* 100 */       this.weight = weight;
/*     */     }
/*     */     
/*     */     public static <T> Entry<T> decode(NBT nbt, ClientVersion version, ComponentType.Decoder<T> decoder) {
/* 104 */       NBTCompound compound = (NBTCompound)nbt;
/* 105 */       int weight = compound.getNumberTagOrThrow("weight").getAsInt();
/* 106 */       T data = (T)decoder.decode(compound.getTagOrThrow("data"), version);
/* 107 */       return new Entry<>(data, weight);
/*     */     }
/*     */     
/*     */     public static <T> NBTCompound encode(Entry<T> entry, ClientVersion version, ComponentType.Encoder<T> encoder) {
/* 111 */       NBTCompound compound = new NBTCompound();
/* 112 */       compound.setTag("weight", (NBT)new NBTInt(entry.weight));
/* 113 */       compound.setTag("data", encoder.encode(entry.data, version));
/* 114 */       return compound;
/*     */     }
/*     */     
/*     */     public T getData() {
/* 118 */       return this.data;
/*     */     }
/*     */     
/*     */     public int getWeight() {
/* 122 */       return this.weight;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\RandomWeightedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */