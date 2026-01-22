/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.io.DataInput;
/*     */ import java.io.IOException;
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
/*     */ final class TrackingDataInput
/*     */   implements DataInput, BinaryTagScope
/*     */ {
/*     */   private static final int MAX_DEPTH = 512;
/*     */   private final DataInput input;
/*     */   private final long maxLength;
/*     */   private long counter;
/*     */   private int depth;
/*     */   
/*     */   TrackingDataInput(DataInput input, long maxLength) {
/*  39 */     this.input = input;
/*  40 */     this.maxLength = maxLength;
/*     */   }
/*     */   
/*     */   public static BinaryTagScope enter(DataInput input) throws IOException {
/*  44 */     if (input instanceof TrackingDataInput) {
/*  45 */       return ((TrackingDataInput)input).enter();
/*     */     }
/*  47 */     return BinaryTagScope.NoOp.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BinaryTagScope enter(DataInput input, long expectedSize) throws IOException {
/*  52 */     if (input instanceof TrackingDataInput) {
/*  53 */       return ((TrackingDataInput)input).enter(expectedSize);
/*     */     }
/*  55 */     return BinaryTagScope.NoOp.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   public DataInput input() {
/*  60 */     return this.input;
/*     */   }
/*     */ 
/*     */   
/*     */   public TrackingDataInput enter(long expectedSize) throws IOException {
/*  65 */     if (this.depth++ > 512) {
/*  66 */       throw new IOException("NBT read exceeded maximum depth of 512");
/*     */     }
/*     */     
/*  69 */     ensureMaxLength(expectedSize);
/*  70 */     return this;
/*     */   }
/*     */   
/*     */   public TrackingDataInput enter() throws IOException {
/*  74 */     return enter(0L);
/*     */   }
/*     */   
/*     */   public void exit() throws IOException {
/*  78 */     this.depth--;
/*  79 */     ensureMaxLength(0L);
/*     */   }
/*     */   
/*     */   private void ensureMaxLength(long expected) throws IOException {
/*  83 */     if (this.maxLength > 0L && this.counter + expected > this.maxLength) {
/*  84 */       throw new IOException("The read NBT was longer than the maximum allowed size of " + this.maxLength + " bytes!");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void readFully(byte[] array) throws IOException {
/*  90 */     this.counter += array.length;
/*  91 */     this.input.readFully(array);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readFully(byte[] array, int off, int len) throws IOException {
/*  96 */     this.counter += len;
/*  97 */     this.input.readFully(array, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public int skipBytes(int n) throws IOException {
/* 102 */     return this.input.skipBytes(n);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean readBoolean() throws IOException {
/* 107 */     this.counter++;
/* 108 */     return this.input.readBoolean();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte readByte() throws IOException {
/* 113 */     this.counter++;
/* 114 */     return this.input.readByte();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readUnsignedByte() throws IOException {
/* 119 */     this.counter++;
/* 120 */     return this.input.readUnsignedByte();
/*     */   }
/*     */ 
/*     */   
/*     */   public short readShort() throws IOException {
/* 125 */     this.counter += 2L;
/* 126 */     return this.input.readShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readUnsignedShort() throws IOException {
/* 131 */     this.counter += 2L;
/* 132 */     return this.input.readUnsignedShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public char readChar() throws IOException {
/* 137 */     this.counter += 2L;
/* 138 */     return this.input.readChar();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readInt() throws IOException {
/* 143 */     this.counter += 4L;
/* 144 */     return this.input.readInt();
/*     */   }
/*     */ 
/*     */   
/*     */   public long readLong() throws IOException {
/* 149 */     this.counter += 8L;
/* 150 */     return this.input.readLong();
/*     */   }
/*     */ 
/*     */   
/*     */   public float readFloat() throws IOException {
/* 155 */     this.counter += 4L;
/* 156 */     return this.input.readFloat();
/*     */   }
/*     */ 
/*     */   
/*     */   public double readDouble() throws IOException {
/* 161 */     this.counter += 8L;
/* 162 */     return this.input.readDouble();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String readLine() throws IOException {
/* 167 */     String result = this.input.readLine();
/* 168 */     if (result != null) {
/* 169 */       this.counter += (result.length() + 1);
/*     */     }
/* 171 */     return result;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String readUTF() throws IOException {
/* 176 */     String result = this.input.readUTF();
/* 177 */     this.counter += result.length() * 2L + 2L;
/* 178 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 183 */     exit();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\TrackingDataInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */