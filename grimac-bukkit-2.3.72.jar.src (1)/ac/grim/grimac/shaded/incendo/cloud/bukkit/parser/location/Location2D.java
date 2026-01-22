/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.location;
/*    */ 
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.World;
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
/*    */ public class Location2D
/*    */   extends Location
/*    */ {
/*    */   protected Location2D(World world, double x, double z) {
/* 39 */     super(world, x, 0.0D, z);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Location2D from(World world, double x, double z) {
/* 51 */     return new Location2D(world, x, z);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\location\Location2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */