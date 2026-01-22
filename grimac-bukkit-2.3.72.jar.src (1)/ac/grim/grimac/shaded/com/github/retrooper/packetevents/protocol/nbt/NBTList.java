/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt;
/*     */ 
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public class NBTList<T extends NBT>
/*     */   extends NBT
/*     */ {
/*     */   protected final NBTType<T> type;
/*     */   protected final List<T> tags;
/*     */   
/*     */   public NBTList(NBTType<T> type) {
/*  33 */     this.type = type;
/*  34 */     this.tags = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public NBTList(NBTType<T> type, int size) {
/*  38 */     this.type = type;
/*  39 */     this.tags = new ArrayList<>(size);
/*     */   }
/*     */   
/*     */   public NBTList(NBTType<T> type, List<T> tags) {
/*  43 */     this.type = type;
/*  44 */     this.tags = new ArrayList<>();
/*  45 */     this.tags.addAll(tags);
/*     */   }
/*     */   
/*     */   public static NBTList<NBTCompound> createCompoundList() {
/*  49 */     return new NBTList<>(NBTType.COMPOUND);
/*     */   }
/*     */   
/*     */   public static NBTList<NBTString> createStringList() {
/*  53 */     return new NBTList<>(NBTType.STRING);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public NBTType<NBTList> getType() {
/*  59 */     return NBTType.LIST;
/*     */   }
/*     */   
/*     */   public NBTType<T> getTagsType() {
/*  63 */     return this.type;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  67 */     return this.tags.isEmpty();
/*     */   }
/*     */   
/*     */   public int size() {
/*  71 */     return this.tags.size();
/*     */   }
/*     */   
/*     */   public List<T> getTags() {
/*  75 */     return Collections.unmodifiableList(this.tags);
/*     */   }
/*     */   
/*     */   public T getTag(int index) {
/*  79 */     return this.tags.get(index);
/*     */   }
/*     */   
/*     */   public void setTag(int index, T tag) {
/*  83 */     validateAddTag(tag);
/*  84 */     this.tags.set(index, tag);
/*     */   }
/*     */   
/*     */   public void addTag(int index, T tag) {
/*  88 */     validateAddTag(tag);
/*  89 */     this.tags.add(index, tag);
/*     */   }
/*     */   
/*     */   public void addTag(T tag) {
/*  93 */     validateAddTag(tag);
/*  94 */     this.tags.add(tag);
/*     */   }
/*     */   
/*     */   public void addTagUnsafe(int index, NBT nbt) {
/*  98 */     addTag(index, (T)nbt);
/*     */   }
/*     */   
/*     */   public void addTagUnsafe(NBT nbt) {
/* 102 */     addTag((T)nbt);
/*     */   }
/*     */   
/*     */   public void removeTag(int index) {
/* 106 */     this.tags.remove(index);
/*     */   }
/*     */   
/*     */   protected void validateAddTag(T tag) {
/* 110 */     if (this.type != tag.getType()) {
/* 111 */       throw new IllegalArgumentException(MessageFormat.format("Invalid tag type. Expected {0}, got {1}.", new Object[] { this.type.getNBTClass(), tag.getClass() }));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 118 */     if (this == obj) {
/* 119 */       return true;
/*     */     }
/* 121 */     if (obj == null) {
/* 122 */       return false;
/*     */     }
/* 124 */     if (getClass() != obj.getClass()) {
/* 125 */       return false;
/*     */     }
/* 127 */     NBTList<T> other = (NBTList<T>)obj;
/* 128 */     return (Objects.equals(this.type, other.type) && Objects.equals(this.tags, other.tags));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 133 */     return Objects.hash(new Object[] { this.type, this.tags });
/*     */   }
/*     */ 
/*     */   
/*     */   public NBTList<T> copy() {
/* 138 */     List<T> newTags = new ArrayList<>();
/* 139 */     for (NBT nBT : this.tags) {
/* 140 */       newTags.add((T)nBT.copy());
/*     */     }
/* 142 */     return new NBTList(this.type, newTags);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 147 */     return "List(" + this.tags + ")";
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */