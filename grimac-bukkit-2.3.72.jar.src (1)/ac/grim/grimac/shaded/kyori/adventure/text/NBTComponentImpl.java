/*    */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*    */ import java.util.List;
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
/*    */ abstract class NBTComponentImpl<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>>
/*    */   extends AbstractComponent
/*    */   implements NBTComponent<C, B>
/*    */ {
/*    */   static final boolean INTERPRET_DEFAULT = false;
/*    */   final String nbtPath;
/*    */   final boolean interpret;
/*    */   @Nullable
/*    */   final Component separator;
/*    */   
/*    */   NBTComponentImpl(@NotNull List<Component> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable Component separator) {
/* 39 */     super((List)children, style);
/* 40 */     this.nbtPath = nbtPath;
/* 41 */     this.interpret = interpret;
/* 42 */     this.separator = separator;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String nbtPath() {
/* 47 */     return this.nbtPath;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean interpret() {
/* 52 */     return this.interpret;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 57 */     if (this == other) return true; 
/* 58 */     if (!(other instanceof NBTComponent)) return false; 
/* 59 */     if (!super.equals(other)) return false; 
/* 60 */     NBTComponent<?, ?> that = (NBTComponent<?, ?>)other;
/* 61 */     return (Objects.equals(this.nbtPath, that.nbtPath()) && this.interpret == that.interpret() && Objects.equals(this.separator, that.separator()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 66 */     int result = super.hashCode();
/* 67 */     result = 31 * result + this.nbtPath.hashCode();
/* 68 */     result = 31 * result + Boolean.hashCode(this.interpret);
/* 69 */     result = 31 * result + Objects.hashCode(this.separator);
/* 70 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\NBTComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */