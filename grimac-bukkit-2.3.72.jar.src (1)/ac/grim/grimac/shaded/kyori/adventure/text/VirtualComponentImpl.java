/*    */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ final class VirtualComponentImpl<C>
/*    */   extends TextComponentImpl
/*    */   implements VirtualComponent
/*    */ {
/*    */   private final Class<C> contextType;
/*    */   private final VirtualComponentRenderer<C> renderer;
/*    */   
/*    */   static <C> VirtualComponent createVirtual(@NotNull Class<C> contextType, @NotNull VirtualComponentRenderer<C> renderer) {
/* 33 */     return createVirtual(contextType, renderer, Collections.emptyList(), Style.empty());
/*    */   }
/*    */   
/*    */   static <C> VirtualComponent createVirtual(@NotNull Class<C> contextType, @NotNull VirtualComponentRenderer<C> renderer, List<? extends ComponentLike> children, Style style) {
/* 37 */     List<Component> filteredChildren = ComponentLike.asComponents(children, IS_NOT_EMPTY);
/*    */     
/* 39 */     return new VirtualComponentImpl<>(filteredChildren, style, "", contextType, renderer);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private VirtualComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String content, @NotNull Class<C> contextType, @NotNull VirtualComponentRenderer<C> renderer) {
/* 46 */     super(children, style, content);
/* 47 */     this.contextType = contextType;
/* 48 */     this.renderer = renderer;
/*    */   }
/*    */ 
/*    */   
/*    */   VirtualComponent create0(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String content) {
/* 53 */     return new VirtualComponentImpl(ComponentLike.asComponents(children, IS_NOT_EMPTY), style, content, this.contextType, this.renderer);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Class<C> contextType() {
/* 58 */     return this.contextType;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public VirtualComponentRenderer<C> renderer() {
/* 63 */     return this.renderer;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String content() {
/* 68 */     return this.renderer.fallbackString();
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public TextComponent.Builder toBuilder() {
/* 73 */     return new BuilderImpl<>(this);
/*    */   }
/*    */   
/*    */   static final class BuilderImpl<C> extends TextComponentImpl.BuilderImpl {
/*    */     private final Class<C> contextType;
/*    */     private final VirtualComponentRenderer<C> renderer;
/*    */     
/*    */     BuilderImpl(VirtualComponentImpl<C> other) {
/* 81 */       super(other);
/* 82 */       this.contextType = other.contextType();
/* 83 */       this.renderer = other.renderer();
/*    */     }
/*    */     
/*    */     @NotNull
/*    */     public TextComponent build() {
/* 88 */       return VirtualComponentImpl.createVirtual(this.contextType, this.renderer, (List)this.children, buildStyle());
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\VirtualComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */