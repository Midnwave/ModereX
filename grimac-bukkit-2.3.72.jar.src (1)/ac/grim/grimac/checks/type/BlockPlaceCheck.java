/*     */ package ac.grim.grimac.checks.type;
/*     */ 
/*     */ import ac.grim.grimac.api.config.ConfigManager;
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*     */ import ac.grim.grimac.utils.collisions.HitboxData;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class BlockPlaceCheck
/*     */   extends Check
/*     */   implements RotationCheck, BlockBreakCheck {
/*  19 */   private static final List<StateType> weirdBoxes = new ArrayList<>();
/*  20 */   private static final List<StateType> buggyBoxes = new ArrayList<>();
/*     */ 
/*     */   
/*     */   static {
/*  24 */     weirdBoxes.addAll(new ArrayList<>(BlockTags.FENCES.getStates()));
/*  25 */     weirdBoxes.addAll(new ArrayList<>(BlockTags.WALLS.getStates()));
/*  26 */     weirdBoxes.add(StateTypes.LECTERN);
/*     */     
/*  28 */     buggyBoxes.addAll(new ArrayList<>(BlockTags.DOORS.getStates()));
/*  29 */     buggyBoxes.addAll(new ArrayList<>(BlockTags.STAIRS.getStates()));
/*  30 */     buggyBoxes.add(StateTypes.CHEST);
/*  31 */     buggyBoxes.add(StateTypes.TRAPPED_CHEST);
/*  32 */     buggyBoxes.add(StateTypes.CHORUS_PLANT);
/*     */ 
/*     */     
/*  35 */     buggyBoxes.add(StateTypes.KELP);
/*  36 */     buggyBoxes.add(StateTypes.KELP_PLANT);
/*  37 */     buggyBoxes.add(StateTypes.TWISTING_VINES);
/*  38 */     buggyBoxes.add(StateTypes.TWISTING_VINES_PLANT);
/*  39 */     buggyBoxes.add(StateTypes.WEEPING_VINES);
/*  40 */     buggyBoxes.add(StateTypes.WEEPING_VINES_PLANT);
/*  41 */     buggyBoxes.add(StateTypes.REDSTONE_WIRE);
/*     */   }
/*     */   
/*  44 */   private final SimpleCollisionBox[] boxes = new SimpleCollisionBox[15];
/*     */   protected int cancelVL;
/*     */   
/*     */   public BlockPlaceCheck(GrimPlayer player) {
/*  48 */     super(player);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onBlockPlace(BlockPlace place) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void onPostFlyingBlockPlace(BlockPlace place) {}
/*     */ 
/*     */   
/*     */   public void onReload(ConfigManager config) {
/*  61 */     this.cancelVL = config.getIntElse(getConfigName() + ".cancelVL", 5);
/*     */   }
/*     */   
/*     */   protected boolean shouldCancel() {
/*  65 */     return (this.cancelVL >= 0 && this.violations >= this.cancelVL);
/*     */   }
/*     */ 
/*     */   
/*     */   protected SimpleCollisionBox getCombinedBox(BlockPlace place) {
/*  70 */     Vector3i clicked = place.position;
/*     */     
/*  72 */     if (weirdBoxes.contains(place.getPlacedAgainstMaterial()) || buggyBoxes.contains(place.getPlacedAgainstMaterial()))
/*     */     {
/*  74 */       return new SimpleCollisionBox((clicked.getX() + 1), (clicked.getY() + 1), (clicked.getZ() + 1), clicked.getX(), clicked.getY(), clicked.getZ());
/*     */     }
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
/*  86 */     int size = HitboxData.getBlockHitbox(this.player, place.material, this.player.getClientVersion(), this.player.compensatedWorld.getBlock(clicked), true, clicked.getX(), clicked.getY(), clicked.getZ()).downCast(this.boxes);
/*     */     
/*  88 */     SimpleCollisionBox combined = new SimpleCollisionBox(clicked.getX(), clicked.getY(), clicked.getZ());
/*  89 */     for (int i = 0; i < size; i++) {
/*  90 */       SimpleCollisionBox box = this.boxes[i];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  97 */       combined = new SimpleCollisionBox(Math.max(box.minX, combined.minX), Math.max(box.minY, combined.minY), Math.max(box.minZ, combined.minZ), Math.min(box.maxX, combined.maxX), Math.min(box.maxY, combined.maxY), Math.min(box.maxZ, combined.maxZ));
/*     */     } 
/*     */ 
/*     */     
/* 101 */     return combined;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\type\BlockPlaceCheck.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */