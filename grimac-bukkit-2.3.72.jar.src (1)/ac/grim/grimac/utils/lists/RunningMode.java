/*    */ package ac.grim.grimac.utils.lists;
/*    */ 
/*    */ import ac.grim.grimac.shaded.fastutil.doubles.Double2IntMap;
/*    */ import ac.grim.grimac.shaded.fastutil.doubles.Double2IntOpenHashMap;
/*    */ import ac.grim.grimac.shaded.fastutil.objects.ObjectIterator;
/*    */ import ac.grim.grimac.utils.data.Pair;
/*    */ import java.util.Queue;
/*    */ import java.util.concurrent.ArrayBlockingQueue;
/*    */ import lombok.Generated;
/*    */ 
/*    */ 
/*    */ public class RunningMode
/*    */ {
/*    */   private static final double threshold = 0.001D;
/*    */   private final Queue<Double> addList;
/*    */   private final int maxSize;
/* 17 */   private final Double2IntMap popularityMap = (Double2IntMap)new Double2IntOpenHashMap(); @Generated
/* 18 */   public int getMaxSize() { return this.maxSize; }
/*    */   
/*    */   public RunningMode(int maxSize) {
/* 21 */     if (maxSize == 0) throw new IllegalArgumentException("There's no mode to a size 0 list!"); 
/* 22 */     this.addList = new ArrayBlockingQueue<>(maxSize);
/* 23 */     this.maxSize = maxSize;
/*    */   }
/*    */   
/*    */   public int size() {
/* 27 */     return this.addList.size();
/*    */   }
/*    */   
/*    */   public void add(double value) {
/* 31 */     pop();
/*    */     
/* 33 */     for (ObjectIterator<Double2IntMap.Entry> objectIterator = this.popularityMap.double2IntEntrySet().iterator(); objectIterator.hasNext(); ) { Double2IntMap.Entry entry = objectIterator.next();
/* 34 */       if (Math.abs(entry.getDoubleKey() - value) < 0.001D) {
/* 35 */         entry.setValue(entry.getIntValue() + 1);
/* 36 */         this.addList.add(Double.valueOf(entry.getDoubleKey()));
/*    */         
/*    */         return;
/*    */       }  }
/*    */ 
/*    */     
/* 42 */     this.popularityMap.put(value, 1);
/* 43 */     this.addList.add(Double.valueOf(value));
/*    */   }
/*    */   
/*    */   private void pop() {
/* 47 */     if (this.addList.size() >= this.maxSize) {
/* 48 */       double type = ((Double)this.addList.remove()).doubleValue();
/* 49 */       int popularity = this.popularityMap.get(type);
/* 50 */       if (popularity == 1) {
/* 51 */         this.popularityMap.remove(type);
/*    */       } else {
/* 53 */         this.popularityMap.put(type, popularity - 1);
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public Pair<Double, Integer> getMode() {
/* 59 */     int max = 0;
/* 60 */     Double mostPopular = null;
/*    */     
/* 62 */     for (ObjectIterator<Double2IntMap.Entry> objectIterator = this.popularityMap.double2IntEntrySet().iterator(); objectIterator.hasNext(); ) { Double2IntMap.Entry entry = objectIterator.next();
/* 63 */       if (entry.getIntValue() > max) {
/* 64 */         max = entry.getIntValue();
/* 65 */         mostPopular = Double.valueOf(entry.getDoubleKey());
/*    */       }  }
/*    */ 
/*    */     
/* 69 */     return new Pair(mostPopular, Integer.valueOf(max));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\lists\RunningMode.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */