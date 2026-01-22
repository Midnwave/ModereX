/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class NBTCompound
/*     */   extends NBT
/*     */ {
/*  37 */   protected final Map<String, NBT> tags = new LinkedHashMap<>();
/*     */ 
/*     */   
/*     */   public NBTType<NBTCompound> getType() {
/*  41 */     return NBTType.COMPOUND;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  45 */     return this.tags.isEmpty();
/*     */   }
/*     */   
/*     */   public Set<String> getTagNames() {
/*  49 */     return Collections.unmodifiableSet(this.tags.keySet());
/*     */   }
/*     */   
/*     */   public Map<String, NBT> getTags() {
/*  53 */     return Collections.unmodifiableMap(this.tags);
/*     */   }
/*     */   
/*     */   public int size() {
/*  57 */     return this.tags.size();
/*     */   }
/*     */   
/*     */   public NBT getTagOrThrow(String key) {
/*  61 */     NBT tag = getTagOrNull(key);
/*  62 */     if (tag == null) {
/*  63 */       throw new IllegalStateException(MessageFormat.format("NBT {0} does not exist", new Object[] { key }));
/*     */     }
/*  65 */     return tag;
/*     */   }
/*     */   @Nullable
/*     */   public NBT getTagOrNull(String key) {
/*  69 */     return this.tags.get(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends NBT> T getTagOfTypeOrThrow(String key, Class<T> type) {
/*  74 */     NBT tag = getTagOrThrow(key);
/*  75 */     if (type.isInstance(tag)) {
/*  76 */       return (T)tag;
/*     */     }
/*  78 */     throw new IllegalStateException(MessageFormat.format("NBT {0} has unexpected type, expected {1}, but got {2}", new Object[] { key, type, tag.getClass() }));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public <T extends NBT> T getTagOfTypeOrNull(String key, Class<T> type) {
/*  84 */     NBT tag = getTagOrNull(key);
/*  85 */     if (type.isInstance(tag)) {
/*  86 */       return (T)tag;
/*     */     }
/*  88 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends NBT> NBTList<T> getTagListOfTypeOrThrow(String key, Class<T> type) {
/*  93 */     NBTList<? extends NBT> list = getTagOfTypeOrThrow(key, (Class)NBTList.class);
/*  94 */     if (!type.isAssignableFrom(list.getTagsType().getNBTClass())) {
/*  95 */       throw new IllegalStateException(MessageFormat.format("NBTList {0} tags type has unexpected type, expected {1}, but got {2}", new Object[] { key, type, list.getTagsType().getNBTClass() }));
/*     */     }
/*  97 */     return (NBTList)list;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public <T extends NBT> NBTList<T> getTagListOfTypeOrNull(String key, Class<T> type) {
/* 102 */     NBTList<? extends NBT> list = getTagOfTypeOrNull(key, (Class)NBTList.class);
/* 103 */     if (list != null && type.isAssignableFrom(list.getTagsType().getNBTClass())) {
/* 104 */       return (NBTList)list;
/*     */     }
/* 106 */     return null;
/*     */   }
/*     */   
/*     */   public NBTCompound getCompoundTagOrThrow(String key) {
/* 110 */     return getTagOfTypeOrThrow(key, NBTCompound.class);
/*     */   }
/*     */   @Nullable
/*     */   public NBTCompound getCompoundTagOrNull(String key) {
/* 114 */     return getTagOfTypeOrNull(key, NBTCompound.class);
/*     */   }
/*     */   
/*     */   public Number getNumberTagValueOrThrow(String key) {
/* 118 */     return getNumberTagOrThrow(key).getAsNumber();
/*     */   }
/*     */   @Nullable
/*     */   public Number getNumberTagValueOrNull(String key) {
/* 122 */     return getNumberTagValueOrDefault(key, null);
/*     */   }
/*     */   @Contract("_, !null -> !null")
/*     */   @Nullable
/*     */   public Number getNumberTagValueOrDefault(String key, @Nullable Number number) {
/* 127 */     NBTNumber tag = getNumberTagOrNull(key);
/* 128 */     return (tag != null) ? tag.getAsNumber() : number;
/*     */   }
/*     */   
/*     */   public NBTNumber getNumberTagOrThrow(String key) {
/* 132 */     return getTagOfTypeOrThrow(key, NBTNumber.class);
/*     */   }
/*     */   @Nullable
/*     */   public NBTNumber getNumberTagOrNull(String key) {
/* 136 */     return getTagOfTypeOrNull(key, NBTNumber.class);
/*     */   }
/*     */   
/*     */   public NBTString getStringTagOrThrow(String key) {
/* 140 */     return getTagOfTypeOrThrow(key, NBTString.class);
/*     */   }
/*     */   @Nullable
/*     */   public NBTString getStringTagOrNull(String key) {
/* 144 */     return getTagOfTypeOrNull(key, NBTString.class);
/*     */   }
/*     */   
/*     */   public NBTList<NBTCompound> getCompoundListTagOrThrow(String key) {
/* 148 */     return getTagListOfTypeOrThrow(key, NBTCompound.class);
/*     */   }
/*     */   @Nullable
/*     */   public NBTList<NBTCompound> getCompoundListTagOrNull(String key) {
/* 152 */     return getTagListOfTypeOrNull(key, NBTCompound.class);
/*     */   }
/*     */   
/*     */   public NBTList<NBTNumber> getNumberTagListTagOrThrow(String key) {
/* 156 */     return getTagListOfTypeOrThrow(key, NBTNumber.class);
/*     */   }
/*     */   @Nullable
/*     */   public NBTList<NBTNumber> getNumberListTagOrNull(String key) {
/* 160 */     return getTagListOfTypeOrNull(key, NBTNumber.class);
/*     */   }
/*     */   
/*     */   public NBTList<NBTString> getStringListTagOrThrow(String key) {
/* 164 */     return getTagListOfTypeOrThrow(key, NBTString.class);
/*     */   }
/*     */   @Nullable
/*     */   public NBTList<NBTString> getStringListTagOrNull(String key) {
/* 168 */     return getTagListOfTypeOrNull(key, NBTString.class);
/*     */   }
/*     */   
/*     */   public String getStringTagValueOrThrow(String key) {
/* 172 */     return getStringTagOrThrow(key).getValue();
/*     */   }
/*     */   @Nullable
/*     */   public String getStringTagValueOrNull(String key) {
/* 176 */     NBT tag = getTagOrNull(key);
/* 177 */     if (tag instanceof NBTString) {
/* 178 */       return ((NBTString)tag).getValue();
/*     */     }
/* 180 */     return null;
/*     */   }
/*     */   
/*     */   public String getStringTagValueOrDefault(String key, String defaultValue) {
/* 184 */     NBT tag = getTagOrNull(key);
/* 185 */     if (tag instanceof NBTString) {
/* 186 */       return ((NBTString)tag).getValue();
/*     */     }
/* 188 */     return defaultValue;
/*     */   }
/*     */   
/*     */   public NBT removeTag(String key) {
/* 192 */     return this.tags.remove(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends NBT> T removeTagAndReturnIfType(String key, Class<T> type) {
/* 197 */     NBT tag = removeTag(key);
/* 198 */     if (type.isInstance(tag)) {
/* 199 */       return (T)tag;
/*     */     }
/* 201 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends NBT> NBTList<T> removeTagAndReturnIfListType(String key, Class<T> type) {
/* 206 */     NBTList<?> list = removeTagAndReturnIfType(key, NBTList.class);
/* 207 */     if (list != null && type.isAssignableFrom(list.getTagsType().getNBTClass())) {
/* 208 */       return (NBTList)list;
/*     */     }
/* 210 */     return null;
/*     */   }
/*     */   
/*     */   public void setTag(String key, NBT tag) {
/* 214 */     if (tag != null) {
/* 215 */       this.tags.put(key, tag);
/*     */     } else {
/* 217 */       this.tags.remove(key);
/*     */     } 
/*     */   }
/*     */   
/*     */   public NBTCompound copy() {
/* 222 */     NBTCompound clone = new NBTCompound();
/* 223 */     for (Map.Entry<String, NBT> entry : this.tags.entrySet()) {
/* 224 */       clone.setTag(entry.getKey(), ((NBT)entry.getValue()).copy());
/*     */     }
/* 226 */     return clone;
/*     */   }
/*     */   
/*     */   public boolean getBoolean(String string) {
/* 230 */     return getBooleanOr(string, false);
/*     */   }
/*     */   
/*     */   public boolean getBooleanOr(String string, boolean defaultValue) {
/* 234 */     NBTNumber nbtByte = getTagOfTypeOrNull(string, NBTNumber.class);
/* 235 */     return (nbtByte != null) ? ((nbtByte.getAsByte() != 0)) : defaultValue;
/*     */   }
/*     */   @Contract("_, _, !null, _ -> !null")
/*     */   @Nullable
/*     */   public <T> T getOr(String key, NbtDecoder<T> decoder, @Nullable T def, PacketWrapper<?> wrapper) {
/* 240 */     NBT tag = getTagOrNull(key);
/* 241 */     return (tag != null) ? (T)decoder.decode(tag, wrapper) : def;
/*     */   }
/*     */   @Nullable
/*     */   public <T> T getOrNull(String key, NbtDecoder<T> decoder, PacketWrapper<?> wrapper) {
/* 245 */     return getOr(key, decoder, null, wrapper);
/*     */   }
/*     */   
/*     */   public <T> T getOrThrow(String key, NbtDecoder<T> decoder, PacketWrapper<?> wrapper) {
/* 249 */     return (T)decoder.decode(getTagOrThrow(key), wrapper);
/*     */   }
/*     */   @Contract("_, _, !null, _ -> !null")
/*     */   @Nullable
/*     */   public <T> List<T> getListOr(String key, NbtDecoder<T> decoder, @Nullable List<T> def, PacketWrapper<?> wrapper) {
/* 254 */     NBT tag = getTagOrNull(key);
/* 255 */     if (tag instanceof NBTList) {
/*     */       
/* 257 */       List<? extends NBT> tags = ((NBTList<? extends NBT>)tag).getTags();
/* 258 */       List<T> list = new ArrayList<>(tags.size());
/* 259 */       for (NBT element : tags) {
/* 260 */         list.add((T)decoder.decode(element, wrapper));
/*     */       }
/* 262 */       return list;
/* 263 */     }  if (tag != null) {
/*     */       
/* 265 */       List<T> list = new ArrayList<>(1);
/* 266 */       list.add((T)decoder.decode(tag, wrapper));
/* 267 */       return list;
/*     */     } 
/*     */     
/* 270 */     return def;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public <T> List<T> getListOrNull(String key, NbtDecoder<T> decoder, PacketWrapper<?> wrapper) {
/* 275 */     return getListOr(key, decoder, null, wrapper);
/*     */   }
/*     */   
/*     */   public <T> List<T> getListOrEmpty(String key, NbtDecoder<T> decoder, PacketWrapper<?> wrapper) {
/* 279 */     return getListOr(key, decoder, Collections.emptyList(), wrapper);
/*     */   }
/*     */   
/*     */   public <T> List<T> getListOrThrow(String key, NbtDecoder<T> decoder, PacketWrapper<?> wrapper) {
/* 283 */     List<T> list = getListOrNull(key, decoder, wrapper);
/* 284 */     if (list == null) {
/* 285 */       throw new IllegalStateException(MessageFormat.format("NBT {0} does not exist", new Object[] { key }));
/*     */     }
/* 287 */     return list;
/*     */   }
/*     */   
/*     */   public <T> void set(String key, T value, NbtEncoder<T> encoder, PacketWrapper<?> wrapper) {
/* 291 */     setTag(key, encoder.encode(wrapper, value));
/*     */   }
/*     */   
/*     */   public <T> void setList(String key, List<T> value, NbtEncoder<T> encoder, PacketWrapper<?> wrapper) {
/* 295 */     if (value.isEmpty()) {
/* 296 */       setTag(key, new NBTList<>(NBTType.END, 0));
/*     */     } else {
/*     */       
/* 299 */       NBT firstVal = encoder.encode(wrapper, value.get(0));
/* 300 */       NBTList<?> list = new NBTList(firstVal.getType(), value.size());
/* 301 */       list.addTagUnsafe(firstVal);
/*     */       
/* 303 */       for (int i = 1; i < value.size(); i++) {
/* 304 */         list.addTagUnsafe(encoder.encode(wrapper, value.get(i)));
/*     */       }
/* 306 */       setTag(key, list);
/*     */     } 
/*     */   }
/*     */   
/*     */   public <T> void setCompactList(String key, List<T> value, NbtEncoder<T> encoder, PacketWrapper<?> wrapper) {
/* 311 */     if (value.size() == 1) {
/* 312 */       set(key, value.get(0), encoder, wrapper);
/*     */     } else {
/* 314 */       setList(key, value, encoder, wrapper);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 320 */     if (other instanceof NBTCompound) {
/* 321 */       if (isEmpty() && ((NBTCompound)other).isEmpty()) {
/* 322 */         return true;
/*     */       }
/* 324 */       return this.tags.equals(((NBTCompound)other).tags);
/*     */     } 
/* 326 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 331 */     return this.tags.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 336 */     return "Compound{" + this.tags + "}";
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTCompound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */