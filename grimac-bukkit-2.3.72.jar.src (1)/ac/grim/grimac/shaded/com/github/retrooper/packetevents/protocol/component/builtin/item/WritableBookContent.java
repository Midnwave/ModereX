/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Filterable;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ public class WritableBookContent
/*    */ {
/*    */   private List<Filterable<String>> pages;
/*    */   
/*    */   public WritableBookContent(List<Filterable<String>> pages) {
/* 33 */     this.pages = pages;
/*    */   }
/*    */   
/*    */   public static WritableBookContent read(PacketWrapper<?> wrapper) {
/* 37 */     List<Filterable<String>> pages = wrapper.readList(ew -> Filterable.read(ew, ()));
/*    */     
/* 39 */     return new WritableBookContent(pages);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, WritableBookContent content) {
/* 43 */     wrapper.writeList(content.pages, (ew, page) -> Filterable.write(ew, page, ()));
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Filterable<String> getPage(int index) {
/* 48 */     return (index >= 0 && index < this.pages.size()) ? this.pages.get(index) : null;
/*    */   }
/*    */   
/*    */   public void addPage(Filterable<String> page) {
/* 52 */     this.pages.add(page);
/*    */   }
/*    */   
/*    */   public List<Filterable<String>> getPages() {
/* 56 */     return this.pages;
/*    */   }
/*    */   
/*    */   public void setPages(List<Filterable<String>> pages) {
/* 60 */     this.pages = pages;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 65 */     if (this == obj) return true; 
/* 66 */     if (!(obj instanceof WritableBookContent)) return false; 
/* 67 */     WritableBookContent that = (WritableBookContent)obj;
/* 68 */     return this.pages.equals(that.pages);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 73 */     return Objects.hashCode(this.pages);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\WritableBookContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */