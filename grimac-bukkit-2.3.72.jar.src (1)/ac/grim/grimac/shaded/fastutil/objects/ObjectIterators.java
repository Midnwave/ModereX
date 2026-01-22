/*      */ package ac.grim.grimac.shaded.fastutil.objects;
/*      */ 
/*      */ import ac.grim.grimac.shaded.fastutil.BigArrays;
/*      */ import java.io.Serializable;
/*      */ import java.util.Iterator;
/*      */ import java.util.ListIterator;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Objects;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.function.Predicate;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ObjectIterators
/*      */ {
/*      */   public static class EmptyIterator<K>
/*      */     implements ObjectListIterator<K>, Serializable, Cloneable
/*      */   {
/*      */     private static final long serialVersionUID = -7046029254386353129L;
/*      */     
/*      */     public boolean hasNext() {
/*   52 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasPrevious() {
/*   57 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     public K next() {
/*   62 */       throw new NoSuchElementException();
/*      */     }
/*      */ 
/*      */     
/*      */     public K previous() {
/*   67 */       throw new NoSuchElementException();
/*      */     }
/*      */ 
/*      */     
/*      */     public int nextIndex() {
/*   72 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*      */     public int previousIndex() {
/*   77 */       return -1;
/*      */     }
/*      */ 
/*      */     
/*      */     public int skip(int n) {
/*   82 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*      */     public int back(int n) {
/*   87 */       return 0;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> action) {}
/*      */ 
/*      */     
/*      */     public Object clone() {
/*   96 */       return ObjectIterators.EMPTY_ITERATOR;
/*      */     }
/*      */     
/*      */     private Object readResolve() {
/*  100 */       return ObjectIterators.EMPTY_ITERATOR;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  112 */   public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectIterator<K> emptyIterator() {
/*  128 */     return EMPTY_ITERATOR;
/*      */   }
/*      */   
/*      */   private static class SingletonIterator<K>
/*      */     implements ObjectListIterator<K> {
/*      */     private final K element;
/*      */     private byte curr;
/*      */     
/*      */     public SingletonIterator(K element) {
/*  137 */       this.element = element;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/*  142 */       return (this.curr == 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasPrevious() {
/*  147 */       return (this.curr == 1);
/*      */     }
/*      */ 
/*      */     
/*      */     public K next() {
/*  152 */       if (!hasNext()) throw new NoSuchElementException(); 
/*  153 */       this.curr = 1;
/*  154 */       return this.element;
/*      */     }
/*      */ 
/*      */     
/*      */     public K previous() {
/*  159 */       if (!hasPrevious()) throw new NoSuchElementException(); 
/*  160 */       this.curr = 0;
/*  161 */       return this.element;
/*      */     }
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> action) {
/*  166 */       Objects.requireNonNull(action);
/*  167 */       if (this.curr == 0) {
/*  168 */         action.accept(this.element);
/*  169 */         this.curr = 1;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public int nextIndex() {
/*  175 */       return this.curr;
/*      */     }
/*      */ 
/*      */     
/*      */     public int previousIndex() {
/*  180 */       return this.curr - 1;
/*      */     }
/*      */ 
/*      */     
/*      */     public int back(int n) {
/*  185 */       if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
/*  186 */       if (n == 0 || this.curr < 1) return 0; 
/*  187 */       this.curr = 1;
/*  188 */       return 1;
/*      */     }
/*      */ 
/*      */     
/*      */     public int skip(int n) {
/*  193 */       if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
/*  194 */       if (n == 0 || this.curr > 0) return 0; 
/*  195 */       this.curr = 0;
/*  196 */       return 1;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectListIterator<K> singleton(K element) {
/*  207 */     return new SingletonIterator<>(element);
/*      */   }
/*      */   
/*      */   private static class ArrayIterator<K> implements ObjectListIterator<K> {
/*      */     private final K[] array;
/*      */     private final int offset;
/*      */     private final int length;
/*      */     private int curr;
/*      */     
/*      */     public ArrayIterator(K[] array, int offset, int length) {
/*  217 */       this.array = array;
/*  218 */       this.offset = offset;
/*  219 */       this.length = length;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/*  224 */       return (this.curr < this.length);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasPrevious() {
/*  229 */       return (this.curr > 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public K next() {
/*  234 */       if (!hasNext()) throw new NoSuchElementException(); 
/*  235 */       return this.array[this.offset + this.curr++];
/*      */     }
/*      */ 
/*      */     
/*      */     public K previous() {
/*  240 */       if (!hasPrevious()) throw new NoSuchElementException(); 
/*  241 */       return this.array[this.offset + --this.curr];
/*      */     }
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> action) {
/*  246 */       Objects.requireNonNull(action);
/*  247 */       K[] array = this.array;
/*  248 */       for (; this.curr < this.length; this.curr++) {
/*  249 */         action.accept(array[this.offset + this.curr]);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public int skip(int n) {
/*  255 */       if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
/*  256 */       if (n <= this.length - this.curr) {
/*  257 */         this.curr += n;
/*  258 */         return n;
/*      */       } 
/*  260 */       n = this.length - this.curr;
/*  261 */       this.curr = this.length;
/*  262 */       return n;
/*      */     }
/*      */ 
/*      */     
/*      */     public int back(int n) {
/*  267 */       if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
/*  268 */       if (n <= this.curr) {
/*  269 */         this.curr -= n;
/*  270 */         return n;
/*      */       } 
/*  272 */       n = this.curr;
/*  273 */       this.curr = 0;
/*  274 */       return n;
/*      */     }
/*      */ 
/*      */     
/*      */     public int nextIndex() {
/*  279 */       return this.curr;
/*      */     }
/*      */ 
/*      */     
/*      */     public int previousIndex() {
/*  284 */       return this.curr - 1;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectListIterator<K> wrap(K[] array, int offset, int length) {
/*  303 */     ObjectArrays.ensureOffsetLength(array, offset, length);
/*  304 */     return new ArrayIterator<>(array, offset, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectListIterator<K> wrap(K[] array) {
/*  318 */     return new ArrayIterator<>(array, 0, array.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> int unwrap(Iterator<? extends K> i, K[] array, int offset, int max) {
/*  337 */     if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative"); 
/*  338 */     if (offset < 0 || offset + max > array.length) throw new IllegalArgumentException(); 
/*  339 */     int j = max;
/*  340 */     for (; j-- != 0 && i.hasNext(); array[offset++] = i.next());
/*  341 */     return max - j - 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> int unwrap(Iterator<? extends K> i, K[] array) {
/*  357 */     return unwrap(i, array, 0, array.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> K[] unwrap(Iterator<? extends K> i, int max) {
/*  373 */     if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative"); 
/*  374 */     K[] array = (K[])new Object[16];
/*  375 */     int j = 0;
/*  376 */     while (max-- != 0 && i.hasNext()) {
/*  377 */       if (j == array.length) array = ObjectArrays.grow(array, j + 1); 
/*  378 */       array[j++] = i.next();
/*      */     } 
/*  380 */     return ObjectArrays.trim(array, j);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> K[] unwrap(Iterator<? extends K> i) {
/*  394 */     return unwrap(i, 2147483647);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> long unwrap(Iterator<? extends K> i, K[][] array, long offset, long max) {
/*      */     // Byte code:
/*      */     //   0: lload #4
/*      */     //   2: lconst_0
/*      */     //   3: lcmp
/*      */     //   4: ifge -> 40
/*      */     //   7: new java/lang/IllegalArgumentException
/*      */     //   10: dup
/*      */     //   11: new java/lang/StringBuilder
/*      */     //   14: dup
/*      */     //   15: invokespecial <init> : ()V
/*      */     //   18: ldc 'The maximum number of elements ('
/*      */     //   20: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   23: lload #4
/*      */     //   25: invokevirtual append : (J)Ljava/lang/StringBuilder;
/*      */     //   28: ldc ') is negative'
/*      */     //   30: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   33: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   36: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   39: athrow
/*      */     //   40: lload_2
/*      */     //   41: lconst_0
/*      */     //   42: lcmp
/*      */     //   43: iflt -> 58
/*      */     //   46: lload_2
/*      */     //   47: lload #4
/*      */     //   49: ladd
/*      */     //   50: aload_1
/*      */     //   51: invokestatic length : ([[Ljava/lang/Object;)J
/*      */     //   54: lcmp
/*      */     //   55: ifle -> 66
/*      */     //   58: new java/lang/IllegalArgumentException
/*      */     //   61: dup
/*      */     //   62: invokespecial <init> : ()V
/*      */     //   65: athrow
/*      */     //   66: lload #4
/*      */     //   68: lstore #6
/*      */     //   70: lload #6
/*      */     //   72: dup2
/*      */     //   73: lconst_1
/*      */     //   74: lsub
/*      */     //   75: lstore #6
/*      */     //   77: lconst_0
/*      */     //   78: lcmp
/*      */     //   79: ifeq -> 109
/*      */     //   82: aload_0
/*      */     //   83: invokeinterface hasNext : ()Z
/*      */     //   88: ifeq -> 109
/*      */     //   91: aload_1
/*      */     //   92: lload_2
/*      */     //   93: dup2
/*      */     //   94: lconst_1
/*      */     //   95: ladd
/*      */     //   96: lstore_2
/*      */     //   97: aload_0
/*      */     //   98: invokeinterface next : ()Ljava/lang/Object;
/*      */     //   103: invokestatic set : ([[Ljava/lang/Object;JLjava/lang/Object;)V
/*      */     //   106: goto -> 70
/*      */     //   109: lload #4
/*      */     //   111: lload #6
/*      */     //   113: lsub
/*      */     //   114: lconst_1
/*      */     //   115: lsub
/*      */     //   116: lreturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #413	-> 0
/*      */     //   #414	-> 40
/*      */     //   #415	-> 66
/*      */     //   #416	-> 70
/*      */     //   #417	-> 109
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   0	117	0	i	Ljava/util/Iterator;
/*      */     //   0	117	1	array	[[Ljava/lang/Object;
/*      */     //   0	117	2	offset	J
/*      */     //   0	117	4	max	J
/*      */     //   70	47	6	j	J
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	117	0	i	Ljava/util/Iterator<+TK;>;
/*      */     //   0	117	1	array	[[TK;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> long unwrap(Iterator<? extends K> i, K[][] array) {
/*  433 */     return unwrap(i, array, 0L, BigArrays.length((Object[][])array));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> int unwrap(Iterator<K> i, ObjectCollection<? super K> c, int max) {
/*  453 */     if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative"); 
/*  454 */     int j = max;
/*  455 */     for (; j-- != 0 && i.hasNext(); c.add(i.next()));
/*  456 */     return max - j - 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> K[][] unwrapBig(Iterator<? extends K> i, long max) {
/*      */     // Byte code:
/*      */     //   0: lload_1
/*      */     //   1: lconst_0
/*      */     //   2: lcmp
/*      */     //   3: ifge -> 38
/*      */     //   6: new java/lang/IllegalArgumentException
/*      */     //   9: dup
/*      */     //   10: new java/lang/StringBuilder
/*      */     //   13: dup
/*      */     //   14: invokespecial <init> : ()V
/*      */     //   17: ldc 'The maximum number of elements ('
/*      */     //   19: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   22: lload_1
/*      */     //   23: invokevirtual append : (J)Ljava/lang/StringBuilder;
/*      */     //   26: ldc ') is negative'
/*      */     //   28: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   31: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   34: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   37: athrow
/*      */     //   38: ldc2_w 16
/*      */     //   41: invokestatic newBigArray : (J)[[Ljava/lang/Object;
/*      */     //   44: astore_3
/*      */     //   45: lconst_0
/*      */     //   46: lstore #4
/*      */     //   48: lload_1
/*      */     //   49: dup2
/*      */     //   50: lconst_1
/*      */     //   51: lsub
/*      */     //   52: lstore_1
/*      */     //   53: lconst_0
/*      */     //   54: lcmp
/*      */     //   55: ifeq -> 106
/*      */     //   58: aload_0
/*      */     //   59: invokeinterface hasNext : ()Z
/*      */     //   64: ifeq -> 106
/*      */     //   67: lload #4
/*      */     //   69: aload_3
/*      */     //   70: invokestatic length : ([[Ljava/lang/Object;)J
/*      */     //   73: lcmp
/*      */     //   74: ifne -> 86
/*      */     //   77: aload_3
/*      */     //   78: lload #4
/*      */     //   80: lconst_1
/*      */     //   81: ladd
/*      */     //   82: invokestatic grow : ([[Ljava/lang/Object;J)[[Ljava/lang/Object;
/*      */     //   85: astore_3
/*      */     //   86: aload_3
/*      */     //   87: lload #4
/*      */     //   89: dup2
/*      */     //   90: lconst_1
/*      */     //   91: ladd
/*      */     //   92: lstore #4
/*      */     //   94: aload_0
/*      */     //   95: invokeinterface next : ()Ljava/lang/Object;
/*      */     //   100: invokestatic set : ([[Ljava/lang/Object;JLjava/lang/Object;)V
/*      */     //   103: goto -> 48
/*      */     //   106: aload_3
/*      */     //   107: lload #4
/*      */     //   109: invokestatic trim : ([[Ljava/lang/Object;J)[[Ljava/lang/Object;
/*      */     //   112: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #472	-> 0
/*      */     //   #473	-> 38
/*      */     //   #474	-> 45
/*      */     //   #475	-> 48
/*      */     //   #476	-> 67
/*      */     //   #477	-> 86
/*      */     //   #479	-> 106
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   0	113	0	i	Ljava/util/Iterator;
/*      */     //   0	113	1	max	J
/*      */     //   45	68	3	array	[[Ljava/lang/Object;
/*      */     //   48	65	4	j	J
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	113	0	i	Ljava/util/Iterator<+TK;>;
/*      */     //   45	68	3	array	[[TK;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> K[][] unwrapBig(Iterator<? extends K> i) {
/*  493 */     return unwrapBig(i, Long.MAX_VALUE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> long unwrap(Iterator<K> i, ObjectCollection<? super K> c) {
/*  511 */     long n = 0L;
/*  512 */     while (i.hasNext()) {
/*  513 */       c.add(i.next());
/*  514 */       n++;
/*      */     } 
/*  516 */     return n;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> int pour(Iterator<K> i, ObjectCollection<? super K> s, int max) {
/*  534 */     if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative"); 
/*  535 */     int j = max;
/*  536 */     for (; j-- != 0 && i.hasNext(); s.add(i.next()));
/*  537 */     return max - j - 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> int pour(Iterator<K> i, ObjectCollection<? super K> s) {
/*  554 */     return pour(i, s, 2147483647);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectList<K> pour(Iterator<K> i, int max) {
/*  571 */     ObjectArrayList<K> l = new ObjectArrayList<>();
/*  572 */     pour(i, l, max);
/*  573 */     l.trim();
/*  574 */     return l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectList<K> pour(Iterator<K> i) {
/*  589 */     return pour(i, 2147483647);
/*      */   }
/*      */   
/*      */   private static class IteratorWrapper<K> implements ObjectIterator<K> {
/*      */     final Iterator<K> i;
/*      */     
/*      */     public IteratorWrapper(Iterator<K> i) {
/*  596 */       this.i = i;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/*  601 */       return this.i.hasNext();
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove() {
/*  606 */       this.i.remove();
/*      */     }
/*      */ 
/*      */     
/*      */     public K next() {
/*  611 */       return this.i.next();
/*      */     }
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> action) {
/*  616 */       this.i.forEachRemaining(action);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectIterator<K> asObjectIterator(Iterator<K> i) {
/*  636 */     if (i instanceof ObjectIterator) return (ObjectIterator<K>)i; 
/*  637 */     return new IteratorWrapper<>(i);
/*      */   }
/*      */   
/*      */   private static class ListIteratorWrapper<K> implements ObjectListIterator<K> {
/*      */     final ListIterator<K> i;
/*      */     
/*      */     public ListIteratorWrapper(ListIterator<K> i) {
/*  644 */       this.i = i;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/*  649 */       return this.i.hasNext();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasPrevious() {
/*  654 */       return this.i.hasPrevious();
/*      */     }
/*      */ 
/*      */     
/*      */     public int nextIndex() {
/*  659 */       return this.i.nextIndex();
/*      */     }
/*      */ 
/*      */     
/*      */     public int previousIndex() {
/*  664 */       return this.i.previousIndex();
/*      */     }
/*      */ 
/*      */     
/*      */     public void set(K k) {
/*  669 */       this.i.set(k);
/*      */     }
/*      */ 
/*      */     
/*      */     public void add(K k) {
/*  674 */       this.i.add(k);
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove() {
/*  679 */       this.i.remove();
/*      */     }
/*      */ 
/*      */     
/*      */     public K next() {
/*  684 */       return this.i.next();
/*      */     }
/*      */ 
/*      */     
/*      */     public K previous() {
/*  689 */       return this.i.previous();
/*      */     }
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> action) {
/*  694 */       this.i.forEachRemaining(action);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectListIterator<K> asObjectIterator(ListIterator<K> i) {
/*  714 */     if (i instanceof ObjectListIterator) return (ObjectListIterator<K>)i; 
/*  715 */     return new ListIteratorWrapper<>(i);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> boolean any(Iterator<K> iterator, Predicate<? super K> predicate) {
/*  727 */     return (indexOf(iterator, predicate) != -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> boolean all(Iterator<K> iterator, Predicate<? super K> predicate) {
/*  739 */     Objects.requireNonNull(predicate);
/*      */     while (true) {
/*  741 */       if (!iterator.hasNext()) return true; 
/*  742 */       if (!predicate.test(iterator.next())) {
/*  743 */         return false;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> int indexOf(Iterator<K> iterator, Predicate<? super K> predicate) {
/*  758 */     Objects.requireNonNull(predicate);
/*  759 */     for (int i = 0; iterator.hasNext(); i++) {
/*  760 */       if (predicate.test(iterator.next())) return i; 
/*      */     } 
/*  762 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class AbstractIndexBasedIterator<K>
/*      */     extends AbstractObjectIterator<K>
/*      */   {
/*      */     protected final int minPos;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected int pos;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected int lastReturned;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected AbstractIndexBasedIterator(int minPos, int initialPos) {
/*  812 */       this.minPos = minPos;
/*  813 */       this.pos = initialPos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected abstract K get(int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected abstract void remove(int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected abstract int getMaxPos();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/*  862 */       return (this.pos < getMaxPos());
/*      */     }
/*      */ 
/*      */     
/*      */     public K next() {
/*  867 */       if (!hasNext()) throw new NoSuchElementException(); 
/*  868 */       return get(this.lastReturned = this.pos++);
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove() {
/*  873 */       if (this.lastReturned == -1) throw new IllegalStateException(); 
/*  874 */       remove(this.lastReturned);
/*      */       
/*  876 */       if (this.lastReturned < this.pos) this.pos--; 
/*  877 */       this.lastReturned = -1;
/*      */     }
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> action) {
/*  882 */       while (this.pos < getMaxPos()) {
/*  883 */         action.accept(get(this.lastReturned = this.pos++));
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int skip(int n) {
/*  891 */       if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
/*  892 */       int max = getMaxPos();
/*  893 */       int remaining = max - this.pos;
/*  894 */       if (n < remaining) {
/*  895 */         this.pos += n;
/*      */       } else {
/*  897 */         n = remaining;
/*  898 */         this.pos = max;
/*      */       } 
/*  900 */       this.lastReturned = this.pos - 1;
/*  901 */       return n;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class AbstractIndexBasedListIterator<K>
/*      */     extends AbstractIndexBasedIterator<K>
/*      */     implements ObjectListIterator<K>
/*      */   {
/*      */     protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
/*  923 */       super(minPos, initialPos);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected abstract void add(int param1Int, K param1K);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected abstract void set(int param1Int, K param1K);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean hasPrevious() {
/*  956 */       return (this.pos > this.minPos);
/*      */     }
/*      */ 
/*      */     
/*      */     public K previous() {
/*  961 */       if (!hasPrevious()) throw new NoSuchElementException(); 
/*  962 */       return get(this.lastReturned = --this.pos);
/*      */     }
/*      */ 
/*      */     
/*      */     public int nextIndex() {
/*  967 */       return this.pos;
/*      */     }
/*      */ 
/*      */     
/*      */     public int previousIndex() {
/*  972 */       return this.pos - 1;
/*      */     }
/*      */ 
/*      */     
/*      */     public void add(K k) {
/*  977 */       add(this.pos++, k);
/*  978 */       this.lastReturned = -1;
/*      */     }
/*      */ 
/*      */     
/*      */     public void set(K k) {
/*  983 */       if (this.lastReturned == -1) throw new IllegalStateException(); 
/*  984 */       set(this.lastReturned, k);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int back(int n) {
/*  991 */       if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
/*  992 */       int remaining = this.pos - this.minPos;
/*  993 */       if (n < remaining) {
/*  994 */         this.pos -= n;
/*      */       } else {
/*  996 */         n = remaining;
/*  997 */         this.pos = this.minPos;
/*      */       } 
/*  999 */       this.lastReturned = this.pos;
/* 1000 */       return n;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class IteratorConcatenator<K> implements ObjectIterator<K> {
/*      */     final ObjectIterator<? extends K>[] a;
/* 1006 */     int lastOffset = -1; int offset;
/*      */     
/*      */     public IteratorConcatenator(ObjectIterator<? extends K>[] a, int offset, int length) {
/* 1009 */       this.a = a;
/* 1010 */       this.offset = offset;
/* 1011 */       this.length = length;
/* 1012 */       advance();
/*      */     }
/*      */     int length;
/*      */     private void advance() {
/* 1016 */       while (this.length != 0 && 
/* 1017 */         !this.a[this.offset].hasNext()) {
/* 1018 */         this.length--;
/* 1019 */         this.offset++;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/* 1026 */       return (this.length > 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public K next() {
/* 1031 */       if (!hasNext()) throw new NoSuchElementException(); 
/* 1032 */       K next = this.a[this.lastOffset = this.offset].next();
/* 1033 */       advance();
/* 1034 */       return next;
/*      */     }
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> action) {
/* 1039 */       while (this.length > 0) {
/* 1040 */         this.a[this.lastOffset = this.offset].forEachRemaining(action);
/* 1041 */         advance();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove() {
/* 1047 */       if (this.lastOffset == -1) throw new IllegalStateException(); 
/* 1048 */       this.a[this.lastOffset].remove();
/*      */     }
/*      */ 
/*      */     
/*      */     public int skip(int n) {
/* 1053 */       if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
/* 1054 */       this.lastOffset = -1;
/* 1055 */       int skipped = 0;
/* 1056 */       while (skipped < n && this.length != 0) {
/* 1057 */         skipped += this.a[this.offset].skip(n - skipped);
/* 1058 */         if (this.a[this.offset].hasNext())
/* 1059 */           break;  this.length--;
/* 1060 */         this.offset++;
/*      */       } 
/* 1062 */       return skipped;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @SafeVarargs
/*      */   public static <K> ObjectIterator<K> concat(ObjectIterator<? extends K>... a) {
/* 1078 */     return concat(a, 0, a.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectIterator<K> concat(ObjectIterator<? extends K>[] a, int offset, int length) {
/* 1096 */     return new IteratorConcatenator<>(a, offset, length);
/*      */   }
/*      */   
/*      */   public static class UnmodifiableIterator<K>
/*      */     implements ObjectIterator<K> {
/*      */     protected final ObjectIterator<? extends K> i;
/*      */     
/*      */     public UnmodifiableIterator(ObjectIterator<? extends K> i) {
/* 1104 */       this.i = i;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/* 1109 */       return this.i.hasNext();
/*      */     }
/*      */ 
/*      */     
/*      */     public K next() {
/* 1114 */       return this.i.next();
/*      */     }
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> action) {
/* 1119 */       this.i.forEachRemaining(action);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectIterator<K> unmodifiable(ObjectIterator<? extends K> i) {
/* 1130 */     return new UnmodifiableIterator<>(i);
/*      */   }
/*      */   
/*      */   public static class UnmodifiableBidirectionalIterator<K>
/*      */     implements ObjectBidirectionalIterator<K> {
/*      */     protected final ObjectBidirectionalIterator<? extends K> i;
/*      */     
/*      */     public UnmodifiableBidirectionalIterator(ObjectBidirectionalIterator<? extends K> i) {
/* 1138 */       this.i = i;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/* 1143 */       return this.i.hasNext();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasPrevious() {
/* 1148 */       return this.i.hasPrevious();
/*      */     }
/*      */ 
/*      */     
/*      */     public K next() {
/* 1153 */       return this.i.next();
/*      */     }
/*      */ 
/*      */     
/*      */     public K previous() {
/* 1158 */       return (K)this.i.previous();
/*      */     }
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> action) {
/* 1163 */       this.i.forEachRemaining(action);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectBidirectionalIterator<K> unmodifiable(ObjectBidirectionalIterator<? extends K> i) {
/* 1174 */     return new UnmodifiableBidirectionalIterator<>(i);
/*      */   }
/*      */   
/*      */   public static class UnmodifiableListIterator<K>
/*      */     implements ObjectListIterator<K> {
/*      */     protected final ObjectListIterator<? extends K> i;
/*      */     
/*      */     public UnmodifiableListIterator(ObjectListIterator<? extends K> i) {
/* 1182 */       this.i = i;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/* 1187 */       return this.i.hasNext();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasPrevious() {
/* 1192 */       return this.i.hasPrevious();
/*      */     }
/*      */ 
/*      */     
/*      */     public K next() {
/* 1197 */       return this.i.next();
/*      */     }
/*      */ 
/*      */     
/*      */     public K previous() {
/* 1202 */       return (K)this.i.previous();
/*      */     }
/*      */ 
/*      */     
/*      */     public int nextIndex() {
/* 1207 */       return this.i.nextIndex();
/*      */     }
/*      */ 
/*      */     
/*      */     public int previousIndex() {
/* 1212 */       return this.i.previousIndex();
/*      */     }
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> action) {
/* 1217 */       this.i.forEachRemaining(action);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K> ObjectListIterator<K> unmodifiable(ObjectListIterator<? extends K> i) {
/* 1228 */     return new UnmodifiableListIterator<>(i);
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\fastutil\objects\ObjectIterators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */