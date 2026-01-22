/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Objects;
/*    */ import java.util.Optional;
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
/*    */ public class Filterable<T>
/*    */ {
/*    */   private T raw;
/*    */   @Nullable
/*    */   private T filtered;
/*    */   
/*    */   public Filterable(T raw) {
/* 34 */     this(raw, (T)null);
/*    */   }
/*    */   
/*    */   public Filterable(T raw, Optional<T> filtered) {
/* 38 */     this(raw, filtered.orElse(null));
/*    */   }
/*    */   
/*    */   public Filterable(T raw, @Nullable T filtered) {
/* 42 */     this.raw = raw;
/* 43 */     this.filtered = filtered;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> Filterable<T> read(PacketWrapper<?> wrapper, PacketWrapper.Reader<T> reader) {
/* 50 */     T raw = (T)reader.apply(wrapper);
/* 51 */     T filtered = (T)wrapper.readOptional(reader);
/* 52 */     return new Filterable<>(raw, filtered);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> void write(PacketWrapper<?> wrapper, Filterable<T> filterable, PacketWrapper.Writer<T> writer) {
/* 60 */     writer.accept(wrapper, filterable.raw);
/* 61 */     wrapper.writeOptional(filterable.filtered, writer);
/*    */   }
/*    */   
/*    */   public T getRaw() {
/* 65 */     return this.raw;
/*    */   }
/*    */   
/*    */   public void setRaw(T raw) {
/* 69 */     this.raw = raw;
/*    */   }
/*    */   @Nullable
/*    */   public T getFiltered() {
/* 73 */     return this.filtered;
/*    */   }
/*    */   
/*    */   public void setFiltered(@Nullable T filtered) {
/* 77 */     this.filtered = filtered;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 82 */     if (this == obj) return true; 
/* 83 */     if (!(obj instanceof Filterable)) return false; 
/* 84 */     Filterable<?> that = (Filterable)obj;
/* 85 */     if (!this.raw.equals(that.raw)) return false; 
/* 86 */     return Objects.equals(this.filtered, that.filtered);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 91 */     return Objects.hash(new Object[] { this.raw, this.filtered });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\Filterable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */