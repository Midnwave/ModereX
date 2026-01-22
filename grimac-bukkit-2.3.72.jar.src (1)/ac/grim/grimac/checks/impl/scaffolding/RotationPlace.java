/*     */ package ac.grim.grimac.checks.impl.scaffolding;
/*     */ 
/*     */ import ac.grim.grimac.checks.CheckData;
/*     */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*     */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
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
/*     */ @CheckData(name = "RotationPlace", description = "Placed a block while not looking at it")
/*     */ public class RotationPlace
/*     */   extends BlockPlaceCheck {
/*  27 */   double flagBuffer = 0.0D;
/*     */   boolean ignorePost = false;
/*     */   
/*     */   public RotationPlace(GrimPlayer player) {
/*  31 */     super(player);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onBlockPlace(BlockPlace place) {
/*  36 */     if (place.material == StateTypes.SCAFFOLDING)
/*  37 */       return;  if (this.player.gamemode == GameMode.SPECTATOR)
/*     */       return; 
/*  39 */     if (this.player.inVehicle())
/*  40 */       return;  if (this.flagBuffer > 0.0D && !didRayTraceHit(place)) {
/*  41 */       this.ignorePost = true;
/*     */       
/*  43 */       if (flagAndAlert("pre-flying") && shouldModifyPackets() && shouldCancel()) {
/*  44 */         place.resync();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onPostFlyingBlockPlace(BlockPlace place) {
/*  52 */     if (place.material == StateTypes.SCAFFOLDING)
/*  53 */       return;  if (this.player.gamemode == GameMode.SPECTATOR)
/*     */       return; 
/*  55 */     if (this.player.inVehicle()) {
/*     */       return;
/*     */     }
/*  58 */     if (this.ignorePost) {
/*  59 */       this.ignorePost = false;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  64 */     boolean hit = didRayTraceHit(place);
/*     */     
/*  66 */     if (!hit) {
/*  67 */       this.flagBuffer = 1.0D;
/*  68 */       flagAndAlert("post-flying");
/*     */     } else {
/*  70 */       this.flagBuffer = Math.max(0.0D, this.flagBuffer - 0.1D);
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean didRayTraceHit(BlockPlace place) {
/*  75 */     SimpleCollisionBox box = new SimpleCollisionBox(place.position);
/*     */     
/*  77 */     List<Vector3f> possibleLookDirs = new ArrayList<>(Arrays.asList(new Vector3f[] { new Vector3f(this.player.xRot, this.player.yRot, 0.0F), new Vector3f(this.player.lastXRot, this.player.yRot, 0.0F) }));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  82 */     double[] possibleEyeHeights = this.player.getPossibleEyeHeights();
/*     */ 
/*     */     
/*  85 */     double minEyeHeight = Double.MAX_VALUE;
/*  86 */     double maxEyeHeight = Double.MIN_VALUE;
/*  87 */     for (double height : possibleEyeHeights) {
/*  88 */       minEyeHeight = Math.min(minEyeHeight, height);
/*  89 */       maxEyeHeight = Math.max(maxEyeHeight, height);
/*     */     } 
/*     */     
/*  92 */     SimpleCollisionBox eyePositions = new SimpleCollisionBox(this.player.x, this.player.y + minEyeHeight, this.player.z, this.player.x, this.player.y + maxEyeHeight, this.player.z);
/*  93 */     eyePositions.expand(this.player.getMovementThreshold());
/*     */ 
/*     */     
/*  96 */     if (eyePositions.isIntersected(box)) {
/*  97 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 102 */     if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
/* 103 */       possibleLookDirs.add(new Vector3f(this.player.lastXRot, this.player.lastYRot, 0.0F));
/*     */     }
/*     */ 
/*     */     
/* 107 */     if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_8)) {
/* 108 */       possibleLookDirs = Collections.singletonList(new Vector3f(this.player.xRot, this.player.yRot, 0.0F));
/*     */     }
/*     */     
/* 111 */     double distance = this.player.compensatedEntities.self.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
/* 112 */     for (double d : possibleEyeHeights) {
/* 113 */       for (Vector3f lookDir : possibleLookDirs) {
/*     */         
/* 115 */         Vector3d starting = new Vector3d(this.player.x, this.player.y + d, this.player.z);
/*     */         
/* 117 */         Ray trace = new Ray(this.player, starting.getX(), starting.getY(), starting.getZ(), lookDir.getX(), lookDir.getY());
/* 118 */         Pair<Vector3dm, BlockFace> intercept = ReachUtils.calculateIntercept(box, trace.getOrigin(), trace.getPointAtDistance(distance));
/*     */         
/* 120 */         if (intercept.first() != null) return true;
/*     */       
/*     */       } 
/*     */     } 
/* 124 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\scaffolding\RotationPlace.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */