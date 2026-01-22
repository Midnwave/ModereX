/*    */ package ac.grim.grimac.platform.bukkit.world;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.world.PlatformChunk;
/*    */ import ac.grim.grimac.platform.api.world.PlatformWorld;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotConversionUtil;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.UUID;
/*    */ import lombok.Generated;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.block.Block;
/*    */ 
/*    */ public class BukkitPlatformWorld
/*    */   implements PlatformWorld
/*    */ {
/*    */   private final World bukkitWorld;
/* 21 */   private static final boolean LEGACY_SERVER_VERSION = PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_12_2); @Generated
/* 22 */   public World getBukkitWorld() { return this.bukkitWorld; }
/*    */   
/*    */   public BukkitPlatformWorld(@NotNull World world) {
/* 25 */     this.bukkitWorld = world;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isChunkLoaded(int chunkX, int chunkZ) {
/* 30 */     return this.bukkitWorld.isChunkLoaded(chunkX, chunkZ);
/*    */   }
/*    */ 
/*    */   
/*    */   public WrappedBlockState getBlockAt(int x, int y, int z) {
/* 35 */     if (LEGACY_SERVER_VERSION) {
/* 36 */       Block block = this.bukkitWorld.getBlockAt(x, y, z);
/* 37 */       int blockId = block.getType().getId() << 4 | block.getData();
/* 38 */       return WrappedBlockState.getByGlobalId(blockId);
/*    */     } 
/* 40 */     return SpigotConversionUtil.fromBukkitBlockData(this.bukkitWorld.getBlockAt(x, y, z).getBlockData());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 46 */     return this.bukkitWorld.getName();
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public UUID getUID() {
/* 51 */     return this.bukkitWorld.getUID();
/*    */   }
/*    */ 
/*    */   
/*    */   public PlatformChunk getChunkAt(int currChunkX, int currChunkZ) {
/* 56 */     return new BukkitPlatformChunk(this.bukkitWorld.getChunkAt(currChunkX, currChunkZ));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isLoaded() {
/* 61 */     return (Bukkit.getWorld(this.bukkitWorld.getUID()) != null);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\world\BukkitPlatformWorld.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */