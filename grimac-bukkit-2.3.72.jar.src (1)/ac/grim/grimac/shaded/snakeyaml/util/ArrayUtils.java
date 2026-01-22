/*     */ package ac.grim.grimac.shaded.snakeyaml.util;
/*     */ 
/*     */ import java.util.AbstractList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ArrayUtils
/*     */ {
/*     */   public static <E> List<E> toUnmodifiableList(E[] elements) {
/*  36 */     return (elements.length == 0) ? Collections.<E>emptyList() : 
/*  37 */       new UnmodifiableArrayList<>(elements);
/*     */   }
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
/*     */   public static <E> List<E> toUnmodifiableCompositeList(E[] array1, E[] array2) {
/*     */     List<E> result;
/*  52 */     if (array1.length == 0) {
/*  53 */       result = toUnmodifiableList(array2);
/*  54 */     } else if (array2.length == 0) {
/*  55 */       result = toUnmodifiableList(array1);
/*     */     } else {
/*  57 */       result = new CompositeUnmodifiableArrayList<>(array1, array2);
/*     */     } 
/*  59 */     return result;
/*     */   }
/*     */   
/*     */   private static class UnmodifiableArrayList<E>
/*     */     extends AbstractList<E> {
/*     */     private final E[] array;
/*     */     
/*     */     UnmodifiableArrayList(E[] array) {
/*  67 */       this.array = array;
/*     */     }
/*     */ 
/*     */     
/*     */     public E get(int index) {
/*  72 */       if (index >= this.array.length) {
/*  73 */         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
/*     */       }
/*  75 */       return this.array[index];
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/*  80 */       return this.array.length;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class CompositeUnmodifiableArrayList<E>
/*     */     extends AbstractList<E> {
/*     */     private final E[] array1;
/*     */     private final E[] array2;
/*     */     
/*     */     CompositeUnmodifiableArrayList(E[] array1, E[] array2) {
/*  90 */       this.array1 = array1;
/*  91 */       this.array2 = array2;
/*     */     }
/*     */ 
/*     */     
/*     */     public E get(int index) {
/*     */       E element;
/*  97 */       if (index < this.array1.length) {
/*  98 */         element = this.array1[index];
/*  99 */       } else if (index - this.array1.length < this.array2.length) {
/* 100 */         element = this.array2[index - this.array1.length];
/*     */       } else {
/* 102 */         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
/*     */       } 
/* 104 */       return element;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 109 */       return this.array1.length + this.array2.length;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyam\\util\ArrayUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */