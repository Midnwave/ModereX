/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.location;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
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
/*     */ public final class LocationCoordinate
/*     */ {
/*     */   private final LocationCoordinateType type;
/*     */   private final double coordinate;
/*     */   
/*     */   private LocationCoordinate(LocationCoordinateType type, double coordinate) {
/*  44 */     this.type = type;
/*  45 */     this.coordinate = coordinate;
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
/*     */   public static LocationCoordinate of(LocationCoordinateType type, double coordinate) {
/*  59 */     return new LocationCoordinate(type, coordinate);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LocationCoordinateType type() {
/*  68 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double coordinate() {
/*  77 */     return this.coordinate;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  82 */     if (this == o) {
/*  83 */       return true;
/*     */     }
/*  85 */     if (o == null || getClass() != o.getClass()) {
/*  86 */       return false;
/*     */     }
/*  88 */     LocationCoordinate that = (LocationCoordinate)o;
/*  89 */     return (Double.compare(that.coordinate, this.coordinate) == 0 && this.type == that.type);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  95 */     return Objects.hash(new Object[] { this.type, Double.valueOf(this.coordinate) });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 100 */     return String.format("LocationCoordinate{type=%s, coordinate=%f}", new Object[] { this.type.name().toLowerCase(Locale.ROOT), Double.valueOf(this.coordinate) });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\location\LocationCoordinate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */