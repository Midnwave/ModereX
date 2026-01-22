/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLimiter;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer.SequentialNBTReader;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.StreamSupport;
/*     */ import java.util.zip.GZIPInputStream;
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
/*     */ 
/*     */ 
/*     */ @Internal
/*     */ public class MappingHelper
/*     */ {
/*     */   public static SequentialNBTReader.Compound decompress(String path) {
/*     */     try {
/*  49 */       DataInputStream dataInput = new DataInputStream(new GZIPInputStream(new BufferedInputStream(PacketEvents.getAPI().getSettings().getResourceProvider().apply("assets/" + path + ".nbt"))));
/*  50 */       return (SequentialNBTReader.Compound)SequentialNBTReader.INSTANCE.deserializeTag(NBTLimiter.noop(), dataInput);
/*  51 */     } catch (IOException e) {
/*  52 */       throw new RuntimeException("Cannot find resource file " + path + ".nbt", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static List<ListDiff<String>> createListDiff(SequentialNBTReader.Compound compound) {
/*  57 */     List<ListDiff<String>> diffs = new ArrayList<>();
/*     */     
/*  59 */     SequentialNBTReader.List removals = (SequentialNBTReader.List)compound.next().getValue();
/*  60 */     for (NBT entry : removals) {
/*  61 */       SequentialNBTReader.Compound c = (SequentialNBTReader.Compound)entry;
/*  62 */       diffs.add(new ListDiff.Removal<>(((NBTNumber)c
/*  63 */             .next().getValue()).getAsInt(), ((NBTNumber)c
/*  64 */             .next().getValue()).getAsInt()));
/*     */     } 
/*     */ 
/*     */     
/*  68 */     SequentialNBTReader.List additions = (SequentialNBTReader.List)compound.next().getValue();
/*  69 */     for (NBT entry : additions) {
/*  70 */       SequentialNBTReader.Compound c = (SequentialNBTReader.Compound)entry;
/*  71 */       diffs.add(new ListDiff.Addition<>(((NBTNumber)c
/*  72 */             .next().getValue()).getAsInt(), 
/*  73 */             (List<String>)StreamSupport.stream(((SequentialNBTReader.List)c.next().getValue()).spliterator(), false)
/*  74 */             .map(nbt -> ((NBTString)nbt).getValue())
/*  75 */             .collect(Collectors.toList())));
/*     */     } 
/*     */ 
/*     */     
/*  79 */     SequentialNBTReader.List changes = (SequentialNBTReader.List)compound.next().getValue();
/*  80 */     for (NBT entry : changes) {
/*  81 */       SequentialNBTReader.Compound c = (SequentialNBTReader.Compound)entry;
/*  82 */       diffs.add(new ListDiff.Changed<>(((NBTNumber)c
/*  83 */             .next().getValue()).getAsInt(), ((NBTNumber)c
/*  84 */             .next().getValue()).getAsInt(), 
/*  85 */             (List<String>)StreamSupport.stream(((SequentialNBTReader.List)c.next().getValue()).spliterator(), false)
/*  86 */             .map(nbt -> ((NBTString)nbt).getValue())
/*  87 */             .collect(Collectors.toList())));
/*     */     } 
/*     */ 
/*     */     
/*  91 */     diffs.sort(Comparator.comparingInt(ListDiff::getIndex));
/*     */     
/*  93 */     return diffs;
/*     */   }
/*     */   
/*     */   public static List<MapDiff<String, Integer>> createDiff(SequentialNBTReader.Compound compound) {
/*  97 */     List<MapDiff<String, Integer>> diffs = new ArrayList<>();
/*     */     
/*  99 */     SequentialNBTReader.Compound removal = (SequentialNBTReader.Compound)compound.next().getValue();
/* 100 */     for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)removal) {
/* 101 */       diffs.add(new MapDiff.Removal<>(entry.getKey()));
/*     */     }
/*     */     
/* 104 */     SequentialNBTReader.Compound additions = (SequentialNBTReader.Compound)compound.next().getValue();
/* 105 */     for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)additions) {
/* 106 */       diffs.add(new MapDiff.Addition<>(entry.getKey(), Integer.valueOf(((NBTNumber)entry.getValue()).getAsInt())));
/*     */     }
/*     */     
/* 109 */     return diffs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity> void registerMapping(TypesBuilder builder, Map<String, T> typeMap, Map<Byte, Map<Integer, T>> typeIdMap, TypesBuilderData typeData, T type) {
/* 119 */     typeMap.put(typeData.getName().toString(), type);
/* 120 */     for (ClientVersion version : builder.getVersions()) {
/* 121 */       int index = builder.getDataIndex(version);
/* 122 */       Map<Integer, T> idMap = typeIdMap.computeIfAbsent(Byte.valueOf((byte)index), k -> new HashMap<>());
/* 123 */       idMap.put(Integer.valueOf(typeData.getId(version)), type);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static int getId(ClientVersion version, TypesBuilder builder, TypesBuilderData data) {
/* 128 */     return data.getData()[builder.getDataIndex(version)];
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\mappings\MappingHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */