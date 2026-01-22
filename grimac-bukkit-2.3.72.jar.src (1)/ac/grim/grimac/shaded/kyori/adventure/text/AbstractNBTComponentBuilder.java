/*    */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Objects;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class AbstractNBTComponentBuilder<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>>
/*    */   extends AbstractComponentBuilder<C, B>
/*    */   implements NBTComponentBuilder<C, B>
/*    */ {
/*    */   @Nullable
/*    */   protected String nbtPath;
/*    */   protected boolean interpret = false;
/*    */   @Nullable
/*    */   protected Component separator;
/*    */   
/*    */   AbstractNBTComponentBuilder() {}
/*    */   
/*    */   AbstractNBTComponentBuilder(@NotNull C component) {
/* 40 */     super(component);
/* 41 */     this.nbtPath = component.nbtPath();
/* 42 */     this.interpret = component.interpret();
/* 43 */     this.separator = component.separator();
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public B nbtPath(@NotNull String nbtPath) {
/* 49 */     this.nbtPath = Objects.<String>requireNonNull(nbtPath, "nbtPath");
/* 50 */     return (B)this;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public B interpret(boolean interpret) {
/* 56 */     this.interpret = interpret;
/* 57 */     return (B)this;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public B separator(@Nullable ComponentLike separator) {
/* 63 */     this.separator = ComponentLike.unbox(separator);
/* 64 */     return (B)this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\AbstractNBTComponentBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */