/*    */ package ac.grim.grimac.shaded.kyori.examination;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ public interface Examiner<R>
/*    */ {
/*    */   @NotNull
/*    */   default R examine(@NotNull Examinable examinable) {
/* 45 */     return examine(examinable.examinableName(), examinable.examinableProperties());
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   R examine(@NotNull String paramString, @NotNull Stream<? extends ExaminableProperty> paramStream);
/*    */   
/*    */   @NotNull
/*    */   R examine(@Nullable Object paramObject);
/*    */   
/*    */   @NotNull
/*    */   R examine(boolean paramBoolean);
/*    */   
/*    */   @NotNull
/*    */   R examine(boolean[] paramArrayOfboolean);
/*    */   
/*    */   @NotNull
/*    */   R examine(byte paramByte);
/*    */   
/*    */   @NotNull
/*    */   R examine(byte[] paramArrayOfbyte);
/*    */   
/*    */   @NotNull
/*    */   R examine(char paramChar);
/*    */   
/*    */   @NotNull
/*    */   R examine(char[] paramArrayOfchar);
/*    */   
/*    */   @NotNull
/*    */   R examine(double paramDouble);
/*    */   
/*    */   @NotNull
/*    */   R examine(double[] paramArrayOfdouble);
/*    */   
/*    */   @NotNull
/*    */   R examine(float paramFloat);
/*    */   
/*    */   @NotNull
/*    */   R examine(float[] paramArrayOffloat);
/*    */   
/*    */   @NotNull
/*    */   R examine(int paramInt);
/*    */   
/*    */   @NotNull
/*    */   R examine(int[] paramArrayOfint);
/*    */   
/*    */   @NotNull
/*    */   R examine(long paramLong);
/*    */   
/*    */   @NotNull
/*    */   R examine(long[] paramArrayOflong);
/*    */   
/*    */   @NotNull
/*    */   R examine(short paramShort);
/*    */   
/*    */   @NotNull
/*    */   R examine(short[] paramArrayOfshort);
/*    */   
/*    */   @NotNull
/*    */   R examine(@Nullable String paramString);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\examination\Examiner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */