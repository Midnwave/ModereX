/*    */ package ac.grim.grimac.shaded.kyori.adventure.platform.facet;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.util.Set;
/*    */ import java.util.function.Function;
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
/*    */ class FacetBossBarListener<V>
/*    */   implements Facet.BossBar<V>
/*    */ {
/*    */   private final Facet.BossBar<V> facet;
/*    */   private final Function<Component, Component> translator;
/*    */   
/*    */   FacetBossBarListener(Facet.BossBar<V> facet, @NotNull Function<Component, Component> translator) {
/* 37 */     this.facet = facet;
/* 38 */     this.translator = translator;
/*    */   }
/*    */ 
/*    */   
/*    */   public void bossBarInitialized(@NotNull BossBar bar) {
/* 43 */     this.facet.bossBarInitialized(bar);
/* 44 */     bossBarNameChanged(bar, bar.name(), bar.name());
/*    */   }
/*    */ 
/*    */   
/*    */   public void bossBarNameChanged(@NotNull BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
/* 49 */     this.facet.bossBarNameChanged(bar, oldName, this.translator.apply(newName));
/*    */   }
/*    */ 
/*    */   
/*    */   public void bossBarProgressChanged(@NotNull BossBar bar, float oldPercent, float newPercent) {
/* 54 */     this.facet.bossBarProgressChanged(bar, oldPercent, newPercent);
/*    */   }
/*    */ 
/*    */   
/*    */   public void bossBarColorChanged(@NotNull BossBar bar, BossBar.Color oldColor, BossBar.Color newColor) {
/* 59 */     this.facet.bossBarColorChanged(bar, oldColor, newColor);
/*    */   }
/*    */ 
/*    */   
/*    */   public void bossBarOverlayChanged(@NotNull BossBar bar, BossBar.Overlay oldOverlay, BossBar.Overlay newOverlay) {
/* 64 */     this.facet.bossBarOverlayChanged(bar, oldOverlay, newOverlay);
/*    */   }
/*    */ 
/*    */   
/*    */   public void bossBarFlagsChanged(@NotNull BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {
/* 69 */     this.facet.bossBarFlagsChanged(bar, flagsAdded, flagsRemoved);
/*    */   }
/*    */ 
/*    */   
/*    */   public void addViewer(@NotNull V viewer) {
/* 74 */     this.facet.addViewer(viewer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeViewer(@NotNull V viewer) {
/* 79 */     this.facet.removeViewer(viewer);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 84 */     return this.facet.isEmpty();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/* 89 */     this.facet.close();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\facet\FacetBossBarListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */