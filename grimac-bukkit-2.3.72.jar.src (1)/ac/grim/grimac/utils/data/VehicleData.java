/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*    */ import ac.grim.grimac.utils.enums.BoatEntityStatus;
/*    */ import java.util.concurrent.ConcurrentLinkedQueue;
/*    */ 
/*    */ public class VehicleData
/*    */ {
/*    */   public boolean boatUnderwater = false;
/*    */   public double lastYd;
/*    */   public double midTickY;
/*    */   public float landFriction;
/*    */   public BoatEntityStatus status;
/*    */   public BoatEntityStatus oldStatus;
/*    */   public double waterLevel;
/*    */   public float deltaRotation;
/* 17 */   public float nextVehicleHorizontal = 0.0F;
/* 18 */   public float nextVehicleForward = 0.0F;
/* 19 */   public float vehicleHorizontal = 0.0F;
/* 20 */   public float vehicleForward = 0.0F;
/*    */   public boolean lastDummy = false;
/*    */   public boolean wasVehicleSwitch = false;
/* 23 */   public ConcurrentLinkedQueue<Pair<Integer, Vector3d>> vehicleTeleports = new ConcurrentLinkedQueue<>();
/* 24 */   public float nextHorseJump = 0.0F;
/* 25 */   public float horseJump = 0.0F;
/*    */   public boolean horseJumping = false;
/*    */   public int camelDashCooldown;
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\VehicleData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */