/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.waypoint;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.color.Color;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ @NullMarked
/*    */ public final class WaypointIcon
/*    */ {
/* 30 */   public static final ResourceLocation ICON_STYLE_DEFAULT = new ResourceLocation("default");
/* 31 */   public static final ResourceLocation ICON_STYLE_BOWTIE = new ResourceLocation("bowtie");
/*    */   
/*    */   private final ResourceLocation style;
/*    */   private final Color color;
/*    */   
/*    */   public WaypointIcon(ResourceLocation style, Color color) {
/* 37 */     this.style = style;
/* 38 */     this.color = color;
/*    */   }
/*    */   
/*    */   public static WaypointIcon read(PacketWrapper<?> wrapper) {
/* 42 */     ResourceLocation style = wrapper.readIdentifier();
/* 43 */     Color color = (Color)wrapper.readOptional(Color::readShort);
/* 44 */     return new WaypointIcon(style, color);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, WaypointIcon icon) {
/* 48 */     wrapper.writeIdentifier(icon.style);
/* 49 */     wrapper.writeOptional(icon.color, Color::writeShort);
/*    */   }
/*    */   
/*    */   public ResourceLocation getStyle() {
/* 53 */     return this.style;
/*    */   }
/*    */   
/*    */   public Color getColor() {
/* 57 */     return this.color;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\waypoint\WaypointIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */