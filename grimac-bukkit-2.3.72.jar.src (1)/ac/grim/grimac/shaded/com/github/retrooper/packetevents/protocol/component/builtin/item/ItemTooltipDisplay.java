/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.Objects;
/*    */ import java.util.Set;
/*    */ import java.util.function.IntFunction;
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
/*    */ public class ItemTooltipDisplay
/*    */ {
/*    */   private boolean hideTooltip;
/*    */   private Set<ComponentType<?>> hiddenComponents;
/*    */   
/*    */   public ItemTooltipDisplay(boolean hideTooltip, Set<ComponentType<?>> hiddenComponents) {
/* 35 */     this.hideTooltip = hideTooltip;
/* 36 */     this.hiddenComponents = hiddenComponents;
/*    */   }
/*    */   
/*    */   public static ItemTooltipDisplay read(PacketWrapper<?> wrapper) {
/* 40 */     boolean hideTooltip = wrapper.readBoolean();
/* 41 */     Set<ComponentType<?>> hiddenComponents = (Set<ComponentType<?>>)wrapper.readCollection(java.util.LinkedHashSet::new, ew -> (ComponentType)ew.readMappedEntity((IRegistry)ComponentTypes.getRegistry()));
/*    */     
/* 43 */     return new ItemTooltipDisplay(hideTooltip, hiddenComponents);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemTooltipDisplay tooltipDisplay) {
/* 47 */     wrapper.writeBoolean(tooltipDisplay.hideTooltip);
/* 48 */     wrapper.writeCollection(tooltipDisplay.hiddenComponents, PacketWrapper::writeMappedEntity);
/*    */   }
/*    */   
/*    */   public boolean isHideTooltip() {
/* 52 */     return this.hideTooltip;
/*    */   }
/*    */   
/*    */   public void setHideTooltip(boolean hideTooltip) {
/* 56 */     this.hideTooltip = hideTooltip;
/*    */   }
/*    */   
/*    */   public Set<ComponentType<?>> getHiddenComponents() {
/* 60 */     return this.hiddenComponents;
/*    */   }
/*    */   
/*    */   public void setHiddenComponents(Set<ComponentType<?>> hiddenComponents) {
/* 64 */     this.hiddenComponents = hiddenComponents;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 69 */     if (!(obj instanceof ItemTooltipDisplay)) return false; 
/* 70 */     ItemTooltipDisplay that = (ItemTooltipDisplay)obj;
/* 71 */     if (this.hideTooltip != that.hideTooltip) return false; 
/* 72 */     return this.hiddenComponents.equals(that.hiddenComponents);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 77 */     return Objects.hash(new Object[] { Boolean.valueOf(this.hideTooltip), this.hiddenComponents });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemTooltipDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */