/*     */ package ac.grim.grimac.shaded.kyori.adventure.inventory;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
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
/*     */ @NonExtendable
/*     */ public interface Book
/*     */   extends Buildable<Book, Book.Builder>, Examinable
/*     */ {
/*     */   @NotNull
/*     */   static Book book(@NotNull Component title, @NotNull Component author, @NotNull Collection<Component> pages) {
/*  62 */     return new BookImpl(title, author, new ArrayList<>(pages));
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
/*     */   @NotNull
/*     */   static Book book(@NotNull Component title, @NotNull Component author, @NotNull Component... pages) {
/*  75 */     return book(title, author, Arrays.asList(pages));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Builder builder() {
/*  85 */     return new BookImpl.BuilderImpl();
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
/*     */   @Contract(value = "_ -> new", pure = true)
/*     */   @NotNull
/*     */   Book pages(@NotNull Component... pages) {
/* 143 */     return pages(Arrays.asList(pages));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default Builder toBuilder() {
/* 163 */     return builder()
/* 164 */       .title(title())
/* 165 */       .author(author())
/* 166 */       .pages(pages());
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   Component title();
/*     */   
/*     */   @Contract(value = "_ -> new", pure = true)
/*     */   @NotNull
/*     */   Book title(@NotNull Component paramComponent);
/*     */   
/*     */   @NotNull
/*     */   Component author();
/*     */   
/*     */   @Contract(value = "_ -> new", pure = true)
/*     */   @NotNull
/*     */   Book author(@NotNull Component paramComponent);
/*     */   
/*     */   @NotNull
/*     */   List<Component> pages();
/*     */   
/*     */   @Contract(value = "_ -> new", pure = true)
/*     */   @NotNull
/*     */   Book pages(@NotNull List<Component> paramList);
/*     */   
/*     */   public static interface Builder extends AbstractBuilder<Book>, Buildable.Builder<Book> {
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder title(@NotNull Component param1Component);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder author(@NotNull Component param1Component);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder addPage(@NotNull Component param1Component);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder pages(@NotNull Component... param1VarArgs);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder pages(@NotNull Collection<Component> param1Collection);
/*     */     
/*     */     @NotNull
/*     */     Book build();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\inventory\Book.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */