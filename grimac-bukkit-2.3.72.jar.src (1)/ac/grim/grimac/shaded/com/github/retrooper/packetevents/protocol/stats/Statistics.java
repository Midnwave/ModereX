/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stats;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer.SequentialNBTReader;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.MappingHelper;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Statistics
/*    */ {
/* 36 */   private static final Map<String, Statistic> STATISTIC_MAP = new HashMap<>();
/*    */   
/*    */   public static Statistic getById(String id) {
/* 39 */     return STATISTIC_MAP.get(id);
/*    */   }
/*    */   
/*    */   static {
/* 43 */     ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();
/*    */     
/* 45 */     if (version.isOlderThan(ServerVersion.V_1_12_2))
/*    */       
/* 47 */       try { SequentialNBTReader.Compound rootMapping = MappingHelper.decompress("mappings/data/statistics"); 
/* 48 */         try { rootMapping.skipOne();
/* 49 */           SequentialNBTReader.Compound mapping = (SequentialNBTReader.Compound)rootMapping.next().getValue();
/*    */           
/* 51 */           if (version.isOlderThanOrEquals(ServerVersion.V_1_8_3)) {
/* 52 */             mapping.skipOne();
/*    */           }
/*    */           
/* 55 */           SequentialNBTReader.Compound toLoad = (SequentialNBTReader.Compound)mapping.next().getValue();
/*    */           
/* 57 */           for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)toLoad) {
/* 58 */             final String value = ((NBTString)entry.getValue()).getValue();
/* 59 */             Statistic statistic = new Statistic()
/*    */               {
/*    */                 private Component cachedDisplay;
/*    */                 
/*    */                 public String getId() {
/* 64 */                   return (String)entry.getKey();
/*    */                 }
/*    */ 
/*    */                 
/*    */                 public Component display() {
/* 69 */                   if (this.cachedDisplay == null) {
/* 70 */                     this.cachedDisplay = AdventureSerializer.serializer().fromJson(value);
/*    */                   }
/* 72 */                   return this.cachedDisplay;
/*    */                 }
/*    */ 
/*    */                 
/*    */                 public boolean equals(Object obj) {
/* 77 */                   if (obj instanceof Statistic) {
/* 78 */                     return ((Statistic)obj).getId().equals(getId());
/*    */                   }
/* 80 */                   return false;
/*    */                 }
/*    */               };
/*    */             
/* 84 */             STATISTIC_MAP.put(entry.getKey(), statistic);
/*    */           } 
/* 86 */           if (rootMapping != null) rootMapping.close();  } catch (Throwable throwable) { if (rootMapping != null) try { rootMapping.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException e)
/* 87 */       { throw new RuntimeException("Cannot load statistics mappings", e); }
/*    */        
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\stats\Statistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */