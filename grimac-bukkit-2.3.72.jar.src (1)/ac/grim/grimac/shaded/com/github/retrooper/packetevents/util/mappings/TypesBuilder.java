/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer.SequentialNBTReader;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.VersionMapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.VisibleForTesting;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.StreamSupport;
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
/*     */ @Internal
/*     */ public class TypesBuilder
/*     */ {
/*     */   private final String mapPath;
/*  45 */   private Map<ClientVersion, Map<String, Integer>> entries = new HashMap<>();
/*     */   
/*     */   private VersionMapper versionMapper;
/*     */   @Nullable
/*     */   VersionedRegistry<?> registry;
/*     */   
/*     */   public TypesBuilder(String mapPath, boolean lazy) {
/*  52 */     this.mapPath = mapPath;
/*  53 */     if (!lazy) {
/*  54 */       load();
/*     */     }
/*     */   }
/*     */   
/*     */   public TypesBuilder(String mapPath) {
/*  59 */     this(mapPath, false);
/*     */   }
/*     */   
/*     */   public void load() {
/*  63 */     if (this.entries == null)
/*  64 */       this.entries = new HashMap<>(); 
/*     */     
/*  66 */     try { SequentialNBTReader.Compound rootCompound = MappingHelper.decompress("mappings/" + this.mapPath); 
/*  67 */       try { rootCompound.skipOne();
/*  68 */         SequentialNBTReader.Compound compound = (SequentialNBTReader.Compound)rootCompound.next().getValue();
/*     */         
/*  70 */         int length = ((NBTNumber)compound.next().getValue()).getAsInt();
/*  71 */         SequentialNBTReader.Compound entries = (SequentialNBTReader.Compound)compound.next().getValue();
/*     */         
/*  73 */         ClientVersion[] versions = new ClientVersion[length];
/*  74 */         Map.Entry<String, NBT> first = entries.next();
/*  75 */         if (((NBT)first.getValue()).getType() == NBTType.LIST) {
/*  76 */           loadAsArray(first, entries, versions);
/*     */         } else {
/*  78 */           loadAsMap(first, entries, versions);
/*     */         } 
/*     */         
/*  81 */         this.versionMapper = new VersionMapper(versions);
/*  82 */         if (rootCompound != null) rootCompound.close();  } catch (Throwable throwable) { if (rootCompound != null) try { rootCompound.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException e)
/*  83 */     { throw new RuntimeException("Unable to load mapping files.", e); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadAsArray(Map.Entry<String, NBT> first, SequentialNBTReader.Compound entries, ClientVersion[] versions) {
/*  92 */     ClientVersion start = ClientVersion.valueOf(first.getKey());
/*  93 */     versions[0] = start;
/*  94 */     List<String> lastEntries = new ArrayList<>();
/*  95 */     for (NBT entry : first.getValue()) {
/*  96 */       lastEntries.add(((NBTString)entry).getValue());
/*     */     }
/*     */     
/*  99 */     Consumer<ClientVersion> mapLoader = version -> {
/*     */         Map<String, Integer> map = new HashMap<>();
/*     */         for (int i = 0; i < lastEntries.size(); i++) {
/*     */           map.put(lastEntries.get(i), Integer.valueOf(i));
/*     */         }
/*     */         this.entries.put(version, map);
/*     */       };
/* 106 */     mapLoader.accept(start);
/*     */     
/* 108 */     int i = 1;
/* 109 */     for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)entries) {
/* 110 */       ClientVersion version = ClientVersion.valueOf(entry.getKey());
/* 111 */       versions[i++] = version;
/* 112 */       List<ListDiff<String>> diff = MappingHelper.createListDiff((SequentialNBTReader.Compound)entry.getValue());
/*     */       
/* 114 */       for (int j = diff.size() - 1; j >= 0; j--) {
/* 115 */         ((ListDiff<String>)diff.get(j)).applyTo(lastEntries);
/*     */       }
/* 117 */       mapLoader.accept(version);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadAsMap(Map.Entry<String, NBT> first, SequentialNBTReader.Compound entries, ClientVersion[] versions) {
/* 126 */     ClientVersion start = ClientVersion.valueOf(first.getKey());
/* 127 */     versions[0] = start;
/*     */     
/* 129 */     Map<String, Integer> lastEntries = (Map<String, Integer>)StreamSupport.stream(((SequentialNBTReader.Compound)first.getValue()).spliterator(), false).collect(Collectors.toMap(Map.Entry::getKey, entry -> Integer.valueOf(((NBTNumber)entry.getValue()).getAsInt())));
/*     */     
/* 131 */     Consumer<ClientVersion> mapLoader = version -> {
/*     */         Map<String, Integer> map = new HashMap<>(lastEntries);
/*     */         this.entries.put(version, map);
/*     */       };
/* 135 */     mapLoader.accept(start);
/*     */     
/* 137 */     int i = 1;
/* 138 */     for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)entries) {
/* 139 */       ClientVersion version = ClientVersion.valueOf(entry.getKey());
/* 140 */       versions[i++] = version;
/* 141 */       List<MapDiff<String, Integer>> diff = MappingHelper.createDiff((SequentialNBTReader.Compound)entry.getValue());
/*     */       
/* 143 */       for (MapDiff<String, Integer> d : diff) {
/* 144 */         d.applyTo(lastEntries);
/*     */       }
/* 146 */       mapLoader.accept(version);
/*     */     } 
/*     */   }
/*     */   @Nullable
/*     */   public VersionedRegistry<?> getRegistry() {
/* 151 */     return this.registry;
/*     */   }
/*     */   
/*     */   public ClientVersion[] getVersions() {
/* 155 */     return this.versionMapper.getVersions();
/*     */   }
/*     */   
/*     */   public ClientVersion[] getReversedVersions() {
/* 159 */     return this.versionMapper.getReversedVersions();
/*     */   }
/*     */   
/*     */   public int getDataIndex(ClientVersion rawVersion) {
/* 163 */     return this.versionMapper.getIndex(rawVersion);
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   public boolean isMappingDataLoaded() {
/* 168 */     return (this.entries != null);
/*     */   }
/*     */   
/*     */   public void unloadFileMappings() {
/* 172 */     this.entries.clear();
/* 173 */     this.entries = null;
/*     */   }
/*     */   
/*     */   public TypesBuilderData define(String key) {
/* 177 */     ResourceLocation name = new ResourceLocation(key);
/* 178 */     int[] ids = new int[(getVersions()).length];
/* 179 */     int index = 0;
/* 180 */     for (ClientVersion v : getVersions()) {
/* 181 */       Map<String, Integer> map = this.entries.get(v);
/* 182 */       if (map.containsKey(key)) {
/* 183 */         int id = ((Integer)map.get(key)).intValue();
/* 184 */         ids[index] = id;
/*     */       } else {
/* 186 */         ids[index] = -1;
/*     */       } 
/* 188 */       index++;
/*     */     } 
/* 190 */     return new TypesBuilderData(this, name, ids);
/*     */   }
/*     */   @Nullable
/*     */   public Map<ClientVersion, Map<String, Integer>> getEntries() {
/* 194 */     return this.entries;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\mappings\TypesBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */