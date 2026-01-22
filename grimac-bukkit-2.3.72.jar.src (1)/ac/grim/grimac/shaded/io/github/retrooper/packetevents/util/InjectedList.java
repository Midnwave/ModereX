/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.function.Consumer;
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
/*     */ public class InjectedList<E>
/*     */   implements List<E>
/*     */ {
/*     */   private final List<E> originalList;
/*     */   private final Consumer<E> pushBackAction;
/*     */   
/*     */   public InjectedList(List<E> originalList, Consumer<E> pushBackAction) {
/*  34 */     for (E key : originalList) {
/*  35 */       pushBackAction.accept(key);
/*     */     }
/*  37 */     this.originalList = originalList;
/*  38 */     this.pushBackAction = pushBackAction;
/*     */   }
/*     */   
/*     */   public List<E> originalList() {
/*  42 */     return this.originalList;
/*     */   }
/*     */   
/*     */   public Consumer<E> pushBackAction() {
/*  46 */     return this.pushBackAction;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean add(E e) {
/*  51 */     this.pushBackAction.accept(e);
/*  52 */     return this.originalList.add(e);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean addAll(@NotNull Collection<? extends E> c) {
/*  57 */     for (E element : c) {
/*  58 */       this.pushBackAction.accept(element);
/*     */     }
/*  60 */     return this.originalList.addAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean addAll(int index, @NotNull Collection<? extends E> c) {
/*  65 */     for (E element : c) {
/*  66 */       this.pushBackAction.accept(element);
/*     */     }
/*  68 */     return this.originalList.addAll(index, c);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void add(int index, E element) {
/*  73 */     this.pushBackAction.accept(element);
/*  74 */     this.originalList.add(index, element);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized int size() {
/*  79 */     return this.originalList.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean isEmpty() {
/*  84 */     return this.originalList.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean contains(Object o) {
/*  89 */     return this.originalList.contains(o);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public synchronized Iterator<E> iterator() {
/*  95 */     return this.originalList.iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public synchronized Object[] toArray() {
/* 101 */     return this.originalList.toArray();
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public synchronized <T> T[] toArray(@NotNull T[] a) {
/* 107 */     return this.originalList.toArray(a);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean remove(Object o) {
/* 112 */     return this.originalList.remove(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean containsAll(@NotNull Collection<?> c) {
/* 117 */     return this.originalList.containsAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean removeAll(@NotNull Collection<?> c) {
/* 122 */     return this.originalList.removeAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean retainAll(@NotNull Collection<?> c) {
/* 127 */     return this.originalList.retainAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void clear() {
/* 132 */     this.originalList.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized E get(int index) {
/* 137 */     return this.originalList.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized E set(int index, E element) {
/* 142 */     return this.originalList.set(index, element);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized E remove(int index) {
/* 147 */     return this.originalList.remove(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized int indexOf(Object o) {
/* 152 */     return this.originalList.indexOf(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized int lastIndexOf(Object o) {
/* 157 */     return this.originalList.lastIndexOf(o);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public synchronized ListIterator<E> listIterator() {
/* 163 */     return this.originalList.listIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public synchronized ListIterator<E> listIterator(int index) {
/* 169 */     return this.originalList.listIterator(index);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public synchronized List<E> subList(int fromIndex, int toIndex) {
/* 175 */     return this.originalList.subList(fromIndex, toIndex);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\InjectedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */