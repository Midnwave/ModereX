/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Filterable;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.util.List;
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
/*     */ public class WrittenBookContent
/*     */ {
/*     */   private Filterable<String> title;
/*     */   private String author;
/*     */   private int generation;
/*     */   private List<Filterable<Component>> pages;
/*     */   private boolean resolved;
/*     */   
/*     */   public WrittenBookContent(Filterable<String> title, String author, int generation, List<Filterable<Component>> pages, boolean resolved) {
/*  41 */     this.title = title;
/*  42 */     this.author = author;
/*  43 */     this.generation = generation;
/*  44 */     this.pages = pages;
/*  45 */     this.resolved = resolved;
/*     */   }
/*     */   
/*     */   public static WrittenBookContent read(PacketWrapper<?> wrapper) {
/*  49 */     Filterable<String> title = Filterable.read(wrapper, ew -> ew.readString(32));
/*  50 */     String author = wrapper.readString();
/*  51 */     int generation = wrapper.readVarInt();
/*  52 */     List<Filterable<Component>> pages = wrapper.readList(ew -> Filterable.read(ew, PacketWrapper::readComponent));
/*     */     
/*  54 */     boolean resolved = wrapper.readBoolean();
/*  55 */     return new WrittenBookContent(title, author, generation, pages, resolved);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, WrittenBookContent content) {
/*  59 */     Filterable.write(wrapper, content.title, (ew, text) -> ew.writeString(text, 32));
/*  60 */     wrapper.writeString(content.author);
/*  61 */     wrapper.writeVarInt(content.generation);
/*  62 */     wrapper.writeList(content.pages, (ew, page) -> Filterable.write(ew, page, PacketWrapper::writeComponent));
/*     */     
/*  64 */     wrapper.writeBoolean(content.resolved);
/*     */   }
/*     */   
/*     */   public Filterable<String> getTitle() {
/*  68 */     return this.title;
/*     */   }
/*     */   
/*     */   public void setTitle(Filterable<String> title) {
/*  72 */     this.title = title;
/*     */   }
/*     */   
/*     */   public String getAuthor() {
/*  76 */     return this.author;
/*     */   }
/*     */   
/*     */   public void setAuthor(String author) {
/*  80 */     this.author = author;
/*     */   }
/*     */   
/*     */   public int getGeneration() {
/*  84 */     return this.generation;
/*     */   }
/*     */   
/*     */   public void setGeneration(int generation) {
/*  88 */     this.generation = generation;
/*     */   }
/*     */   @Nullable
/*     */   public Filterable<Component> getPage(int index) {
/*  92 */     return (index >= 0 && index < this.pages.size()) ? this.pages.get(index) : null;
/*     */   }
/*     */   
/*     */   public void addPage(Filterable<Component> page) {
/*  96 */     this.pages.add(page);
/*     */   }
/*     */   
/*     */   public List<Filterable<Component>> getPages() {
/* 100 */     return this.pages;
/*     */   }
/*     */   
/*     */   public void setPages(List<Filterable<Component>> pages) {
/* 104 */     this.pages = pages;
/*     */   }
/*     */   
/*     */   public boolean isResolved() {
/* 108 */     return this.resolved;
/*     */   }
/*     */   
/*     */   public void setResolved(boolean resolved) {
/* 112 */     this.resolved = resolved;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 117 */     if (this == obj) return true; 
/* 118 */     if (!(obj instanceof WrittenBookContent)) return false; 
/* 119 */     WrittenBookContent that = (WrittenBookContent)obj;
/* 120 */     if (this.generation != that.generation) return false; 
/* 121 */     if (this.resolved != that.resolved) return false; 
/* 122 */     if (!this.title.equals(that.title)) return false; 
/* 123 */     if (!this.author.equals(that.author)) return false; 
/* 124 */     return this.pages.equals(that.pages);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 129 */     return Objects.hash(new Object[] { this.title, this.author, Integer.valueOf(this.generation), this.pages, Boolean.valueOf(this.resolved) });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\WrittenBookContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */