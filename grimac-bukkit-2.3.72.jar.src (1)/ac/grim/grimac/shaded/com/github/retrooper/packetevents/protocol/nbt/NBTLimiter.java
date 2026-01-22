/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ @NullMarked
/*    */ @NonExtendable
/*    */ public interface NBTLimiter
/*    */ {
/* 28 */   public static final int DEFAULT_MAX_SIZE = Integer.getInteger("packetevents.nbt.default-max-size", 1048576).intValue();
/* 29 */   public static final int DEFAULT_MAX_DEPTH = Integer.getInteger("packetevents.nbt.default-max-depth", 512).intValue();
/*    */   
/*    */   static NBTLimiter noop() {
/* 32 */     return new NBTLimiter()
/*    */       {
/*    */         public void increment(int amount) {}
/*    */ 
/*    */ 
/*    */ 
/*    */         
/*    */         public void checkReadability(int length) {}
/*    */ 
/*    */ 
/*    */ 
/*    */         
/*    */         public void enterDepth() {}
/*    */ 
/*    */ 
/*    */ 
/*    */         
/*    */         public void exitDepth() {}
/*    */       };
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   static NBTLimiter forBuffer(Object byteBuf) {
/* 56 */     return forBuffer(byteBuf, DEFAULT_MAX_SIZE);
/*    */   }
/*    */   
/*    */   static NBTLimiter forBuffer(Object byteBuf, int maxBytes) {
/* 60 */     return forBuffer(byteBuf, maxBytes, DEFAULT_MAX_DEPTH);
/*    */   }
/*    */   
/*    */   static NBTLimiter forBuffer(final Object byteBuf, final int maxBytes, final int maxDepth) {
/* 64 */     return new NBTLimiter()
/*    */       {
/*    */         private int bytes;
/*    */         private int depth;
/*    */         
/*    */         public void increment(int amount) {
/* 70 */           if (amount < 0)
/* 71 */             throw new IllegalArgumentException("Can't increment NBT limiter by negative amount: " + amount); 
/* 72 */           if (this.bytes + amount > maxBytes) {
/* 73 */             throw new IllegalArgumentException("NBT size limit reached (" + this.bytes + "/" + maxBytes + ")");
/*    */           }
/* 75 */           this.bytes += amount;
/*    */         }
/*    */ 
/*    */         
/*    */         public void checkReadability(int length) {
/* 80 */           int readableBytes = ByteBufHelper.readableBytes(byteBuf);
/* 81 */           if (length > readableBytes) {
/* 82 */             throw new IllegalArgumentException("Can't read more than possible: " + length + " > " + readableBytes);
/*    */           }
/*    */         }
/*    */ 
/*    */         
/*    */         public void enterDepth() {
/* 88 */           if (this.depth >= maxDepth) {
/* 89 */             throw new IllegalArgumentException("NBT depth limit reached (" + this.depth + "/" + maxDepth + ")");
/*    */           }
/* 91 */           this.depth++;
/*    */         }
/*    */ 
/*    */         
/*    */         public void exitDepth() {
/* 96 */           if (this.depth <= 0) {
/* 97 */             throw new IllegalArgumentException("Can't exit top-level depth");
/*    */           }
/* 99 */           this.depth--;
/*    */         }
/*    */       };
/*    */   }
/*    */   
/*    */   void increment(int paramInt);
/*    */   
/*    */   void checkReadability(int paramInt);
/*    */   
/*    */   void enterDepth();
/*    */   
/*    */   void exitDepth();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\NBTLimiter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */