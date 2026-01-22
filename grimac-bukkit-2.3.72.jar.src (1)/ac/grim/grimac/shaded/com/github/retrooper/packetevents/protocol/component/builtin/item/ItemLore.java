/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ 
/*    */ public class ItemLore
/*    */ {
/* 30 */   public static final ItemLore EMPTY = new ItemLore(Collections.emptyList());
/*    */   
/*    */   private List<Component> lines;
/*    */   
/*    */   public ItemLore(List<Component> lines) {
/* 35 */     this.lines = lines;
/*    */   }
/*    */   
/*    */   public static ItemLore read(PacketWrapper<?> wrapper) {
/* 39 */     List<Component> lines = wrapper.readList(PacketWrapper::readComponent);
/* 40 */     return new ItemLore(lines);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemLore lore) {
/* 44 */     wrapper.writeList(lore.lines, PacketWrapper::writeComponent);
/*    */   }
/*    */   
/*    */   public void addLine(Component line) {
/* 48 */     this.lines.add(line);
/*    */   }
/*    */   
/*    */   public List<Component> getLines() {
/* 52 */     return this.lines;
/*    */   }
/*    */   
/*    */   public void setLines(List<Component> lines) {
/* 56 */     this.lines = lines;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 61 */     if (this == obj) return true; 
/* 62 */     if (!(obj instanceof ItemLore)) return false; 
/* 63 */     ItemLore itemLore = (ItemLore)obj;
/* 64 */     return this.lines.equals(itemLore.lines);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 69 */     return Objects.hash(new Object[] { this.lines });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     return "ItemLore{lines=" + this.lines + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemLore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */