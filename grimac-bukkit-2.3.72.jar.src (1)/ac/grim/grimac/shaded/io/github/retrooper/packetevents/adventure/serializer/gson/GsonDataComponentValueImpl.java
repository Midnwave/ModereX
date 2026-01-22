/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.gson;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.event.DataComponentValue;
/*    */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*    */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonNull;
/*    */ import java.util.Objects;
/*    */ import java.util.stream.Stream;
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
/*    */ class GsonDataComponentValueImpl
/*    */   implements GsonDataComponentValue
/*    */ {
/*    */   private final JsonElement element;
/*    */   
/*    */   GsonDataComponentValueImpl(@NotNull JsonElement element) {
/* 40 */     this.element = element;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public JsonElement element() {
/* 45 */     return this.element;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 50 */     return Stream.of(
/* 51 */         ExaminableProperty.of("element", this.element));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 57 */     return Internals.toString((Examinable)this);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 62 */     if (this == other) return true; 
/* 63 */     if (other == null || getClass() != other.getClass()) return false; 
/* 64 */     GsonDataComponentValueImpl that = (GsonDataComponentValueImpl)other;
/* 65 */     return Objects.equals(this.element, that.element);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 70 */     return Objects.hashCode(this.element);
/*    */   }
/*    */   
/*    */   static final class RemovedGsonComponentValueImpl extends GsonDataComponentValueImpl implements DataComponentValue.Removed {
/* 74 */     static final RemovedGsonComponentValueImpl INSTANCE = new RemovedGsonComponentValueImpl();
/*    */     
/*    */     private RemovedGsonComponentValueImpl() {
/* 77 */       super((JsonElement)JsonNull.INSTANCE);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\serializer\gson\GsonDataComponentValueImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */