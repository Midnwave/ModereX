/*    */ package ac.grim.grimac.platform.bukkit.world;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.platform.api.Platform;
/*    */ import ac.grim.grimac.platform.api.world.PlatformChunk;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import org.bukkit.Chunk;
/*    */ import org.bukkit.block.Block;
/*    */ import org.bukkit.block.data.BlockData;
/*    */ 
/*    */ public class BukkitPlatformChunk
/*    */   implements PlatformChunk {
/* 19 */   private static final Map<BlockData, Integer> blockDataToId = (GrimAPI.INSTANCE.getPlatform() == Platform.FOLIA) ? new ConcurrentHashMap<>() : new HashMap<>();
/* 20 */   private static final boolean isFlat = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13);
/*    */   private final Chunk chunk;
/*    */   
/*    */   public BukkitPlatformChunk(@NotNull Chunk chunk) {
/* 24 */     this.chunk = chunk;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getBlockID(int x, int y, int z) {
/* 29 */     Block block = this.chunk.getBlock(x, y, z);
/*    */     
/* 31 */     return isFlat ? (
/* 32 */       (Integer)blockDataToId.computeIfAbsent(block.getBlockData(), data -> Integer.valueOf(WrappedBlockState.getByString(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(), data.getAsString(false)).getGlobalId()))).intValue() : (
/* 33 */       block.getType().getId() << 4 | block.getData());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\world\BukkitPlatformChunk.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */