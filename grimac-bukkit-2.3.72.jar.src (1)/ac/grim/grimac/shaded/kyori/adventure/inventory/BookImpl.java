/*     */ package ac.grim.grimac.shaded.kyori.adventure.inventory;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ final class BookImpl
/*     */   implements Book
/*     */ {
/*     */   private final Component title;
/*     */   private final Component author;
/*     */   private final List<Component> pages;
/*     */   
/*     */   BookImpl(@NotNull Component title, @NotNull Component author, @NotNull List<Component> pages) {
/*  44 */     this.title = Objects.<Component>requireNonNull(title, "title");
/*  45 */     this.author = Objects.<Component>requireNonNull(author, "author");
/*  46 */     this.pages = Collections.unmodifiableList(Objects.<List<? extends Component>>requireNonNull(pages, "pages"));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component title() {
/*  51 */     return this.title;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Book title(@NotNull Component title) {
/*  56 */     return new BookImpl(Objects.<Component>requireNonNull(title, "title"), this.author, this.pages);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component author() {
/*  61 */     return this.author;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Book author(@NotNull Component author) {
/*  66 */     return new BookImpl(this.title, Objects.<Component>requireNonNull(author, "author"), this.pages);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public List<Component> pages() {
/*  71 */     return this.pages;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Book pages(@NotNull List<Component> pages) {
/*  76 */     return new BookImpl(this.title, this.author, new ArrayList<>(Objects.<Collection<? extends Component>>requireNonNull(pages, "pages")));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/*  81 */     return Stream.of(new ExaminableProperty[] {
/*  82 */           ExaminableProperty.of("title", this.title), 
/*  83 */           ExaminableProperty.of("author", this.author), 
/*  84 */           ExaminableProperty.of("pages", this.pages)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  90 */     if (this == o) return true; 
/*  91 */     if (!(o instanceof BookImpl)) return false; 
/*  92 */     BookImpl that = (BookImpl)o;
/*  93 */     return (this.title.equals(that.title) && this.author
/*  94 */       .equals(that.author) && this.pages
/*  95 */       .equals(that.pages));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 100 */     int result = this.title.hashCode();
/* 101 */     result = 31 * result + this.author.hashCode();
/* 102 */     result = 31 * result + this.pages.hashCode();
/* 103 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 108 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl implements Book.Builder {
/* 112 */     private Component title = (Component)Component.empty();
/* 113 */     private Component author = (Component)Component.empty();
/* 114 */     private final List<Component> pages = new ArrayList<>();
/*     */     
/*     */     @NotNull
/*     */     public Book.Builder title(@NotNull Component title) {
/* 118 */       this.title = Objects.<Component>requireNonNull(title, "title");
/* 119 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Book.Builder author(@NotNull Component author) {
/* 124 */       this.author = Objects.<Component>requireNonNull(author, "author");
/* 125 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Book.Builder addPage(@NotNull Component page) {
/* 130 */       this.pages.add(Objects.<Component>requireNonNull(page, "page"));
/* 131 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Book.Builder pages(@NotNull Collection<Component> pages) {
/* 136 */       this.pages.addAll(Objects.<Collection<? extends Component>>requireNonNull(pages, "pages"));
/* 137 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Book.Builder pages(@NotNull Component... pages) {
/* 142 */       Collections.addAll(this.pages, pages);
/* 143 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Book build() {
/* 148 */       return new BookImpl(this.title, this.author, new ArrayList<>(this.pages));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\inventory\BookImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */