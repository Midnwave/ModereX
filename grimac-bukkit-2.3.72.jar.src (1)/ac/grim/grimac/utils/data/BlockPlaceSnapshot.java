/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ public class BlockPlaceSnapshot {
/*    */   PacketWrapper<?> wrapper;
/*    */   
/*    */   @Generated
/*  7 */   public void setWrapper(PacketWrapper<?> wrapper) { this.wrapper = wrapper; } boolean sneaking; @Generated public void setSneaking(boolean sneaking) { this.sneaking = sneaking; } @Generated public boolean equals(Object o) { if (o == this) return true;  if (!(o instanceof BlockPlaceSnapshot)) return false;  BlockPlaceSnapshot other = (BlockPlaceSnapshot)o; if (!other.canEqual(this)) return false;  if (isSneaking() != other.isSneaking()) return false;  Object<?> this$wrapper = (Object<?>)getWrapper(), other$wrapper = (Object<?>)other.getWrapper(); return !((this$wrapper == null) ? (other$wrapper != null) : !this$wrapper.equals(other$wrapper)); } @Generated protected boolean canEqual(Object other) { return other instanceof BlockPlaceSnapshot; } @Generated public int hashCode() { int PRIME = 59; result = 1; result = result * 59 + (isSneaking() ? 79 : 97); Object<?> $wrapper = (Object<?>)getWrapper(); return result * 59 + (($wrapper == null) ? 43 : $wrapper.hashCode()); } @Generated public String toString() { return "BlockPlaceSnapshot(wrapper=" + String.valueOf(getWrapper()) + ", sneaking=" + isSneaking() + ")"; } @Generated
/*  8 */   public BlockPlaceSnapshot(PacketWrapper<?> wrapper, boolean sneaking) { this.wrapper = wrapper; this.sneaking = sneaking; }
/*    */   @Generated
/* 10 */   public PacketWrapper<?> getWrapper() { return this.wrapper; } @Generated
/* 11 */   public boolean isSneaking() { return this.sneaking; }
/*    */ 
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\BlockPlaceSnapshot.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */