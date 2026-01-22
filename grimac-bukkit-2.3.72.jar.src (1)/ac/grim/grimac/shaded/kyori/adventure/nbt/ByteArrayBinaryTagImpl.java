/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Debug.Renderer;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*    */ import java.util.Arrays;
/*    */ import java.util.Iterator;
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
/*    */ @Renderer(text = "\"byte[\" + this.value.length + \"]\"", childrenArray = "this.value", hasChildren = "this.value.length > 0")
/*    */ final class ByteArrayBinaryTagImpl
/*    */   extends ArrayBinaryTagImpl
/*    */   implements ByteArrayBinaryTag
/*    */ {
/*    */   final byte[] value;
/*    */   
/*    */   ByteArrayBinaryTagImpl(byte[] value) {
/* 39 */     this.value = Arrays.copyOf(value, value.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] value() {
/* 44 */     return Arrays.copyOf(this.value, this.value.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 49 */     return this.value.length;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte get(int index) {
/* 54 */     checkIndex(index, this.value.length);
/* 55 */     return this.value[index];
/*    */   }
/*    */ 
/*    */   
/*    */   static byte[] value(ByteArrayBinaryTag tag) {
/* 60 */     return (tag instanceof ByteArrayBinaryTagImpl) ? ((ByteArrayBinaryTagImpl)tag).value : tag.value();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 65 */     if (this == other) return true; 
/* 66 */     if (other == null || getClass() != other.getClass()) return false; 
/* 67 */     ByteArrayBinaryTagImpl that = (ByteArrayBinaryTagImpl)other;
/* 68 */     return Arrays.equals(this.value, that.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 73 */     return Arrays.hashCode(this.value);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 78 */     return Stream.of(ExaminableProperty.of("value", this.value));
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<Byte> iterator() {
/* 83 */     return new Iterator<Byte>()
/*    */       {
/*    */         private int index;
/*    */         
/*    */         public boolean hasNext() {
/* 88 */           return (this.index < ByteArrayBinaryTagImpl.this.value.length - 1);
/*    */         }
/*    */ 
/*    */         
/*    */         public Byte next() {
/* 93 */           return Byte.valueOf(ByteArrayBinaryTagImpl.this.value[this.index++]);
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\ByteArrayBinaryTagImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */