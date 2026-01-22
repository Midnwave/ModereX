/*    */ package ac.grim.grimac.shaded.kyori.adventure.platform.facet;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class FacetBase<V>
/*    */   implements Facet<V>
/*    */ {
/*    */   protected final Class<? extends V> viewerClass;
/*    */   
/*    */   protected FacetBase(@Nullable Class<? extends V> viewerClass) {
/* 41 */     this.viewerClass = viewerClass;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isSupported() {
/* 46 */     return (this.viewerClass != null);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isApplicable(@NotNull V viewer) {
/* 51 */     return (this.viewerClass != null && this.viewerClass.isInstance(viewer));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\facet\FacetBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */