/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Debug.Renderer;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
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
/*    */ @Renderer(text = "String.valueOf(this.value) + \"l\"", hasChildren = "false")
/*    */ final class LongBinaryTagImpl
/*    */   extends AbstractBinaryTag
/*    */   implements LongBinaryTag
/*    */ {
/*    */   private final long value;
/*    */   
/*    */   LongBinaryTagImpl(long value) {
/* 37 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public long value() {
/* 42 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte byteValue() {
/* 47 */     return (byte)(int)(this.value & 0xFFL);
/*    */   }
/*    */ 
/*    */   
/*    */   public double doubleValue() {
/* 52 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public float floatValue() {
/* 57 */     return (float)this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public int intValue() {
/* 62 */     return (int)this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public long longValue() {
/* 67 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public short shortValue() {
/* 72 */     return (short)(int)(this.value & 0xFFFFL);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Number numberValue() {
/* 77 */     return Long.valueOf(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 82 */     if (this == other) return true; 
/* 83 */     if (other == null || getClass() != other.getClass()) return false; 
/* 84 */     LongBinaryTagImpl that = (LongBinaryTagImpl)other;
/* 85 */     return (this.value == that.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 90 */     return Long.hashCode(this.value);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 95 */     return Stream.of(ExaminableProperty.of("value", this.value));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\LongBinaryTagImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */