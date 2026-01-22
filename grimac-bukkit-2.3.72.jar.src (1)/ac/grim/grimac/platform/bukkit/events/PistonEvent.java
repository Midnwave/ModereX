/*     */ package ac.grim.grimac.platform.bukkit.events;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.platform.bukkit.utils.convert.BukkitConversionUtils;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.PistonData;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.BlockPistonExtendEvent;
/*     */ import org.bukkit.event.block.BlockPistonRetractEvent;
/*     */ 
/*     */ public class PistonEvent
/*     */   implements Listener
/*     */ {
/*  24 */   private final Material SLIME_BLOCK = Material.getMaterial("SLIME_BLOCK");
/*  25 */   private final Material HONEY_BLOCK = Material.getMaterial("HONEY_BLOCK");
/*     */   
/*     */   private static final double MAX_HORIZONTAL_DISTANCE = 24.0D;
/*     */   
/*     */   private static final double MAX_VERTICAL_DISTANCE = 64.0D;
/*     */   
/*     */   private static boolean isCloseEnough(Vector3i vectorA, Vector3d vectorB) {
/*  32 */     return (Math.abs(vectorA.getX() - vectorB.getX()) <= 24.0D && 
/*  33 */       Math.abs(vectorA.getY() - vectorB.getY()) <= 64.0D && 
/*  34 */       Math.abs(vectorA.getZ() - vectorB.getZ()) <= 24.0D);
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
/*     */   public void onPistonPushEvent(BlockPistonExtendEvent event) {
/*  39 */     boolean hasSlimeBlock = false;
/*  40 */     boolean hasHoneyBlock = false;
/*     */     
/*  42 */     List<SimpleCollisionBox> boxes = new ArrayList<>();
/*  43 */     for (Block block : event.getBlocks()) {
/*  44 */       boxes.add((new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, true))
/*  45 */           .offset(block.getX(), block
/*  46 */             .getY(), block
/*  47 */             .getZ()));
/*  48 */       boxes.add((new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, true))
/*  49 */           .offset((block.getX() + event.getDirection().getModX()), (block
/*  50 */             .getY() + event.getDirection().getModY()), (block
/*  51 */             .getZ() + event.getDirection().getModZ())));
/*     */ 
/*     */       
/*  54 */       if (block.getType() == this.SLIME_BLOCK) {
/*  55 */         hasSlimeBlock = true;
/*     */       }
/*     */       
/*  58 */       if (block.getType() == this.HONEY_BLOCK) {
/*  59 */         hasHoneyBlock = true;
/*     */       }
/*     */     } 
/*     */     
/*  63 */     Block piston = event.getBlock();
/*     */ 
/*     */     
/*  66 */     boxes.add((new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, true))
/*  67 */         .offset((piston.getX() + event.getDirection().getModX()), (piston
/*  68 */           .getY() + event.getDirection().getModY()), (piston
/*  69 */           .getZ() + event.getDirection().getModZ())));
/*     */     
/*  71 */     int chunkX = event.getBlock().getX() >> 4;
/*  72 */     int chunkZ = event.getBlock().getZ() >> 4;
/*  73 */     BlockFace blockFace = BukkitConversionUtils.fromBukkitFace(event.getDirection());
/*  74 */     Vector3i sourcePos = new Vector3i(piston.getX(), piston.getY(), piston.getZ());
/*     */     
/*  76 */     for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
/*  77 */       if (player.compensatedWorld.isChunkLoaded(chunkX, chunkZ) && isCloseEnough(sourcePos, player.compensatedEntities.self.trackedServerPosition.getPos())) {
/*  78 */         int lastTrans = player.lastTransactionSent.get();
/*  79 */         PistonData data = new PistonData(blockFace, boxes, lastTrans, true, hasSlimeBlock, hasHoneyBlock);
/*  80 */         player.latencyUtils.addRealTimeTaskAsync(lastTrans, () -> player.compensatedWorld.activePistons.add(data));
/*     */       } 
/*     */     } 
/*     */   }
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
/*     */   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
/*     */   public void onPistonRetractEvent(BlockPistonRetractEvent event) {
/*  97 */     boolean hasSlimeBlock = false;
/*  98 */     boolean hasHoneyBlock = false;
/*     */     
/* 100 */     List<SimpleCollisionBox> boxes = new ArrayList<>();
/* 101 */     BlockFace face = BukkitConversionUtils.fromBukkitFace(event.getDirection());
/*     */ 
/*     */     
/* 104 */     if (event.getBlocks().isEmpty()) {
/* 105 */       Block piston = event.getBlock();
/*     */ 
/*     */       
/* 108 */       boxes.add((new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, true))
/* 109 */           .offset((piston.getX() + face.getModX()), (piston
/* 110 */             .getY() + face.getModY()), (piston
/* 111 */             .getZ() + face.getModZ())));
/*     */     } 
/*     */     
/* 114 */     for (Block block : event.getBlocks()) {
/* 115 */       boxes.add((new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, true))
/* 116 */           .offset(block.getX(), block.getY(), block.getZ()));
/* 117 */       boxes.add((new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, true))
/* 118 */           .offset((block.getX() + face.getModX()), (block.getY() + face.getModY()), (block.getZ() + face.getModZ())));
/*     */ 
/*     */       
/* 121 */       if (block.getType() == this.SLIME_BLOCK) {
/* 122 */         hasSlimeBlock = true;
/*     */       }
/*     */       
/* 125 */       if (block.getType() == this.HONEY_BLOCK) {
/* 126 */         hasHoneyBlock = true;
/*     */       }
/*     */     } 
/*     */     
/* 130 */     int chunkX = event.getBlock().getX() >> 4;
/* 131 */     int chunkZ = event.getBlock().getZ() >> 4;
/* 132 */     Vector3i sourcePos = new Vector3i(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
/*     */     
/* 134 */     for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
/* 135 */       if (player.compensatedWorld.isChunkLoaded(chunkX, chunkZ) && isCloseEnough(sourcePos, player.compensatedEntities.self.trackedServerPosition.getPos())) {
/* 136 */         int lastTrans = player.lastTransactionSent.get();
/* 137 */         PistonData data = new PistonData(face, boxes, lastTrans, false, hasSlimeBlock, hasHoneyBlock);
/* 138 */         player.latencyUtils.addRealTimeTaskAsync(lastTrans, () -> player.compensatedWorld.activePistons.add(data));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\events\PistonEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */