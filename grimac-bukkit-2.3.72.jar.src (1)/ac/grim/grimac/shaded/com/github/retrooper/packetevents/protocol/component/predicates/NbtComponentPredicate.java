/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.predicates;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Experimental;
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
/*    */ @Experimental
/*    */ public class NbtComponentPredicate
/*    */   implements IComponentPredicate
/*    */ {
/*    */   private final NBT tag;
/*    */   
/*    */   public NbtComponentPredicate(NBT tag) {
/* 33 */     this.tag = tag;
/*    */   }
/*    */   
/*    */   public static NbtComponentPredicate read(PacketWrapper<?> wrapper) {
/* 37 */     NBT tag = wrapper.readNBTRaw();
/* 38 */     return new NbtComponentPredicate(tag);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, NbtComponentPredicate predicate) {
/* 42 */     wrapper.writeNBTRaw(predicate.tag);
/*    */   }
/*    */   
/*    */   public NBT getTag() {
/* 46 */     return this.tag;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 51 */     if (!(obj instanceof NbtComponentPredicate)) return false; 
/* 52 */     NbtComponentPredicate that = (NbtComponentPredicate)obj;
/* 53 */     return this.tag.equals(that.tag);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 58 */     return Objects.hashCode(this.tag);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\predicates\NbtComponentPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */