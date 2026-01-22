/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistryHolder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.BiFunction;
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
/*     */ @NullMarked
/*     */ public class MappedEntitySet<T extends MappedEntity>
/*     */   implements MappedEntityRefSet<T>
/*     */ {
/*     */   @Nullable
/*     */   private final ResourceLocation tagKey;
/*     */   @Nullable
/*     */   private final List<T> entities;
/*     */   
/*     */   public MappedEntitySet(ResourceLocation tagKey) {
/*  49 */     this(tagKey, null);
/*     */   }
/*     */   
/*     */   public MappedEntitySet(List<T> entities) {
/*  53 */     this(null, entities);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MappedEntitySet(@Nullable ResourceLocation tagKey, @Nullable List<T> entities) {
/*  60 */     if (tagKey == null && entities == null) {
/*  61 */       throw new IllegalArgumentException("Illegal generic holder set: either tag key or holder ids have to be set");
/*     */     }
/*  63 */     this.tagKey = tagKey;
/*  64 */     this.entities = entities;
/*     */   }
/*     */   
/*     */   public static <Z extends MappedEntity> MappedEntitySet<Z> createEmpty() {
/*  68 */     return new MappedEntitySet<>(new ArrayList<>(0));
/*     */   }
/*     */   
/*     */   public static <Z extends MappedEntity> MappedEntityRefSet<Z> readRefSet(PacketWrapper<?> wrapper) {
/*  72 */     int count = wrapper.readVarInt() - 1;
/*  73 */     if (count == -1) {
/*  74 */       return new MappedEntitySet<>(wrapper.readIdentifier());
/*     */     }
/*  76 */     int[] entries = wrapper.readVarIntArrayOfSize(Math.min(count, 65536));
/*  77 */     return new IdRefSetImpl<>(entries);
/*     */   }
/*     */   
/*     */   public static void writeRefSet(PacketWrapper<?> wrapper, MappedEntityRefSet<?> refSet) {
/*  81 */     if (refSet instanceof IdRefSetImpl) {
/*  82 */       IdRefSetImpl<?> idRefSet = (IdRefSetImpl)refSet;
/*  83 */       wrapper.writeVarInt(idRefSet.entries.length + 1);
/*  84 */       wrapper.writeVarIntArrayOfSize(idRefSet.entries);
/*  85 */     } else if (refSet instanceof MappedEntitySet) {
/*  86 */       write(wrapper, (MappedEntitySet)refSet);
/*     */     } else {
/*  88 */       throw new UnsupportedOperationException("Unsupported mapped entity reference set implementation: " + refSet);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static <Z extends MappedEntity> MappedEntitySet<Z> read(PacketWrapper<?> wrapper, BiFunction<ClientVersion, Integer, Z> getter) {
/*  94 */     int count = wrapper.readVarInt() - 1;
/*  95 */     if (count == -1) {
/*  96 */       return new MappedEntitySet<>(wrapper.readIdentifier(), null);
/*     */     }
/*  98 */     List<Z> entities = new ArrayList<>(Math.min(count, 65536));
/*  99 */     for (int i = 0; i < count; i++) {
/* 100 */       entities.add((Z)wrapper.readMappedEntity(getter));
/*     */     }
/* 102 */     return new MappedEntitySet<>(null, entities);
/*     */   }
/*     */   
/*     */   public static <Z extends MappedEntity> void write(PacketWrapper<?> wrapper, MappedEntitySet<Z> set) {
/* 106 */     if (set.tagKey != null) {
/* 107 */       wrapper.writeVarInt(0);
/* 108 */       wrapper.writeIdentifier(set.tagKey);
/*     */       
/*     */       return;
/*     */     } 
/* 112 */     assert set.entities != null;
/* 113 */     wrapper.writeVarInt(set.entities.size() + 1);
/* 114 */     for (MappedEntity mappedEntity : set.entities) {
/* 115 */       wrapper.writeMappedEntity(mappedEntity);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static <Z extends MappedEntity> MappedEntitySet<Z> decode(NBT nbt, ClientVersion version, IRegistry<Z> registry) {
/* 122 */     return decode(nbt, PacketWrapper.createDummyWrapper(version), registry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static <Z extends MappedEntity> MappedEntitySet<Z> decode(NBT nbt, PacketWrapper<?> wrapper, IRegistry<Z> registry) {
/*     */     List<Z> list;
/* 128 */     if (nbt instanceof NBTString) {
/* 129 */       String singleEntry = ((NBTString)nbt).getValue();
/*     */       
/* 131 */       if (!singleEntry.isEmpty() && singleEntry.charAt(0) == '#') {
/* 132 */         String tagName = singleEntry.substring(1);
/* 133 */         ResourceLocation tagKey = new ResourceLocation(tagName);
/* 134 */         return new MappedEntitySet<>(tagKey);
/*     */       } 
/*     */       
/* 137 */       list = new ArrayList<>(1);
/* 138 */       ResourceLocation key = new ResourceLocation(singleEntry);
/* 139 */       list.add((Z)registry.getByNameOrThrow(key));
/*     */     } else {
/*     */       
/* 142 */       NBTList<?> listTag = (NBTList)nbt;
/* 143 */       list = new ArrayList<>(listTag.size());
/* 144 */       for (NBT tag : listTag.getTags()) {
/* 145 */         ResourceLocation key = new ResourceLocation(((NBTString)tag).getValue());
/* 146 */         list.add((Z)registry.getByNameOrThrow(key));
/*     */       } 
/*     */     } 
/* 149 */     return new MappedEntitySet<>(list);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static <Z extends MappedEntity> NBT encode(MappedEntitySet<Z> set, ClientVersion version) {
/* 154 */     return encodeRefSet(PacketWrapper.createDummyWrapper(version), set);
/*     */   }
/*     */   
/*     */   public static <Z extends MappedEntity> NBT encode(PacketWrapper<?> wrapper, MappedEntitySet<Z> set) {
/* 158 */     if (set.tagKey != null) {
/* 159 */       return (NBT)new NBTString("#" + set.tagKey);
/*     */     }
/*     */     
/* 162 */     assert set.entities != null;
/* 163 */     NBTList<NBTString> listTag = NBTList.createStringList();
/* 164 */     for (MappedEntity mappedEntity : set.entities) {
/* 165 */       listTag.addTag((NBT)new NBTString(mappedEntity.getName().toString()));
/*     */     }
/* 167 */     return (NBT)listTag;
/*     */   }
/*     */   
/*     */   public static <Z extends MappedEntity> MappedEntityRefSet<Z> decodeRefSet(NBT nbt, PacketWrapper<?> wrapper) {
/* 171 */     return decodeRefSet(nbt, wrapper.getServerVersion().toClientVersion());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static <Z extends MappedEntity> MappedEntityRefSet<Z> decodeRefSet(NBT nbt, ClientVersion version) {
/*     */     List<String> list;
/* 177 */     if (nbt instanceof NBTString) {
/* 178 */       String singleEntry = ((NBTString)nbt).getValue();
/*     */       
/* 180 */       if (!singleEntry.isEmpty() && singleEntry.charAt(0) == '#') {
/* 181 */         String tagName = singleEntry.substring(1);
/* 182 */         ResourceLocation tagKey = new ResourceLocation(tagName);
/* 183 */         return new MappedEntitySet<>(tagKey);
/*     */       } 
/*     */       
/* 186 */       list = Collections.singletonList(singleEntry);
/*     */     } else {
/*     */       
/* 189 */       NBTList<?> listTag = (NBTList)nbt;
/* 190 */       list = new ArrayList<>(listTag.size());
/* 191 */       for (NBT tag : listTag.getTags()) {
/* 192 */         list.add(((NBTString)tag).getValue());
/*     */       }
/*     */     } 
/* 195 */     return new NameRefSetImpl<>(list);
/*     */   }
/*     */   
/*     */   public static <Z extends MappedEntity> NBT encodeRefSet(PacketWrapper<?> wrapper, MappedEntityRefSet<Z> refSet) {
/* 199 */     return encodeRefSet(refSet, wrapper.getServerVersion().toClientVersion());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static <Z extends MappedEntity> NBT encodeRefSet(MappedEntityRefSet<Z> refSet, ClientVersion version) {
/* 204 */     if (refSet instanceof NameRefSetImpl) {
/* 205 */       NameRefSetImpl<?> nameRefSet = (NameRefSetImpl)refSet;
/* 206 */       NBTList<NBTString> listTag = NBTList.createStringList();
/* 207 */       for (String entityName : nameRefSet.entries) {
/* 208 */         listTag.addTag((NBT)new NBTString(entityName));
/*     */       }
/* 210 */       return (NBT)listTag;
/* 211 */     }  if (refSet instanceof MappedEntitySet) {
/* 212 */       return encode((MappedEntitySet<MappedEntity>)refSet, version);
/*     */     }
/* 214 */     throw new UnsupportedOperationException("Unsupported mapped entity reference set implementation: " + refSet);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MappedEntitySet<T> resolve(PacketWrapper<?> wrapper, IRegistry<T> registry) {
/* 220 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public MappedEntitySet<T> resolve(ClientVersion version, IRegistryHolder registryHolder, IRegistry<T> registry) {
/* 225 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public MappedEntitySet<T> resolve(ClientVersion version, IRegistry<T> registry) {
/* 230 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 234 */     return (this.entities != null && this.entities.isEmpty());
/*     */   }
/*     */   @Nullable
/*     */   public ResourceLocation getTagKey() {
/* 238 */     return this.tagKey;
/*     */   }
/*     */   @Nullable
/*     */   public List<T> getEntities() {
/* 242 */     return this.entities;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 247 */     if (this == obj) return true; 
/* 248 */     if (!(obj instanceof MappedEntitySet)) return false; 
/* 249 */     MappedEntitySet<?> that = (MappedEntitySet)obj;
/* 250 */     if (!Objects.equals(this.tagKey, that.tagKey)) return false; 
/* 251 */     return Objects.equals(this.entities, that.entities);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 256 */     return Objects.hash(new Object[] { this.tagKey, this.entities });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 261 */     return "MappedEntitySet{tagKey=" + this.tagKey + ", entities=" + this.entities + '}';
/*     */   }
/*     */   
/*     */   private static final class IdRefSetImpl<T extends MappedEntity>
/*     */     implements MappedEntityRefSet<T> {
/*     */     private final int[] entries;
/*     */     
/*     */     public IdRefSetImpl(int[] entries) {
/* 269 */       this.entries = entries;
/*     */     }
/*     */ 
/*     */     
/*     */     public MappedEntitySet<T> resolve(ClientVersion version, IRegistry<T> registry) {
/* 274 */       List<T> entities = new ArrayList<>(this.entries.length);
/* 275 */       for (int entityId : this.entries) {
/* 276 */         entities.add((T)registry.getByIdOrThrow(version, entityId));
/*     */       }
/* 278 */       return new MappedEntitySet<>(entities);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 283 */       return (this.entries.length == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 288 */       if (!(obj instanceof IdRefSetImpl)) return false; 
/* 289 */       IdRefSetImpl<?> idRefSet = (IdRefSetImpl)obj;
/* 290 */       return Arrays.equals(this.entries, idRefSet.entries);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 295 */       return Arrays.hashCode(this.entries);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 300 */       return "IdRefSetImpl{entries=" + Arrays.toString(this.entries) + '}';
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class NameRefSetImpl<T extends MappedEntity>
/*     */     implements MappedEntityRefSet<T> {
/*     */     private final List<String> entries;
/*     */     
/*     */     public NameRefSetImpl(List<String> entries) {
/* 309 */       this.entries = entries;
/*     */     }
/*     */ 
/*     */     
/*     */     public MappedEntitySet<T> resolve(ClientVersion version, IRegistry<T> registry) {
/* 314 */       List<T> entities = new ArrayList<>(this.entries.size());
/* 315 */       for (String entityName : this.entries) {
/* 316 */         entities.add((T)registry.getByNameOrThrow(entityName));
/*     */       }
/* 318 */       return new MappedEntitySet<>(entities);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 323 */       return this.entries.isEmpty();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 328 */       if (!(obj instanceof NameRefSetImpl)) return false; 
/* 329 */       NameRefSetImpl<?> that = (NameRefSetImpl)obj;
/* 330 */       return this.entries.equals(that.entries);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 335 */       return Objects.hashCode(this.entries);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 340 */       return "NameRefSetImpl{entries=" + this.entries + '}';
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\mapper\MappedEntitySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */