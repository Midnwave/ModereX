/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Unmodifiable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.Spliterators;
/*     */ import java.util.stream.Stream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Unmodifiable
/*     */ final class DecorationMap
/*     */   extends AbstractMap<TextDecoration, TextDecoration.State>
/*     */   implements Examinable
/*     */ {
/*  82 */   static final TextDecoration[] DECORATIONS = TextDecoration.values();
/*  83 */   private static final TextDecoration.State[] STATES = TextDecoration.State.values();
/*  84 */   private static final int MAP_SIZE = DECORATIONS.length;
/*  85 */   private static final TextDecoration.State[] EMPTY_STATE_ARRAY = new TextDecoration.State[0];
/*     */   
/*  87 */   static final DecorationMap EMPTY = new DecorationMap(0);
/*     */   
/*  89 */   private static final KeySet KEY_SET = new KeySet();
/*     */   
/*     */   static DecorationMap fromMap(Map<TextDecoration, TextDecoration.State> decorationMap) {
/*  92 */     if (decorationMap instanceof DecorationMap) return (DecorationMap)decorationMap; 
/*  93 */     int bitSet = 0;
/*  94 */     for (TextDecoration decoration : DECORATIONS) {
/*  95 */       bitSet |= ((TextDecoration.State)decorationMap.getOrDefault(decoration, TextDecoration.State.NOT_SET)).ordinal() * offset(decoration);
/*     */     }
/*  97 */     return withBitSet(bitSet);
/*     */   }
/*     */   private final int bitSet;
/*     */   static DecorationMap merge(Map<TextDecoration, TextDecoration.State> first, Map<TextDecoration, TextDecoration.State> second) {
/* 101 */     int bitSet = 0;
/* 102 */     for (TextDecoration decoration : DECORATIONS) {
/* 103 */       bitSet |= ((TextDecoration.State)first.getOrDefault(decoration, second.getOrDefault(decoration, TextDecoration.State.NOT_SET))).ordinal() * offset(decoration);
/*     */     }
/* 105 */     return withBitSet(bitSet);
/*     */   }
/*     */ 
/*     */   
/*     */   private static DecorationMap withBitSet(int bitSet) {
/* 110 */     return (bitSet == 0) ? EMPTY : new DecorationMap(bitSet);
/*     */   }
/*     */ 
/*     */   
/*     */   private static int offset(TextDecoration decoration) {
/* 115 */     return 1 << decoration.ordinal() * 2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   private volatile EntrySet entrySet = null;
/* 122 */   private volatile Values values = null;
/*     */   
/*     */   private DecorationMap(int bitSet) {
/* 125 */     this.bitSet = bitSet;
/*     */   }
/*     */   @NotNull
/*     */   public DecorationMap with(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 129 */     Objects.requireNonNull(state, "state");
/* 130 */     Objects.requireNonNull(decoration, "decoration");
/* 131 */     int offset = offset(decoration);
/*     */     
/* 133 */     return withBitSet(this.bitSet & (3 * offset ^ 0xFFFFFFFF) | state.ordinal() * offset);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 138 */     return Arrays.<TextDecoration>stream(DECORATIONS)
/* 139 */       .map(decoration -> ExaminableProperty.of(decoration.toString(), get(decoration)));
/*     */   }
/*     */ 
/*     */   
/*     */   public TextDecoration.State get(Object o) {
/* 144 */     if (o instanceof TextDecoration) {
/* 145 */       return STATES[this.bitSet >> ((TextDecoration)o).ordinal() * 2 & 0x3];
/*     */     }
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 153 */     return key instanceof TextDecoration;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 158 */     return MAP_SIZE;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 163 */     return false;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Set<Map.Entry<TextDecoration, TextDecoration.State>> entrySet() {
/* 168 */     if (this.entrySet == null) {
/* 169 */       synchronized (this) {
/*     */         
/* 171 */         if (this.entrySet == null) {
/* 172 */           this.entrySet = new EntrySet();
/*     */         }
/*     */       } 
/*     */     }
/* 176 */     return this.entrySet;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Set<TextDecoration> keySet() {
/* 181 */     return KEY_SET;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Collection<TextDecoration.State> values() {
/* 186 */     if (this.values == null) {
/* 187 */       synchronized (this) {
/*     */         
/* 189 */         if (this.values == null) {
/* 190 */           this.values = new Values();
/*     */         }
/*     */       } 
/*     */     }
/* 194 */     return this.values;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 199 */     if (other == this) return true; 
/* 200 */     if (other == null || other.getClass() != DecorationMap.class) return false; 
/* 201 */     return (this.bitSet == ((DecorationMap)other).bitSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 206 */     return this.bitSet;
/*     */   }
/*     */   
/*     */   final class EntrySet extends AbstractSet<Map.Entry<TextDecoration, TextDecoration.State>> {
/*     */     @NotNull
/*     */     public Iterator<Map.Entry<TextDecoration, TextDecoration.State>> iterator() {
/* 212 */       return new Iterator<Map.Entry<TextDecoration, TextDecoration.State>>() {
/* 213 */           private final Iterator<TextDecoration> decorations = DecorationMap.KEY_SET.iterator();
/* 214 */           private final Iterator<TextDecoration.State> states = DecorationMap.this.values().iterator();
/*     */ 
/*     */           
/*     */           public boolean hasNext() {
/* 218 */             return (this.decorations.hasNext() && this.states.hasNext());
/*     */           }
/*     */ 
/*     */           
/*     */           public Map.Entry<TextDecoration, TextDecoration.State> next() {
/* 223 */             if (hasNext()) {
/* 224 */               return new AbstractMap.SimpleImmutableEntry<>(this.decorations.next(), this.states.next());
/*     */             }
/* 226 */             throw new NoSuchElementException();
/*     */           }
/*     */         };
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 233 */       return DecorationMap.MAP_SIZE;
/*     */     }
/*     */   }
/*     */   
/*     */   final class Values extends AbstractCollection<TextDecoration.State> {
/*     */     @NotNull
/*     */     public Iterator<TextDecoration.State> iterator() {
/* 240 */       return Spliterators.iterator(Arrays.spliterator(toArray(DecorationMap.EMPTY_STATE_ARRAY)));
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 245 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object[] toArray() {
/* 250 */       Object[] states = new Object[DecorationMap.MAP_SIZE];
/* 251 */       for (int i = 0; i < DecorationMap.MAP_SIZE; i++) {
/* 252 */         states[i] = DecorationMap.this.get(DecorationMap.DECORATIONS[i]);
/*     */       }
/* 254 */       return states;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> T[] toArray(T[] dest) {
/* 260 */       if (dest.length < DecorationMap.MAP_SIZE) {
/* 261 */         return Arrays.copyOf(toArray(), DecorationMap.MAP_SIZE, (Class)dest.getClass());
/*     */       }
/* 263 */       System.arraycopy(toArray(), 0, dest, 0, DecorationMap.MAP_SIZE);
/* 264 */       if (dest.length > DecorationMap.MAP_SIZE) {
/* 265 */         dest[DecorationMap.MAP_SIZE] = null;
/*     */       }
/* 267 */       return dest;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean contains(Object o) {
/* 272 */       return (o instanceof TextDecoration.State && super.contains(o));
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 277 */       return DecorationMap.MAP_SIZE;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class KeySet
/*     */     extends AbstractSet<TextDecoration>
/*     */   {
/*     */     public boolean contains(Object o) {
/* 285 */       return o instanceof TextDecoration;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 290 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object[] toArray() {
/* 295 */       return Arrays.copyOf(DecorationMap.DECORATIONS, DecorationMap.MAP_SIZE, Object[].class);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> T[] toArray(T[] dest) {
/* 301 */       if (dest.length < DecorationMap.MAP_SIZE) {
/* 302 */         return Arrays.copyOf(DecorationMap.DECORATIONS, DecorationMap.MAP_SIZE, (Class)dest.getClass());
/*     */       }
/* 304 */       System.arraycopy(DecorationMap.DECORATIONS, 0, dest, 0, DecorationMap.MAP_SIZE);
/* 305 */       if (dest.length > DecorationMap.MAP_SIZE) {
/* 306 */         dest[DecorationMap.MAP_SIZE] = null;
/*     */       }
/* 308 */       return dest;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Iterator<TextDecoration> iterator() {
/* 313 */       return Spliterators.iterator(Arrays.spliterator(DecorationMap.DECORATIONS));
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 318 */       return DecorationMap.MAP_SIZE;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\DecorationMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */