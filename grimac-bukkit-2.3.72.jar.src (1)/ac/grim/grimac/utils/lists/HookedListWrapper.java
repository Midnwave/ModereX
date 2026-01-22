/*     */ package ac.grim.grimac.utils.lists;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ 
/*     */ 
/*     */ public abstract class HookedListWrapper<T>
/*     */   extends ListWrapper<T>
/*     */ {
/*     */   public HookedListWrapper(List<T> base) {
/*  14 */     super(base);
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract void onIterator();
/*     */ 
/*     */   
/*     */   public int size() {
/*  22 */     return this.base.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  27 */     return this.base.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(Object o) {
/*  32 */     return this.base.contains(o);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Iterator<T> iterator() {
/*  37 */     onIterator();
/*  38 */     return listIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object[] toArray() {
/*  43 */     return this.base.toArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(T o) {
/*  48 */     return this.base.add(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/*  53 */     return this.base.remove(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(@NotNull Collection<? extends T> c) {
/*  58 */     return this.base.addAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(int index, @NotNull Collection<? extends T> c) {
/*  63 */     return this.base.addAll(index, c);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/*  68 */     this.base.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public T get(int index) {
/*  73 */     return this.base.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public T set(int index, T element) {
/*  78 */     return this.base.set(index, element);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, T element) {
/*  83 */     this.base.add(index, element);
/*     */   }
/*     */ 
/*     */   
/*     */   public T remove(int index) {
/*  88 */     return this.base.remove(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int indexOf(Object o) {
/*  93 */     return this.base.indexOf(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public int lastIndexOf(Object o) {
/*  98 */     return this.base.lastIndexOf(o);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ListIterator<T> listIterator() {
/* 103 */     return this.base.listIterator();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ListIterator<T> listIterator(int index) {
/* 108 */     return this.base.listIterator(index);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public List<T> subList(int fromIndex, int toIndex) {
/* 113 */     return this.base.subList(fromIndex, toIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean retainAll(@NotNull Collection<?> c) {
/* 118 */     return this.base.retainAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean removeAll(@NotNull Collection<?> c) {
/* 123 */     return this.base.removeAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsAll(@NotNull Collection<?> c) {
/* 128 */     return this.base.containsAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object[] toArray(Object[] a) {
/* 133 */     return this.base.toArray(a);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\lists\HookedListWrapper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */