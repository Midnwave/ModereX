/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Debug.Renderer;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Spliterator;
/*     */ import java.util.Spliterators;
/*     */ import java.util.function.Consumer;
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
/*     */ @Renderer(text = "\"ListBinaryTag[type=\" + this.type.toString() + \"]\"", childrenArray = "this.tags.toArray()", hasChildren = "!this.tags.isEmpty()")
/*     */ final class ListBinaryTagImpl
/*     */   extends AbstractBinaryTag
/*     */   implements ListBinaryTag
/*     */ {
/*  44 */   static final ListBinaryTag EMPTY = new ListBinaryTagImpl((BinaryTagType)BinaryTagTypes.END, false, Collections.emptyList());
/*     */   private final List<BinaryTag> tags;
/*     */   private final boolean permitsHeterogeneity;
/*     */   private final BinaryTagType<? extends BinaryTag> elementType;
/*     */   private final int hashCode;
/*     */   
/*     */   ListBinaryTagImpl(BinaryTagType<? extends BinaryTag> elementType, boolean permitsHeterogeneity, List<BinaryTag> tags) {
/*  51 */     this.tags = Collections.unmodifiableList(tags);
/*  52 */     this.permitsHeterogeneity = permitsHeterogeneity;
/*  53 */     this.elementType = elementType;
/*  54 */     this.hashCode = tags.hashCode();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BinaryTagType<? extends BinaryTag> elementType() {
/*  59 */     return this.elementType;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  64 */     return this.tags.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  69 */     return this.tags.isEmpty();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BinaryTag get(int index) {
/*  74 */     return this.tags.get(index);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ListBinaryTag set(int index, @NotNull BinaryTag newTag, @Nullable Consumer<? super BinaryTag> removed) {
/*  79 */     BinaryTagType<?> targetType = validateTagType(newTag, this.elementType, this.permitsHeterogeneity);
/*  80 */     return edit(tags -> { BinaryTag oldTag = tags.set(index, newTag); if (removed != null) removed.accept(oldTag);  }(BinaryTagType)targetType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ListBinaryTag remove(int index, @Nullable Consumer<? super BinaryTag> removed) {
/*  90 */     return edit(tags -> { BinaryTag oldTag = tags.remove(index); if (removed != null) removed.accept(oldTag);  }null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ListBinaryTag add(BinaryTag tag) {
/* 100 */     BinaryTagType<?> targetType = validateTagType(tag, this.elementType, this.permitsHeterogeneity);
/* 101 */     return edit(tags -> tags.add(tag), (BinaryTagType)targetType);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ListBinaryTag add(Iterable<? extends BinaryTag> tagsToAdd) {
/* 106 */     if (tagsToAdd instanceof Collection && ((Collection)tagsToAdd).isEmpty()) {
/* 107 */       return this;
/*     */     }
/* 109 */     BinaryTagType<?> type = validateTagType(tagsToAdd, this.permitsHeterogeneity);
/* 110 */     return edit(tags -> { for (BinaryTag tag : tagsToAdd) tags.add(tag);  }(BinaryTagType)type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void noAddEnd(BinaryTag tag) {
/* 119 */     if (tag.type() == BinaryTagTypes.END) {
/* 120 */       throw new IllegalArgumentException(String.format("Cannot add a %s to a %s", new Object[] { BinaryTagTypes.END, BinaryTagTypes.LIST }));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static BinaryTagType<?> validateTagType(Iterable<? extends BinaryTag> tags, boolean permitHeterogeneity) {
/* 126 */     BinaryTagType<?> type = null;
/* 127 */     for (BinaryTag tag : tags) {
/* 128 */       if (type == null) {
/* 129 */         noAddEnd(tag);
/* 130 */         type = tag.type(); continue;
/*     */       } 
/* 132 */       validateTagType(tag, (BinaryTagType)type, permitHeterogeneity);
/* 133 */       if (type != tag.type()) {
/* 134 */         type = BinaryTagTypes.LIST_WILDCARD;
/*     */       }
/*     */     } 
/*     */     
/* 138 */     return type;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static BinaryTagType<?> validateTagType(BinaryTag tag, BinaryTagType<? extends BinaryTag> type, boolean permitHeterogenity) {
/* 144 */     noAddEnd(tag);
/* 145 */     if (type == BinaryTagTypes.END) {
/* 146 */       return tag.type();
/*     */     }
/*     */     
/* 149 */     if (tag.type() != type && !permitHeterogenity) {
/* 150 */       throw new IllegalArgumentException(String.format("Trying to add tag of type %s to list of %s", new Object[] { tag.type(), type }));
/*     */     }
/* 152 */     return (tag.type() != type) ? BinaryTagTypes.LIST_WILDCARD : type;
/*     */   }
/*     */   
/*     */   private ListBinaryTag edit(Consumer<List<BinaryTag>> consumer, @Nullable BinaryTagType<? extends BinaryTag> maybeElementType) {
/* 156 */     List<BinaryTag> tags = new ArrayList<>(this.tags);
/* 157 */     consumer.accept(tags);
/* 158 */     BinaryTagType<? extends BinaryTag> elementType = this.elementType;
/*     */     
/* 160 */     if (maybeElementType != null) {
/* 161 */       elementType = maybeElementType;
/*     */     }
/* 163 */     return new ListBinaryTagImpl(elementType, this.permitsHeterogeneity, new ArrayList<>(tags));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<BinaryTag> stream() {
/* 168 */     return this.tags.stream();
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ListBinaryTag unwrapHeterogeneity() {
/* 174 */     if (!this.permitsHeterogeneity) {
/* 175 */       if (this.elementType != BinaryTagTypes.COMPOUND) {
/* 176 */         return new ListBinaryTagImpl(this.elementType, true, this.tags);
/*     */       }
/* 178 */       List<BinaryTag> newTags = null;
/*     */       
/* 180 */       for (ListIterator<BinaryTag> it = this.tags.listIterator(); it.hasNext(); ) {
/* 181 */         BinaryTag current = it.next();
/* 182 */         BinaryTag unboxed = ListBinaryTag0.unbox((CompoundBinaryTag)current);
/*     */         
/* 184 */         if (unboxed != current && newTags == null) {
/* 185 */           newTags = new ArrayList<>(this.tags.size());
/* 186 */           for (int idx = it.nextIndex() - 1, ptr = 0; ptr < idx; ptr++) {
/* 187 */             newTags.add(this.tags.get(ptr));
/*     */           }
/*     */         } 
/*     */         
/* 191 */         if (newTags != null) {
/* 192 */           newTags.add(unboxed);
/*     */         }
/*     */       } 
/* 195 */       return new ListBinaryTagImpl((newTags == null) ? (BinaryTagType)BinaryTagTypes.COMPOUND : BinaryTagTypes.LIST_WILDCARD, true, (newTags == null) ? this.tags : newTags);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 200 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ListBinaryTag wrapHeterogeneity() {
/* 205 */     if (this.elementType != BinaryTagTypes.LIST_WILDCARD) {
/* 206 */       return this;
/*     */     }
/*     */     
/* 209 */     List<BinaryTag> newTags = new ArrayList<>(this.tags.size());
/* 210 */     for (BinaryTag tag : this.tags) {
/* 211 */       newTags.add(ListBinaryTag0.box(tag));
/*     */     }
/*     */     
/* 214 */     return new ListBinaryTagImpl((BinaryTagType)BinaryTagTypes.COMPOUND, false, newTags);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Iterator<BinaryTag> iterator() {
/* 219 */     final Iterator<BinaryTag> iterator = this.tags.iterator();
/* 220 */     return new Iterator<BinaryTag>()
/*     */       {
/*     */         public boolean hasNext() {
/* 223 */           return iterator.hasNext();
/*     */         }
/*     */ 
/*     */         
/*     */         public BinaryTag next() {
/* 228 */           return iterator.next();
/*     */         }
/*     */ 
/*     */         
/*     */         public void forEachRemaining(Consumer<? super BinaryTag> action) {
/* 233 */           iterator.forEachRemaining(action);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public void forEach(Consumer<? super BinaryTag> action) {
/* 240 */     this.tags.forEach(action);
/*     */   }
/*     */ 
/*     */   
/*     */   public Spliterator<BinaryTag> spliterator() {
/* 245 */     return Spliterators.spliterator(this.tags, 1040);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object that) {
/* 250 */     return (this == that || (that instanceof ListBinaryTagImpl && this.tags.equals(((ListBinaryTagImpl)that).tags)));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 255 */     return this.hashCode;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 260 */     return Stream.of(new ExaminableProperty[] {
/* 261 */           ExaminableProperty.of("tags", this.tags), 
/* 262 */           ExaminableProperty.of("type", this.elementType)
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\ListBinaryTagImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */