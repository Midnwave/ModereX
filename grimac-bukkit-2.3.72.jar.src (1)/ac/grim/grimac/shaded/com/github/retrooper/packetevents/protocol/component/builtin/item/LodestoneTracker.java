/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.WorldBlockPosition;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Objects;
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
/*    */ public class LodestoneTracker
/*    */ {
/*    */   @Nullable
/*    */   private WorldBlockPosition target;
/*    */   private boolean tracked;
/*    */   
/*    */   public LodestoneTracker(@Nullable WorldBlockPosition target, boolean tracked) {
/* 33 */     this.target = target;
/* 34 */     this.tracked = tracked;
/*    */   }
/*    */   
/*    */   public static LodestoneTracker read(PacketWrapper<?> wrapper) {
/* 38 */     WorldBlockPosition target = (WorldBlockPosition)wrapper.readOptional(PacketWrapper::readWorldBlockPosition);
/* 39 */     boolean tracked = wrapper.readBoolean();
/* 40 */     return new LodestoneTracker(target, tracked);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, LodestoneTracker tracker) {
/* 44 */     wrapper.writeOptional(tracker.target, PacketWrapper::writeWorldBlockPosition);
/* 45 */     wrapper.writeBoolean(tracker.tracked);
/*    */   }
/*    */   @Nullable
/*    */   public WorldBlockPosition getTarget() {
/* 49 */     return this.target;
/*    */   }
/*    */   
/*    */   public void setTarget(@Nullable WorldBlockPosition target) {
/* 53 */     this.target = target;
/*    */   }
/*    */   
/*    */   public boolean isTracked() {
/* 57 */     return this.tracked;
/*    */   }
/*    */   
/*    */   public void setTracked(boolean tracked) {
/* 61 */     this.tracked = tracked;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 66 */     if (this == obj) return true; 
/* 67 */     if (!(obj instanceof LodestoneTracker)) return false; 
/* 68 */     LodestoneTracker that = (LodestoneTracker)obj;
/* 69 */     if (this.tracked != that.tracked) return false; 
/* 70 */     return Objects.equals(this.target, that.target);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 75 */     return Objects.hash(new Object[] { this.target, Boolean.valueOf(this.tracked) });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\LodestoneTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */