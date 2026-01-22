/*     */ package ac.grim.grimac.checks.impl.breaking;
/*     */ 
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.CheckData;
/*     */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*     */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.Pair;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.Ray;
/*     */ import ac.grim.grimac.utils.nmsutil.ReachUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ @CheckData(name = "RotationBreak", experimental = true)
/*     */ public class RotationBreak
/*     */   extends Check implements BlockBreakCheck {
/*  28 */   private double flagBuffer = 0.0D;
/*     */   private boolean ignorePost = false;
/*     */   
/*     */   public RotationBreak(GrimPlayer player) {
/*  32 */     super(player);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onBlockBreak(BlockBreak blockBreak) {
/*  37 */     if (this.player.gamemode == GameMode.SPECTATOR)
/*     */       return; 
/*  39 */     if (this.player.inVehicle())
/*  40 */       return;  if (blockBreak.action == DiggingAction.CANCELLED_DIGGING)
/*     */       return; 
/*  42 */     if (this.flagBuffer > 0.0D && !didRayTraceHit(blockBreak)) {
/*  43 */       this.ignorePost = true;
/*     */       
/*  45 */       if (flagAndAlert("pre-flying, action=" + String.valueOf(blockBreak.action)) && shouldModifyPackets()) {
/*  46 */         blockBreak.cancel();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPostFlyingBlockBreak(BlockBreak blockBreak) {
/*  53 */     if (this.player.gamemode == GameMode.SPECTATOR)
/*     */       return; 
/*  55 */     if (this.player.inVehicle())
/*  56 */       return;  if (blockBreak.action == DiggingAction.CANCELLED_DIGGING) {
/*     */       return;
/*     */     }
/*  59 */     if (this.ignorePost) {
/*  60 */       this.ignorePost = false;
/*     */       
/*     */       return;
/*     */     } 
/*  64 */     if (didRayTraceHit(blockBreak)) {
/*  65 */       this.flagBuffer = Math.max(0.0D, this.flagBuffer - 0.1D);
/*     */     } else {
/*  67 */       this.flagBuffer = 1.0D;
/*  68 */       flagAndAlert("post-flying, action=" + String.valueOf(blockBreak.action));
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean didRayTraceHit(BlockBreak blockBreak) {
/*  73 */     SimpleCollisionBox box = new SimpleCollisionBox(blockBreak.position);
/*     */     
/*  75 */     double[] possibleEyeHeights = this.player.getPossibleEyeHeights();
/*     */ 
/*     */     
/*  78 */     double minEyeHeight = Double.MAX_VALUE;
/*  79 */     double maxEyeHeight = Double.MIN_VALUE;
/*  80 */     for (double height : possibleEyeHeights) {
/*  81 */       minEyeHeight = Math.min(minEyeHeight, height);
/*  82 */       maxEyeHeight = Math.max(maxEyeHeight, height);
/*     */     } 
/*     */     
/*  85 */     SimpleCollisionBox eyePositions = new SimpleCollisionBox(this.player.x, this.player.y + minEyeHeight, this.player.z, this.player.x, this.player.y + maxEyeHeight, this.player.z);
/*  86 */     eyePositions.expand(this.player.getMovementThreshold());
/*     */ 
/*     */     
/*  89 */     if (eyePositions.isIntersected(box)) {
/*  90 */       return true;
/*     */     }
/*     */ 
/*     */     
/*  94 */     List<Vector3f> possibleLookDirs = new ArrayList<>(Arrays.asList(new Vector3f[] { new Vector3f(this.player.lastXRot, this.player.yRot, 0.0F), new Vector3f(this.player.xRot, this.player.yRot, 0.0F) }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 100 */     if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
/* 101 */       possibleLookDirs.add(new Vector3f(this.player.lastXRot, this.player.lastYRot, 0.0F));
/*     */     }
/*     */ 
/*     */     
/* 105 */     if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_8)) {
/* 106 */       possibleLookDirs = Collections.singletonList(new Vector3f(this.player.xRot, this.player.yRot, 0.0F));
/*     */     }
/*     */     
/* 109 */     double distance = this.player.compensatedEntities.self.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
/* 110 */     for (double d : possibleEyeHeights) {
/* 111 */       for (Vector3f lookDir : possibleLookDirs) {
/* 112 */         Vector3d starting = new Vector3d(this.player.x, this.player.y + d, this.player.z);
/* 113 */         Ray trace = new Ray(this.player, starting.getX(), starting.getY(), starting.getZ(), lookDir.getX(), lookDir.getY());
/* 114 */         Pair<Vector3dm, BlockFace> intercept = ReachUtils.calculateIntercept(box, trace.getOrigin(), trace.getPointAtDistance(distance));
/*     */         
/* 116 */         if (intercept.first() != null) return true;
/*     */       
/*     */       } 
/*     */     } 
/* 120 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\breaking\RotationBreak.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */